package com.wingstars.base.net.beans

data class FashionResponse (
    val id: Int,
    val title: Title,
    val yoast_head_json: YoastHeadJson,
    ) {

    data class Title(
        val rendered: String,
        ):java.io.Serializable

    data class YoastHeadJson(
        val title: String,
        val og_image: List<OgImage>,
    ):java.io.Serializable{
        data class OgImage(
            val url: String,
        ):java.io.Serializable
    }
    val imageF: String      //image url
    get() {
        return if(yoast_head_json?.og_image !== null && yoast_head_json?.og_image!!.isNotEmpty()) {
            return yoast_head_json?.og_image?.get(0)?.url!!
        } else {
            ""
        }
    }

}