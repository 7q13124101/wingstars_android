package com.wingstars.net.beans.request_respone

import android.content.Context
import com.wingstars.base.net.NetBase
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.Collections
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object RetrofitClient {

    private const val TIMEOUT_SECONDS = 60
    lateinit var appContext: Context
    fun init(context: Context) {
        appContext = context.applicationContext
    }
    private val plainOkHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cache(null)
            .protocols(listOf(Protocol.HTTP_1_1))
            .connectTimeout(TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
            .sslSocketFactory(
                getInsecureSocketFactory(),
                getInsecureTrustManager()
            )
            .hostnameVerifier(getHostnameVerifier())
            .build()
    }
    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .cache(null)
            .addInterceptor(
                AuthorizationInterceptor(
                    appContext,
                    tokenApi
                )
            )
            .addInterceptor(logging)
            .protocols(listOf(Protocol.HTTP_1_1))
            .connectTimeout(TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
            .sslSocketFactory(
                getInsecureSocketFactory(),
                getInsecureTrustManager()
            )
            .hostnameVerifier(getHostnameVerifier())
            .build()
    }
    private val crmRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(NetBase.CRM_HOST)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    val crmApi: ApiService by lazy {
        crmRetrofit.create(ApiService::class.java)
    }
    private val tokenRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(NetBase.CRM_HOST)
            .client(plainOkHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    val tokenApi: ApiService by lazy {
        tokenRetrofit.create(ApiService::class.java)
    }
    private fun getInsecureSocketFactory(): SSLSocketFactory {
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(
            null,
            arrayOf<TrustManager>(getInsecureTrustManager()),
            SecureRandom()
        )
        return sslContext.socketFactory
    }

    private fun getInsecureTrustManager(): X509TrustManager {
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
        }
    }

    private fun getHostnameVerifier(): HostnameVerifier = HostnameVerifier { _, _ -> true }
}