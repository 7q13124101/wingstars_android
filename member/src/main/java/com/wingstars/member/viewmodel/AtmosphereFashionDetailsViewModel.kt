package com.wingstars.member.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wingstars.base.net.API
import com.wingstars.base.net.beans.WSFashionDetailResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class AtmosphereFashionDetailsViewModel : ViewModel()  {
    var loading = MutableLiveData<Boolean>()
    var wsFashion = MutableLiveData<WSFashionDetailResponse>()

    public fun wsFashionCategorys(fashion_id:Int){
        loading.postValue(true)
        API.shared?.api?.let {
            val observer = it.wsFashion(fashion_id)
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    loading.postValue(false)
                    if (next!=null){
                        wsFashion.postValue(next)
                    }
                },
                { error ->
                    loading.postValue(false)
                }
            )
        }
    }
}