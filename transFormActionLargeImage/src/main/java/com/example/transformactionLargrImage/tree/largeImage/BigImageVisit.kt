package com.example.transformactionLargrImage.tree.largeImage

import com.example.transformactionLargrImage.LargeImageConfig
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode

/**
 * 文件名： <br/>
 * 描述：🌝🌝🌝
 *
 * @author wulinran
 * @since 2023/1/16 17:02
 */


class BigImageVisit(
    private val nextClassVisitor: ClassVisitor,
    private val config : LargeImageConfig
) :
    ClassNode(Opcodes.ASM5) {

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        println("visit: name -> $name  superName -> $superName")
        if (name != config.hookImageViewClass && superName == config.imageViewClass) {
            println("visit insert: name -> $name  superName -> $superName")
            super.visit(
                version,
                access,
                name,
                signature,
                config.hookImageViewClass,
                interfaces
            )
        } else {
            super.visit(version, access, name, signature, superName, interfaces)
        }
    }

    override fun visitEnd() {
        super.visitEnd()
        accept(nextClassVisitor)
    }

}