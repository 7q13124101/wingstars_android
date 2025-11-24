package com.wingstars.count.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CountViewModel: ViewModel() {
    var popularitylist = MutableLiveData<MutableList<Int>>()

    public fun  getPopularitylist(){
        var arrayList = mutableListOf(1,2,3)
        popularitylist.postValue(arrayList)
    }
}