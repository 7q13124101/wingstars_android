package com.wingstars.user.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.wingstars.user.databinding.DialogLogoutAccountBinding

class LogoutDialog(
    context: Context,
    private val onConfirm: () -> Unit
) {
    private val dialog = Dialog(context)
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
            attributes.windowAnimations = android.R.style.Animation_Dialog
        }

        dialog.setCancelable(true)

        // Xử lý nút Cancel
        binding.edtMobile.setOnClickListener {
            dialog.dismiss()
        }

        // Xử lý nút Confirm
        binding.edtMobile1.setOnClickListener {
            dialog.dismiss()
            onConfirm.invoke()  // Gọi performLogout()
        }
    }

    fun show() {
        dialog.show()
    }
}
