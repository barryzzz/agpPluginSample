package com.example.transformsaction.view

import android.util.Log
import android.view.View


/*
 * 计算方法的耗时
 */
object ToastClick {

    var timeStamp:Long = 0
    @JvmStatic
    fun startClick(){
        timeStamp = System.currentTimeMillis()
        Log.e("开始点击", "开始时间："+(timeStamp).toString())
    }
    @JvmStatic
    fun endClick(){
        val nowStamp = System.currentTimeMillis()
        Log.e("耗时", "耗时:"+(nowStamp - timeStamp).toString())
        timeStamp = nowStamp
    }
}