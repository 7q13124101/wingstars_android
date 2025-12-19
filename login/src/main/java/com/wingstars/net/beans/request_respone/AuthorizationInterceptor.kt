package com.wingstars.net.beans.request_respone

import android.content.Context
import android.content.SharedPreferences
import com.wingstars.base.net.NetBase
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import java.io.IOException
class AuthorizationInterceptor(
    private val context: Context,
    private val tokenApi: ApiService
) : Interceptor {

    private val prefs: SharedPreferences
        get() = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val url = originalRequest.url.toString()
        if (url.contains("/api/v1/oauth/verify")) {
            return chain.proceed(originalRequest)
        }
        var accessToken = prefs.getString("access_token", null)
        val expireAt = prefs.getLong("expire_at", 0)
        if (
            url.contains("/api/v1/client") &&
            (accessToken.isNullOrEmpty() || System.currentTimeMillis() >= expireAt)
        ) {
            synchronized(this) {
                accessToken = prefs.getString("access_token", null)
                if (accessToken.isNullOrEmpty() || System.currentTimeMillis() >= expireAt) {
                    fetchTokenBlocking()
                    accessToken = prefs.getString("access_token", null)
                }
            }
        }
        val builder = originalRequest.newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
        if (!accessToken.isNullOrEmpty() && url.contains("/api/v1/client")) {
            builder.addHeader("Authorization", "Bearer $accessToken")
        }
        else if (url.startsWith(NetBase.HOST_BASE)) {
            val account = NetBase.decrypt(NetBase.WINGSTARS_ACCOUNT_ENC)
            val password = NetBase.decrypt(NetBase.WINGSTARS_PASSWORD_ENC)
            val basic = NetBase.base64Encode("$account:$password".toByteArray())
            builder.addHeader("Authorization", "Basic $basic")
        }

        val request = builder.build()
        val response = chain.proceed(request)
        logIntercept(request, response)
        return response
    }

    private fun fetchTokenBlocking() {
        try {
            val request = AccessTokenRequest(
                NetBase.API_KEY,
                NetBase.TOKEN_TYPE
            )

            val tokenResponse = tokenApi
                .getAccessToken(request)
                .blockingFirst()

            prefs.edit()
                .putString("access_token", tokenResponse.data?.accessToken)
                .putString("refresh_token", tokenResponse.data?.refreshToken)
                .apply()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun requestBodyString(request: Request): String {
        val buffer = Buffer()
        request.body?.writeTo(buffer)
        return buffer.readUtf8()
    }

    private fun logIntercept(request: Request, response: Response) {
        val responseBody = response.peekBody(2048).string()
        println("---------- BEGIN ---------")
        println("request ${request.method} / ${request.url}")
        println("request headers ${request.headers}")
        println("request body --> ${requestBodyString(request)}")
        println("response --> ${response.code} / $responseBody")
        println("---------- END ---------")
    }
}


