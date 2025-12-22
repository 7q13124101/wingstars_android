package com.wingstars.net.beans.request_respone

data class OtpSmsResponse(
    val success: Boolean,
    val message: String,
    val time: String,
    val data: Any?
)