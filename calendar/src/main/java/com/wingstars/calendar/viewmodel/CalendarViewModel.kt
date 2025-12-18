package com.wingstars.calendar.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wingstars.base.net.API
import com.wingstars.base.net.beans.WSCalendarCategoryResponse
import com.wingstars.base.net.beans.WSCalendarNResponse
import com.wingstars.base.net.beans.WSMemberResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Calendar
import java.util.Date

// 重组后的每日活动数据模型（用于日历显示）
data class DailyCalendarData(
    val date: Date,          // 日期（精确到天）
    val year: Int,           // 年
    val month: Int,          // 月（Calendar.MONTH 格式，0=1月）
    val day: Int,            // 日
    val originalItem: WSCalendarNResponse // 关联的原始数据
)

class CalendarViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    private val compositeDisposable = CompositeDisposable()
    var selectedData = MutableLiveData<MutableList<WSCalendarNResponse>>()
    var wSCalendarData = MutableLiveData<MutableList<WSCalendarNResponse>>()
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

    fun getWsCalendar(year: Int = Calendar.getInstance().get(Calendar.YEAR)) {
        setIsLoading(true)
        val allMonthData = ArrayList<WSCalendarNResponse>()
        var completedCount = 0 // 已完成的月份请求数

        // 遍历1-12月，逐个构建参数并请求
        for (month in 1..12) {
            val monthParam = HashMap<String, String>().apply {
                put("ym", year.toString() + "-" + String.format("%02d", month))
            }

            API.shared?.api?.let { api ->
                val observer = api.wsCalendarN(monthParam)
                observer.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe({ monthData ->
                        completedCount++
                        allMonthData.addAll(monthData) // 累加当月数据
                        // 所有月份请求完成
                        if (completedCount == 12) {
                            setIsLoading(false)
                            wSCalendarData.postValue(allMonthData)
                        }
                    }, { error ->
                        completedCount++
                        val errorMsg = error.message ?: "未知错误"
                        // 个别月份失败，仍等待全部请求完成
                        if (completedCount == 12) {
                            setIsLoading(false)
                            wSCalendarData.postValue(allMonthData) // 返回已获取的部分数据
                        }
                    }).let {
                        compositeDisposable.add(it)
                    }
            } ?: run {
                completedCount++
                if (completedCount == 12) {
                    setIsLoading(false)
                }
            }
        }
    }

    //获取全部成员
    fun getWsMembersBirthdayData() {
        API.shared?.api?.let {
            val observer = it.wsMembers(100, 1)
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe({ next ->
                wsMembersBirthdayData.postValue(next)
            }, { error ->
                var msg = error.message.toString()
                msg.let { it1 ->
                    //Toast.makeText(BaseApplication.shared()!!, "$it1", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    //活动类型
    fun getWSCalendarCategory() {
        API.shared?.api?.let {
            val observer = it.wsCalendarCategory(100, 1)
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe({ next ->
                calendarCategoryData.postValue(next)
            }, { error ->
                error.message?.let { it1 ->
//                        Log.d("API", "[wsCalendar] error.message: ${it1?.toString()}")
                }
            })
        }
    }
}