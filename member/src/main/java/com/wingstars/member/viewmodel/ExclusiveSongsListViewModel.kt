package com.wingstars.member.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wingstars.member.adapter.HighlightsData

class ExclusiveSongsListViewModel : ViewModel(){
    var exclusiveSongsListData = MutableLiveData<MutableList<HighlightsData>>()
    var isLoading = MutableLiveData<Boolean>()
    fun setIsLoading(isLoading: Boolean) {
        this.isLoading.postValue(isLoading)
    }

    fun getExclusiveSongsListData() {
        setIsLoading(true)
        val songsList = mutableListOf<HighlightsData>()
        var node = HighlightsData("【Wing Stars】 2025首張單曲 《\uD835\uDE3E\uD835\uDE5D\uD835\uDE5A\uD835\uDE5A\uD835\uDE67 \uD835\uDE5E\uD835\uDE69 \uD835\uDE6A\uD835\uDE65 加大》 Official Music Video","2025.03.20",false)
        songsList.add(node)
        node = HighlightsData("\uD835\uDDEA\uD835\uDDF6\uD835\uDDFB\uD835\uDDF4 \uD835\uDDE6\uD835\uDE01\uD835\uDDEE\uD835\uDDFF\uD835\uDE00 - \uD83C\uDFAC 明星賽幕後全紀錄 ✨","2025.07.28",true)
        songsList.add(node)
        node = HighlightsData("Ｗing Stars - 魷戲玩到底","2025.07.16",false)
        songsList.add(node)
        exclusiveSongsListData.postValue(songsList)
        setIsLoading(false)
    }
}