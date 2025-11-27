package com.wingstars.user.net.beans

data class CRMVerifyResponse (
    val userType: String,       //"client"
    val id: String,
    val code: String,
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
){

}