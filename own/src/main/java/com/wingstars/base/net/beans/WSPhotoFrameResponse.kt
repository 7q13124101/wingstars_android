package com.wingstars.base.net.beans


data class WSPhotoFrameResponse(
    val id: Int,
    val title: Title,               //姓名
    val acf: Acf,                   //圖框
) {
    val titleF: String              //title format
        get() {
            return when (title.rendered) {
                null -> ""
                else -> title.rendered
            }
        }

    val frameUrlF: String           //acf.photoFrame_image_urls.image1.full format
        get() {
            return acf.photoFrame_image_urls.image1.full
        }

    val numberF: String             //acf.number format
        get() {
            return acf.number
        }

    data class Title(
        val rendered: String,
    ) : java.io.Serializable

    data class Acf(
        val number: String,         //背號
        val photoFrame_image_urls: PhotoFrameImageUrls,        //臉書連結
        val photoFrame:PhotoFrameBean
    ) : java.io.Serializable {

        val numberF: String              //title format
            get() {
                return when (number) {
                    null -> ""
                    else -> number
                }
            }

        data class PhotoFrameImageUrls(
            val image1: Image,
        ) : java.io.Serializable {
            data class Image(
                val full: String,
                val large: String,
                val medium: String,
                val thumbnail: String,
            ) : java.io.Serializable
        }
    }
    data class PhotoFrameBean(
        val image1: Any
    )
    data class ImageBean(
        val sizes:SizeBean
    )
    data class SizeBean(
        val `1536x1536`: String
    )
}
