package com.wingstars.base.net.beans

data class ProductsResponse (
    val id: Int,
    val name: String,
    val price: String,
    val yoast_head_json: YoastHeadJson,
    ) {

        data class YoastHeadJson(
            val title: String,
            val canonical: String,
            val og_url: String,
            val og_image: List<OgImage>,
        ):java.io.Serializable {
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

        val linkF: String      //product url
        get() {
            return if(yoast_head_json !== null && yoast_head_json?.canonical != null) {
                return yoast_head_json?.canonical!!
            } else {
                ""
            }
        }

        val titleF: String
        get() {
            return if(yoast_head_json !== null && yoast_head_json?.title != null) {
                return yoast_head_json?.title!!
            } else {
                ""
            }
        }

    }