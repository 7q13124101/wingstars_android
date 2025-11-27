package com.wingstars.user.memberlevel

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.wingstars.user.R
import com.wingstars.user.cheer.ChooseMemberActivity
import com.wingstars.user.databinding.ActivityMemberInformationBinding
import com.wingstars.user.databinding.ActivityMembershipLevelsBinding

class MemberLevelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMembershipLevelsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true  // icon đen
        window.statusBarColor = getColor(R.color.color_DE9DBA)



        binding = ActivityMembershipLevelsBinding.inflate(layoutInflater)
        setContentView(binding.root) // dùng root của binding

        initView()
    }
    private fun initView(){
        binding.ivBack.setOnClickListener {
            finish()
        }
    }
}