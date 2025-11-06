package com.wingstars.member.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SupportSuitViewModel : ViewModel() {

    var categorylist = MutableLiveData<MutableList<String>>()

    public fun  getCategoryList(){
        var list = mutableListOf<String>()
        list.add("球衣")
        list.add("應援")
        categorylist.postValue(list)
    }
}