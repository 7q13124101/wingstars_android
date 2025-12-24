package com.wingstars.base.net.beans

data class CRMResetPasswordResponse (
    val success: Boolean,
    val message: String,
    val data: Any?,
    val time: String
)