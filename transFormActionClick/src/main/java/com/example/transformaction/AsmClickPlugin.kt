package com.example.transformaction

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project


/**
 * 文件名：AsmOlugin <br/>
 * 描述：TODO
 *
 * @since 2022/06/13 18:21
 */
class AsmClickPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("我是点击插件")
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponents.onVariants { variant ->
            variant.instrumentation.transformClassesWith(
                TimeCostTransform::class.java,
                InstrumentationScope.ALL) {params->
                params.config.set(DoubleClickConfig())
            }
            variant.instrumentation.setAsmFramesComputationMode(
                FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS
            )
        }
        println("点击插件插入完成")
    }
}