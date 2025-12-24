package com.wingstars.base.net.beans

data class CRMSignUpRequest(
    val name: String,               //姓名
    val phone: String,              //手機號碼
    val otp: String,                //簡訊驗證碼
    val password: String,           //密碼
    val email: String,              //電子郵件
    val birthday: String,           //生日
    val gender: String,             //性別  男 M  女 F 保密 S
    val identity: String,           //身份证
    val address: String = "",       //地址
    val carrierCode: String = "",   //運營商代碼, ex: 1
    val city: String = "",          //城市
    val district: String = "",      //區域
    val extraData: CRMExtraData? = null,                //額外資料
    val newsoftExtraData: CRMNewsoftExtraData? = null,  //額外資料
)
