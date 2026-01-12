package com.wingstars.count.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRadioButton
import android.widget.LinearLayout
import com.wingstars.count.R

enum class SortMethod {
    SORT_DATE_NEW_TO_OLD,      //日期新到舊
    SORT_DATE_OLD_TO_NEW,      //日期舊到新
    SORT_POINTS_HIGH_TO_LOW,   //點數高到低
    SORT_POINTS_LOW_TO_HIGH,    //點數低到高
    SORT_BY_BEEN_COMPLETED      //按已完成排序

}

class PublicPopupSortTypeDialog : Dialog {

    constructor(context: Context) : super(context) {}
    constructor(context: Context, theme: Int) : super(context, theme) {}

    class Builder(context: Context, currentSortMethod: SortMethod,  var navigationBarHeight:Int) : BaseBuilder(){

        private var buttonClickListener: View.OnClickListener? = null
        private val layout: View
        private var popupSortMethod: SortMethod

        private val dialog: PublicPopupSortTypeDialog =
            PublicPopupSortTypeDialog(context, R.style.CalendarInfoDialogStyle)

        init {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            layout = inflater.inflate(R.layout.dialog_public_popup_sort_type, null)
            if (navigationBarHeight!=0){
                val cl_add_person = layout.findViewById<LinearLayout>(R.id.cl_add_person)
                setNavigationBar(cl_add_person,navigationBarHeight,context)
            }
            dialog.addContentView(
                layout,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )

            popupSortMethod = currentSortMethod
        }

        fun setClickListener(listener: OnClickListener): Builder {
            this.buttonClickListener = listener
            return this
        }


        fun createDialog(): PublicPopupSortTypeDialog {
            layout.findViewById<TextView>(R.id.tv_dialog_confirm).setOnClickListener(buttonClickListener)

            when(popupSortMethod){
                SortMethod.SORT_DATE_NEW_TO_OLD->{
                    layout.findViewById<AppCompatRadioButton>(R.id.rb_sort_date_new_to_old).isChecked = true
                }
                SortMethod.SORT_DATE_OLD_TO_NEW->{
                    layout.findViewById<AppCompatRadioButton>(R.id.rb_sort_date_old_to_new).isChecked = true
                }
                SortMethod.SORT_POINTS_HIGH_TO_LOW->{
                    layout.findViewById<AppCompatRadioButton>(R.id.rb_sort_points_high_to_low).isChecked = true
                }
                SortMethod.SORT_POINTS_LOW_TO_HIGH->{
                    layout.findViewById<AppCompatRadioButton>(R.id.rb_sort_points_low_to_high).isChecked = true
                }
                SortMethod.SORT_BY_BEEN_COMPLETED->{
                    layout.findViewById<AppCompatRadioButton>(R.id.rb_sort_date_new_to_old).isChecked = true
                }
            }

//            layout.findViewById<RadioGroup>(R.id.rg_sort).setOnCheckedChangeListener { radioGroup, i ->
//                if (i == R.id.rb_sort_date_new_to_old) {
//                    popupSortMethod = SortMethod.SORT_DATE_NEW_TO_OLD
//                } else if (i == R.id.rb_sort_date_old_to_new) {
//                    popupSortMethod = SortMethod.SORT_DATE_OLD_TO_NEW
//                } else if (i == R.id.rb_sort_points_high_to_low) {
//                    popupSortMethod = SortMethod.SORT_POINTS_HIGH_TO_LOW
//                } else if (i == R.id.rb_sort_points_low_to_high) {
//                    popupSortMethod = SortMethod.SORT_POINTS_LOW_TO_HIGH
//                }
//            }

            dialog.setContentView(layout)
            dialog.setCancelable(false) //用户可以点击手机Back键取消对话框显示
            dialog.setCanceledOnTouchOutside(false)//用户不能通过点击对话框之外的地方取消对话框显示

            dialog.window?.apply {
                // 关键代码：允许内容延伸到导航栏下方
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )
                // 全屏模式 + 沉浸式处理
                decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        )

                val params = attributes
                params.flags =
                    params.flags or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                attributes = params
                setGravity(Gravity.BOTTOM)
            }
            return dialog
        }
    }
}
