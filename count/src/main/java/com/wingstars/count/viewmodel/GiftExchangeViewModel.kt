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
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException

class GiftExchangeViewModel : ViewModel() {

    var countWS = MutableLiveData<Int>()
    var productCouponsData = MutableLiveData<MutableList<CRMCouponsAvailableResponse>>()
    var searchActivityNewF = mutableListOf<CRMCouponsAvailableResponse>()
    val searchActivityData =
        MutableLiveData<List<CRMCouponsAvailableResponse>>(emptyList())

    var isLoading = MutableLiveData<Boolean>()
    var memberCards: ArrayList<String> = ArrayList()

    fun setIsLoading(isLoading: Boolean) {
        this.isLoading.postValue(isLoading)
    }

    fun getMemberPointFromDetailsData(showLoading: Boolean = false) {
        if (MMKV.defaultMMKV().decodeBool("isLogin")) {
            if (showLoading) {
                setIsLoading(true)
            }

            API.shared?.api?.let { api ->
                val id = MMKV.defaultMMKV().decodeString("crm_member_id") ?: ""
                val observer = api.crmMemberDetail(id)

                observer?.subscribeOn(Schedulers.io())
                    ?.unsubscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(
                        { next ->
                            if (showLoading) setIsLoading(false)

                            if (next.success) {
                                memberCards.clear()
                                val memberCardsList = next.data.MemberCards
                                if (!memberCardsList.isNullOrEmpty()) {
                                    for (card in memberCardsList) {
                                        val mtid = card.MemberTypeId?.trim()
                                        if (!mtid.isNullOrEmpty() && !memberCards.contains(mtid)) {
                                            memberCards.add(mtid)
                                        }
                                    }
                                }
                                countWS.postValue(next.data.Points)
                            } else {
                                countWS.postValue(0)
                            }
                        },
                        { error ->
                            if (showLoading) setIsLoading(false)
                            handleApiError(error)
                        }
                    )
            }
        }
    }

    fun setProductCouponsInfo(count: Int, curSortMethod: SortMethod) {
        var bHaveLoading = false
        if (count == -1) {
            bHaveLoading = true
            getMemberPointFromDetailsData(bHaveLoading)
        } else {
            countWS.postValue(count)
        }
        getProductCouponsAvailable(bHaveLoading, curSortMethod)
    }

    fun getProductCouponsAvailable(bHaveLoading: Boolean, curSortMethod: SortMethod) {
        if (!bHaveLoading) {
            setIsLoading(true)
        }

        API.shared?.api?.let { api ->
            val id = MMKV.defaultMMKV().decodeString("crm_member_id") ?: ""
            //Log.e("productCouponsData", "ID: $id")

            val couponType = 1 // 1：商品券，2：活动券
            val page = 1
            val size = 100
            val observer = api.crmCouponsAvailable(id, couponType, page, size)

            observer?.subscribeOn(Schedulers.io())
                ?.unsubscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { next ->
                        setIsLoading(false)
                        val data = next.data

                        // Debug Log
                         Log.d("GiftExchangeViewModel", "API Response: $data")

                        if (data != null) {
                            val sortedList = sortData(data, curSortMethod)
                            productCouponsData.postValue(sortedList.toMutableList())
                        } else {
                            productCouponsData.postValue(mutableListOf())
                        }
                    },
                    { error ->
                        productCouponsData.postValue(mutableListOf())
                        setIsLoading(false)
                        handleApiError(error)
                    }
                )
        }
    }

    fun searchData(keyword: String, curSortMethod: SortMethod) {
        searchActivityNewF.clear()

        val currentList = productCouponsData.value
        if (currentList.isNullOrEmpty()) {
            searchActivityData.postValue(mutableListOf())
            return
        }

        val sortedList = sortData(currentList, curSortMethod)

        productCouponsData.value = sortedList.toMutableList()
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

    private fun sortData(list: List<CRMCouponsAvailableResponse>, method: SortMethod): List<CRMCouponsAvailableResponse> {
        return when (method) {
            SortMethod.SORT_DATE_NEW_TO_OLD -> list.sortedByDescending { it.couponStartDate ?: "" }
            SortMethod.SORT_DATE_OLD_TO_NEW -> list.sortedBy { it.couponStartDate ?: "" }
            SortMethod.SORT_POINTS_HIGH_TO_LOW -> list.sortedByDescending { it.pointCost ?: 0 }
            SortMethod.SORT_POINTS_LOW_TO_HIGH -> list.sortedBy { it.pointCost ?: 0 }
            SortMethod.SORT_BY_BEEN_COMPLETED -> list.sortedByDescending { it.couponStartDate ?: "" }
        }
    }

    private fun handleApiError(error: Throwable) {
        var msg = error.message.toString()
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
                e.printStackTrace()
            }
        }
//        Log.e("GiftExchangeViewModel", "API Error: $msg")
    }
}