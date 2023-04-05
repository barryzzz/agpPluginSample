package com.example.transformsaction.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Looper
import android.os.MessageQueue
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView


/**
 * 文件名： <br/>
 * 描述：🌝🌝🌝
 *
 * @author wulinran
 * @since 2023/1/16 16:53
 */
@SuppressLint("AppCompatCustomView")
open class MyHookImageView  @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr), MessageQueue.IdleHandler {

    companion object {

        private const val MAX_ALARM_IMAGE_SIZE = 1024

    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        monitor()
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        monitor()
    }

    private fun monitor() {
        Looper.myQueue().removeIdleHandler(this)
        Looper.myQueue().addIdleHandler(this)
    }

    override fun queueIdle(): Boolean {
        checkDrawable()
        return false
    }

    private fun checkDrawable() {
        val mDrawable = drawable ?: return
        val drawableWidth = mDrawable.intrinsicWidth
        val drawableHeight = mDrawable.intrinsicHeight
        val viewWidth = measuredWidth
        val viewHeight = measuredHeight
        val imageSize = calculateImageSize(mDrawable)

        Log.e("检查图片","检查图片大小 -> $imageSize")

        if (imageSize > MAX_ALARM_IMAGE_SIZE) {
            log(log = "图片大小超标 -> $imageSize")
        }
        if (drawableWidth > viewWidth || drawableHeight > viewHeight) {
            log(log = "图片尺寸超标 -> drawable：$drawableWidth x $drawableHeight  view：$viewWidth x $viewHeight")
        }
    }

    private fun calculateImageSize(drawable: Drawable): Int {
        return when (drawable) {
            is BitmapDrawable -> {
                drawable.bitmap.byteCount
            }
            else -> {
                0
            }
        }
    }

    private fun log(log: String) {
        Log.e(javaClass.simpleName, log)
    }

}
