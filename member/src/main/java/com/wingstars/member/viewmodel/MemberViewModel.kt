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
import com.wingstars.base.net.beans.WSMemberResponse



class MemberViewModel: ViewModel() {
    var popularitylist = MutableLiveData<MutableList<Int>>()
    var wsMembersData = MutableLiveData<MutableList<WSMemberResponse>>()

    public fun  getPopularitylist(){
        var arrayList = mutableListOf(1,2,3)
        popularitylist.postValue(arrayList)

//        NetBase.ut()
    }

    fun  getWsMembersData() {
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