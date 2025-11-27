package com.wingstars.base.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.tabs.TabLayout
import android.graphics.*

class DynamicWidthIndicatorDrawable(
    private val context: Context,
    private val tabLayout: TabLayout,      // 关联的TabLayout
    private val widthRatio: Float = 0.8f,  // 指示器宽度占内容宽度的比例 (0.8=80%)
    private val heightDp: Float = 4f,      // 指示器高度
    private val color: Int = Color.RED     // 指示器颜色
) : Drawable() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = this@DynamicWidthIndicatorDrawable.color
        style = Paint.Style.FILL
    }

    private var indicatorWidth = 0f
    private var currentPosition = 0

    // 将 dp 转换为像素
    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        )
    }


    // 部分圆角矩形（只有左上和右上圆角）
    private fun drawPartialRoundRect(canvas: Canvas, rect: RectF, radius: Float) {
        val path = Path()
        path.moveTo(rect.left, rect.top + radius)
        path.quadTo(rect.left, rect.top, rect.left + radius, rect.top)
        path.lineTo(rect.right - radius, rect.top)
        path.quadTo(rect.right, rect.top, rect.right, rect.top + radius)
        path.lineTo(rect.right, rect.bottom)
        path.lineTo(rect.left, rect.bottom)
        path.close()

        canvas.drawPath(path, paint)
    }

    override fun draw(canvas: Canvas) {
        if (indicatorWidth <= 0) return

        val bounds: Rect = bounds
        val indicatorHeightPx = dpToPx(heightDp)

        // 计算指示器位置（水平居中）
        val left = bounds.centerX() - indicatorWidth / 2
        val right = left + indicatorWidth
        val bottom = bounds.bottom.toFloat()
        val top = bottom - indicatorHeightPx

        //canvas.drawRect(left, top, right, bottom, paint)
        //canvas.drawRoundRect(RectF(left, top, right, bottom), 20f,20f,paint)
        drawPartialRoundRect(canvas,RectF(left, top, right, bottom), 20f)
    }

    // 更新指示器宽度
    fun updateIndicatorWidth(position: Int) {
        currentPosition = position

        // 获取对应位置的Tab视图
        val tabView = tabLayout.getTabAt(position)?.view as? ViewGroup
        tabView?.let {
            // 查找TextView（Tab内容）
            val textView = findTextView(it)

            // 计算内容宽度（包括内边距）
            val contentWidth = textView?.let { tv ->
                // 获取文本宽度
                val textWidth = tv.paint.measureText(tv.text.toString())
                // 包括内边距
                textWidth + tv.paddingLeft + tv.paddingRight
            } ?: it.measuredWidth.toFloat()

            // 设置指示器宽度 = 内容宽度 * 比例
            indicatorWidth = contentWidth * widthRatio
            invalidateSelf() // 触发重绘
        }
    }

    // 更新指示器宽度
    fun updateIndicatorWidth(position: Int,textView: TextView?) {
        currentPosition = position
        // 获取对应位置的Tab视图
        val tabView = tabLayout.getTabAt(position)?.view as? ViewGroup
        tabView?.let {
            // 计算内容宽度（包括内边距）
            val contentWidth = textView?.let { tv ->
                // 获取文本宽度
                val textWidth = tv.paint.measureText(tv.text.toString())
                // 包括内边距
                textWidth + tv.paddingLeft + tv.paddingRight
            } ?: it.measuredWidth.toFloat()

            // 设置指示器宽度 = 内容宽度 * 比例
            indicatorWidth = contentWidth * widthRatio
            invalidateSelf() // 触发重绘
        }
    }


    // 查找Tab中的TextView
    private fun findTextView(viewGroup: ViewGroup): TextView? {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child is TextView) return child
            if (child is ViewGroup) {
                findTextView(child)?.let { return it }
            }
        }
        return null
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = PixelFormat.TRANSPARENT
}