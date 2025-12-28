package com.wingstars.base.net.beans

import com.google.gson.Gson
import com.wingstars.base.net.NetBase
import kotlin.math.min


data class YoutubeListResponse(
    val kind: String,
    val items: List<Item>?,
) {
    data class Item(
        val id: Any,
        val snippet: Snippet?,
    ):java.io.Serializable {
        data class Id(
            val kind: String,
            val videoId: String,
        ):java.io.Serializable

        data class Snippet(
            val publishedAt: String,
            val title: String,
            val description: String,
            val thumbnails: Thumbnails,
            val resourceId: Id?,
        ):java.io.Serializable {
            data class Thumbnails(
                val default: ThumbDefault,
                val medium: ThumbMedium,
            ):java.io.Serializable {
                data class ThumbDefault(
                    val url: String,
                    val width: Int,
                    val height: Int,
                ):java.io.Serializable

                data class ThumbMedium(
                    val url: String,
                    val width: Int,
                    val height: Int,
                ):java.io.Serializable
            }
        }

        val videoIdF: String
            get() {
                var idr = ""

                val ids = id as? String
                if (ids != null) {
                    idr = snippet?.resourceId?.videoId?: (ids?:"")
                } else {
                    val fromJson = Gson().fromJson(
                        Gson().toJson(id),
                        Id::class.java
                    )
                    idr = fromJson?.videoId?:""
                }
                return idr
            }

        val titleF: String
            get() = snippet?.title?:""

        val dateF: String
            get() {
                val date = snippet?.publishedAt?:""
                return date.substring(0, min(10, date.length)).replace("-", ".")
            }

        val imageF: String //thumbnail url
            get() {
                return snippet?.thumbnails?.medium?.url?:""
            }

        val linkF: String //video url
            get() {
                return if(videoIdF.isNotEmpty()) {
                    "${NetBase.HOST_YOUTUBE}/watch?v=${videoIdF}"
                } else {
                    ""
                }
            }
    }

}

