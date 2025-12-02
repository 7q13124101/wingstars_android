package com.wingstars.base.net.beans


data class WSFashionDetailResponse(
    val id: Int,
    val title: Title,                   //標題
    val content: Content,               //內文
    val acf: Acf,                       //详情
    val yoast_head_json: YoastHeadJson,
) {
    val titleF: String                  //title format
        get() {
            return title.rendered
        }

    val contentF: String                //content format
        get() {
            return content.rendered
        }

    data class Title(
        val rendered: String,
    ) : java.io.Serializable

    data class Content(
        val rendered: String,           //內文
    ) : java.io.Serializable

    data class Acf(
        val recommend_1: Recommend,     //
        val recommend_2: Recommend,
        val recommend_3: Recommend,
        val recommend_4: Recommend,
        val recommend_5: Recommend,
        val gallery_image_urls: GalleryImageUrls
    ) : java.io.Serializable {

        fun recommend(index: Int): Recommend?   {       //recommend_1 ~ recommend_5 format
            return when(index){
                1 -> recommend_1
                2 -> recommend_2
                3 -> recommend_3
                4 -> recommend_4
                5 -> recommend_5
                else -> null
            }
        }

        data class Recommend(
            val product_title: String,                  //商品標題
            val product_url: String,                    //商品連結
            val product_image_url_full: String,         //商品圖片
            val product_image_url_large: String,        //
            val product_image_url_medium: String,       //
            val product_image_url_thumbnail: String,    //
        ) : java.io.Serializable {

            val product_image_urlF: String              //product_image_url_full format
                get() {
                    return product_image_url_full
                }
        }

        data class GalleryImageUrls(
            val image1: ImageUrl,           //
            val image2: ImageUrl,           //
            val image3: ImageUrl,           //
            val image4: ImageUrl,           //
            val image5: ImageUrl,           //
        ) : java.io.Serializable {

            fun image(index: Int): ImageUrl?   {        //image1 ~ image5 format
                return when(index){
                    1 -> image1
                    2 -> image2
                    3 -> image3
                    4 -> image4
                    5 -> image5
                    else -> null
                }
            }

            data class ImageUrl(
                val full: String,
                val large: String,
                val medium: String,
                val thumbnail: String,
            ) : java.io.Serializable {

                val image_urlF: String                  //full format
                    get() {
                        return full
                    }
            }
        }
    }

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
