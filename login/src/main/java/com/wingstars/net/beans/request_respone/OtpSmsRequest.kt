package com.wingstars.net.beans.request_respone


data class OtpSmsRequest(
    val phone: String,
    val type: String // resetPassword / register / login
)
