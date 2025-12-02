package com.wingstars.base.net.beans


data class WSCalendarResponse(
    val id: Int,
    val title: Title,                   //活動標題
    val content: Content,               //活動資訊
    val calendar_category: List<Int>,   //活動分類. 366:啦啦隊, 365:天鷹, 364:獵鷹, 363:雄鷹
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

    val dateF: String                   //acf.date format
        get() {
            return acf?.date?: ""
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
            return if(yoast_head_json.og_image.count() > 0) {
                yoast_head_json.og_image[0].url
            } else {
                ""
            }
        }

    data class Title(
        val rendered: String,           //活動標題
    ) : java.io.Serializable

    data class Content(
        val rendered: String,           //活動資訊
    ) : java.io.Serializable

    data class Acf(
        val date: String,               //活動日期
        val map: String,                //活動地點
        val Precautions: String,        //注意事項
    ) : java.io.Serializable

    data class YoastHeadJson(
        val og_image: List<OGImage>,    //精選圖片
    ) : java.io.Serializable {
        data class OGImage(
            val width: Int,
            val height: Int,
            val url: String,            //精選圖片地址
            val type: String,
        ) : java.io.Serializable
    }
}
