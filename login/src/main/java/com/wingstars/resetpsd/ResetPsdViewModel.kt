package com.wingstars.resetpsd

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wingstars.base.net.API
import com.wingstars.base.net.beans.CRMBaseFailResponse
import com.wingstars.base.net.beans.CRMForgotPasswordRequest
import com.wingstars.base.net.beans.CRMResetPasswordRequest
import com.wingstars.base.net.beans.CRMSMSRequest

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException
import kotlin.let
import kotlin.toString

class ResetPsdViewModel : ViewModel() {
    var inputTag = MutableLiveData<String>()
    var isLoading = MutableLiveData<Boolean>()

    init {
        inputTag.postValue("inputCode")
    }

    private var navigator: ResetPsdNavigator? = null
    fun setNavigator(navigator: ResetPsdNavigator) {
        this.navigator = navigator
    }

    fun getResetPsdPhoneCode(phone: String) {
        isLoading.postValue(true)

        API?.shared?.api?.let { api ->
            // Bước 1: Kiểm tra đăng ký (crmSignUpCheck)
            val observer = api.crmSignUpCheck(phone)
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    // Log kết quả check
                    android.util.Log.e("API_DEBUG", "signUpCheck Success: ${Gson().toJson(next)}")

                    if (next.success) {
                        // Bước 2: Nếu Check thành công -> Gọi API gửi OTP
                        sendOtpInternal(phone)
                    } else {
                        isLoading.postValue(false)
                        android.util.Log.e("API_DEBUG", "signUpCheck Failed: ${next.message}")

                        navigator?.showToast(next.message ?: "尚未註冊")
                    }
                },
                { error ->
                    isLoading.postValue(false)
                    handleError(error, "signUpCheck")
                }
            )
        }
    }

    private fun sendOtpInternal(phone: String) {
        API?.shared?.api?.let { api ->
            val observer = api.crmSMS(CRMSMSRequest(phone, "forgotPassword"))
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    isLoading.postValue(false)
                    android.util.Log.e("API_DEBUG", "resetPsdOtp Success: ${Gson().toJson(next)}")

                    if (next.success) {
                        navigator!!.getPhoneCodeSuccess()
                    } else {
                        android.util.Log.e("API_DEBUG", "resetPsdOtp Failed logic: ${next.message}")
                        navigator?.showToast(next.message)
                    }
                },
                { error ->
                    isLoading.postValue(false)
                    handleError(error, "resetPsdOtp")
                }
            )
        }
    }

    fun resetPsd(request: CRMForgotPasswordRequest) {
        isLoading.postValue(true)
        android.util.Log.e("API_DEBUG", "resetPsd Request: ${Gson().toJson(request)}")

        API?.shared?.api?.let {
            val observer = it.crmForgotPassword(request)
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    isLoading.postValue(false)
                    android.util.Log.e("API_DEBUG", "resetPsd Success: ${Gson().toJson(next)}")

                    if (next.success) {
                        navigator!!.resetPsdSuccess()
                    } else {
                        android.util.Log.e("API_DEBUG", "resetPsd Failed logic: ${next.message}")
                        navigator?.showToast(next.message)
                    }
                },
                { error ->
                    isLoading.postValue(false)
                    handleError(error, "resetPsd")
                }
            )
        }
    }

    private fun handleError(error: Throwable, tag: String) {
        var msg = error.message.toString()
        if (error is HttpException) {
            try {
                val errorBody = error.response()?.errorBody()?.string()
                android.util.Log.e("API_DEBUG", "$tag Error Body: $errorBody")
                val gson = Gson()
                val type = object : TypeToken<CRMBaseFailResponse>() {}.type
                val failResponse = gson.fromJson<CRMBaseFailResponse>(errorBody, type)
                if (failResponse != null) {
                    failResponse.message?.let { msg = it }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        android.util.Log.e("API_DEBUG", "$tag Error Final Msg: $msg")
        navigator?.showToast(msg)
    }
}