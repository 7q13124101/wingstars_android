package com.wingstars.base.net.beans


data class WSOrderResponse(
    val id: Int,                        //訂單編號
    val customer_id: Int,               //會員id
    val date_created: String?,          //訂單日期
    val shipping_total: String,         //運費
    val total: String,                  //總金額
    val currency_symbol: String,        //"NT$"
    val billing: Billing,
    val meta_data: List<MetaData>,
    val line_items: List<LineItem>,
    val shipping_lines: List<ShippingLines>,
) {
    val date_createdF: String           //content format
        get() {
            val d = if (date_created.isNullOrEmpty()) {
                ""
            } else if (date_created.length > 10) {
                date_created.substring(0, 10)
            } else {
                date_created
            }

            return d    //d.replace('-', '.')
        }

    val customer_nameF: String          //billing.first_name format
        get() {
            return billing.first_name
        }

    data class Billing(
        val first_name: String,         //會員姓名
        val address_1: String,
        val email: String,
        val phone: String,
    ) : java.io.Serializable

    data class MetaData(
        val id: Int,
        val key: String,
        val value: Any,
    ) : java.io.Serializable

    data class LineItem(
        val id: Int,
        val name: String,               //商品名稱
        val quantity: Int,              //數量
        val subtotal: String,           //小計
        val price: Int,                 //產品價格
        val image: Image,               //商品圖片
    ) : java.io.Serializable {

        val urlF: String                //image.src format
            get() {
                return image.src
            }

        data class Image(
            val id: String,
            val src: String,
        ) : java.io.Serializable
    }

    data class ShippingLines(
        val id: Int,
        val total: String,
    ) : java.io.Serializable
}
