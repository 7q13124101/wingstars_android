package com.wingstars.user.net

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
    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .cache(null)
            .addInterceptor(AuthorizationInterceptor())
            .addInterceptor(logging)
            .protocols(Collections.singletonList(Protocol.HTTP_1_1))
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
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BaseApplication.CRM_MEMBER_LIST_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }
    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
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
            override fun checkClientTrusted(
                chain: Array<X509Certificate?>?,
                authType: String?
            ) {}

            override fun checkServerTrusted(
                chain: Array<X509Certificate?>?,
                authType: String?
            ) {}

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return emptyArray()
            }
        }
    }

    private fun getHostnameVerifier(): HostnameVerifier {
        return HostnameVerifier { _, _ -> true }
    }
}
