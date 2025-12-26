package com.wingstars.base.net.beans

data class CRMOTPCoupons (
    val otp : OtpData
)
data class OtpData(
    val code: String,
    val ttlSec: Int
)