package com.wingstars.user.net.beans

import android.util.Log
import com.wingstars.user.BaseApplication

class CRMVerifyRequest (
    var apiKey: String = "",
    val scope: List<String> = listOf(""),
    val tokenType: String = "Bearer",
) {
    init{
        apiKey = CRMHashKey.decrypt(BaseApplication.CRM_APP_KEY_ENC)
        Log.d("test",apiKey)
    }
}