package com.wingstars.base.utils

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import okhttp3.OkHttpClient
import java.io.InputStream
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class GlideSSLUtils {

    companion object{
        fun  init(context: Context){
            Glide.get(context).registry.replace(
                GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory
                    (getNoCheckOkHttpClient()!!)
            )
        }
        fun getNoCheckOkHttpClient(): OkHttpClient? {
            val ssl: SSLSocketFactory = getNoCheckSSLSocketFactory()!!
            val trustManager: X509TrustManager = getTrustManager()!!
            return OkHttpClient.Builder()
                .connectTimeout(TimeUnit.SECONDS.toMillis(30), TimeUnit.SECONDS)
                .readTimeout(TimeUnit.SECONDS.toMillis(30), TimeUnit.SECONDS)
                .writeTimeout(TimeUnit.SECONDS.toMillis(30), TimeUnit.SECONDS)
                .sslSocketFactory(ssl, trustManager)
                .hostnameVerifier(HostnameVerifier { hostname: String?, session: SSLSession? -> true })
                .retryOnConnectionFailure(true)
                .build()
        }

        fun getNoCheckSSLSocketFactory(): SSLSocketFactory? {
            return try {
                val sslContext: SSLContext = SSLContext.getInstance("SSL")
                sslContext.init(null, arrayOf<TrustManager>(getTrustManager()!!), SecureRandom())
                sslContext.getSocketFactory()
            } catch (e: java.lang.Exception) {
                throw RuntimeException(e)
            }
        }

        fun getTrustManager(): X509TrustManager? {
            return object : X509TrustManager {
                override fun checkClientTrusted(
                    serverX509Certificates: Array<X509Certificate?>?,
                    s: String?
                ) {
                }

                override fun checkServerTrusted(
                    x509Certificates: Array<X509Certificate?>?,
                    s: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                    return arrayOfNulls(0)
                }
            }
        }

    }
}