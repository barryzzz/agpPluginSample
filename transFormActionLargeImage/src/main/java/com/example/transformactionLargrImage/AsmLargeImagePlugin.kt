package com.example.transformactionLargrImage

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
class AsmLargeImagePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("我是大图检测插件")
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponents.onVariants { variant ->
            variant.instrumentation.transformClassesWith(
                LargeImageTransform::class.java,
                InstrumentationScope.ALL) {params->
                params.config.set(LargeImageConfig())
            }
            variant.instrumentation.setAsmFramesComputationMode(
                FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS
            )
        }
        println("大图检测插件插入完成")
    }
}