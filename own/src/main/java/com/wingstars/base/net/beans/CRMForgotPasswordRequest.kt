package com.wingstars.base.net.beans

data class CRMForgotPasswordRequest (
    val otp: String,
    val password: String,
    val phone: String,
)
