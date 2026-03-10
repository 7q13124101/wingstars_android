package com.wingstars.login

import android.content.Context
import android.content.Intent
import com.tencent.mmkv.MMKV

object checkLogin {
    fun isLoggedIn(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("isLogin", false)
    }
}


//val mmkv = MMKV.defaultMMKV()
//val isFirstRun = mmkv.decodeBool("isFirstRun", true) // Mặc định true
//val isLogin = mmkv.decodeBool("isLogin", false)     // Mặc định false
//
//val nextIntent: Intent
//
//if (isFirstRun) {
//    nextIntent = Intent(this, PermissionRequestActivity::class.java)
//} else {
//    if (isLogin) {
//        nextIntent = Intent(this, MainActivity::class.java)
//    } else {
//        nextIntent = Intent(this, LoginActivity::class.java)
////                nextIntent = Intent(this, MainActivity::class.java)
//
//        nextIntent.putExtra("isFromSplash", true)
//    }
//}