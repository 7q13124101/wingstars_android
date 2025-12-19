package com.wingstars.user.net


import com.wingstars.base.net.NetBase
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import java.io.IOException


class AuthorizationInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val requestBuilder = chain.request().newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")

        var token: String? = ""
        var forceRefreshToken = false
        var crmMethodCategory = 0   //0: oauth, 1: client, 2: member
        val urlS = chain.request().url.toString()
//        println("* AuthorizationInterceptor url: $urlS\n")
        if(urlS.startsWith(NetBase.HOST_BASE)) {
            val account = NetBase.decrypt(NetBase.WINGSTARS_ACCOUNT_ENC)
            val password = NetBase.decrypt(NetBase.WINGSTARS_PASSWORD_ENC)

            val authorization =
                NetBase.base64Encode("$account:$password".toByteArray(Charsets.UTF_8))
            requestBuilder.addHeader("Authorization", "Basic $authorization")
        } else {
//            println("AuthorizationInterceptor. Other host ${BaseApplication.HOST_NEWSOFT}\n")
        }

        val request = requestBuilder.build()
        var response = chain.proceed(request)
        logIntercept(request, response)
        return response
    }

    private fun requestBodyString(request: Request): String {
        val buffer = Buffer()
        request.body?.writeTo(buffer)

        return buffer.readUtf8()
    }

    private fun logIntercept(request: Request, response: Response) {
        val responseBody = response.peekBody(2048).string()
        println("---------- BEGIN ---------\n")
        println("request ${request.method} / ${request.url}\n")
        println("request headers ${request.headers}\n")
        println("request body --> ${requestBodyString(request)}\n")
        println("response --> ${response.code} / ${responseBody}\n")
        println("---------- END ---------\n")
    }
}
