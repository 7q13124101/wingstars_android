package com.wingstars.base.utils

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView



class RecyclerViewScrollHelper {
    companion object{
        fun setupScrollListener(recyclerView: RecyclerView,listener: onScrollListener) {
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    //Log.e("dy","dy=$dy")
                    // 判断是否到达顶部
                    if (!recyclerView.canScrollVertically(-1)) {
                        listener.onScrollTop()
                    }else{
                        listener.onScrollDown()
                    }
                  /*  if (!recyclerView.canScrollVertically(-1)) {
                        listener.onScrollTop()
                    }
                    // 判断滑动方向
                    if (dy > 0) {
                        // 向下滑动或停止
                        listener.onScrollDown()
                    }*/
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val layoutManager = recyclerView.getLayoutManager() as LinearLayoutManager?
                        // 判断是否到达顶部
                        val isTop = layoutManager!!.findFirstVisibleItemPosition() == 0
                        // 或者使用: boolean isTop = !recyclerView.canScrollVertically(-1);

                    }
                }
            })
        }
    }

    interface onScrollListener{
        fun onScrollTop()
        fun onScrollDown()
    }
}