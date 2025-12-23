package com.wingstars.home.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wingstars.base.net.API
import com.wingstars.base.net.NetBase
import com.wingstars.base.net.beans.YoutubeUiData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class YoutubeViewModel : ViewModel() {

    // LiveData để Adapter lắng nghe
    val youtubeVideoList = MutableLiveData<List<YoutubeUiData>>()
    val isLoading = MutableLiveData<Boolean>()

    fun getYoutubeData() {
        isLoading.postValue(true)

        API.shared?.api?.let { api ->
            api.getYoutubeVideos(
                "snippet",
                NetBase.YOUTUBE_CHANNEL_ID,
                10,
                "date",
                "video",
                NetBase.YOUTUBE_API_KEY
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        isLoading.postValue(false)
                        val rawItems = response.items

                        if (!rawItems.isNullOrEmpty()) {
                            val uiList = mutableListOf<YoutubeUiData>()

                            // Xử lý dữ liệu thô sang dữ liệu đẹp
                            rawItems.forEach { item ->
                                val snippet = item.snippet
                                val idObj = item.id

                                if (snippet != null && idObj != null) {
                                    val title = snippet.title ?: ""
                                    val image = snippet.thumbnails?.medium?.url ?: ""
                                    val videoId = idObj.videoId ?: ""

                                    // Xử lý ngày: Lấy 10 ký tự đầu (2025-12-22) và thay dấu - bằng dấu .
                                    val rawDate = snippet.publishTime ?: ""
                                    val formattedDate = if (rawDate.length >= 10) {
                                        rawDate.substring(0, 10).replace("-", ".")
                                    } else {
                                        rawDate
                                    }

                                    // Tạo link youtube
                                    val videoLink = "https://www.youtube.com/watch?v=$videoId"

                                    uiList.add(YoutubeUiData(title, image, formattedDate, videoLink))
                                }
                            }
                            Log.d("YoutubeViewModel", "youtubeVideoList: $uiList")

                            // Bắn dữ liệu sang UI
                            youtubeVideoList.postValue(uiList)
                        }
                    },
                    { error ->
                        isLoading.postValue(false)
                        error.printStackTrace() // Log lỗi nếu có
                    }
                )
        }
    }
}