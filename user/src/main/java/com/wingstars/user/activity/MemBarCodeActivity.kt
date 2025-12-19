package com.wingstars.user.activity

import android.os.Bundle
import android.view.View
import com.wingstars.base.base.BaseActivity
import com.wingstars.user.databinding.ActivityMemBarCodeBinding

class MemBarCodeActivity : BaseActivity(){
    private lateinit var binding: ActivityMemBarCodeBinding
    private var phone: String?=null
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityMemBarCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        phone = intent.getStringExtra("phone")
        initView()
    }

    override fun initView() {
        binding.includeTop.ivClose.setOnClickListener { finish() }
        binding.ivQrLarger.setOnClickListener {
            binding.llCodeSmall.visibility = View.GONE
            binding.llQrLarger.visibility = View.VISIBLE
        }
        binding.ivQrSmall.setOnClickListener {
            binding.llQrLarger.visibility = View.GONE
            binding.llCodeSmall.visibility = View.VISIBLE
        }
        binding.ivBarLager.setOnClickListener {
            binding.llCodeSmall.visibility = View.GONE
            binding.llBarLarger.visibility = View.VISIBLE
        }
        binding.ivBarSmall.setOnClickListener {
            binding.llBarLarger.visibility = View.GONE
            binding.llCodeSmall.visibility = View.VISIBLE
        }

    }


}