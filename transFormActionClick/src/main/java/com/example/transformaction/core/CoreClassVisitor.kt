package com.example.transformaction.core

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * 文件名： <br/>
 * 描述：🌝🌝🌝
 *
 * @author wulinran
 * @since 2022/12/2 13:44
 */
class CoreClassVisitor(nextVisitor: ClassVisitor) : ClassVisitor(Opcodes.ASM7, nextVisitor) {

    override fun visitMethod(
        access: Int,
        name: String,
        desc: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val methodVisitor = super.visitMethod(access, name, desc, signature, exceptions)
        return MyClickVisitor(Opcodes.ASM7,methodVisitor,access,name,desc)
    }

}
