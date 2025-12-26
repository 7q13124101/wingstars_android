package com.wingstars.base.net.beans

data class CRMRedeemCouponResponse(
    val id: String,             //
    val createdAt: String,      //
    val updatedAt: String,      //
    val couponId: String,       //coupon id
    val memberId: String,       //
    val couponCode: String,     //
    val couponStatus: Int,      //
    val journalId: String,      //
    val redeemedAt: String?,    //
    val redeemedStore: String,  //兑换门市代码
)

/*
    {
        "id": "b0f33c34-f6d1-4f5f-b972-effc1a78d5e1",
        "createdAt": "2025-05-06T15:59:20.544750804+08:00",
        "updatedAt": "2025-05-06T15:59:20.544750804+08:00",
        "couponId": "8c976eda-42a3-437f-aeab-303c160dcef9",
        "memberId": "e55caa39-7a5b-4378-8d07-079fa79482ef",
        "couponCode": "5A01BXY1JON",
        "couponStatus": 0,
        "journalId": "09e205d6-ce81-48ba-8364-d1dd654d6ca1",
        "redeemedAt": null,
        "redeemedStore": ""
    }
*/