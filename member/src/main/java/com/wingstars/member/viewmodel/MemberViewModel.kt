package com.wingstars.member.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MemberViewModel: ViewModel() {
    var popularitylist = MutableLiveData<MutableList<Int>>()

    public fun  getPopularitylist(){
        var arrayList = mutableListOf(1,2,3)
        popularitylist.postValue(arrayList)
    }
}