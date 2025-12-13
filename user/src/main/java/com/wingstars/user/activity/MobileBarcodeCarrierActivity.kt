package com.wingstars.user.activity

import android.content.Intent
import android.os.Bundle
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.addTextChangedListener
import com.wingstars.base.base.BaseActivity
import com.wingstars.user.utils.KeyboardUtils
import com.wingstars.user.R
import com.wingstars.user.databinding.ActivityMobileBarcodeCarrierBinding

class MobileBarcodeCarrierActivity: BaseActivity() {
    private lateinit var binding: ActivityMobileBarcodeCarrierBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        val view = ActivityMobileBarcodeCarrierBinding.inflate(layoutInflater)
        binding = view
        setContentView(view.root)

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true
        initView()
    }
    override fun initView() {
        binding.ivBack.setOnClickListener { finish() }
        binding.rlMobile.setOnClickListener {
            binding.edtMobile.requestFocus()
            binding.edtMobile.hint = ""
            KeyboardUtils.showKeyboard(binding.edtMobile)
        }
        binding.edtMobile.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.edtMobile.hint = ""
            } else {
                if (binding.edtMobile.text.isNullOrEmpty()) {
                    binding.edtMobile.hint = getString(R.string.user_enter_your_mobile)
                }
            }
        }
        binding.edtMobile.addTextChangedListener {
            val input = it.toString()
            if(input.length >= 8){
                binding.btnSave.isEnabled = true
                binding.btnSave.setBackgroundColor(getColor(R.color.color_DE9DBA))
                binding.btnSave.setTextColor(getColor(R.color.white))

            }else{
                binding.btnSave.isEnabled = false
                binding.btnSave.setBackgroundColor(getColor(R.color.color_F3F4F6))
                binding.btnSave.setTextColor(getColor(R.color.black))
            }
        }
        binding.btnSave.setOnClickListener {
            val mobile = binding.edtMobile.text.toString().trim()
            val intent = Intent()
            intent.putExtra("mobile_number", mobile)
            setResult(RESULT_OK, intent)
            finish()
        }

    }

}