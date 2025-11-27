package com.wingstars.member.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wingstars.member.bean.TakePhotosMembersListBean

class FanInteractionViewModel : ViewModel() {
    var takePhotosMembersList = MutableLiveData<MutableList<TakePhotosMembersListBean>>()
    var membersList = mutableListOf<TakePhotosMembersListBean>()

    public fun getTakePhotosMembersList() : MutableList<TakePhotosMembersListBean>{
        if (membersList.isEmpty()){
            membersList.add(TakePhotosMembersListBean(number = "2", name = "安芝儇"))
            membersList.add(TakePhotosMembersListBean(number = "90", name = "Mingo"))
            membersList.add(TakePhotosMembersListBean(number = "22", name = "一粒"))
            membersList.add(TakePhotosMembersListBean(number = "00", name = "圈圈"))
            membersList.add(TakePhotosMembersListBean(number = "5", name = "恬魚"))
        }
        return membersList
    }
}