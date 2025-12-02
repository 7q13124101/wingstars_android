package com.wingstars.base.net.beans


data class WSMemberResponse(
    val id: Int,
    val title: Title,                   //姓名
    val acf: Acf,                       //资讯
    val yoast_head_json: YoastHeadJson, //圖片
) {
    val titleF: String                  //title format
        get() {
            return title.rendered
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
        val rendered: String,
    ) : java.io.Serializable

    data class Acf(
        val number: String,             //背號
        val fb_link: String,            //臉書連結
        val ig_link: String,            //Ig 連結
        val about: String,              //關於我
        val say: String,                //想說的話
        val interest: String,           //興趣喜好
        val height: String,             //身高
        val weight: String,             //體重
        val birthdate: String,          //生日
        val sign: String,               //星座
        val blood_type: String,         //血型
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
