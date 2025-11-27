package com.wingstars.member.view

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingstars.member.R
import com.wingstars.member.adapter.RankListAdapter
import com.wingstars.member.databinding.PopupPopularityViewBinding


class PopularityPopupView(
    var activity: Activity,
    var onPopupConfirm: OnPopupConfirm,
    var navigationBarHeight: Int
) {
    private lateinit var popupWindow: PopupWindow
    private lateinit var binding: PopupPopularityViewBinding

    init {
        initView(activity)
    }

    private fun initView(mContext: Context) {
        Log.e("navigationBarHeight", "$navigationBarHeight")
        binding = PopupPopularityViewBinding.inflate(LayoutInflater.from(mContext))
        popupWindow = PopupWindow(
            binding.root,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        setNavigationBar(binding.item, navigationBarHeight, mContext, color = R.color.white)
        popupWindow.isClippingEnabled = false
// 设置动画样式
        popupWindow.animationStyle = R.style.PopupWindowAnimation;
        popupWindow.isOutsideTouchable = false
        popupWindow.isFocusable = true
        var stringArray = activity.resources.getStringArray(R.array.rank_list)
        var adapter = RankListAdapter(activity, stringArray.toMutableList())
        binding.rankList.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rankList.adapter = adapter
        binding.exit.setOnClickListener {
            popupWindow.dismiss()
        }
        /*  binding.circle.setCircle(dpToPx(24f).toFloat())
          binding.cancel.setOnClickListener {
              popupWindow.dismiss()
          }
          if (sure.isNotEmpty()){
              binding.sure.text = sure
          }
          if (cancel.isNotEmpty()){
              binding.cancel.text = cancel
          }
          binding.confirm.setOnClickListener {
              popupWindow.dismiss()
              onPopupConfirm.onPopupConfirm()
          }*/
        popupWindow.setOnDismissListener {
            setActivityBackgroundDim(activity, 1f)
        }
        //    popupWindow.setAnimationStyle(R.style.PopupWindowAnimation)

    }


    public fun setNavigationBar(
        viewGroup: ViewGroup, navigationBarHeight: Int, context: Context,
        color: Int = R.color.white
    ) {
        if (navigationBarHeight != 0) {
            var view = View(context)
            view.setBackgroundColor(context.getColor(color))
            var params1 = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                navigationBarHeight
            )
            view.layoutParams = params1
            viewGroup.addView(view)

        }
    }

    fun setActivityBackgroundDim(activity: Activity, alpha: Float) {
        val window = activity.window
        val params = window.attributes
        params.alpha = alpha // 0.0到1.0之间，0.0为完全透明，1.0为完全不透明
        window.attributes = params
    }


    fun ptToPx(pt: Float): Float {
        val metrics: DisplayMetrics = activity.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, pt, metrics)
    }

    fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            activity.resources.displayMetrics
        ).toInt()
    }

    fun show(view: View?) {
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0)
        setActivityBackgroundDim(activity, 0.5f)
    }

    interface OnPopupConfirm {
        fun onPopupConfirm()
    }


}