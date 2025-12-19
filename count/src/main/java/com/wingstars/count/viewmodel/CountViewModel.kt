package com.wingstars.count.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wingstars.base.net.API
import com.wingstars.base.net.NetBase
import com.wingstars.base.net.beans.EvtTaskResponse
import com.wingstars.count.dialog.SortMethod
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class CountViewModel: ViewModel() {
    var popularitylist = MutableLiveData<MutableList<Int>>()
    var isLoading = MutableLiveData<Boolean>()
    val taskList = MutableLiveData<List<EvtTaskResponse>>()
    enum class TaskListState { PENDING, STARTED, FAILED, SUCCESS }
    private var taskListData: ArrayList<EvtTaskResponse> = ArrayList()
    private var curTaskState: TaskListState = TaskListState.PENDING //init state
    private var currentSortMethod: SortMethod = SortMethod.SORT_DATE_NEW_TO_OLD
    private var taskListIsCompleted = true

    fun setIsLoading(isLoading: Boolean) {
        this.isLoading.postValue(isLoading)
    }

    public fun  getPopularitylist(){
        var arrayList = mutableListOf(1,2,3)
        popularitylist.postValue(arrayList)
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

                    if (taskListData.isEmpty()) {
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

//    private fun getEvtMemberTasks(encryptedIdentity: String, bRefreshUI: Boolean) {
//        //Event > 会员任务状态列表
//        API.shared?.api?.let {
//
//            // encryptedIdentity 的值通过 api crmGenQRCode 取得
//            //val encryptedIdentity = "RMafhFbPQedleQ0E6fk9P8gNEoXdwAjZTULb1bLk73Ute9axTtxxSAonuM2jJ3WaXsN4zlpq3SkFZUB8NlNVtNAmX1myKBeOBerbk56Uu+YTKlHNB+/0iCh9R+5wEV+HvRNU7/RU/DKZZf+jU2L88w=="
//            val observer = it.evtMemberTasks(encryptedIdentity)
//            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
//                AndroidSchedulers.mainThread()
//            )?.subscribe(
//                { next ->
//                    if (!next.isNullOrEmpty()) {
//                        next.forEach { tr ->
//                            val node = NetBase.taskListData.find { it.id == tr.id }
//                            if (node != null) {
//                                node.status = tr.personalEventTaskStatus
//                                node.isSendApiF = false
//                            }
//                        }
//
//                        checkInListData.clear()
//                        NetBase.taskListData.forEach {
//                            if (it.triggerType == "app" && !it.triggerTag.isNullOrEmpty() && it.triggerTag == "checkin") {
//                                checkInListData.add(it)
//                            }
//
//                        }
//
////                        if (bRefreshUI)
////                            sendBroadcast(Intent(BaseApplication.BROADCAST_TASK_REFRESH))
//                    }
//                    taskListIsCompleted = true
//                },
//                { error ->
//                    taskListIsCompleted = true
//                    error.message?.let { it1 ->
//                    }
//                }
//            )
//        }
//    }

    // Member > 赠送点数
//    fun setEvtReward(data: EvtTaskResponse, encryptedIdentity: String) {
//        API.shared?.api?.let {
//            Log.d("API", "[evtReward] next.statusF: ${encryptedIdentity}")
//            Log.d("API", "[evtReward] next.statusF: ${data.id}")
//            //encryptedIdentity 的值通过 api crmGenQRCode 取得
////            val encryptedIdentity = "RMafhFbPQedleQ0E6fk9P8gNEoXdwAjZTULb1bLk73Ute9axTtxxSAonuM2jJ3WaXsN4zlpq3SkFZUB8NlNVtNAmX1myKBeOBerbk56Uu+YTKlHNB+/0iCh9R+5wEV+HvRNU7/RU/DKZZf+jU2L88w=="
//            val observer = it.evtReward(
//                "${NetBase.HOST_EVENT}/api/v1/public/events/reward",
//                EvtCheckinRequest(encryptedIdentity, data.id)
//            )
//            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
//                AndroidSchedulers.mainThread()
//            )?.subscribe(
//                { next ->
//                    Log.d("API", "[evtReward] next.statusF: ${next.message}")
//
//
//                    if (next.statusF >= 0) {
//                        NetBase.shared()!!.refreshEvtTasks(true)
//                        println("point: ${next.point}, status: ${next.status}")
//                    } else {
//                        println("message: ${next.message}")
//                    }
//                    setIsLoading(false)
//                    data.isSendApiF = false
//                },
//                { error ->
//                    data.isSendApiF = false
//                    setIsLoading(false)
//                    error.message?.let { it1 ->
//                        Toast.makeText(NetBase.shared()!!, "$it1", Toast.LENGTH_LONG).show()
//                        Log.d("API", "[evtReward] next.statusF: ${it1}")
//                    }
//                }
//            )
//        }
//    }
}