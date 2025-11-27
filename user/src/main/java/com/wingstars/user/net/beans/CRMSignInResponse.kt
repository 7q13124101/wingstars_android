package com.wingstars.user.net.beans

class CRMSignInResponse (
    val userType: String,       //"member"
    val id: String,
    val code: String,
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
)