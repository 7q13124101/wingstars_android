package com.wingstars.user.cheer

import android.os.Bundle
import androidx.core.view.WindowInsetsControllerCompat
import com.wingstars.base.base.BaseActivity
import com.wingstars.user.databinding.ActivityChangeMemberPasswordBinding

class ChangeMemberPasswordActivity: BaseActivity() {
    private lateinit var binding: ActivityChangeMemberPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeMemberPasswordBinding.inflate(layoutInflater)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true  // icon đen
        setContentView(binding.root)
//        window.statusBarColor = getColor(R.color.color_DE9DBA)
//        window.navigationBarColor = getColor(R.color.color_DE9DBA)
//        setTitleFoot(
//            view1 = binding.root,
//            statusBarColor = R.color.color_F3F4F6,
//            navigationBarColor = R.color.color_F3F4F6
//        )

        initView()

    }
    override fun initView(){
        binding.ivBack.setOnClickListener { finish() }
    }
}