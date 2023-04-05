package com.example.transformaction.tree.doubleclickandchecktime

import com.example.transformaction.DoubleClickConfig
import com.example.transformaction.filterLambda
import com.example.transformaction.hasAnnotation
import com.example.transformaction.isStatic
import com.example.transformaction.nameWithDesc
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Handle
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.JumpInsnNode
import org.objectweb.asm.tree.LabelNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.VarInsnNode


/**
 * 文件名： <br/>
 * 描述：🌝🌝🌝
 *
 * @author wulinran
 * @since 2022/12/2 16:53
 */
class TreeTestVisitor(
    private val nextClassVisitor: ClassVisitor,
    private val config: DoubleClickConfig
) : ClassNode(Opcodes.ASM5) {

    private companion object {
        private const val ViewDescriptor = "Landroid/view/View;"

        private const val ButterKnifeOnClickAnnotationDesc = "Lbutterknife/OnClick;"
    }

    override fun visitEnd() {
        super.visitEnd()
        val shouldHookMethodList = mutableSetOf<MethodNode>()
        methods.forEach { methodNode ->
            when {
                methodNode.isStatic -> {
                    //不处理静态方法
                }

                methodNode.hasUncheckViewOnClickAnnotation() -> {
                    //不处理包含 UncheckViewOnClick 注解的方法
                }

                methodNode.hasCheckViewAnnotation() -> {
                    //使用了 CheckViewOnClick 注解的情况
                    shouldHookMethodList.add(methodNode)
                }

                methodNode.hasButterKnifeOnClickAnnotation() -> {
                    //使用了 ButterKnife OnClick 注解的情况
                    shouldHookMethodList.add(methodNode)
                }

                methodNode.isHookPoint() -> {
                    //使用了匿名内部类的情况
                    shouldHookMethodList.add(methodNode)
                }
            }
            //判断方法内部是否有需要处理的 lambda 表达式
            val dynamicInsnNodes = methodNode.filterLambda {
                val nodeName = it.name
                val nodeDesc = it.desc
                val find = config.hookPointList.find { point ->
                    nodeName == point.methodName && nodeDesc.endsWith(point.interfaceSignSuffix)
                }
                find != null
            }
            dynamicInsnNodes.forEach {
                val handle = it.bsmArgs[1] as? Handle
                if (handle != null) {
                    //找到 lambda 指向的目标方法
                    val nameWithDesc = handle.name + handle.desc
                    val method = methods.find { it.nameWithDesc == nameWithDesc }!!
                    shouldHookMethodList.add(method)
                }
            }
        }
        shouldHookMethodList.forEach {
            hookMethod(modeNode = it)
        }
        accept(nextClassVisitor)
    }

    private fun hookMethod(modeNode: MethodNode) {
        val argumentTypes = Type.getArgumentTypes(modeNode.desc)
        val viewArgumentIndex = argumentTypes?.indexOfFirst {
            it.descriptor == ViewDescriptor
        } ?: -1
        if (viewArgumentIndex >= 0) {
            val instructions = modeNode.instructions
            if (instructions != null && instructions.size() > 0) {

                val listCheck = InsnList()
                val index =  getVisitPosition(
                    argumentTypes,
                    viewArgumentIndex,
                    modeNode.isStatic
                )

                listCheck.add(
                    VarInsnNode(
                        Opcodes.ALOAD, index
                    )
                )
                listCheck.add(
                    MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        config.formatDoubleClickCheckClass,
                        config.onClickMethodName,
                        config.onClickMethodDesc
                    )
                )
                val labelNode = LabelNode()
                listCheck.add(JumpInsnNode(Opcodes.IFNE, labelNode))
                listCheck.add(InsnNode(Opcodes.RETURN))
                listCheck.add(labelNode)
                instructions.insert(listCheck)



                // 目的是在方法末尾插入字节码
                for( node in instructions){
                    //判断是不是方法结尾的AbstractInsnNode
                    if(node.opcode == Opcodes.ARETURN || node.opcode == Opcodes.RETURN){
                        // 创建字节码
                        val listEnd = InsnList()

                        listEnd.add(
                            VarInsnNode(
                                Opcodes.ALOAD, index
                            )
                        )
                        listEnd.add(
                            MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                "com/example/transformsaction/view/ToastClick",
                                "endClick",
                                "()V"
                            )
                        )

                        // 将字节码插入到结尾node之前，使用insertBefore
                        instructions.insertBefore(node,listEnd)
                    }

                }


                // 在方法开始插入字节码
                val list = InsnList()

                list.add(
                    VarInsnNode(
                        Opcodes.ALOAD, index
                    )
                )
                list.add(
                    MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "com/example/transformsaction/view/ToastClick",
                        "startClick",
                       "()V"
                    )
                )
                instructions.insert(list)
            }

            System.out.println("点击防抖，耗时统计已插入")
        }
    }

    private fun getVisitPosition(
        argumentTypes: Array<Type>,
        parameterIndex: Int,
        isStaticMethod: Boolean
    ): Int {
        if (parameterIndex < 0 || parameterIndex >= argumentTypes.size) {
            throw Error("getVisitPosition error")
        }
        return if (parameterIndex == 0) {
            if (isStaticMethod) {
                0
            } else {
                1
            }
        } else {
            getVisitPosition(
                argumentTypes,
                parameterIndex - 1,
                isStaticMethod
            ) + argumentTypes[parameterIndex - 1].size
        }
    }

    private fun MethodNode.isHookPoint(): Boolean {
        val myInterfaces = interfaces
        if (myInterfaces.isNullOrEmpty()) {
            return false
        }
        val extraHookMethodList = config.hookPointList
        extraHookMethodList.forEach {
            if (myInterfaces.contains(it.interfaceName) && this.nameWithDesc == it.nameWithDesc) {
                return true
            }
        }
        return false
    }

    private fun MethodNode.hasCheckViewAnnotation(): Boolean {
        return hasAnnotation(config.formatCheckViewOnClickAnnotation)
    }

    private fun MethodNode.hasUncheckViewOnClickAnnotation(): Boolean {
        return hasAnnotation(config.formatUncheckViewOnClickAnnotation)
    }

    private fun MethodNode.hasButterKnifeOnClickAnnotation(): Boolean {
        return hasAnnotation(ButterKnifeOnClickAnnotationDesc)
    }
}