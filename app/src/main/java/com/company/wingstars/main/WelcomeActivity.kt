package com.company.wingstars.main

import android.R
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.company.wingstars.databinding.ActivityWelcomeBinding
import com.gyf.immersionbar.ktx.immersionBar
import com.wingstars.base.base.BaseActivity
import com.wingstars.login.LoginActivity

class WelcomeActivity : BaseActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setTitleFoot(view1 = binding.root)
        window.statusBarColor = ContextCompat.getColor(this, com.wingstars.login.R.color.white)
        window.navigationBarColor = ContextCompat.getColor(this, com.wingstars.login.R.color.white)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        binding.btnStart.setOnClickListener {
            startActivity(Intent(this, com.wingstars.login.LoginActivity::class.java))
            // Nếu không muốn quay lại màn trước:
            // finish()
        }
    }
    fun onInitializationSuccessful() {
        initView()
    }
    override fun initView() {
        TODO("Not yet implemented")
    }
}
