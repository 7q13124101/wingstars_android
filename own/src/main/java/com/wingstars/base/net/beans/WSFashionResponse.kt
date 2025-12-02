package com.wingstars.base.net.beans


data class WSFashionResponse(
    val id: Int,
    val title: Title,                   //圖示
    val fashion_category: List<Int>,    //分類
    val yoast_head_json: YoastHeadJson, //圖片
) {
    val titleF: String                  //title format
        get() {
            return title.rendered
        }

    val fashion_categoryF: Int          //fashion_category[0] format
        get() {
            return if(fashion_category.count() > 0){
                fashion_category[0]
            } else {
                0
            }
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
        val rendered: String,           //圖示
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
