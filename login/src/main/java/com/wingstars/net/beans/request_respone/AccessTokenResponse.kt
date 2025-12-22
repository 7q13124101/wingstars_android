package com.wingstars.net.beans.request_respone

class AccessTokenResponse (
    val success: Boolean,
    val message: String,
    val data: TokenData?
)
data class TokenData(
    val accessToken: String,
    val refreshToken: String
)