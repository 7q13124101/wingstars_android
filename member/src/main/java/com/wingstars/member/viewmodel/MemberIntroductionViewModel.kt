package com.wingstars.member.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wingstars.member.adapter.HighlightsData

class MemberIntroductionViewModel : ViewModel(){
    var memberIntroductionListData = MutableLiveData<MutableList<Int>>()
    var isLoading = MutableLiveData<Boolean>()
    fun setIsLoading(isLoading: Boolean) {
        this.isLoading.postValue(isLoading)
    }

    fun getMemberIntroductionListData() {
        setIsLoading(true)
        var arrayList = mutableListOf(1,2,3,4,5,6,7,8)
        memberIntroductionListData.postValue(arrayList)
        setIsLoading(false)
    }
}