package com.wingstars.member.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wingstars.base.net.NetBase

class MemberViewModel: ViewModel() {
    var popularitylist = MutableLiveData<MutableList<Int>>()

    public fun  getPopularitylist(){
        var arrayList = mutableListOf(1,2,3)
        popularitylist.postValue(arrayList)

//        NetBase.ut()
    }
}