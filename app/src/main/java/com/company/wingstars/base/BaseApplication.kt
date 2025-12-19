package com.company.wingstars.base

import android.app.Application
import android.util.Log
import com.tencent.mmkv.MMKV
import com.wingstars.base.net.API
import com.wingstars.base.net.NetBase
import com.wingstars.base.net.beans.CRMVerifyRequest
import com.wingstars.base.utils.GlideSSLUtils
import com.wingstars.base.utils.MMKVManagement
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MMKVManagement.init(this)
        GlideSSLUtils.init(this)
        getCRMQauthToken()

    }
    private var isCrmTokenCompleted = false
    fun getCrmTokenCompleted(): Boolean {
        return isCrmTokenCompleted
    }

    fun setCrmTokenCompleted(value: Boolean) {
        this.isCrmTokenCompleted = value
    }
    fun getCRMQauthToken() {
        //Oauth > 客户端验证
        API?.shared?.api?.let {
            val observer =
                it.crmVerify("${NetBase.HOST_CRM}/api/v1/oauth/verify", CRMVerifyRequest())
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    if (next.success) {
                        setCrmTokenCompleted(true)
                        val rd = next.data
                        MMKV.defaultMMKV().encode("crm_client_id", rd.id)
                        MMKV.defaultMMKV().encode("crm_client_access_token", rd.accessToken)
                        MMKV.defaultMMKV().encode("crm_client_refresh_token", rd.refreshToken)
                        Log.d("crm_client_access_token", rd.accessToken)
                    }
                },
                { error ->
                    error.message?.let { it1 ->
                    }
                }
            )
        }
    }
}