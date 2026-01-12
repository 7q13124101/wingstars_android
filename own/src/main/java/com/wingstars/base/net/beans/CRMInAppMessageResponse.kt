package com.wingstars.base.net.beans


data class CRMInAppMessageResponse(
    val id: String,
    val type: String,           // announcement | notification. 後端訊息類型 (全體公告 or 通知)，*目前以 categories 做訊息分類，暫不須理會
    val title: String,          // 訊息標題
    val content: String,        // 訊息內容
    val coverImage: String,     // 訊息封面圖片。系統訊息時回傳空字串""
    val images: List<String>,   // 內文圖片。目前系統訊息時回傳空陣列[]
    val targetUrl: String,      // 如該訊息有加上推播，會帶有 APP 推播深度連結。
    val payload: List<Any>,     // 未來擴充功能使用
    val categories: String,     // activity | system. 訊息類別 (活動通知 or 系統通知)
    var status: Int,            // 使用者是否已讀取. 0=未讀, 1=已讀
    val readAt: String,         // 使用者已讀時間，如果還沒已讀，不會回傳這個參數，固定回傳無毫秒+08:00
    val CreatedAt: String,      // 系統預設資料建立時間(有毫秒), 等於系統/排程訊息送出的時間，
    val UpdatedAt: String,      // 系統預設資料更新時間(有毫秒)
) {
    val UpdatedAtF: String      //UpdatedAt format
        get() {
            val ut = if(UpdatedAt == null) {
                ""
            } else if(UpdatedAt.length >= 10) {
                UpdatedAt.substring(0, 10)
            } else {
                UpdatedAt
            }

            return ut.replace('-', '/')
        }

    val CreatedAtF: String      //UpdatedAt format
        get() {
            val ut = if(CreatedAt == null) {
                ""
            } else if(CreatedAt.length >= 10) {
                CreatedAt.substring(0, 10)
            } else {
                CreatedAt
            }

            return ut.replace('-', '/')
        }
}
