package com.wingstars.count.viewmodel

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tencent.mmkv.MMKV
import com.wingstars.base.net.API
import com.wingstars.base.net.NetBase
import com.wingstars.base.net.NetBase.refreshEvtTasks
import com.wingstars.base.net.NetBase.sendBroadcast
import com.wingstars.base.net.NetworkMonitorNew
import com.wingstars.base.net.beans.CRMGenQRCodeRequest
import com.wingstars.base.net.beans.EvtCheckinRequest
import com.wingstars.base.net.beans.EvtTaskResponse
import com.wingstars.count.dialog.SortMethod
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class CountViewModel: ViewModel() {
    var isLoading = MutableLiveData<Boolean>()
    val taskList = MutableLiveData<List<EvtTaskResponse>>()
    var eventTaskType = "limited"
    var points = MutableLiveData<String>()
    var eventGroupNewF: ArrayList<EvtTaskResponse> = ArrayList()
    enum class TaskListState { PENDING, STARTED, FAILED, SUCCESS }
    private var taskListData: ArrayList<EvtTaskResponse> = ArrayList()
    private var curTaskState: TaskListState = TaskListState.PENDING //init state
    private var currentSortMethod: SortMethod = SortMethod.SORT_DATE_NEW_TO_OLD
    private var taskListIsCompleted = true
    private var checkInListData: ArrayList<EvtTaskResponse> = ArrayList()

    fun setIsLoading(isLoading: Boolean) {
        this.isLoading.postValue(isLoading)
    }

    interface LoadData {
        fun loadDatas(list: MutableList<EvtTaskResponse>)
    }

    fun loadMore(eventType: String, netBase: NetBase): ArrayList<EvtTaskResponse> {
        eventGroupNewF.clear()

        if (NetworkMonitorNew.getInstance(netBase).currentNetworkState.isConnected) {
            val filter =getTaskListData()
                .filter { it.eventType.equals(eventType, ignoreCase = true) }

            if (filter.size > 4) {
                val subList = filter.subList(4, filter.size)
                eventGroupNewF.addAll(subList)
            }
        }
        return eventGroupNewF
    }

    fun loadEventData(eventType: String, netBase: NetBase, bShowAll:Boolean,loadData: LoadData) {
        eventTaskType = eventType
        var eventGroupF = mutableListOf<EvtTaskResponse>()

        if (NetworkMonitorNew.getInstance(netBase).currentNetworkState.isConnected) {
            val filter =getTaskListData()
                .filter { it.eventType.equals(eventType, ignoreCase = true) }
            if( bShowAll )
                eventGroupF.addAll(filter)
            else
                eventGroupF.addAll(filter.take(4))
            //   countEventData.postValue(eventGroupF)
            loadData.loadDatas(eventGroupF)
        } else {
            // countEventData.postValue(eventGroupF)
            loadData.loadDatas(eventGroupF)
        }
    }

    fun getTaskListData(): ArrayList<EvtTaskResponse> {
        return taskListData
    }


    fun sortTaskListData(curSortMethod: SortMethod) {
        if (taskListData.isEmpty()) return

        currentSortMethod = curSortMethod
        curTaskState = TaskListState.STARTED

        val tdSorted = when (curSortMethod) {
            SortMethod.SORT_DATE_NEW_TO_OLD -> taskListData.sortedByDescending { tr -> tr.startDate }
            SortMethod.SORT_DATE_OLD_TO_NEW -> taskListData.sortedBy { tr -> tr.startDate }
            SortMethod.SORT_POINTS_HIGH_TO_LOW -> taskListData.sortedByDescending { tr -> tr.point }
            SortMethod.SORT_POINTS_LOW_TO_HIGH -> taskListData.sortedBy { tr -> tr.point }
            SortMethod.SORT_BY_BEEN_COMPLETED -> taskListData.sortedWith(compareBy<EvtTaskResponse> { tr -> tr.statusSortF }.thenByDescending { tr -> tr.startDate })
        }

        taskListData.clear()
        taskListData.addAll(tdSorted)
        curTaskState = TaskListState.SUCCESS
        taskList.value = taskListData
    }

    fun getEvtTasks() {
        API.shared?.api?.let {
            val observer = it.evtTasks()
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next -> //排序
                    val tdSorted = when(currentSortMethod) {
                        SortMethod.SORT_DATE_NEW_TO_OLD -> next.sortedByDescending { tr -> tr.startDate }   //日期新到旧
                        SortMethod.SORT_DATE_OLD_TO_NEW -> next.sortedBy { tr -> tr.startDate }             //日期旧到新
                        SortMethod.SORT_POINTS_HIGH_TO_LOW -> next.sortedByDescending { tr -> tr.point }    //点数高到低
                        SortMethod.SORT_POINTS_LOW_TO_HIGH -> next.sortedBy { tr -> tr.point }              //点数低到高
                        SortMethod.SORT_BY_BEEN_COMPLETED -> taskListData.sortedWith(compareBy<EvtTaskResponse> { tr -> tr.statusSortF }.thenByDescending { tr -> tr.startDate })  //按已完成排序
                    else -> next
                    }
                    taskListData.clear()
                    taskListData.addAll(tdSorted)
//                    countTitleAdapter.setList(taskListData)
                    Log.d("getEvtTasksData", tdSorted.toString())

                    taskList.value = taskListData

                    if (taskListData.isNotEmpty()) {
                        if (MMKV.defaultMMKV().decodeBool("isLogin")) {
                            refreshEvtTasks(true)
                        } else {
                            taskListIsCompleted = true
                            //没有登录状态下，也需要刷新原始任务列表
                            sendBroadcast(Intent(NetBase.BROADCAST_TASK_REFRESH))
                        }
                    } else {
                        taskListIsCompleted = true
                    }
                },
                { error ->
                    taskListIsCompleted = true
                    error.message?.let { it1 ->
                    }
                }
            )
        }
    }

    private fun getEvtMemberTasks(encryptedIdentity: String, bRefreshUI: Boolean) {
        //Event > 会员任务状态列表
        API.shared?.api?.let {

            // encryptedIdentity 的值通过 api crmGenQRCode 取得
            //val encryptedIdentity = "RMafhFbPQedleQ0E6fk9P8gNEoXdwAjZTULb1bLk73Ute9axTtxxSAonuM2jJ3WaXsN4zlpq3SkFZUB8NlNVtNAmX1myKBeOBerbk56Uu+YTKlHNB+/0iCh9R+5wEV+HvRNU7/RU/DKZZf+jU2L88w=="
            val observer = it.evtMemberTasks(encryptedIdentity)
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    if (!next.isNullOrEmpty()) {
                        next.forEach { tr ->
                            val node = taskListData.find { it.id == tr.id }
                            if (node != null) {
                                node.status = tr.personalEventTaskStatus
                                node.isSendApiF = false
                            }
                        }

                        checkInListData.clear()
                        taskListData.forEach {
                            if (it.triggerType == "app" && !it.triggerTag.isNullOrEmpty() && it.triggerTag == "checkin") {
                                checkInListData.add(it)
                            }

                        }

                        if (bRefreshUI)
                            sendBroadcast(Intent(NetBase.BROADCAST_TASK_REFRESH))
                    }
                    taskListIsCompleted = true
                },
                { error ->
                    taskListIsCompleted = true
                    error.message?.let { it1 ->
                    }
                }
            )
        }
    }

    // Member > 查询会员详细资料=>點數
    fun getMemberPointFromDetailsData(showLoading: Boolean = true) {
        if (MMKV.defaultMMKV().decodeBool("isLogin")) {
            if (showLoading) {
                setIsLoading(true)
            }
            API.shared?.api?.let {
                val id = MMKV.defaultMMKV().decodeString("crm_member_id")
                val observer =
                    it.crmMemberDetail()
                observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                    AndroidSchedulers.mainThread()
                )?.subscribe(
                    { next ->
                        if (showLoading) {
                            setIsLoading(false)
                        }
                        if (next.success) {
                            points.postValue(next.data.Points.toString())
                        } else {
                            // points.postValue("0")
                            //Toast.makeText(BaseApplication.shared()!!, next.message, Toast.LENGTH_LONG).show()
                        }
                    },
                    { error ->
                        if (showLoading) {
                            setIsLoading(false)
                        }
                        //  points.postValue("0")
                        var msg = error.message.toString()
//                        if (error is HttpException) {
//                            try {
//                                val gson = Gson()
//                                val type = object : TypeToken<CRMBaseFailResponse>() {}.type
//                                val failResponse = gson.fromJson<CRMBaseFailResponse>(
//                                    error.response()?.errorBody()?.string(), type
//                                )
//                                failResponse?.message?.let {
//                                    msg = it
//                                }
//                            } catch (e: Exception) {
//
//                            }
//                        }

                        msg.let { it1 ->
                            //Toast.makeText(BaseApplication.shared()!!, "$it1", Toast.LENGTH_LONG).show()
                        }
                    }
                )
            }
        } else {
            //points.postValue("0")
        }
    }

