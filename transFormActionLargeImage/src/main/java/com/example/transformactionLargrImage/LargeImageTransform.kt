package com.example.transformactionLargrImage

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import com.example.transformactionLargrImage.tree.largeImage.BigImageVisit
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.objectweb.asm.ClassVisitor

interface ViewDoubleClickConfigParameters : InstrumentationParameters {
    @get:Input
    val config: Property<LargeImageConfig>
}
abstract class LargeImageTransform: AsmClassVisitorFactory<ViewDoubleClickConfigParameters> {
    override fun createClassVisitor(classContext: ClassContext, nextClassVisitor: ClassVisitor): ClassVisitor {
        return BigImageVisit(
            nextClassVisitor = nextClassVisitor,
            config = LargeImageConfig()
        )
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return true
    }
}
