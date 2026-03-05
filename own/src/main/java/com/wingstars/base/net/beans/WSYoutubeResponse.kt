package com.wingstars.base.net.beans

import com.google.gson.annotations.SerializedName

data class YoutubeSearchResponse(
    @SerializedName("items")
    val items: List<YoutubeVideoItem>?
)

data class YoutubeVideoItem(
    @SerializedName("id")
    val id: VideoId?,

    @SerializedName("snippet")
    val snippet: VideoSnippet?
)

data class VideoId(
    @SerializedName("videoId")
    val videoId: String?
)

data class VideoSnippet(
    @SerializedName("title")
    val title: String?,

    @SerializedName("publishTime")
    val publishTime: String?, // Dạng gốc: "2025-12-22T11:30:42Z"

    @SerializedName("thumbnails")
    val thumbnails: VideoThumbnails?
)

data class VideoThumbnails(
    @SerializedName("medium")
    val medium: ThumbnailInfo?,

    @SerializedName("high")
    val high: ThumbnailInfo?, // Kích thước 480x360

    @SerializedName("standard")
    val standard: ThumbnailInfo?, // Kích thước 640x480

    @SerializedName("maxres")
    val maxres: ThumbnailInfo? // Kích thước 1280x720
)

data class ThumbnailInfo(
    @SerializedName("url")
    val url: String?
)

data class YoutubeUiData(
    val title: String,
    val imageUrl: String,
    val date: String,
    val videoUrl: String
)