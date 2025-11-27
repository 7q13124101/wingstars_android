package com.wingstars.user.net.beans

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HksOrdersResponse (
    val id: Int,                    //订单编号
    val customer_id: String,        //客户id
    val date_created: String,       //订单日期
    val shipping_total: String,     //运费
    val total: String,              //总金额
    val currency_symbol: String,    //货币符号。"NT$"
    val meta_data: List<MetaData>,  //Points等信息
    val line_items: List<LineItem>, //子订单商品
) {
    data class MetaData(
        val id: Int,                    //id
        val key: String,                //"th_points_use", "th_points_get", ...
        val value: Any,                 //th_points_use: String; th_points_get: PointsGet
    ):java.io.Serializable {
        data class PointsGet(
            val total_points: Int,          //总点数
            val all_rules: List<AllRule>,
        ) : java.io.Serializable {
            data class AllRule(
                val points: Int,
                val rule_id: String,
                val rule_name: String,
            ) : java.io.Serializable
        }
    }

    data class LineItem(
        val id: Int,                    //子订单编号
        val name: String,               //品名
        val product_id: Int,            //商品id
        val quantity: Int,              //数量
        val subtotal: String,           //折扣前
        val total: String,              //折扣后
        val price: Int,                 //价格
        val image: Image,               //图片
        val parent_name: String,        //
    ):java.io.Serializable {
        data class Image(
            val src: String,            //url
        ):java.io.Serializable

        val imageF: String              //image url
            get() {
                return image?.src ?: ""
            }
    }

    val dateF: String
        get() {
            return if(date_created.length > 10) {
                date_created.substring(0, 10)
            } else {
                date_created
            }
        }

    val titleF: String
        get() {
            return if(line_items?.isNotEmpty() == true) {
                line_items[0].parent_name?:line_items[0].name
            } else {
                ""
            }
        }

    val subTotalF: String
        get() {
            var total = 0
            if(line_items?.isNotEmpty() == true) {
                for(li in line_items) {
                    val sub = li.total.toIntOrNull()
                    if(sub != null) {
                        total += sub
                    }
                }
            }

            return total.toString()
        }

    val pointsUsedF: String
        get() {
            if(meta_data?.isNotEmpty() == true) {
                for(md in meta_data) {
                    if(md.key == "th_points_use") {
                        return "-${md.value.toString()}"
                    }
                }
            }

            return "0"
        }

    val pointsGetF: String
        get() {
            if(meta_data?.isNotEmpty() == true) {
                for(md in meta_data) {
                    if(md.key == "th_points_get") {
                        val gson = Gson()
                        val toJson = gson.toJson(md.value)

                        val type = object : TypeToken<MetaData.PointsGet>() {}.type
                        val pg = gson.fromJson<MetaData.PointsGet>(toJson, type)
                        if (pg != null) {
                            //println("   total_points: ${pg.total_points}, all_rules.count: ${pg.all_rules.count()}, all_rules: ${pg.all_rules}")
                            return pg.total_points.toString()
                        }
                    }
                }
            }

            return "0"
        }
}