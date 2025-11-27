package com.wingstars.user.cheer

import android.content.Intent
import android.os.Bundle
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.addTextChangedListener
import com.wingstars.base.base.BaseActivity
import com.wingstars.user.KeyboardUtils
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
        window.statusBarColor = getColor(R.color.color_DE9DBA)
        initView()
    }
    override fun initView() {
        binding.ivBack.setOnClickListener { finish() }
        KeyboardUtils.showKeyboard(binding.edtMobile)

        binding.edtMobile.addTextChangedListener{
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