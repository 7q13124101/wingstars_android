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
import com.wingstars.base.utils.DPUtils
import com.wingstars.base.utils.ScreenUtils
import com.wingstars.member.R
import com.wingstars.member.adapter.RankListAdapter
import com.wingstars.member.adapter.TakePhotoMemberListAdapter
import com.wingstars.member.bean.TakePhotosMembersListBean
import com.wingstars.member.databinding.PopupPopularityViewBinding
import com.wingstars.member.databinding.PopupTakePhotoMemberViewBinding
import kotlinx.coroutines.Runnable


class TakePhotosMemberPopupView(
    var activity: Activity,
    var navigationBarHeight: Int,
    var takePhotoList: MutableList<TakePhotosMembersListBean>,
    var listener: OnSelectImageUrl,
    var pos:Int
) : TakePhotoMemberListAdapter.OnItemListener {
    private lateinit var popupWindow: PopupWindow
    private lateinit var binding: PopupTakePhotoMemberViewBinding

    init {
        initView(activity)
    }

    private fun initView(mContext: Context) {
        binding = PopupTakePhotoMemberViewBinding.inflate(LayoutInflater.from(mContext))
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
        setHeight(binding.shadow)

        var adapter = TakePhotoMemberListAdapter(activity, takePhotoList,this)
        binding.rankList.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rankList.adapter = adapter
        adapter.setPos(pos)
        binding.exit.setOnClickListener {
            popupWindow.dismiss()
        }

        popupWindow.setOnDismissListener {
            setActivityBackgroundDim(activity, 1f)
        }


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

    fun show(view: View?) {
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0)
        setActivityBackgroundDim(activity, 0.5f)
    }

    fun setHeight(view: View) {
        var params = view.layoutParams as LinearLayout.LayoutParams
        params.width = LinearLayout.LayoutParams.MATCH_PARENT
        params.height = ScreenUtils.getHeight(activity) - DPUtils.dpToPx(50f,activity).toInt()
        view.layoutParams = params
    }

    override fun onItemClick(pos: Int) {
        Log.e("onItemClick","pos=$pos")
        Thread(object: Runnable{
            override fun run() {
                Thread.sleep(200)
                activity.runOnUiThread {
                    listener.onSelectImageUrl(pos)
                    popupWindow.dismiss()
                }
            }

        }).start()
    }

    interface OnSelectImageUrl {
        fun onSelectImageUrl(pos:Int)
    }


}