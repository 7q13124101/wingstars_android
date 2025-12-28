package com.wingstars.count.Repository

enum class ActivityStatusEnum  {
    DETAILS_EVENT_REDEMPTION,  //从活动兑换列表进入
    USED_REDEMPTION,   //历程已使用的兑换
    UNUSED_REDEMPTION,    //历程未使用的兑换
    GIFT_REDEEMED          //赠品兑换列表进入
}