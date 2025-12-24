package com.wingstars.base.net.beans

data class CRMMemberContactResponse(
    val Address: String,            //地址
    val Age: Int,                   //年龄
    val Birthday: String,           //生日
    val CarrierCode: String,        //運營商代碼,載具代碼?
    val City: String,               //城市
    val Code: String,               //會員代碼
    val District: String,           //區域
    val Email: String,              //電子郵件
    val Gender: String,             //性別
    val Id: String,                 //會員ID
    val Identity: String,           //身份
    val IsSubscribedToNews: Boolean,            //是否訂閱
    val IsSubscribedToPartnersNews: Boolean,    //是否訂閱合作夥伴
    val Name: String,               //姓名
    val Phone: String,              //手機號碼
    val Stores: List<String>,       //商店
    val ExtraData: CRMExtraData,    //額外資料
    val NewsoftExtraData: CRMNewsoftExtraData,  //額外資料
    val NextTokenExpiredDate: String    // 會員到期
)
