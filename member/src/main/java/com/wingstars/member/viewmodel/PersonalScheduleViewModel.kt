package com.wingstars.member.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wingstars.base.net.API
import com.wingstars.base.net.beans.WSScheduleResponse
import com.wingstars.member.R
import com.wingstars.member.adapter.SelectTeamFunBean
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

enum class TeamType { TEAM_ALL, TEAM_BASEBALL, TEAM_BASKETBALL, TEAM_VOLLEYBALL }
class PersonalScheduleViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    var teamCategoryList = MutableLiveData<MutableList<SelectTeamFunBean>>()

    var personalScheduleList = MutableLiveData<MutableList<WSScheduleResponse>>()


    var isLoading = MutableLiveData<Boolean>()
    fun setIsLoading(isLoading: Boolean) {
        this.isLoading.postValue(isLoading)
    }

    public fun getTeamCategoryList() {
        val itemList: MutableList<SelectTeamFunBean> = mutableListOf()
        var bean =
            SelectTeamFunBean(TeamType.TEAM_ALL, R.drawable.ic_all_dark, R.drawable.ic_all_pink)
        itemList.add(bean)
        bean =
            SelectTeamFunBean(
                TeamType.TEAM_BASEBALL,
                R.drawable.ic_baseball_dark,
                R.drawable.ic_baseball_pink
            )
        itemList.add(bean)
        bean =
            SelectTeamFunBean(
                TeamType.TEAM_BASKETBALL,
                R.drawable.ic_basketball_dark,
                R.drawable.ic_basketball_pink
            )
        itemList.add(bean)
        bean =
            SelectTeamFunBean(
                TeamType.TEAM_VOLLEYBALL,
                R.drawable.ic_volleyball_dark,
                R.drawable.ic_volleyball_pink
            )
        itemList.add(bean)

        teamCategoryList.postValue(itemList)
    }

    /*public fun getPersonalScheduleList() {
        val itemList: MutableList<ScheduleFunBean> = mutableListOf()
        var bean = ScheduleFunBean("12/1 (一)", "Stars House 一日店長")
        itemList.add(bean)
        bean = ScheduleFunBean("12/2 (二)", "雄鷹")
        itemList.add(bean)
        bean = ScheduleFunBean("12/3 (三)", "獵鷹")
        itemList.add(bean)
        bean = ScheduleFunBean("12/4 (四)", "天鷹")
        itemList.add(bean)

        bean = ScheduleFunBean("12/5 (五)", "雄鷹")
        itemList.add(bean)
        bean = ScheduleFunBean("12/6 (六)", "獵鷹")
        itemList.add(bean)
        bean = ScheduleFunBean("12/7 (日)", "天鷹")
        itemList.add(bean)

        bean = ScheduleFunBean("12/9 (二)", "雄鷹")
        itemList.add(bean)
        bean = ScheduleFunBean("12/10 (三)", "獵鷹")
        itemList.add(bean)
        bean = ScheduleFunBean("12/11 (四)", "天鷹")
        itemList.add(bean)

        bean = ScheduleFunBean("12/12 (五)", "雄鷹")
        itemList.add(bean)
        bean = ScheduleFunBean("12/13 (六)", "獵鷹")
        itemList.add(bean)
        bean = ScheduleFunBean("12/14 (日)", "天鷹")
        itemList.add(bean)

        bean = ScheduleFunBean("12/16 (二)", "雄鷹")
        itemList.add(bean)
        bean = ScheduleFunBean("12/17 (三)", "獵鷹")
        itemList.add(bean)
        bean = ScheduleFunBean("12/18 (四)", "天鷹")
        itemList.add(bean)

        bean = ScheduleFunBean("12/19 (五)", "雄鷹")
        itemList.add(bean)
        bean = ScheduleFunBean("12/20 (六)", "獵鷹")
        itemList.add(bean)
        bean = ScheduleFunBean("12/21 (日)", "天鷹")
        itemList.add(bean)

        bean = ScheduleFunBean("12/23 (二)", "雄鷹")
        itemList.add(bean)
        bean = ScheduleFunBean("12/24 (三)", "獵鷹")
        itemList.add(bean)
        bean = ScheduleFunBean("12/25 (四)", "天鷹")
        itemList.add(bean)

        bean = ScheduleFunBean("12/26 (五)", "雄鷹")
        itemList.add(bean)
        bean = ScheduleFunBean("12/27 (六)", "獵鷹")
        itemList.add(bean)
        bean = ScheduleFunBean("12/28 (日)", "天鷹")
        itemList.add(bean)

        bean = ScheduleFunBean("12/29 (一)", "雄鷹")
        itemList.add(bean)
        bean = ScheduleFunBean("12/30 (二)", "獵鷹")
        itemList.add(bean)
        bean = ScheduleFunBean("12/31 (三)", "天鷹")
        itemList.add(bean)

        personalScheduleListOnMonth.addAll(itemList)
    }*/

    /*fun setScheduleListByTeam(data: SelectTeamFunBean) {
        if (personalScheduleListOnMonth.isNotEmpty()) {
            if (data.teamType != TeamType.TEAM_ALL) {
                val scheduleListByTeam =
                    personalScheduleListOnMonth.filter { it.teamName == data.teamName }
                personalScheduleList.postValue(scheduleListByTeam.toMutableList())
            } else {
                personalScheduleList.postValue(personalScheduleListOnMonth)
            }
        }
    }*/

    fun getWingStarsSchedule(wingStarsNumber: String,selectMonth: String) {
        setIsLoading(true)
        API.shared?.api?.let {
            val params = HashMap<String, String>()
            params["member_number"] = wingStarsNumber
            params["ym"] = selectMonth.replace("/", "-")
            val observer = it.wsSchedules(params)
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    setIsLoading(false)
                    personalScheduleList.postValue(next.toMutableList())
                },
                { error ->
                    setIsLoading(false)
                    val msg = error.message.toString()
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