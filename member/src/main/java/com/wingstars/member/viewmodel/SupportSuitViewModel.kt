package com.wingstars.member.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wingstars.base.net.API
import com.wingstars.base.net.beans.WSFashionCategoryResponse
import com.wingstars.base.net.beans.WSFashionResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class SupportSuitViewModel : ViewModel() {

    var categorylist = MutableLiveData<MutableList<String>>()
    var wsFashionCategorysData = MutableLiveData<MutableList<WSFashionCategoryResponse>>()
    var wsFashions = MutableLiveData<MutableList<WSFashionResponse>>()
    var loading = MutableLiveData<Boolean>()
    public fun  getCategoryList(){
        var list = mutableListOf<String>()
        list.add("球衣")
        list.add("應援")
        categorylist.postValue(list)
    }

    /**
     * @param type  1 應援服  2 活動服
     */
    public fun wsFashionCategorys(type: Int=1) {
        loading.postValue(true)
        API.shared?.api?.let {
            val observer = it.wsFashionCategorys()
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    if (!next.isNullOrEmpty()) {
                        wsFashionCategorysData.postValue(next)
                        val typeData = next!!.find { it.name ==  if (type==1) "應援服" else "活動服" }
                        if (typeData!=null){
                            wsFashions(typeData.id)
                        }else{
                            loading.postValue(false)
                        }

                    }else{
                        loading.postValue(false)
                    }
                },
                { error ->
                    loading.postValue(false)
                }
            )
        }
    }

    public fun wsFashions(fashionId: Int) {
        API.shared?.api?.let {
            val emptyHashMap: java.util.HashMap<String, Int> = HashMap()
            emptyHashMap["fashion_category"] = fashionId
            val observer = it.wsFashions(emptyHashMap, 10, 1)
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    loading.postValue(false)
                    if (!next.isNullOrEmpty()) {
                        Log.e("wsFashions", "${next}")
                        wsFashions.postValue(next)
                    }
                },
                { error ->
                    loading.postValue(false)
                }
            )
        }
    }

}