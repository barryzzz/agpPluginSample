package com.example.transformsaction.view

import android.util.Log
import android.view.View

/**
 * 文件名： <br/>
 * 描述：🌝🌝🌝
 *
 * @author wulinran
 * @since 2022/12/8 11:08
 */
object ViewDoubleClickCheck {

    private const val MIN_DURATION = 500L

    private var lastClickTime = 0L

    private var clickIndex = 0

    @JvmStatic
    fun onClick(view: View): Boolean {
        clickIndex++
        log("onClick $clickIndex")
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > MIN_DURATION) {
            lastClickTime = currentTime
            log("onClick isEnabled : true")
            return true
        }
        log("onClick isEnabled : false")
        return false
    }

    private fun log(log: String) {
        Log.e("ViewDoubleClickCheck", log)
    }

}