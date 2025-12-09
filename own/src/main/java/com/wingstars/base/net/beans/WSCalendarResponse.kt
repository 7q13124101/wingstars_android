package com.wingstars.base.net.beans

import java.io.Serializable

data class WSCalendarResponse(
    val id: Int,
    val title: Title,                   //活動標題
    val content: Content,               //活動資訊
    val calendar_category: List<Int>,   //活動分類. 369:生日, 368:一般活動, 366:啦啦隊, 365:天鷹, 364:獵鷹, 363:雄鷹
    val acf: Acf,
    val yoast_head_json: YoastHeadJson  //精選圖片 数据类型有误
) {
    val titleF: String                  //title format
        get() {
            return title.rendered
        }

    val contentF: String                //content format
        get() {
            return content.rendered
        }

    val calendar_categoryF: Int        //calendar_category format
        get() {
            return if(calendar_category.count() > 0) {
                calendar_category[0]
            } else {
                0
            }
        }

    val st_dateF: String                   //acf.Activity_time.st_date format
        get() {
            return acf.Activity_time.st_date?: ""
        }

    val ed_dateF: String                   //acf.Activity_time.ed_date format
        get() {
            return acf.Activity_time.ed_date?: ""
        }

    val mapF: String                    //acf.map format
        get() {
            return acf?.map?: ""
        }

    val PrecautionsF: String            //acf.Precautions format
        get() {
            return acf?.Precautions?: ""
        }

    val urlF: String                    //yoast_head_json.og_image.url format
        get() {
            return yoast_head_json.og_image?.get(0)?.url?: ""
        }

    data class Title(
        val rendered: String,
    ) : Serializable

    data class Content(
        val rendered: String,
    ) : Serializable

    data class Acf(
        val Activity_time: ActivityTime,    //活動日期
        val map: String,                //活動地點
        val Precautions: String,        //注意事項
    ) : java.io.Serializable {
        data class ActivityTime(
            val st_date: String?,           //开始日期时间，如 "2025-12-05 14:00:00"
            val ed_date: String?,           //结束日期时间，如 "2025-12-05 16:00:00"
        ) : java.io.Serializable
    }

    data class YoastHeadJson(
        val og_image: List<OGImage>?,    //精選圖片
    ) : java.io.Serializable {
        data class OGImage(
            val width: Int,
            val height: Int,
            val url: String,            //精選圖片地址
            val type: String,
        ) : java.io.Serializable
    }
}