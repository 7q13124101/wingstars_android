package com.wingstars.base.net.beans

data class CRMSignInResponse(
    val userType: String,       //"member"
    val id: String,
    val code: String,
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
)
