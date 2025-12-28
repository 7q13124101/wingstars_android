package com.wingstars.count.Repository

enum class EventState {
    VERIFICATION_SUCCESSFUL, //核销券成功
    EXCHANGE_COMMODITY_VOUCHERS,//兑换商品券
    EXCHANGE_ACTIVITY_VOUCHERS, //兑换活动券
    ACTIVITY_HISTORY,//活动券领取成功后，跳转到兑换历程里面
    COMMODITY_HISTORY,//商品券领取成功后，跳转到兑换历程里面
    GLOBAL_REFRESH, //全局刷新点数
    LOG_OUT,        //退出登录
    LOG_IN,         //用户登录
    REFRESH_MEDAL,  //打卡成功刷新勋章列表
    NOTIFY_SWITCH_VIEW,//推播通知 跳转連結頁面
}