package com.example.transformaction.core

import com.example.transformaction.DoubleClickConfig
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Handle
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.InvokeDynamicInsnNode


/**
 * 文件名： <br/>
 * 描述：🌝🌝🌝
 *
 * @author wulinran
 * @since 2022/11/30 15:05
 */
class MyClickVisitor(api: Int, methodVisitor: MethodVisitor?, access: Int, name: String?,
                     val descriptor: String?
) : AdviceAdapter(api, methodVisitor,
    access,
    name, descriptor
) {

    var config: DoubleClickConfig = DoubleClickConfig()
    var visibleAnnotations: ArrayList<AnnotationNode>? = null
    var instructions: InsnList = InsnList()

    private companion object {
        private const val ViewDescriptor = "Landroid/view/View;"

        private const val ButterKnifeOnClickAnnotationDesc = "Lbutterknife/OnClick;"
    }

    override fun onMethodEnter() {
        System.out.println("start : ");
        val viewArgumentIndex = argumentTypes?.indexOfFirst {
            it.descriptor == ViewDescriptor
        } ?: -1

        println("打印注解列表长度"+visibleAnnotations?.size)
        if ( !hasUncheckViewOnClickAnnotation() && (matchMethod(name, descriptor) || matchExitMethod())) {
            println("拦截一个")
            mv.visitVarInsn(ALOAD, getVisitPosition(
                argumentTypes,
                viewArgumentIndex,
                access and Opcodes.ACC_STATIC != 0
            ))
            mv.visitMethodInsn(
                INVOKESTATIC,
                "com/example/transformsaction/view/ViewDoubleClickCheck",
                "onClick",
                "(Landroid/view/View;)Z",
                false
            )
            val label0 = Label()
            mv.visitJumpInsn(IFNE, label0)
            mv.visitInsn(RETURN)
            mv.visitLabel(label0)
        }



        super.onMethodEnter()
    }


    private fun getVisitPosition(
        argumentTypes: Array<Type>,
        parameterIndex: Int,
        isStaticMethod: Boolean
    ): Int {
        if (parameterIndex < 0 || parameterIndex >= argumentTypes.size) {
            throw Error("getVisitPosition error"+parameterIndex)
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



    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor? {
        val annotation = AnnotationNode(descriptor)
        if(null == visibleAnnotations){
            visibleAnnotations = ArrayList()
        }
        if (visible) {
            println("添加注解:"+ descriptor)
            visibleAnnotations?.add(annotation)
        }
        return annotation
    }

    override fun visitInvokeDynamicInsn(
        name: String?,
        descriptor: String?,
        bootstrapMethodHandle: Handle?,
        vararg bootstrapMethodArguments: Any?
    ) {
        println("添加lambda:"+ descriptor+"  "+name)
        instructions.add(
            InvokeDynamicInsnNode(
                name, descriptor?.split(")")?.get(1) ?: descriptor, bootstrapMethodHandle, *bootstrapMethodArguments
            )
        )
    }
    private fun hasCheckViewAnnotation(): Boolean {
        return hasAnnotation(config.formatCheckViewOnClickAnnotation)
    }

    private fun hasUncheckViewOnClickAnnotation(): Boolean {
        return hasAnnotation(config.formatUncheckViewOnClickAnnotation)
    }

    private fun hasButterKnifeOnClickAnnotation(): Boolean {
        return hasAnnotation(ButterKnifeOnClickAnnotationDesc)
    }

    fun hasAnnotation(annotationDesc: String): Boolean {
        var value = visibleAnnotations?.find { it.desc == annotationDesc } != null
        println("判断注解:"+ value)
        return value
    }




    private fun matchMethod(name: String, desc: String?): Boolean {
        println("拦截判断$name  $desc")
        return  (name == "onClick" && desc == "(Landroid/view/View;)V")
                || (name == "onItemClick" && desc == "(Lcom/chad/library/adapter/base/BaseQuickAdapter;Landroid/view/View;I)V")
                || (name == "onItemChildClick" && desc == "(Lcom/chad/library/adapter/base/BaseQuickAdapter;Landroid/view/View;I)V")
    }

    private fun matchExitMethod(): Boolean {
        return (hasCheckViewAnnotation() || hasButterKnifeOnClickAnnotation())
    }

    private fun matchLambdaMethod(): Boolean {

        //判断方法内部是否有需要处理的 lambda 表达式
        val dynamicInsnNodes = filterLambda {
            val nodeName = it.name
            val nodeDesc = it.desc
            println("拦截判断lambda:"+nodeName+"  "+nodeDesc )
            val find = config.hookPointList.find { point ->
                nodeName == point.methodName && nodeDesc.endsWith(point.interfaceSignSuffix)
            }
            find != null
        }
        println("匹配lambda:"+ dynamicInsnNodes.isNotEmpty())
        return dynamicInsnNodes.isNotEmpty()
    }


    fun filterLambda(filter: (InvokeDynamicInsnNode) -> Boolean): List<InvokeDynamicInsnNode> {
        val mInstructions = instructions
        println("filterLambda mInstructions:"+ (mInstructions.size()))
        val dynamicList = mutableListOf<InvokeDynamicInsnNode>()
        mInstructions.forEach { instruction ->
            println("filterLambda lambda:"+ (instruction is InvokeDynamicInsnNode))
            if (instruction is InvokeDynamicInsnNode) {
                if (filter(instruction)) {
                    dynamicList.add(instruction)
                }
            }
        }
        return dynamicList
    }

}