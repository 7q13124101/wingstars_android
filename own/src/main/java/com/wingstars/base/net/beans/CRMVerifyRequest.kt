package com.wingstars.base.net.beans


data class CRMVerifyRequest(
    var apiKey: String = "",
    val scope: List<String> = listOf(""),
    val tokenType: String = "Bearer",
) {
    init{
        apiKey = "8e2KeU3Bntw43R09tNE1"
//        Log.d("test",apiKey)
    }
}
