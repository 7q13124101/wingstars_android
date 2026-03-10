package com.company.wingstars

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.window.SplashScreenView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.company.wingstars.main.MainActivity
import com.company.wingstars.main.PermissionRequestActivity
import com.company.wingstars.main.WelcomeActivity
import com.tencent.mmkv.MMKV
import com.wingstars.login.LoginActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener { splashScreenView ->
                splashScreenView.iconView?.background = null
                splashScreenView.remove()
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            checkNextScreen()
        }, 1000)
    }

    private fun checkNextScreen() {
        val mmkv = MMKV.defaultMMKV()
        val isFirstRun = mmkv.decodeBool("isFirstRun", true) // Mặc định true
        val isLogin = mmkv.decodeBool("isLogin", false)     // Mặc định false

        val nextIntent: Intent

        if (isFirstRun) {
            nextIntent = Intent(this, PermissionRequestActivity::class.java)
        } else {
            if (isLogin) {
                nextIntent = Intent(this, MainActivity::class.java)
            } else {
//                nextIntent = Intent(this, LoginActivity::class.java)
                nextIntent = Intent(this, MainActivity::class.java)

                nextIntent.putExtra("isFromSplash", true)
            }
        }


        intent.extras?.getString("activityPageUrl")?.let {
            nextIntent.putExtra("fcmTag", it)
        }

        intent.getStringExtra("fcmTag")?.let {
            nextIntent.putExtra("fcmTag", it)
        }

        startActivity(nextIntent)
        finish()
    }
}