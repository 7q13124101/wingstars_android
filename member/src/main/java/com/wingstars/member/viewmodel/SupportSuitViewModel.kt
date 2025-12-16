package com.wingstars.member.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wingstars.base.net.API
import com.wingstars.base.net.beans.CRMBaseFailResponse
import com.wingstars.base.net.beans.WSFashionCategoryResponse
import com.wingstars.base.net.beans.WSFashionResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException

class SupportSuitViewModel : ViewModel() {

    var categorylist = MutableLiveData<MutableList<String>>()
    var wsFashionCategorysData = MutableLiveData<MutableList<WSFashionCategoryResponse>>()
    var wsFashions = MutableLiveData<MutableList<WSFashionResponse>>()
    var wsMoreFashions = MutableLiveData<MutableList<WSFashionResponse>>()
    var loading = MutableLiveData<Boolean>()
    var tip = MutableLiveData<String>()
    var PER_PAGE = 10             //每页条数
    var PAGE = 1                //当前页数
    var fashionIds = 0
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
                            fashionIds = typeData.id
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

    public fun wsFashions(fashionId: Int=fashionIds,isShowLoading: Boolean = false,isLoadMore: Boolean = false) {
        if (isShowLoading){
            loading.postValue(true)
        }
        if (isLoadMore){
            PAGE++
        }
        API.shared?.api?.let {
            val emptyHashMap: java.util.HashMap<String, Int> = HashMap()
            emptyHashMap["fashion_category"] = fashionId
            val observer = it.wsFashions(emptyHashMap, PER_PAGE, PAGE)
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    loading.postValue(false)
                    if (!next.isNullOrEmpty()) {
                        Log.e("wsFashions", "${next}")
                        if (!isLoadMore){
                            wsFashions.postValue(next)
                        }else{
                            wsMoreFashions.postValue(next)
                        }

                    }
                },
                { error ->
                    if (PAGE>1){
                        PAGE=PAGE-1
                    }
                    if (error is HttpException) {
                        try {
                            val gson = Gson()
                            val type = object : TypeToken<CRMBaseFailResponse>() {}.type
                            val failResponse = gson.fromJson<CRMBaseFailResponse>(
                                error.response()?.errorBody()?.string(), type
                            )
                           if (failResponse.code.trim()=="rest_post_invalid_page_number"){
                               tip.postValue("rest_post_invalid_page_number")
                           }else{
                               tip.postValue("${failResponse.message}")
                           }
                        } catch (e: Exception) {

                        }
                    }else{
                        tip.postValue("${error.message}")
                    }
                    loading.postValue(false)
                }
            )
        }
    }

}