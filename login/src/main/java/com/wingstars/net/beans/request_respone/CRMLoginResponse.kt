package com.wingstars.net.beans.request_respone

data class CRMLoginResponse(
    val success: Boolean,
    val message: String,
    val data: Data?,
    val time: String?
) {
    data class Data(
        val accessToken: String?,
        val code: String?,
        val id: String?,
        val refreshToken: String?,
        val scope: List<String>?,
        val tokenType: String?,
        val userType: String?
    )
}
