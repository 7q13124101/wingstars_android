package com.wingstars.home.viewmodel // Đặt package name cho đúng

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wingstars.base.net.API
import com.wingstars.base.net.NetBase
import com.wingstars.base.net.beans.IteneraryResponse
import com.wingstars.base.net.beans.LatestNewsResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class HomeViewModel : ViewModel() {

    // LiveData cũ
    val homeDataList = MutableLiveData<MutableList<Int>>()
    val newsDataList = MutableLiveData<MutableList<LatestNewsResponse>>()
    val memberDataList = MutableLiveData<MutableList<Int>>()
    val calendarDataList = MutableLiveData<MutableList<IteneraryResponse>>()

    var isLoading = MutableLiveData<Boolean>()





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
    }
    fun getLatestNewsData() {
        isLoading.postValue(true)
        API.shared?.api?.let {
            val observerT =
                it.latestNews(
                    "${NetBase.HOST_HAWKS}/wp-json/wp/v2/posts",
                    4,
                    "date",
                    "desc"
                )
            observerT?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    isLoading.postValue(false)
                    var itemTypeList: MutableList<LatestNewsResponse> = mutableListOf()
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

    // Thêm LiveData để chứa dữ liệu trả về

    fun getCalendarData() {
        // 1. Tạo URL đầy đủ với các tham số _fields
        val fullUrl = "${NetBase.HOST_HAWKS}/wp-json/wp/v2/calendar?_fields=id,title.rendered,acf,content.rendered,yoast_head_json.og_image,calendar_category"

        // 2. Gọi API
        API.shared?.api?.let { api ->
            val observer = api.getItineraryList(fullUrl)

            observer
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { next ->
                        // --- XỬ LÝ THÀNH CÔNG ---
                        val list = mutableListOf<IteneraryResponse>()
                        list.addAll(next)

                        // Đẩy dữ liệu vào LiveData
                        calendarDataList.postValue(list)

                        // Log kiểm tra
                        // Log.d("API", "Calendar size: ${list.size}")
                    },
                    { error ->
                        // --- XỬ LÝ LỖI ---
                        error.printStackTrace()
                        // Log.e("API", "Error: ${error.message}")
                    }
                )
        }
    }
}