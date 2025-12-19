package com.wingstars.base.net.beans

data class CRMExtraData(
    var delivery_address: String = "",      // 配送地址
    var delivery_city: String = "",         // 配送城市
    var delivery_district: String = "",     // 配送區域
    var favorite_players: List<String>? = null, // 喜愛球員
    var id_number: String? = null,             // 身分證字號
    var invoice_number: String = "",        // 發票號碼
    var invoice_option: String = "",        // 發票選項
    var memberid: String = "",              // 會員編號
    var register_time: String = "",         // 註冊時間
    var store_address: String = "",         // 門市地址
    var store_city: String = "",            // 門市城市
    var store_county: String = "",          // 門市縣市
    var store_id: String = "",              // 門市編號
    var store_name: String = "",            // 門市名稱
    var update_time: String = "",           // 更新時間
)

data class CRMNewsoftExtraData(
    val email: String = "",     // 電子郵件, ex: 商城會員email
)
