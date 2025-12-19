package com.wingstars.net.beans.request_respone

class API private constructor() {

    companion object {
        val shared: API by lazy { API() }
    }

    val crmApi: ApiService
        get() = RetrofitClient.crmApi

    val tokenApi: ApiService
        get() = RetrofitClient.tokenApi
}
