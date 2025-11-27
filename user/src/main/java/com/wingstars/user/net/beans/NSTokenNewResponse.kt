package com.wingstars.user.net.beans

class NSTokenNewResponse (
    val token: String,
    val token_expired: String,
    val refresh_token: String,
    val refresh_token_expired: String,
)