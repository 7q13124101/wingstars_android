package com.wingstars.member.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EventUniformViewModel : ViewModel() {

    var categorylist = MutableLiveData<MutableList<String>>()

    public fun  getCategoryList(){
        var list = mutableListOf<String>()
        list.add("團隊形象")
        list.add("主題應援")
        list.add("聯名限定")
        list.add("舞台魅力")
        categorylist.postValue(list)
    }
}