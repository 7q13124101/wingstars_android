package com.wingstars.calendar.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wingstars.base.net.API
import com.wingstars.base.net.beans.WSCalendarCategoryResponse
import com.wingstars.base.net.beans.WSCalendarResponse
import com.wingstars.base.net.beans.WSMemberResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class CalendarViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    var selectedData = MutableLiveData<MutableList<WSCalendarResponse>>()
    var wSCalendarData = MutableLiveData<MutableList<WSCalendarResponse>>()
    var wsMembersBirthdayData = MutableLiveData<MutableList<WSMemberResponse>>()
    var calendarCategoryData = MutableLiveData<MutableList<WSCalendarCategoryResponse>>()

    var isLoading = MutableLiveData<Boolean>()
    fun setIsLoading(isLoading: Boolean) {
        this.isLoading.postValue(isLoading)
    }

    object CalendarCategory {
        const val GENERAL_ACTIVITY = 368 // 一般活动
        const val BIRTHDAY = 369 // 生日活动
        const val SKY_EAGLE = 365 // 天鹰
        const val HUNT_EAGLE = 364 // 猎鹰
        const val MALE_EAGLE = 363 // 雄鹰
        const val MAX_DISPLAY_COUNT = 3 // 单日最大显示类型数
    }

    fun getWsCalendar() {
        setIsLoading(true)
        API.shared?.api?.let {
            val observer = it.wsCalendar(100, 1)
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
//                    Log.d("API", "[wsCalendar] next.data.size: ${next.size}")
                    val dataDTOArrayList: ArrayList<WSCalendarResponse> = ArrayList()
                    dataDTOArrayList.clear()
                    for (pd in next) {
//                        if (pd.id == 79119) {
////                            pd.calendar_category = listOf(369)
//                            pd.acf.Activity_time.st_date = "2026-12-1 14:00:00"
//                            pd.acf.Activity_time.ed_date = "2026-12-15 14:00:00"
//                        }
//                        if (pd.id == 72273) {
//                            pd.acf.Activity_time.st_date = "2025-12-1 14:00:00"
//                            pd.acf.Activity_time.ed_date = "2025-12-15 14:00:00"
//                        }
//                        if (pd.id == 72264) {
//                            pd.acf.Activity_time.st_date = "2025-12-1 14:00:00"
//                            pd.acf.Activity_time.ed_date = "2025-12-15 14:00:00"
//                        }
//                        println("title: ${pd.titleF}, content: ${pd.contentF}, date: ${pd.dateF}, map: ${pd.mapF}, Precautions: ${pd.PrecautionsF}, category: ${pd.calendar_categoryF}")
//                        println("  url: ${pd.urlF}")//pd.yoast_head_json类型有误，数组[]还是对象{}？
                        dataDTOArrayList.add(pd)
                    }
                    wSCalendarData.postValue(dataDTOArrayList)
                },
                { error ->
                    setIsLoading(false)
                    error.message?.let { it1 ->
//                        Log.d("API", "[wsCalendar] error.message: ${it1?.toString()}")
                    }
                }
            )
        }
    }

    //获取全部成员
    fun getWsMembersBirthdayData() {
        API.shared?.api?.let {
            val observer = it.wsMembers(100, 1)
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    wsMembersBirthdayData.postValue(next)
                },
                { error ->
                    var msg = error.message.toString()
                    msg.let { it1 ->
                        //Toast.makeText(BaseApplication.shared()!!, "$it1", Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
    }

    //活动类型
    fun getWSCalendarCategory() {
        API.shared?.api?.let {
            val observer = it.wsCalendarCategory(100, 1)
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
//                    Log.d("API", "[wsCalendar] next.data.size: ${next.size}")
                    calendarCategoryData.postValue(next)
                },
                { error ->
                    error.message?.let { it1 ->
//                        Log.d("API", "[wsCalendar] error.message: ${it1?.toString()}")
                    }
                }
            )
        }
    }
}