package com.wingstars.user.net

import com.wingstars.user.BaseApplication
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.Collections
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.scalars.ScalarsConverterFactory
import okhttp3.Protocol


class API {
    companion object{
        private const val DEFAULT_TIMEOUT = 1 * 60
        private var apiService: API?= null
        val shared: API?
            get(){
                apiService = API()
                if (apiService==null){
                    synchronized(ApiService::class.java){
                        if (apiService == null){
                            apiService = API()
                        }
                    }
                }
                return apiService
            }
    }
    val api: ApiService by lazy {
        retrofitClient.create(ApiService::class.java)
    }

    fun <T> createService(mClass: Class<T>): T{
        return retrofitClient.create(mClass) as T
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


    val pagingDataApi: DataApi by lazy {
        retrofitClient.create(DataApi::class.java)
    }

    private val DEFAULT_TIMEOUT = 30

    private val retrofitClient: Retrofit
        get() {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val builder = OkHttpClient.Builder()
            builder.cache(null)
            builder.addInterceptor(AuthorizationInterceptor())
            builder.addInterceptor ( logging )
            builder.protocols(Collections.singletonList(Protocol.HTTP_1_1))
            builder.connectTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            builder.writeTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            builder.readTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            builder.sslSocketFactory(getInsecureSocketFactory(), getInsecureTrustManager())
            builder.hostnameVerifier(getHostnameVerifier())

            val builder1 = Retrofit.Builder()
            builder1.baseUrl(BaseApplication.HOST_HAWKS)

            return builder1
                .addConverterFactory(GsonConverterFactory.create())
//                .addConverterFactory(ResponseConvertFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(builder.build())
                .build()
        }
    val dataApi: DataApi by lazy {
        retrofitClient.create(DataApi::class.java)
    }
}