package com.wingstars.member.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RankExplanationViewModel: ViewModel() {
    var explanationlist = MutableLiveData<MutableList<Int>>()

    public fun  getExplanationlist(){
        var arrayList = mutableListOf(1,2,3,4,5,6,7,8)
        explanationlist.postValue(arrayList)
    }
}