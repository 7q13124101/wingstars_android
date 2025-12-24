package com.wingstars.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wingstars.base.net.API
import com.wingstars.base.net.beans.CRMBaseFailResponse
import com.wingstars.base.net.beans.CRMSMSRequest
import com.wingstars.base.net.beans.CRMSignUpRequest
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException

class RegisterViewModel : ViewModel() {

    var isLoading = MutableLiveData<Boolean>()

    var message = MutableLiveData<String>()

    private var navigator: RegisterNavigator? = null
    fun setNavigator(navigator: RegisterNavigator){
        this.navigator = navigator
    }


    fun getRegisterPhoneCode(phone: String){
        isLoading.postValue(true)

        API?.shared?.api?.let {
            //Client > 手机SMS发送OTP验证
            val observer = it.crmSMS(CRMSMSRequest(phone, "signUp"))
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread())?.subscribe(
                { next ->
                    isLoading.postValue(false)

                    if(next.success) {
                        navigator!!.getPhoneCodeSuccess()
                    }else{
                        //ToastUtil.showLongToast(BaseApplication.shared()!!,next.message)
                        message.postValue(next.message)
                    }
                },
                { error ->
                    isLoading.postValue(false)

                    var msg = error.message.toString()

                    if(error is HttpException){
                        try{
                            val gson = Gson()
                            val type = object : TypeToken<CRMBaseFailResponse>() {}.type
                            val failResponse = gson.fromJson<CRMBaseFailResponse>(error.response()?.errorBody()?.string(), type)
                            if(failResponse != null){
                                failResponse.message?.let {
                                    msg = it
                                }
                            }
                        }catch (e: Exception){

                        }
                    }
                    message.postValue("$msg")

                }
            )
        }
    }

    fun checkPhone(request: CRMSignUpRequest){
        isLoading.postValue(true)

        API?.shared?.api?.let {
            //Client > 手机SMS发送OTP验证
            val observer = it.crmSignUpCheck( request.phone)
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread())?.subscribe(
                { next ->
                    if(next.success) {
                        register(request)
                    }else{
                        isLoading.postValue(false)
                        message.postValue(next.message)

                    }
                },
                { error ->
                    isLoading.postValue(false)

                    var msg = error.message.toString()

                    if(error is HttpException){
                        try{
                            val gson = Gson()
                            val type = object : TypeToken<CRMBaseFailResponse>() {}.type
                            val failResponse = gson.fromJson<CRMBaseFailResponse>(error.response()?.errorBody()?.string(), type)
                            if(failResponse != null){
                                failResponse.message?.let {
                                    msg = it
                                }
                            }
                        }catch (e: Exception){

                        }
                    }

                    message.postValue("$msg")
                }
            )
        }
    }

    fun register(request: CRMSignUpRequest){

        API?.shared?.api?.let {
            //Client > 手机SMS发送OTP验证
            val observer = it.crmSignUp( request)
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread())?.subscribe(
                { next ->
                    isLoading.postValue(false)

                    if(next.success) {
                        navigator!!.registerSuccess()
                    }else{
                       // ToastUtil.showLongToast(BaseApplication.shared()!!,next.message)
                        message.postValue(next.message)
                    }
                },
                { error ->
                    isLoading.postValue(false)

                    var msg = error.message.toString()

                    if(error is HttpException){
                        try{
                            val gson = Gson()
                            val type = object : TypeToken<CRMBaseFailResponse>() {}.type
                            val failResponse = gson.fromJson<CRMBaseFailResponse>(error.response()?.errorBody()?.string(), type)
                            if(failResponse != null){
                                failResponse.message?.let {
                                    msg = it
                                }
                            }
                        }catch (e: Exception){

                        }
                    }
                    message.postValue("$msg")
                    /*msg.let { it1 ->
                        ToastUtil.showLongToast(BaseApplication.shared()!!,"${MessageProcessUtils.getMessage(it1)}")
                    }*/
                }
            )
        }
    }
}