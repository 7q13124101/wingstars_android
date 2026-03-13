package com.wingstars.user.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.wingstars.user.R
import com.wingstars.user.databinding.DialogLogoutAccountBinding

class LogoutDialog(
    context: Context,
    private val onConfirm: () -> Unit
) {
    // Sử dụng Style BottomDialogStyle đã được cấu hình Animation trượt
    private val dialog = Dialog(context, R.style.BottomDialogStyle)
    private val binding: DialogLogoutAccountBinding
    
    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val inflater = LayoutInflater.from(context)
        binding = DialogLogoutAccountBinding.inflate(inflater)
        dialog.setContentView(binding.root)

        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setGravity(Gravity.BOTTOM)
            // Xóa bỏ dòng gán Animation_Dialog cũ để sử dụng Animation từ Style
            setDimAmount(0.1f)
        }
        dialog.setCancelable(true)
        binding.edtMobile.setOnClickListener {
            dialog.dismiss()
        }
        binding.edtMobile1.setOnClickListener {
            dialog.dismiss()
            onConfirm.invoke()
        }
    }

    fun show() {
        dialog.show()
    }
}
