package com.wingstars.user.net

class API private constructor() {

    companion object {
        val shared: API by lazy { API() }
    }

    val api: ApiService
        get() = RetrofitClient.api
}
