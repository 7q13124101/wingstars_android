package com.wingstars.base.net


import android.util.Log
import com.tencent.mmkv.MMKV
import com.wingstars.base.net.beans.CRMSignInRequest
import com.wingstars.base.net.beans.CRMVerifyRequest
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import org.json.JSONObject
import com.wingstars.base.utils.MMKVManagement
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
        } else if(urlS.startsWith(NetBase.HOST_CRM)) {
            if(urlS.contains("/client/")) {
                if(urlS.contains("/oauth/verify")) {
                    //不需要token
                    crmMethodCategory = 0
                } else if(urlS.contains("/client/sign-in")){
                    crmMethodCategory = 3
                    token = MMKV.defaultMMKV().decodeString("crm_client_access_token")
                    Log.d("token1111",token.toString())
                    if(token == null || token.isNullOrEmpty()) {
                        forceRefreshToken = true
                    }
                } else{
                    crmMethodCategory = 1
                    token = MMKV.defaultMMKV().decodeString("crm_client_access_token")
                    if(token == null || token.isNullOrEmpty()) {
                        forceRefreshToken = true
                    }
                }
            }else if(urlS.contains("member/")){
                    crmMethodCategory = 2
                    token = MMKV.defaultMMKV().decodeString("crm_member_access_token")
                    Log.d("token1111",token.toString())
                    if(token == null || token.isNullOrEmpty()) {
                        forceRefreshToken = true
                    }
                }
            requestBuilder.addHeader("Authorization", "Bearer $token")

            } else {
//            println("AuthorizationInterceptor. Other host ${BaseApplication.HOST_NEWSOFT}\n")
        }

        val request = requestBuilder.build()
        var response = chain.proceed(request)

        logIntercept(request, response)

        if(urlS.startsWith(NetBase.HOST_CRM) && (response.code == 401)) {
            //response.code == 401 账号密码错误的code也是401，不能直接使用401判断token过期 因为response.body.string()只能调用一次，多次调用会导致 java.lang.IllegalStateException: closed
            try{
                val jsonResponse = JSONObject(response.peekBody(1024).string())
                val message = jsonResponse.getString("message")
                if(message == "Token錯誤" || message == "Token已過期" || message.contains("Token", true) || forceRefreshToken){
//                    val client = CRMHashKey.clientVersion()
//                    val version = CRMHashKey.appVersion()

                    if(crmMethodCategory == 1 || crmMethodCategory == 3) {
                        val verifyCall = API.shared?.api!!.crmVerifyCall(
                            "${NetBase.HOST_CRM}/api/v1/oauth/verify",
                            CRMVerifyRequest()
                        )
                        val execute = verifyCall.execute()
                        if(execute.code() == 200) {
                            val responseVerify = execute.body()

                            if(!responseVerify!!.success || responseVerify!!.data.accessToken == null || responseVerify?.data?.accessToken?.isNullOrEmpty() == true) {
                                println("* crm refreshed token is null")
                                return response
                            }

                            MMKVManagement.setCrmClientId(responseVerify!!.data.id)
                            MMKVManagement.setCrmClientAccessToken(responseVerify!!.data.accessToken)
                            MMKVManagement.setCrmClientRefreshToken(responseVerify!!.data.refreshToken)
                            val newRequestBuilder = chain.request().newBuilder()
                                .addHeader("Accept", "application/json")
                                .addHeader("Content-Type", "application/json")
                                .addHeader("Authorization", "Bearer ${responseVerify!!.data.accessToken}")
//                                .addHeader("Client", "$client")
//                                .addHeader("Version", "$version")

                            val newBuild = newRequestBuilder.build()
                            response = chain.proceed(newBuild)
//                            logIntercept(request, response)

                            return response
                        }
                    } else if(crmMethodCategory == 2) {
                        val memberPhone =  MMKVManagement.getMemberPhone()
                        val memberPsd =  MMKVManagement.getMemberPassword()
                        val singInCall = API.shared?.api!!.crmSignInCall("${NetBase.HOST_CRM}/api/v1/client/sign-in",
                            CRMSignInRequest(memberPhone.toString(), memberPsd.toString())
                        )

                        val execute = singInCall.execute()
                        if(execute.code() == 200) {
                            val signInResponse = execute.body()

                            if(!signInResponse!!.success || signInResponse!!.data.accessToken == null || signInResponse?.data?.accessToken?.isNullOrEmpty() == true) {
                                println("* crm refreshed token is null")
                                return response
                            }

                            MMKVManagement.setCrmMemberId(signInResponse!!.data.id)
                            MMKVManagement.setCrmMemberAccessToken(signInResponse!!.data.accessToken)
                            MMKVManagement.setCrmMemberRefreshToken(signInResponse!!.data.refreshToken)
                            MMKVManagement.setCrmMemberUserType(signInResponse!!.data.userType)
                            MMKVManagement.setCrmMemberCode(signInResponse!!.data.code)

                            val newRequestBuilder = chain.request().newBuilder()
                                .addHeader("Accept", "application/json")
                                .addHeader("Content-Type", "application/json")
                                .addHeader("Authorization", "Bearer ${signInResponse!!.data.accessToken}")
//                                .addHeader("Client", "$client")
//                                .addHeader("Version", "$version")

                            val newBuild = newRequestBuilder.build()
                            response = chain.proceed(newBuild)
//                            logIntercept(request, response)

                            return response
                        }
                    }
                }else{
                    return response
                }
            } catch (e: Exception) {
                return response
            }
        }
//        else if(urlS.startsWith(BaseApplication.HOST_NEWSOFT) && !token.isNullOrEmpty()) {
//            try {
//                val jsonResponse = JSONObject(response.peekBody(1024).string())
//                val code = jsonResponse.getInt("code")
//
//                if(response.code == 401 || code == 401) {
//                    //val refresh_token = MMKV.defaultMMKV().decodeString("ns_refresh_token")
//                    val refresh_token =  MMKVManagement.getNsRefreshToken()
//                 //   val user_id = MMKV.defaultMMKV().decodeInt("ns_user_id")
//                    val user_id = MMKVManagement.getNsUserId()
//                    val newTokenCall = API.shared?.api!!.nsRefreshCall(
//                        NSLoginRequest(
//                            "",
//                            "",
//                            refresh_token!!,
//                            user_id
//                        )
//                    )
//                    val execute = newTokenCall.execute()
//
//                    if (execute.code() == 200) {
//                        val newTokenResponse = execute.body()
//                        if (newTokenResponse!!.code != 0 || newTokenResponse!!.data.accessToken == null || newTokenResponse?.data?.accessToken?.isNullOrEmpty() == true) {
//                            return response
//                        }
//
//                        MMKVManagement.setNsAccessToken(newTokenResponse!!.data.accessToken)
//                        MMKVManagement.setNsRefreshToken(newTokenResponse!!.data.refreshToken)
//
//                        val newRequestBuilder = chain.request().newBuilder()
//                            .addHeader("Accept", "application/json")
//                            .addHeader("Content-Type", "application/json")
//                            .addHeader("Authorization", "${newTokenResponse!!.data.accessToken}")
//
//                        val newBuild = newRequestBuilder.build()
//                        response = chain.proceed(newBuild)
//                        logIntercept(request, response)
//                    }
//                }
//
//                return response
//            } catch (e: Exception) {
//                return response
//            }
//        }

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
