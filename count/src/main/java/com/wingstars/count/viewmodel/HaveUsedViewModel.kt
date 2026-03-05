package com.wingstars.count.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV
import com.wingstars.base.net.API
import com.wingstars.base.net.NetBase
import com.wingstars.base.net.beans.CRMBaseFailResponse
import com.wingstars.base.net.beans.CRMCouponsResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException
import kotlin.math.log

class HaveUsedViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    var haveUsedCouponsData = MutableLiveData<MutableList<CRMCouponsResponse>>()
    var isLoading = MutableLiveData<Boolean>()
    fun setIsLoading(isLoading: Boolean) {
        this.isLoading.postValue(isLoading)
    }

    //Coupon > 查询会员持有的赠品券
    fun getHaveUsedCouponsData() {
        setIsLoading(true)
        API.shared?.api?.let {
            val id = MMKV.defaultMMKV().decodeString("crm_member_id")
            val usage_status = 2 //使用者贈品券使用狀態 (0: 未使用, 1: 已鎖定, 2: 已使用, 3: 已過期)
            val observer = it.crmCoupons("${NetBase.HOST_CRM}/api/v1/basic/member/${id}/coupons?usage_status=${usage_status}&size=100")
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
//                    Log.d("test api",next.toString())
                    haveUsedCouponsData.postValue(next.data)
                    setIsLoading(false)
                },
                { error ->
                    var msg = error.message.toString()
                    if (error is HttpException) {
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
                    }

                    msg.let { it1 ->
                        setIsLoading(false)
                        //Toast.makeText(BaseApplication.shared()!!, "$it1", Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
    }

}