package com.example.transformaction

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import com.example.transformaction.tree.doubleclickandchecktime.TreeTestVisitor
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.objectweb.asm.ClassVisitor

interface ViewDoubleClickConfigParameters : InstrumentationParameters {
    @get:Input
    val config: Property<DoubleClickConfig>
}
abstract class TimeCostTransform: AsmClassVisitorFactory<ViewDoubleClickConfigParameters> {
    override fun createClassVisitor(classContext: ClassContext, nextClassVisitor: ClassVisitor): ClassVisitor {
        return TreeTestVisitor(
            nextClassVisitor = nextClassVisitor,
            config = parameters.get().config.get()
        )
//        return TimeCostClassVisitor(nextClassVisitor)

    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return true
    }
}
