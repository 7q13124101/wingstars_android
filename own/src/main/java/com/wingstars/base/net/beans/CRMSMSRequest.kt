package com.wingstars.base.net.beans

data class CRMSMSRequest(
    val phone: String,
    val type: String,   //取值："signUp"（注册验证）, "forgotPassword"（忘记密码）,"resetPassword"（修改密码）, "smsSendOtp"（发送验证码）；
)
