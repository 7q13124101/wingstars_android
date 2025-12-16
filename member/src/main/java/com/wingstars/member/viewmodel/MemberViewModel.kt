package com.wingstars.member.viewmodel

import android.util.Log
import retrofit2.HttpException
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wingstars.base.net.API

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wingstars.base.net.NetBase
import com.wingstars.base.net.beans.WSFashionCategoryResponse
import com.wingstars.base.net.beans.WSFashionResponse
import com.wingstars.base.net.beans.WSMemberResponse
import com.wingstars.member.bean.WSMemberRankBean
import com.wingstars.member.bean.WSRankBean
import com.wingstars.member.bean.WSRankBean.ACFBean
import kotlin.collections.forEach


class MemberViewModel : ViewModel() {
    var popularitylist = MutableLiveData<MutableList<Int>>()
    var wsMembersData = MutableLiveData<MutableList<WSMemberResponse>>()
    var loading = MutableLiveData<Boolean>()
    var wsRankData = MutableLiveData<MutableList<WSMemberRankBean>>()
    var wsFashions = MutableLiveData<MutableList<WSFashionResponse>>()

    var wsFashionCategorysData = MutableLiveData<MutableList<WSFashionCategoryResponse>>()
    public fun getPopularitylist() {
        var arrayList = mutableListOf(1, 2, 3)
        popularitylist.postValue(arrayList)

//        NetBase.ut()
    }

    public fun wsFashionCategorys() {
        API.shared?.api?.let {
            val observer = it.wsFashionCategorys()
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    if (!next.isNullOrEmpty()) {
                        wsFashionCategorysData.postValue(next)
                        wsFashions()
                    }
                },
                { error ->

                }
            )
        }
    }

    public fun wsFashions() {
        API.shared?.api?.let {
            val emptyHashMap: java.util.HashMap<String?, Int?>? = HashMap()
            val observer = it.wsFashions(emptyHashMap, 3, 1)
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    if (!next.isNullOrEmpty()) {
                        Log.e("wsFashions", "${next}")
                        wsFashions.postValue(next)
                    }
                },
                { error ->

                }
            )
        }
    }


    public fun getRenderedList() {
        loading.postValue(true)
        API.shared?.api?.let {
            val observer = it.wsRank()
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    if (!next.isNullOrEmpty()) {
                        var data = mutableListOf<WSMemberRankBean>()
                        Log.e("getRenderedList", "${Gson().toJson(next)}")
                        next.forEach {
                            val title = it.title
                            var rendered = ""
                            if (title != null) {
                                rendered = title.rendered
                            }
                            val acf = it.acf
                            var name = ""
                            var volume = ""
                            if (acf != null) {
                                val rankBean = acf.rankBean(1)
                                if (rankBean != null) {
                                    name = rankBean.name
                                    volume = rankBean.volume
                                }
                            }
                            var bean =
                                WSMemberRankBean(title = rendered, name = name, volume = volume)
                            data.add(bean)
                        }

                        wsPhotos(data)


                    } else {
                        loading.postValue(false)
                    }
                },
                { error ->
                    loading.postValue(false)
                }
            )
        }
    }

    private fun wsPhotos(data: MutableList<WSMemberRankBean>) {
        API.shared?.api?.let {
            val observer = it.wsPhotos()
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    loading.postValue(false)
                    if (!next.isNullOrEmpty()) {
                        data.forEach {
                            val acf = it.name
                            if (acf != null) {
                                val imageList = next.filter { it.title.rendered.trim() == acf }
                                if (!imageList.isNullOrEmpty()) {
                                    val acf1 = imageList[0].acf
                                    if (acf1 != null) {
                                        it.number = acf1.number
                                    }
                                    var yoast_head_json = imageList[0].yoast_head_json
                                    if (yoast_head_json != null) {
                                        val ogImage = yoast_head_json.og_image
                                        if (!ogImage.isNullOrEmpty()) {
                                            it.image = ogImage[0].url
                                        }
                                    }
                                }
                            }
                        }
                        wsRankData.postValue(data)

                    } else {
                        loading.postValue(false)
                    }
                },
                { error ->
                    loading.postValue(false)
                }
            )
        }
    }

    fun getWsMembersData() {
        Log.e("getWsMembersData", "getWsMembersData")
        //成员 > 成员介绍
        API.shared?.api?.let {
            val observer = it.wsMembers(4, 1)
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    Log.e("getWsMembersData", "[wsMembers] next.data.size:  ")
                    /*Log.d("API", "[wsMembers] next.data.size: ${next.size}")

                    for (rd in next) {
                        println("title: ${rd.titleF}, number: ${rd.acf.number}, fb_link: ${rd.acf.fb_link}, ig_link: ${rd.acf.ig_link}, about: ${rd.acf.about}, say: ${rd.acf.say}, interest: ${rd.acf.interest}, height: ${rd.acf.height}, weight: ${rd.acf.weight}, birthdate: ${rd.acf.birthdate}, sign: ${rd.acf.sign}, blood_type: ${rd.acf.blood_type}, url: ${rd.urlF}")
                    }*/
                    wsMembersData.postValue(next)
                },
                { error ->
                    Log.e("getWsMembersData", "error=${error.message}")
                    var msg = error.message.toString()
                    /*if (error is HttpException) {
                        try {
                            val gson = Gson()
                            val type = object : TypeToken<CRMBaseFailResponse>() {}.type
                            val failResponse = gson.fromJson<CRMBaseFailResponse>(
                                error.response()?.errorBody()?.string(), type
                            )
                            if (failResponse != null) {
                                failResponse.message?.let {
                                    msg = it
                                }
                            }
                        } catch (e: Exception) {

                        }
                    }*/

                    msg.let { it1 ->
                        //Toast.makeText(BaseApplication.shared()!!, "$it1", Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
    }
}