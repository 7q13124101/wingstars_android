package com.wingstars.member.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wingstars.base.net.API
import com.wingstars.member.bean.TakePhotosMembersListBean
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class FanInteractionViewModel : ViewModel() {
    var takePhotosMembersList = MutableLiveData<MutableList<TakePhotosMembersListBean>>()
    var membersList = mutableListOf<TakePhotosMembersListBean>()
    var loading = MutableLiveData<Boolean>()

    /*public fun getTakePhotosMembersList() : MutableList<TakePhotosMembersListBean>{
        if (membersList.isEmpty()){
            membersList.add(TakePhotosMembersListBean(number = "2", name = "安芝儇"))
            membersList.add(TakePhotosMembersListBean(number = "90", name = "Mingo"))
            membersList.add(TakePhotosMembersListBean(number = "22", name = "一粒"))
            membersList.add(TakePhotosMembersListBean(number = "00", name = "圈圈"))
            membersList.add(TakePhotosMembersListBean(number = "5", name = "恬魚"))
        }
        return membersList
    }*/
    public fun wsPhotoFrames() {
        loading.postValue(true)
        membersList.clear()
        API.shared?.api?.let {
            val observer = it.wsPhotoFrames()
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    loading.postValue(false)
                    if (!next.isNullOrEmpty()) {
                        next.forEach { data->
                            val name = data.titleF
                            val number = data.acf.numberF
                            var full = ""
                            val photoFrame = data.acf.photoFrame
                            if (photoFrame!=null){
                                val image1 = photoFrame.image1
                                if (image1!=null){
                                    val sizes = image1.sizes
                                    if(sizes!=null){
                                        full =  sizes.`1536x1536`
                                        membersList.add(TakePhotosMembersListBean(number = number, name = name,imgae= full))
                                    }
                                }
                            }

                        }
                        takePhotosMembersList.postValue(membersList)
                    }
                },
                { error ->
                    loading.postValue(false)
                }
            )
        }
    }

}