package com.wingstars.base.net.beans

data class CRMSendOtpResponse(
    val success: Boolean,
    val message: String,
    val data: Any?,
    val time: String
)