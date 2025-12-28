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
import com.wingstars.base.net.beans.CRMCouponQRCodeRequest
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException

class NotUsedViewModel : ViewModel() {

    var notUsedCouponsData = MutableLiveData<MutableList<CRMCouponsResponse>>()
    var couponQRCode = MutableLiveData<String>()
    private val compositeDisposable = CompositeDisposable()

    var isLoading = MutableLiveData<Boolean>()

    fun setIsLoading(isLoading: Boolean) {
        this.isLoading.postValue(isLoading)
    }

    // Coupon > 查询会员持有的赠品券
    fun getNotUsedCouponsData() {
        setIsLoading(true)
        API.shared?.api?.let {
            val id = MMKV.defaultMMKV().decodeString("crm_member_id")
            val usage_status = 0 // 0: 未使用
            val observer = it.crmCoupons("${NetBase.HOST_CRM}/api/v1/basic/member/${id}/coupons?usage_status=${usage_status}&size=100")

            val disposable = observer
                ?.subscribeOn(Schedulers.io())
                ?.unsubscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { next ->
                        Log.d("datacoupon", "$next")
                        notUsedCouponsData.postValue(next.data)
                        setIsLoading(false)
                    },
                    { error ->
                        handleError(error)
                    }
                )

            // Thêm vào quản lý
            if (disposable != null) {
                compositeDisposable.add(disposable)
            }
        }
    }

    fun crmCouponQRCode(couponCode: String) {
        if (!MMKV.defaultMMKV().decodeBool("isLogin")) return

        setIsLoading(true)
        API.shared?.api?.let { api ->
            val id = MMKV.defaultMMKV().decodeString("crm_member_id")
            val url = "${NetBase.HOST_CRM}/api/v1/basic/member/$id/coupons/qrcode/generate"

            val disposable = api.crmCouponQRCode(url, CRMCouponQRCodeRequest(couponCode))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { next ->
                        setIsLoading(false)
                        if (next.success && next.data != null) {
                            couponQRCode.postValue(next.data.qrcode)
                        }
                        Log.e("logcheckdata", "crmCouponQRCode ${next.data}")
                    },
                    { error -> handleError(error) }
                )
            compositeDisposable.add(disposable)
        }
    }

    private fun handleError(error: Throwable) {
        setIsLoading(false)
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
                e.printStackTrace()
            }
        }
        Log.e("API_ERROR", msg)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}