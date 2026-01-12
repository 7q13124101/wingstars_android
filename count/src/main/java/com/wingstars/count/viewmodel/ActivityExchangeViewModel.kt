package com.wingstars.count.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV
import com.wingstars.base.net.API
import com.wingstars.base.net.beans.CRMBaseFailResponse
import com.wingstars.base.net.beans.CRMCouponsAvailableResponse
import com.wingstars.count.dialog.SortMethod
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException

class ActivityExchangeViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val TAG = "ActivityExchangeVM"

    var activityData = MutableLiveData<MutableList<CRMCouponsAvailableResponse>>()
    var searchActivityNewF = mutableListOf<CRMCouponsAvailableResponse>()
    var searchActivityData = MutableLiveData<MutableList<CRMCouponsAvailableResponse>>()
    var isLoading = MutableLiveData<Boolean>()
    var points = MutableLiveData<String?>()
    var messages = MutableLiveData<String>()
    var memberCards: ArrayList<String> = ArrayList()
    val activities = MutableLiveData<List<CRMCouponsAvailableResponse>>()
    val errorMessage = MutableLiveData<String>()

//    private val idToCode = mutableMapOf(
//        "366a8eb1-ff7a-45ac-9a69-50321dfcd84f" to "A004",
//        "7a3b2511-3f7a-4607-9c7b-32257becf20e" to "A003",
//        "e412da17-7ad2-4049-a54d-3b09d6d3d215" to "A005",
//        "ceccde97-fa1e-4d96-bf74-9f6ad148751a" to "A002",
//        "43e223a4-18e0-4ad4-baa9-f4a4dcd97e83" to "A006",
//        "c566304d-06d1-4872-8b03-74cc788b3539" to "A001",
//    )

