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
import com.wingstars.base.net.beans.CRMCouponQRCodeRequest
import com.wingstars.base.net.beans.CRMMemberDetailResponse
import com.wingstars.base.net.beans.CRMOTPCoupons
import com.wingstars.base.net.beans.CRMRedeemCouponRequest
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException

class ActivityDetailsExchangeViewModel : ViewModel() {


    private val compositeDisposable = CompositeDisposable()
    var isLoading = MutableLiveData<Boolean>()
    var points = MutableLiveData<String>()
    var messages = MutableLiveData<String>()
    var address = MutableLiveData<String>()
    var redeemSuccessfully = MutableLiveData<String>()
    var couponQRCode = MutableLiveData<String>()
    var memberCardsData = MutableLiveData<List<CRMMemberDetailResponse.MemberCard>>()
    var memberCards: MutableList<CRMMemberDetailResponse.MemberCard> = mutableListOf()
    val otpData = MutableLiveData<CRMOTPCoupons?>()
    var haveUsedCoupon = MutableLiveData<Boolean>()
    private var isLoop: Boolean = false

    fun setIsLoading(isLoading: Boolean) {
        this.isLoading.postValue(isLoading)
    }

    private fun handleError(error: Throwable) {
        setIsLoading(false)
        var msg = error.message.toString()
        if (error is HttpException) {
            try {
                val gson = Gson()
                val type = object : TypeToken<CRMBaseFailResponse>() {}.type
                val errorBody = error.response()?.errorBody()?.string()
                if (!errorBody.isNullOrEmpty()) {
                    val failResponse = gson.fromJson<CRMBaseFailResponse>(errorBody, type)
                    failResponse?.message?.let { msg = it }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        messages.postValue(msg)
    }

    fun findHaveUsedCouponsData(couponId: String) {
        API.shared?.api?.let { api ->
            val id = MMKV.defaultMMKV().decodeString("crm_member_id")
            val usageStatus = 2
            val url = "${NetBase.HOST_CRM}/api/v1/basic/member/${id}/coupons?usage_status=${usageStatus}&size=100"

            val disposable = api.crmCoupons(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { next ->
                        // Kiểm tra null safety cho data
                        val list = next.data ?: emptyList()
                        val exist = list.any { it.couponId == couponId }
                        if (isLoop) {
                            haveUsedCoupon.postValue(exist)
                        }
                    },
                    { error ->
                        handleError(error)
                        isLoop = false
                        haveUsedCoupon.postValue(false)
                    }
                )
            compositeDisposable.add(disposable)
        }
    }

    fun setLoop(bIsLooping: Boolean) {
        isLoop = bIsLooping
    }

    fun getLoop(): Boolean {
        return isLoop
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

    fun crmRedeemStoresSearch(codes: String) {
        if (!MMKV.defaultMMKV().decodeBool("isLogin")) return

        API.shared?.api?.let { api ->
            val url = "${NetBase.HOST_CRM}/api/v1/client/coupons/redeem-stores/search?codes=$codes"

            val disposable = api.crmRedeemStoresSearch(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { next ->
                        if (next.success) {
                            val list = next.data ?: emptyList()

                            // 2. Tối ưu String Concatenation bằng StringBuilder
                            val sb = StringBuilder()
                            for (i in list.indices) {
                                val data = list[i]
                                sb.append("${data.storeName}(${data.storeAddress})")
                                if (i < list.size - 1) {
                                    sb.append("\n")
                                }
                            }
                            address.postValue(sb.toString())
                        }
                    },
                    { error -> handleError(error) }
                )
            compositeDisposable.add(disposable)
        }
    }

    fun getOTPCoupons(couponId: String) {
        setIsLoading(true)
        API.shared?.api?.let { api ->
            val id = MMKV.defaultMMKV().decodeString("crm_member_id")
            val url = "${NetBase.HOST_CRM}/api/v1/basic/member/${id}/coupons/available/${couponId}"

            val disposable = api.crmOTPCoupons(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { next ->
                        setIsLoading(false)
                        // Log.e("logcheckdata", "productCouponsData ${next.data}")
                        otpData.postValue(next.data)
                    },
                    { error -> handleError(error) }
                )
            compositeDisposable.add(disposable)
        }
    }

    fun crmRedeemCoupon(couponId: String, otp: String) {
        setIsLoading(true)
        API.shared?.api?.let { api ->
            val id = MMKV.defaultMMKV().decodeString("crm_member_id")
            val url = "${NetBase.HOST_CRM}/api/v1/basic/member/${id}/points/redeem-coupon"

            val disposable = api.crmRedeemCoupon(url, CRMRedeemCouponRequest(couponId, otp))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { next ->
                        setIsLoading(false)
                        if (next.success) {
                            redeemSuccessfully.postValue(next.message)
                        }
                    },
                    { error -> handleError(error) }
                )
            compositeDisposable.add(disposable)
        }
    }

    fun getMemberPointFromDetailsData() {
        if (!MMKV.defaultMMKV().decodeBool("isLogin")) return

        API.shared?.api?.let { api ->
            val id = MMKV.defaultMMKV().decodeString("crm_member_id") ?: ""

            val disposable = api.crmMemberDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { next ->
                        if (next.success) {
                            memberCards.clear()
                            if (next.data.MemberCards != null) {
                                memberCards.addAll(next.data.MemberCards)
                                memberCardsData.postValue(next.data.MemberCards)
                            }
                            points.postValue(next.data.Points.toString())
                        }
                    },
                    { error ->
                        points.postValue("0")
                        handleError(error)
                    }
                )
            compositeDisposable.add(disposable)
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}