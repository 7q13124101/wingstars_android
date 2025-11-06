package com.wingstars.member.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class NoInterceptHorizontalRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private var startX = 0f
    private var startY = 0f

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        // 确保 MapView 可以接收触摸事件
        when (ev.getAction()) {
            MotionEvent.ACTION_DOWN ->                 // 请求父容器不拦截事件，让 MapView 处理滚动
                getParent().requestDisallowInterceptTouchEvent(true)

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->                 // 恢复父容器的拦截权限
                getParent().requestDisallowInterceptTouchEvent(false)
        }
        return super.dispatchTouchEvent(ev)
    }


}