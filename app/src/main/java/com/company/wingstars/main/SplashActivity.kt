package com.company.wingstars

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.company.wingstars.main.MainActivity
import com.company.wingstars.main.WelcomeActivity


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val firstLaunch = getSharedPreferences("app_prefs", MODE_PRIVATE)
            .getBoolean("first_launch", true)

        val next = if (firstLaunch) WelcomeActivity::class.java
        else com.company.wingstars.main.MainActivity::class.java

        startActivity(Intent(this, next))
        finish()
    }
}
