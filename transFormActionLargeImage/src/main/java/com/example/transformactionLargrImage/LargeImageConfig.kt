package com.example.transformactionLargrImage

import java.io.Serializable

/**
 * 文件名： <br/>
 * 描述：🌝🌝🌝
 *
 * @author wulinran
 * @since 2022/12/8 09:51
 */
data class LargeImageConfig(
    val hookImageViewClass: String = "com/example/transformsaction/view/MyHookImageView",
    val imageViewClass: String = "android/widget/ImageView"
) : Serializable {

}

