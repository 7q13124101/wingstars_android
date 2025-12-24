package com.wingstars.base.net.beans

data class CRMResetPasswordRequest (
    val oldPassword: String,
    val otp: String,
    val password: String
)