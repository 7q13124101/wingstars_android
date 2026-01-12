package com.wingstars.member.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wingstars.base.net.API
import com.wingstars.base.net.beans.YoutubeListResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

enum class HighlightsType {
    HT_WONDERFUL_VIDEOS,      //精彩影片
    HT_FLASH_SHORT_FILM,      //快閃短片
    HT_DAILY_VLOG             //日常Vlog
}

class HighlightsViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    var highlightsList = MutableLiveData<MutableList<YoutubeListResponse.Item>>()

    var isLoading = MutableLiveData<Boolean>()
    fun setIsLoading(isLoading: Boolean) {
        this.isLoading.postValue(isLoading)
    }

    fun getHighlightsList(highlightsType: HighlightsType) {
        setIsLoading(true)
        val arrayList = mutableListOf<YoutubeListResponse.Item>()
        arrayList.clear()
        when (highlightsType) {
            HighlightsType.HT_WONDERFUL_VIDEOS -> {//精彩影片
                API.shared?.api?.let {
                    //获取Youtube视频
                    val observer = it.nsYtbList()
                    observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())
                        ?.observeOn(
                            AndroidSchedulers.mainThread()
                        )?.subscribe(
                        { next ->
                            setIsLoading(false)
                            next.items?.let { it ->
                                arrayList.addAll(it)
                            }
                            highlightsList.postValue(arrayList)
                        },
                        { error ->
                            setIsLoading(false)
                            highlightsList.postValue(arrayList)
                            var msg = error.message.toString()
                            msg.let { it1 ->
                            }
                        }
                    )
                }
            }

            HighlightsType.HT_FLASH_SHORT_FILM,//快閃短片
            HighlightsType.HT_DAILY_VLOG -> {//日常Vlog
                API.shared?.api?.let {
                    var eventType = "shorts"
                    if (highlightsType == HighlightsType.HT_DAILY_VLOG)
                        eventType = "vlog"
                    val observer = it.nsYtbList(eventType)
                    observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())
                        ?.observeOn(
                            AndroidSchedulers.mainThread()
                        )?.subscribe(
                        { next ->
                            setIsLoading(false)
                            next.items?.let { it ->
                                arrayList.addAll(it)
                            }
                            highlightsList.postValue(arrayList)
                        },
                        { error ->
                            setIsLoading(false)
                            highlightsList.postValue(arrayList)
                            var msg = error.message.toString()
                            msg.let { it1 ->
                            }
                        }
                    )
                }
            }
        }
    }
}