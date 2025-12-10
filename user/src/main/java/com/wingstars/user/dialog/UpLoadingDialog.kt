package com.wingstars.user.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.wingstars.user.R
import com.wingstars.user.net.BaseApplication

class UpLoadingDialog: Dialog {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, theme: Int) : super(context, theme) {}

    class Builder(context: Context) {

        private val layout: View
        private val dialog: UpLoadingDialog = UpLoadingDialog(context, R.style.UpLoadingDialog)
        init {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            layout = inflater.inflate(R.layout.dialog_upload, null)
            dialog.addContentView(layout, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        }


        fun createDialog() : UpLoadingDialog {
            dialog.setContentView(layout)
            dialog.setCancelable(false)     //用户可以点击手机Back键取消对话框显示
            dialog.setCanceledOnTouchOutside(false)        //用户不能通过点击对话框之外的地方取消对话框显示

            val window: Window? = dialog.window
            if (window != null) {
                val attributes = window.attributes
                attributes.width = BaseApplication.Companion.shared()!!.dp2px(64F).toInt()
                attributes.height = BaseApplication.Companion.shared()!!.dp2px(64F).toInt()
                window.attributes = attributes
            }
            return dialog
        }

    }
}