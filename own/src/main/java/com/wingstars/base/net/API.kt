package com.wingstars.base.net


import com.gyf.immersionbar.BuildConfig
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.schedulers.Schedulers
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


class API() {

    companion object {
        private const val DEFAULT_TIMEOUT = 1 * 60

        @Volatile
        private var apiService: API? = null

        val shared: API?
            get() {
                apiService = API()

                if (apiService == null) {
                    synchronized(ApiService::class.java) {
                        if (apiService == null) {
                            apiService = API()
                        }
                    }
                }

                return apiService
            }
    }

    val api = retrofit.create(ApiService::class.java)!!

    fun <T> createService(mClass: Class<T>): T{
        return retrofit.create(mClass) as T
    }

    // 工具方法
    private fun getInsecureSocketFactory(): SSLSocketFactory {
        try {
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, arrayOf<TrustManager>(getInsecureTrustManager()), SecureRandom())
            return sslContext.socketFactory
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun getInsecureTrustManager(): X509TrustManager {
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {
            }

            override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf<X509Certificate>()
            }
        }
    }

    fun getHostnameVerifier(): HostnameVerifier {
        val hostnameVerifier =
            HostnameVerifier { s, sslSession -> true }
        return hostnameVerifier
    }

    private val retrofit: Retrofit
        get() {
            val builder = OkHttpClient.Builder()
            builder.cache(null)
            builder.addInterceptor(AuthorizationInterceptor())
            builder.protocols(Collections.singletonList(Protocol.HTTP_1_1) )
            builder.connectTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            builder.writeTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            builder.readTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            builder.sslSocketFactory(getInsecureSocketFactory(), getInsecureTrustManager())
            builder.hostnameVerifier(getHostnameVerifier())
            if (BuildConfig.DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY) //日志级别
                builder.addInterceptor(loggingInterceptor)
            }
            val builder1 = Retrofit.Builder()

            builder1.baseUrl(NetBase.HOST_BASE)

            return builder1
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ResponseConvertFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(builder.build())
                .build()
        }
}

//CRM系api扩展exec方法
//fun <T> Observable<CRMBaseResponse<T>>.exec(observer: Observer<CRMBaseResponse<T>>): Unit {
//    subscribeOn(Schedulers.io())
//        .unsubscribeOn(Schedulers.io())
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe(
//            { next ->
//                try {
//                    if (next.success) {
//                        observer.onNext(next)
//                    } else {
//                        observer.onError(Throwable(next.message))
//                    }
//                } catch (e: Exception) {
//                    println("CRM api error .message: ${e?.toString()}")
//                }
//            },
//            { error ->
//                observer.onError(error)
//            },
//            {
//                println(" ------ CRM API Completed ------ ")
//            }
//        )
//}