//    private val codeToId = idToCode.entries.associate { (id, code) -> code to id }.toMutableMap()

    fun setIsLoading(isLoading: Boolean) {
        this.isLoading.postValue(isLoading)
    }

    fun activityListData(
        curSortMethod: SortMethod,
        couponType: Int = 2,
        page: Int = 1,
        size: Int = 100,
        showLoading: Boolean = true
    ) {
        if (!MMKV.defaultMMKV().decodeBool("isLogin")) {
            activityData.postValue(mutableListOf())
            return
        }

        if (showLoading) {
            setIsLoading(true)
        }
        API.shared?.api?.let { api ->
            val id = MMKV.defaultMMKV().decodeString("crm_member_id") ?: ""

            if (id.isEmpty()) {
                if (showLoading) setIsLoading(false)
                activityData.postValue(mutableListOf())
                return@let
            }

            val observer = api.crmCouponsAvailable(id, couponType, page, size)

            val disposable = observer?.subscribeOn(Schedulers.io())
                ?.unsubscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { next ->
                        if (showLoading) {
                            setIsLoading(false)
                        }

                        Log.d("TAG", "Activity list response: ${Gson().toJson(next)}")

                        val data = next.data
                        if (data != null) {
                            val sortedList = sortData(data, curSortMethod)
                            activityData.value = sortedList.toMutableList()
                            searchData("", curSortMethod)

                        } else {
                            activityData.value = mutableListOf()
                            searchActivityData.value = mutableListOf()
                        }
                    },
                    { error ->
                        if (showLoading) {
                            setIsLoading(false)
                        }
                        activityData.postValue(mutableListOf())
                        handleApiError(error)
                    }
                )

            if (disposable != null) {
                compositeDisposable.add(disposable)
            }
        }
    }

    fun searchData(keyword: String, curSortMethod: SortMethod) {
        searchActivityNewF.clear()

        val currentList = activityData.value
        if (currentList.isNullOrEmpty()) {
            searchActivityData.postValue(mutableListOf())
            return
        }
        val sortedList = sortData(currentList, curSortMethod)
        activityData.value = sortedList.toMutableList()
        if (keyword.isEmpty()) {
            searchActivityNewF.addAll(sortedList)
        } else {
            val filtered = sortedList.filter {
                it.couponName?.contains(keyword, ignoreCase = true) == true
            }
            searchActivityNewF.addAll(filtered)
        }

        searchActivityData.postValue(searchActivityNewF)
    }


    fun getMemberPointFromDetailsData(showLoading: Boolean = false) {
        if (!MMKV.defaultMMKV().decodeBool("isLogin")) {
            points.postValue("0")
            return
        }
        if (showLoading) {
            setIsLoading(true)
        }
        API.shared?.api?.let { api ->
            val id = MMKV.defaultMMKV().decodeString("crm_member_id") ?: ""

            if (id.isEmpty()) {
                if (showLoading) setIsLoading(false)
                points.postValue("0")
                return@let
            }

            val observer = api.crmMemberDetail(id)

            val disposable = observer?.subscribeOn(Schedulers.io())
                ?.unsubscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { next ->
                        if (showLoading) {
                            setIsLoading(false)
                        }
                        Log.d("TAG", "Member point response: ${Gson().toJson(next)}")

                        if (next.success && next.data != null) {
                            memberCards.clear()
                            val apiMemberCards = next.data.MemberCards

                            if (!apiMemberCards.isNullOrEmpty()) {
                                for (card in apiMemberCards) {
                                    val mtid = card.MemberTypeId?.trim()
                                    if (!mtid.isNullOrEmpty()) {
                                        if (!memberCards.contains(mtid)) {
                                            memberCards.add(mtid)
                                        }

//                                        val code = ensureAndGetCodeFromEither(mtid)
//                                        if (code != null && !memberCards.contains(code)) {
//                                            memberCards.add(code)
//                                        }
                                    }
                                }
                            }

                            // Update points
                            val pointsValue = next.data.Points?.toString() ?: "0"
                            points.postValue(pointsValue)
                        }
                    },
                    { error ->
                        if (showLoading) {
                            setIsLoading(false)
                        }
                        handleApiError(error)
                    }
                )

            if (disposable != null) {
                compositeDisposable.add(disposable)
            }
        }
    }


    fun setPointsCount(count: String?) {
        if (count == "-1" || count == null) {
            getMemberPointFromDetailsData(showLoading = true)
        } else {
            points.postValue(count)
        }
    }

    fun setActivityInfo(count: String?, curSortMethod: SortMethod, showLoading: Boolean = false) {
        if (count == "-1" || count == null) {
            getMemberPointFromDetailsData(showLoading)
        } else {
            points.postValue(count)
        }
        activityListData(curSortMethod, showLoading = !showLoading)
    }

    private fun sortData(
        list: List<CRMCouponsAvailableResponse>,
        method: SortMethod
    ): List<CRMCouponsAvailableResponse> {
        return when (method) {
            SortMethod.SORT_DATE_NEW_TO_OLD -> list.sortedByDescending { it.redeemStartAt ?: "" }
            SortMethod.SORT_DATE_OLD_TO_NEW -> list.sortedBy { it.redeemStartAt ?: "" }
            SortMethod.SORT_POINTS_HIGH_TO_LOW -> list.sortedByDescending { it.pointCost ?: 0 }
            SortMethod.SORT_POINTS_LOW_TO_HIGH -> list.sortedBy { it.pointCost ?: 0 }
            SortMethod.SORT_BY_BEEN_COMPLETED -> list.sortedByDescending { it.redeemStartAt ?: "" }
        }
    }

    private fun handleApiError(error: Throwable) {
        var msg = error.message ?: "Unknown error"

        if (error is HttpException) {
            try {
                val gson = Gson()
                val type = object : TypeToken<CRMBaseFailResponse>() {}.type
                val errorBody = error.response()?.errorBody()?.string()
                val failResponse = gson.fromJson<CRMBaseFailResponse>(errorBody, type)

                failResponse?.message?.let {
                    msg = it
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing error response: ${e.message}")
            }
        }

        Log.e(TAG, "API Error: $msg")
        messages.postValue(msg)
    }

//    private fun ensureAndGetCodeFromEither(input: String): String? {
//        val s = input.trim()
//        return when {
//            idToCode.containsKey(s) -> {
//                val code = idToCode.getValue(s)
//                codeToId.putIfAbsent(code, s)
//                code
//            }
//            codeToId.containsKey(s) -> {
//                val id = codeToId.getValue(s)
//                idToCode.putIfAbsent(id, s)
//                s
//            }
//            else -> null
//        }
//    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}