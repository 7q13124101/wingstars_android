package com.wingstars.home.viewmodel // Đặt package name cho đúng

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.wingstars.base.net.API
import com.wingstars.base.net.beans.WSCalendarResponse
import com.wingstars.base.net.beans.WSFashionResponse
import com.wingstars.base.net.beans.WSPostResponse
import com.wingstars.base.net.beans.WSProductResponse
import com.wingstars.member.bean.WSMemberRankBean
import com.wingstars.member.bean.WSRankBean
import com.wingstars.member.bean.WSRankBean.ACFBean
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class HomeViewModel : ViewModel() {

    // LiveData cũ
    val homeDataList = MutableLiveData<MutableList<Int>>()
    val newsDataList = MutableLiveData<MutableList<WSPostResponse>>()
    val memberDataList = MutableLiveData<MutableList<Int>>()
    val calendarDataList = MutableLiveData<MutableList<WSCalendarResponse>>()
    val productDataList = MutableLiveData<MutableList<WSProductResponse>>()
    val fashionDataList = MutableLiveData<MutableList<WSFashionResponse>>()
    var wsRankData = MutableLiveData<MutableList<WSMemberRankBean>>()



    var isLoading = MutableLiveData<Boolean>()
    var tip = MutableLiveData<String>()






    public fun getHomeData() {
        // Dữ liệu cho 5 list cũ (sản phẩm, thành viên, v.v.)
        val dummyList = mutableListOf(1, 2, 3, 4)
        homeDataList.postValue(dummyList)

//        val newList = mutableListOf(1, 2, 3)
//        newsDataList.postValue(newList)

        val memberList = mutableListOf(1, 2, 3, 4, 5)
        memberDataList.postValue(memberList)
        getLatestNewsData()
        getCalendarData()
        getProductsData()
        getFashionsData()
    }
    fun getLatestNewsData() {
        isLoading.postValue(true)
        API.shared?.api?.let {
            val observerT =it.wsPosts()
            observerT?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    isLoading.postValue(false)
                    var itemTypeList: MutableList<WSPostResponse> = mutableListOf()
                    itemTypeList.clear()
                    itemTypeList.addAll(next)
                    newsDataList.postValue(itemTypeList)
                },
                { error ->
                    isLoading.postValue(false)
//                    error.message?.let { it1 ->
//                        Toast.makeText(
//                            BaseApplication.shared()!!,
//                            it1?.toString(),
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
                }
            )
        }

        //utApi()
    }
    fun getCalendarData() {
        API.shared?.api?.let { api ->
            val observer = api.wsSchedule(3,1)
            observer
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { next ->
                        Log.d("getWsCalendarsData", next.toString())
                        calendarDataList.postValue(next)
                    },
                    { error ->
                        Log.e("getWsCalendarsData", error.toString())

                        error.printStackTrace()
                    }
                )
        }
    }
    fun getProductsData(){
        API.shared?.api?.let { api ->
            val observer = api.wsProducts()
            observer
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {next ->
                            val list = mutableListOf<WSProductResponse>()
                            list.addAll(next)
                        productDataList.postValue(list)
                    },
                    {error ->
                        error.printStackTrace()
                    }
                )
        }
    }
    fun getFashionsData(){
        val  params = HashMap<String, Int>()
        API.shared?.api?.let { api ->
            val observer = api.wsFashions(params, 6,1)
            observer
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {next ->
                        val list = mutableListOf<WSFashionResponse>()
                        list.addAll(next)
                        fashionDataList.postValue(list)
                    },
                    {error ->
                        error.printStackTrace()
                    }
                )
        }
    }
    public fun getRenderedList() {
        isLoading.postValue(true)
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
                        isLoading.postValue(false)
                    }
                },
                { error ->
                    isLoading.postValue(false)
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
                    isLoading.postValue(false)
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
                        isLoading.postValue(false)
                    }
                },
                { error ->
                    isLoading.postValue(false)
                }
            )
        }
    }
}