//     Member > 赠送点数
    fun setEvtReward(data: EvtTaskResponse, encryptedIdentity: String) {
        API.shared?.api?.let {
            Log.d("API", "[evtReward] next.statusF: ${encryptedIdentity}")
            Log.d("API", "[evtReward] next.statusF: ${data.id}")
            //encryptedIdentity 的值通过 api crmGenQRCode 取得
//            val encryptedIdentity = "RMafhFbPQedleQ0E6fk9P8gNEoXdwAjZTULb1bLk73Ute9axTtxxSAonuM2jJ3WaXsN4zlpq3SkFZUB8NlNVtNAmX1myKBeOBerbk56Uu+YTKlHNB+/0iCh9R+5wEV+HvRNU7/RU/DKZZf+jU2L88w=="
            val observer = it.evtReward(
                EvtCheckinRequest(encryptedIdentity, data.id)
            )
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    Log.d("API", "[evtReward] next.statusF: ${next.message}")


                    if (next.statusF >= 0) {
//                        NetBase.shared()!!.refreshEvtTasks(true)
                        println("point: ${next.point}, status: ${next.status}")
                    } else {
                        println("message: ${next.message}")
                    }
                    setIsLoading(false)
                    data.isSendApiF = false
                },
                { error ->
                    data.isSendApiF = false
                    setIsLoading(false)
                    error.message?.let { it1 ->
//                        Toast.makeText(NetBase.shared()!!, "$it1", Toast.LENGTH_LONG).show()
                        Log.d("API", "[evtReward] next.statusF: ${it1}")
                    }
                }
            )
        }
    }

    // Member > 赠送点数
    fun grantMemberPoint(data: EvtTaskResponse) {
        if (MMKV.defaultMMKV().decodeBool("isLogin")) {
            setIsLoading(true)
            val id = MMKV.defaultMMKV().decodeString("crm_member_id")
            val phone = MMKV.defaultMMKV().decodeString("member_phone")
            API?.shared?.api?.let {
                //Member > 会员QRCode
                val observer =
                    it.crmGenQRCode(
                        "${NetBase.HOST_CRM}/api/v1/basic/member/${id}/gen-qrcode",
                        phone?.let { it1 -> CRMGenQRCodeRequest(it1) })
                observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                    AndroidSchedulers.mainThread()
                )?.subscribe(
                    { next ->
                        if (next.success && next.data != null) {
                            setEvtReward(data, next.data.MEMQRCODE)
                            Log.d("Error", next.data.toString())
                        } else {
                            setIsLoading(false)
                            data.isSendApiF = false
                            //Toast.makeText(BaseApplication.shared()!!,next.message, Toast.LENGTH_LONG).show()
                        }
                    },
                    { error ->
                        data.isSendApiF = false
                        var msg = error.message.toString()
//                        if (error is HttpException) {
//                            try {
//                                val gson = Gson()
//                                val type = object : TypeToken<CRMBaseFailResponse>() {}.type
//                                val failResponse = gson.fromJson<CRMBaseFailResponse>(
//                                    error.response()?.errorBody()?.string(), type
//                                )
//                                if (failResponse != null) {
//                                    failResponse.message?.let {
//                                        msg = it
//                                    }
//                                }
//                            } catch (e: Exception) {
//
//                            }
//                        }

                        msg.let { it1 ->
                            setIsLoading(false)
//                            Toast.makeText(NetBase.shared()!!,"$it1", Toast.LENGTH_LONG).show()
                            Log.d("Error", it1.toString())
                        }
                    }
                )
            }
        }
    }
}