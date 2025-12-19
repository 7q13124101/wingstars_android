package com.wingstars.user.net.beans

data class CRMMemberRespone (
    val id: Int,
    val title: Title,
    val acf: AcfFields,
    val yoast_head_json: YoastHeadJson
)
data class Title(
    val rendered: String,
)
data class AcfFields(
    val number: String,
    val photoFrame: PhotoFrame
)
data class PhotoFrame(
    val image: ImageDetails
)
data class ImageDetails(
    val url: String
)
data class YoastHeadJson(
    val og_image: List<OgImage>
)
data class OgImage(
    val url: String
)