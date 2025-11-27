package com.wingstars.user.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.wingstars.user.databinding.DialogDeleteAccountBinding

class DeleteAccountDialog (
    context: Context,
    private val onConfirm:()-> Unit
){
    private val dialog = Dialog(context)
    private val binding: DialogDeleteAccountBinding

    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val inflater = LayoutInflater.from(context)
        binding = DialogDeleteAccountBinding.inflate(inflater)
        dialog.setContentView(binding.root)

        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)

            // FULL WIDTH
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )

            // ĐẶT Ở DƯỚI
            setGravity(Gravity.BOTTOM)

            // ANIMATION TRƯỢT LÊN
            attributes.windowAnimations = android.R.style.Animation_Dialog
        }

        dialog.setCancelable(true)

//        binding.btnCancel.setOnClickListener {
//            dialog.dismiss()
//        }
//        binding.btnConfirm.setOnClickListener {
//            dialog.dismiss()
//            onConfirm.invoke()
//
        }
    fun show(){
        dialog.show()
    }

}