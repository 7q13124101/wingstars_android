package com.wingstars.user.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.wingstars.user.databinding.ActivityMembershipLevelsBinding

class MemberLevelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMembershipLevelsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true
        binding = ActivityMembershipLevelsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }
    private fun initView(){
        binding.ivBack.setOnClickListener {
            finish()
        }
    }
}