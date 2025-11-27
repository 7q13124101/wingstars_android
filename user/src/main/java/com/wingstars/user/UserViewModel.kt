package com.wingstars.user

import android.annotation.SuppressLint
import retrofit2.HttpException
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV
import com.wingstars.user.net.API
import com.wingstars.user.net.beans.CRMBaseFailResponse
import com.wingstars.user.net.beans.CRMExtraData
import com.wingstars.user.net.beans.CRMHashKey
import com.wingstars.user.net.beans.CRMMemberDetailResponse
import com.wingstars.user.net.beans.NSInfoRequest
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.Date

class UserViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    var memberDetailData = MutableLiveData<CRMMemberDetailResponse>()
    var crmExtraDataData = MutableLiveData<CRMExtraData>()

    var totalInStore = MutableLiveData<Int>()//現場消費
    var totalMall = MutableLiveData<Int>()//商城消費

    var isLoading = MutableLiveData<Boolean>()
    fun setIsLoading(isLoading: Boolean) {
        this.isLoading.postValue(isLoading)
    }

    // Member > 查询会员详细资料
    fun getMembershipDetailsData() {
        API.shared?.api?.let {
            val id = MMKV.defaultMMKV().decodeString("crm_member_id")
            val observer =
                it.crmMemberDetail("${BaseApplication.HOST_CRM}/api/v1/basic/member/${id}")
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    if (next.success) {
                        memberDetailData.postValue(next.data)
                    } else {
                        //Toast.makeText(BaseApplication.shared()!!, next.message, Toast.LENGTH_LONG).show()
                    }
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
                        //Toast.makeText(BaseApplication.shared()!!, "$it1", Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
    }

    // Member > 現場消費
    fun getCrmTotalSpentData() {
        setIsLoading(true)
        API.shared?.api?.let {
            val id = MMKV.defaultMMKV().decodeString("crm_member_id")
            val observer = it.crmTotalSpent("${BaseApplication.HOST_CRM}/api/v1/basic/member/${id}/total-spent")
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread())?.subscribe(
                { next ->
                    next.data?.let { rd ->
                        totalInStore.postValue(rd.totalSpent)
                    }
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
                        //Toast.makeText(BaseApplication.shared()!!, "$it1", Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
    }
    // Member >  查询会员联络资料
    fun getMemberContactData() {
        API.shared?.api?.let {
            val id = MMKV.defaultMMKV().decodeString("crm_member_id")
            val observer =
                it.crmGetMemberContact("${BaseApplication.HOST_CRM}/api/v1/basic/member/${id}/contact")
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    if (next.success) {
                        crmExtraDataData.postValue(next.data.ExtraData)
                        if (next.data.NewsoftExtraData.email.isNullOrEmpty()) {
                            setIsLoading(false)
                            totalMall.postValue(0)//request: 查询email是空，默认商城消費是0
                        } else {
                            getCustomerId(next.data.NewsoftExtraData.email) //"sapido@gmail.com"
                        }
                    } else {
                        setIsLoading(false)
                        //Toast.makeText(BaseApplication.shared()!!, next.message, Toast.LENGTH_LONG).show()
                    }
                },
                { error ->
                    var msg = error.message.toString()
                    if (error is HttpException) {
                        try{
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
                        }catch (e: Exception){

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

    //雄鹰 > 查詢customer_id
    @SuppressLint("CheckResult")
    fun getCustomerId(user_email: String) {
        API.shared?.api?.let {
            var customerId = 0
            val key = CRMHashKey.decrypt(BaseApplication.HAWKS_CONSUMER_KEY_ENC)
            val secret = CRMHashKey.decrypt(BaseApplication.HAWKS_CONSUMER_SECRET_ENC)
            val observer_customer = it.hksCustomers(
                "${BaseApplication.HOST_HAWKS}/wp-json/wc/v3/customers",
                user_email,
                key,
                secret
            )
            observer_customer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())
                ?.observeOn(
                    AndroidSchedulers.mainThread()
                )?.subscribe(
                    { next ->
                        if (next.isNotEmpty()) {
                            customerId = next[0].id
                            getHksOrders(customerId)
                        } else {
                            setIsLoading(false)
                        }
                    },
                    { error ->
                        setIsLoading(false)
                        error.message?.let { it1 ->
                            //Toast.makeText(BaseApplication.shared()!!, "$it1", Toast.LENGTH_LONG).show()
                        }
                    }
                )
        }
    }

    //雄鹰 > 消费记录>商城消費
    fun getHksOrders(customerId: Int) {
        API.shared?.api?.let {
            val observer = it.hksOrders("${BaseApplication.HOST_HAWKS}/wp-json/wc/v3/orders", customerId, 1, 100, "completed")
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    var subTotalInMall=0
                    for (pd in next) {
                        subTotalInMall += pd.total.toInt()
                    }
                    totalMall.postValue(subTotalInMall)
                    setIsLoading(false)
                },
                { error ->
                    setIsLoading(false)
                    error.message?.let { it1 ->
                        //Toast.makeText(BaseApplication.shared()!!, "$it1", Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
    }

    fun getMemberInfo() {
        val device_id = MMKV.defaultMMKV().decodeString("device_id")
        val fcm_token = MMKV.defaultMMKV().decodeString("FCM_Token")

        API?.shared?.api?.let {
            //Member > 查询会员联络资料
            val id = MMKV.defaultMMKV().decodeString("crm_member_id")
            val observer =
                it.crmGetMemberContact("${BaseApplication.HOST_CRM}/api/v1/basic/member/${id}/contact")
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())
                ?.observeOn(
                    AndroidSchedulers.mainThread()
                )?.subscribe(
                    { next ->
                        if (next.success) {
                            updateMemberInfo(device_id, fcm_token, next.data.Name)
                        }
                    },
                    { error ->
                        error.message?.let { it1 ->

                        }
                    }
                )
        }
    }

    private fun updateMemberInfo(
        device_id: String?,
        fcm_token: String?,
        userName: String?
    ) {

        val request = NSInfoRequest(
            device_id,
            fcm_token,
            MMKV.defaultMMKV().decodeInt("isPush"),
            1,
            1,
            crmMemberToken = MMKV.defaultMMKV().decodeString("crm_member_access_token")!!,
            userName = userName,
            loginTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(Date()),
            crmMemberId = MMKV.defaultMMKV().decodeString("crm_member_id")!!
        )

        API.shared?.api?.let {
            //中继 > 记录手机设备信息、CRM会员信息
            val observer =
                it.nsInfo("${BaseApplication.HOST_NEWSOFT}/api/v1/app/mobile_crm/info", request)
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->

                },
                { error ->
                    error.message?.let { it1 ->
                    }
                }
            )
        }
    }

    fun updateNotMemberInfo(
    ) {
        val device_id = MMKV.defaultMMKV().decodeString("device_id")
        val fcm_token = MMKV.defaultMMKV().decodeString("FCM_Token")

        val request = NSInfoRequest(
            device_id,
            fcm_token,
            0,
            1,
            0,
            crmClientToken = MMKV.defaultMMKV().decodeString("crm_client_access_token")!!,
            loginTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(Date())
        )

        API.shared?.api?.let {
            //中继 > 记录手机设备信息、CRM会员信息
            val observer =
                it.nsInfo("${BaseApplication.HOST_NEWSOFT}/api/v1/app/mobile_crm/info", request)
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->

                },
                { error ->
                    error.message?.let { it1 ->
                    }
                }
            )
        }
    }
}

data class MemberCardsList(
    val index: Int,
    val image: Int,
    val typecode: String,
    val expiredat: String,
    val typename: String
)