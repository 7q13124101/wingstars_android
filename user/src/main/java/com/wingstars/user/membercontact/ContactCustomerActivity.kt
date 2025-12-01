package com.wingstars.user.membercontact

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wingstars.user.UpLoadingDialog
import com.wingstars.user.databinding.ActivityContactCustomerBinding
import com.wingstars.user.databinding.ActivityMemBarCodeBinding

class ContactCustomerActivity: AppCompatActivity() {
    private lateinit var binding: ActivityContactCustomerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    private fun initView(){
        binding.ivBack.setOnClickListener {
            finish()
        }
    }
}