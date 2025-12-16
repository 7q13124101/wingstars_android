package com.wingstars.base.utils

import android.content.Context

class DPUtils {
    companion object {

        fun dpToPx(dp: Float, context: Context): Float {
            return dp * (context.resources.displayMetrics.density)
        }

        fun sp2px(spValue: Float,context: Context): Float {
            val scaledDensity = context.resources.displayMetrics.scaledDensity
            // 计算 px 并四舍五入（避免小数像素导致显示模糊）
            return (spValue * scaledDensity + 0.5f)
        }
    }
}