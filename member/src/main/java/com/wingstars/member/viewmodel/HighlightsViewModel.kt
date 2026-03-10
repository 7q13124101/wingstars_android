package com.wingstars.member.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wingstars.base.net.API
import com.wingstars.base.net.NetBase
import com.wingstars.base.net.beans.YoutubeListResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

enum class HighlightsType {
    HT_WONDERFUL_VIDEOS,      // 精彩影片
    HT_FLASH_SHORT_FILM,      // 快閃短片
    HT_DAILY_VLOG             // 日常Vlog
}

class HighlightsViewModel : ViewModel() {
    var highlightsList = MutableLiveData<MutableList<YoutubeListResponse.Item>>()
    var isLoading = MutableLiveData<Boolean>()

    companion object {
        private var cacheWonderful: MutableList<YoutubeListResponse.Item>? = null
        private var cacheShorts: MutableList<YoutubeListResponse.Item>? = null
        private var cacheVlog: MutableList<YoutubeListResponse.Item>? = null
    }

    fun setIsLoading(isLoading: Boolean) {
        this.isLoading.postValue(isLoading)
    }

    fun getHighlightsList(highlightsType: HighlightsType, forceRefresh: Boolean = false) {
        // Kiểm tra cache trong companion object
        val currentCache = when (highlightsType) {
            HighlightsType.HT_WONDERFUL_VIDEOS -> cacheWonderful
            HighlightsType.HT_FLASH_SHORT_FILM -> cacheShorts
            HighlightsType.HT_DAILY_VLOG -> cacheVlog
        }
        if (!forceRefresh && currentCache != null) {
            Log.d("YoutubeQuota", "BLOCKED: USING STATIC CACHE - 0 QUOTA POINTS")
            highlightsList.postValue(currentCache)
            return
        }

        // Nếu chưa có cache hoặc bắt buộc làm mới thì gọi API
        setIsLoading(true)
        val playlistId = when (highlightsType) {
            HighlightsType.HT_WONDERFUL_VIDEOS -> "PLTYHsJxRmtwZT2vFEnqXlCVDwiIhSliGf"
            HighlightsType.HT_FLASH_SHORT_FILM -> "PLTYHsJxRmtwYWMPUQbN2MizeS35YkyTFy"
            HighlightsType.HT_DAILY_VLOG -> "PLTYHsJxRmtwa8CotI9T53I1TOWro2IiSA"
        }

        API.shared?.api?.let { api ->
            api.getYoutubePlaylistItemsDirect(
                "snippet",
                playlistId,
                20,
                NetBase.YOUTUBE_API_KEY
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        setIsLoading(false)
                        val items = response.items?.toMutableList() ?: mutableListOf()

                        // Lưu dữ liệu vừa tải vào Cache tương ứng
                        when (highlightsType) {
                            HighlightsType.HT_WONDERFUL_VIDEOS -> cacheWonderful = items
                            HighlightsType.HT_FLASH_SHORT_FILM -> cacheShorts = items
                            HighlightsType.HT_DAILY_VLOG -> cacheVlog = items
                        }

                        highlightsList.postValue(items)
                    },
                    { error ->
                        setIsLoading(false)
                        highlightsList.postValue(mutableListOf())
                        Log.e("YoutubeError", "Error in GOOGLE: ${error.message}", error)
                    }
                )
        }
    }
}