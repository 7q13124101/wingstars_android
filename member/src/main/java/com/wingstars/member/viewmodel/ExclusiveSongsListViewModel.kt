package com.wingstars.member.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wingstars.base.net.beans.YoutubeListResponse

class ExclusiveSongsListViewModel : ViewModel(){
    var exclusiveSongsListData = MutableLiveData<MutableList<YoutubeListResponse.Item>>()
    var isLoading = MutableLiveData<Boolean>()
    fun setIsLoading(isLoading: Boolean) {
        this.isLoading.postValue(isLoading)
    }

    fun getExclusiveSongsListData() {
        setIsLoading(true)
        val songsList = mutableListOf<YoutubeListResponse.Item>()
        var node = YoutubeListResponse.Item("1111",
            YoutubeListResponse.Item.Snippet("2025.03.20",
                "【Wing Stars】 2025首張單曲 《\uD835\uDE3E\uD835\uDE5D\uD835\uDE5A\uD835\uDE5A\uD835\uDE67 \uD835\uDE5E\uD835\uDE69 \uD835\uDE6A\uD835\uDE65 加大》 Official Music Video",

                "false", YoutubeListResponse.Item.Snippet.Thumbnails(
                    YoutubeListResponse.Item.Snippet.Thumbnails.ThumbDefault(
                        "",
                        0,
                        0
                    ), YoutubeListResponse.Item.Snippet.Thumbnails.ThumbMedium("", 0, 0)
                ),null)
            )
        songsList.add(node)

        node = YoutubeListResponse.Item("2222",YoutubeListResponse.Item.Snippet("2025.07.28","\uD835\uDDEA\uD835\uDDF6\uD835\uDDFB\uD835\uDDF4 \uD835\uDDE6\uD835\uDE01\uD835\uDDEE\uD835\uDDFF\uD835\uDE00 - \uD83C\uDFAC 明星賽幕後全紀錄 ✨",
            "true",YoutubeListResponse.Item.Snippet.Thumbnails(
                YoutubeListResponse.Item.Snippet.Thumbnails.ThumbDefault(
                    "",
                    0,
                    0
                ), YoutubeListResponse.Item.Snippet.Thumbnails.ThumbMedium("", 0, 0)
            ),null)
        )
        songsList.add(node)

        node = YoutubeListResponse.Item("333",YoutubeListResponse.Item.Snippet("2025.07.16","Ｗing Stars - 魷戲玩到底","false",YoutubeListResponse.Item.Snippet.Thumbnails(
            YoutubeListResponse.Item.Snippet.Thumbnails.ThumbDefault(
                "",
                0,
                0
            ), YoutubeListResponse.Item.Snippet.Thumbnails.ThumbMedium("", 0, 0)
        ),null))
        songsList.add(node)
        exclusiveSongsListData.postValue(songsList)
        setIsLoading(false)
    }
}