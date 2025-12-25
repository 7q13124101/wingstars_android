package com.wingstars.base.net.beans

data class NSTokenNewResponse(
    val token: String,
    val token_expired: String,
    val refresh_token: String,
    val refresh_token_expired: String,
)
