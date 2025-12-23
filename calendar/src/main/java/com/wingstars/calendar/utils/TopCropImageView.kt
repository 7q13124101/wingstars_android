package com.wingstars.calendar.utils

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class TopCropImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    init {
        scaleType = ScaleType.MATRIX
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        // 布局变化时重新计算缩放和位置
        updateImageMatrix()
    }

    private fun updateImageMatrix() {
        val drawable = drawable ?: return
        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight
        if (drawableWidth == 0 || drawableHeight == 0) return

        val viewWidth = width - paddingLeft - paddingRight
        val viewHeight = height - paddingTop - paddingBottom
        if (viewWidth == 0 || viewHeight == 0) return

        val matrix = Matrix()
        // 计算缩放比例（和 centerCrop 逻辑一致：取覆盖 View 的最小缩放比）
        val scaleX = viewWidth.toFloat() / drawableWidth
        val scaleY = viewHeight.toFloat() / drawableHeight
        val scale = maxOf(scaleX, scaleY)

        // 缩放图片
        matrix.postScale(scale, scale)

        // 计算平移：让图片顶部对齐 View 顶部，水平居中（和 centerCrop 水平逻辑一致）
        val scaledWidth = drawableWidth * scale
        val scaledHeight = drawableHeight * scale
        val translateX = (viewWidth - scaledWidth) / 2f // 水平居中
        val translateY = 0f // 顶部对齐，不垂直平移（关键！）

        matrix.postTranslate(translateX + paddingLeft, translateY + paddingTop)

        // 应用矩阵
        imageMatrix = matrix
    }
}