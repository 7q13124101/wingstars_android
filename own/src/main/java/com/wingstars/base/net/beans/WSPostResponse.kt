package com.wingstars.base.net.beans


data class WSPostResponse(
    val id: Int,
    val date: String,                   //日期
    val title: Title,                   //標題
    val content: Content,               //內文
    val link: String,
    val yoast_head_json: YoastHeadJson, //圖片
) {
    val titleF: String                  //title format
        get() {
            return title.rendered
        }

    val dateF: String                   //date format
        get() {
            val d = if (date == null) {
                ""
            } else if (date.length > 10) {
                date.substring(0, 10)
            } else {
                date
            }

            return d.replace('-', '.')
        }

    val contentF: String                //content format
        get() {
            return content.rendered
        }

    val urlF: String                    //yoast_head_json.og_image[0].url format
        get() {
            return if(yoast_head_json.og_image.count() > 0){
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

    data class YoastHeadJson(
        val og_image: List<OGImage>,    //圖片
    ) : java.io.Serializable {
        data class OGImage(
            val width: Int,
            val height: Int,
            val url: String,            //圖片地址
            val type: String,
        ) : java.io.Serializable
    }
}
