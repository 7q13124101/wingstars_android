package com.wingstars.member.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PopularityRankingViewModel: ViewModel() {
    var rankinglist = MutableLiveData<MutableList<Int>>()

    public fun  getRankinglist(){
        var arrayList = mutableListOf(1,2,3,4,5,6,7,8)
        rankinglist.postValue(arrayList)
    }
}