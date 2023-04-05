package com.example.transformaction

import java.io.Serializable

/**
 * 文件名： <br/>
 * 描述：🌝🌝🌝
 *
 * @author wulinran
 * @since 2022/12/8 09:51
 */
data class DoubleClickConfig(
    private val doubleClickCheckClass: String = "com.example.transformsaction.view.ViewDoubleClickCheck",
    val onClickMethodName: String = "onClick",
    private val checkViewOnClickAnnotation: String = "com.example.transformsaction.annotation.CheckViewOnClick",
    private val uncheckViewOnClickAnnotation: String = "com.example.transformsaction.annotation.UncheckViewOnClick",
    val hookPointList: List<ViewDoubleClickHookPoint> = extraHookPoints
) : Serializable {

    val onClickMethodDesc = "(Landroid/view/View;)Z"

    val formatDoubleClickCheckClass: String
        get() = doubleClickCheckClass.replace(".", "/")

    val formatCheckViewOnClickAnnotation: String
        get() = "L" + checkViewOnClickAnnotation.replace(".", "/") + ";"

    val formatUncheckViewOnClickAnnotation: String
        get() = "L" + uncheckViewOnClickAnnotation.replace(".", "/") + ";"

}

data class ViewDoubleClickHookPoint(
    val interfaceName: String,
    val methodName: String,
    val nameWithDesc: String,
) : Serializable {

    val interfaceSignSuffix = "L$interfaceName;"

}

private val extraHookPoints = listOf(
    ViewDoubleClickHookPoint(
        interfaceName = "android/view/View\$OnClickListener",
        methodName = "onClick",
        nameWithDesc = "onClick(Landroid/view/View;)V"
    ),
    ViewDoubleClickHookPoint(
        interfaceName = "com/chad/library/adapter/base/listener/OnItemClickListener",
        methodName = "onItemClick",
        nameWithDesc = "onItemClick(Lcom/chad/library/adapter/base/BaseQuickAdapter;Landroid/view/View;I)V"
    ),
    ViewDoubleClickHookPoint(
        interfaceName = "com/chad/library/adapter/base/listener/OnItemChildClickListener",
        methodName = "onItemChildClick",
        nameWithDesc = "onItemChildClick(Lcom/chad/library/adapter/base/BaseQuickAdapter;Landroid/view/View;I)V",
    )
)