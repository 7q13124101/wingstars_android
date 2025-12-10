package com.wingstars.user.net

import android.util.Log
import com.tencent.mmkv.MMKV
import com.wingstars.user.net.BaseApplication
import com.wingstars.user.net.beans.CRMHashKey
import com.wingstars.user.net.beans.CRMSignInRequest
import com.wingstars.user.net.beans.CRMVerifyRequest
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.RequestBody
import okio.Buffer
import org.json.JSONObject


class AuthorizationInterceptor : Interceptor{
    override fun intercept(chain:Interceptor.Chain):Response{
        val requestBuilder = chain.request().newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")

        var token: String? = ""
        var forceRefreshToken = false
        var crmMethodCategory = 0   //0: oauth, 1: client, 2: member
        val urlS = chain.request().url.toString()
        //println("AuthorizationInterceptor url: $urlS\n")
        if(urlS.startsWith(BaseApplication.HOST_HAWKS)) {
            val dataConvert = DataConvert()        //Azure CDN。不需要Authorization
            val account = CRMHashKey.decrypt(BaseApplication.HAWKS_ACCOUNT_ENC)
            val password = CRMHashKey.decrypt(BaseApplication.HAWKS_PASSWORD_ENC)
            val newapi = CRMHashKey.encrypt("TiTsTJvlMXazfobzAmXz")
            Log.d("newapi", newapi)

            val authorization = dataConvert.base64Encode("$account:$password".toByteArray(Charsets.UTF_8))
            requestBuilder.addHeader("Authorization", "Basic $authorization")
            //println("Hawks Authorization: Basic {$authorization}\n")
        } else if(urlS.startsWith(BaseApplication.HOST_CRM)) {
            if(urlS.contains("/client/")) {
                if(urlS.contains("/oauth/verify")) {
                    //不需要token
                    crmMethodCategory = 0
                } else if(urlS.contains("/client/sign-in")){
                    crmMethodCategory = 3
                    token = MMKV.defaultMMKV().decodeString("crm_client_access_token")
//                    Log.d("token1111",token.toString())
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
            } else if(urlS.contains("/member/")) {
                crmMethodCategory = 2
                token = MMKV.defaultMMKV().decodeString("crm_member_access_token")
                Log.d("token1111",token.toString())
                if(token == null || token.isNullOrEmpty()) {
                    forceRefreshToken = true
                }
            }

            requestBuilder.addHeader("Authorization", "Bearer $token")

            val client = CRMHashKey.clientVersion()
            val version = CRMHashKey.appVersion()
            requestBuilder.addHeader("Client", "$client")
            requestBuilder.addHeader("Version", "$version")

        } else if(urlS.startsWith(BaseApplication.HOST_HAWK_EVENT)) {
            val client = CRMHashKey.clientVersion()
            val version = CRMHashKey.appVersion()
            requestBuilder.addHeader("Client", "$client")
            requestBuilder.addHeader("Version", "$version")

        }else if(urlS.startsWith(BaseApplication.HOST_TICKET_SKYHAWKS)) {
            requestBuilder.addHeader("x-company-code", "newretail")
//            val client = CRMHashKey.clientVersion()
//            val version = CRMHashKey.appVersion()
//            requestBuilder.addHeader("Client", "$client")
//            requestBuilder.addHeader("Version", "$version")
            if (!urlS.contains("/api/v1/members/sign-in")){
                val access_token = MMKV.defaultMMKV().decodeString("ticket_member_access_token")
                if (access_token != null){
                    requestBuilder.addHeader("Authorization", "Bearer $access_token")
                }else{
                    MMKV.defaultMMKV().encode("isLoginTicket", false)
                }
            }
        }
        else if(urlS.startsWith(BaseApplication.HOST_NEWSOFT)) {
            if(urlS.contains("/token/new/")) {
                val appsecret = CRMHashKey.decrypt(BaseApplication.NEWSOFT_APPSECRET_ENC)

                requestBuilder.addHeader("appsecret", "$appsecret")
                Log.d("token1",appsecret.toString())
            } else if(urlS.contains("/token/refresh/")) {
                //no extral header info
                Log.d("** AuthorizationInterceptor token is null", urlS)
            } else {
                token = MMKV.defaultMMKV().decodeString("newsoft_access_token")
                Log.d("key newsoft",token.toString())
                requestBuilder.addHeader("Authorization", "$token")
                Log.d("token1",token.toString())
                //println("NS Authorization: {$token}\n")
            }
        } else {
            //println("AuthorizationInterceptor. Other host\n")
        }

        val request = requestBuilder.build()
        var response = chain.proceed(request)

//        logIntercept(request, response)

        if(urlS.startsWith(BaseApplication.HOST_CRM) && (response.code == 401)) {
            //response.code == 401 账号密码错误的code也是401，不能直接使用401判断token过期 因为response.body.string()只能调用一次，多次调用会导致 java.lang.IllegalStateException: closed
            try{
                val jsonResponse = JSONObject(response.peekBody(1024).string())
                val message = jsonResponse.getString("message")
                if(message == "Token錯誤" || message == "Token已過期" || message.contains("Token", true) || forceRefreshToken){
                    val client = CRMHashKey.clientVersion()
                    val version = CRMHashKey.appVersion()

                    if(crmMethodCategory == 1 || crmMethodCategory == 3) {
                        val verifyCall = API.shared?.api!!.crmVerifyCall(
                            "${BaseApplication.HOST_CRM}/api/v1/oauth/verify",
                            CRMVerifyRequest()
                        )
                        val execute = verifyCall.execute()
                        if(execute.code() == 200) {
                            val responseVerify = execute.body()

                            if(!responseVerify!!.success || responseVerify!!.data.accessToken == null || responseVerify?.data?.accessToken?.isNullOrEmpty() == true) {
                                println("* crm refreshed token is null")
                                return response
                            }

                            MMKV.defaultMMKV().encode("crm_client_id", responseVerify!!.data.id)
                            MMKV.defaultMMKV().encode("crm_client_access_token", responseVerify!!.data.accessToken)
                            MMKV.defaultMMKV().encode("crm_client_refresh_token", responseVerify!!.data.refreshToken)

                            val newRequestBuilder = chain.request().newBuilder()
                                .addHeader("Accept", "application/json")
                                .addHeader("Content-Type", "application/json")
                                .addHeader("Authorization", "Bearer ${responseVerify!!.data.accessToken}")
                                .addHeader("Client", "$client")
                                .addHeader("Version", "$version")

                            val newBuild = newRequestBuilder.build()
                            response = chain.proceed(newBuild)
//                            logIntercept(request, response)

                            return response
                        }
                    } else if(crmMethodCategory == 2) {
                        val memberPhone = MMKV.defaultMMKV().decodeString("member_phone")
                        val memberPsd = MMKV.defaultMMKV().decodeString("member_psd")
                        val singInCall = API.shared?.api!!.crmSignInCall("${BaseApplication.HOST_CRM}/api/v1/client/sign-in",
                            CRMSignInRequest(memberPhone.toString(), memberPsd.toString())
                        )

                        val execute = singInCall.execute()
                        if(execute.code() == 200) {
                            val signInResponse = execute.body()

                            if(!signInResponse!!.success || signInResponse!!.data.accessToken == null || signInResponse?.data?.accessToken?.isNullOrEmpty() == true) {
                                println("* crm refreshed token is null")
                                return response
                            }

                            MMKV.defaultMMKV().encode("crm_member_id", signInResponse!!.data.id)
                            MMKV.defaultMMKV().encode("crm_member_access_token", signInResponse!!.data.accessToken)
                            MMKV.defaultMMKV().encode("crm_member_refresh_token", signInResponse!!.data.refreshToken)
                            MMKV.defaultMMKV().encode("crm_member_user_type", signInResponse!!.data.userType)
                            MMKV.defaultMMKV().encode("crm_member_code", signInResponse!!.data.code)

                            val newRequestBuilder = chain.request().newBuilder()
                                .addHeader("Accept", "application/json")
                                .addHeader("Content-Type", "application/json")
                                .addHeader("Authorization", "Bearer ${signInResponse!!.data.accessToken}")
                                .addHeader("Client", "$client")
                                .addHeader("Version", "$version")

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
        } else if(urlS.startsWith(BaseApplication.HOST_NEWSOFT) && !token.isNullOrEmpty()) {
            try {
                val jsonResponse = JSONObject(response.peekBody(1024).string())
                val code = jsonResponse.getInt("code")
                if (code == 4005) {
                    val appid = CRMHashKey.decrypt(BaseApplication.NEWSOFT_APPID_ENC)
                    val newTokenCall = API.shared?.api!!.nsTokenNewCall("${BaseApplication.HOST_NEWSOFT}/api/v1/com/token/new/${appid}")
                    val execute = newTokenCall.execute()

                    if(execute.code() == 200) {
                        val newTokenResponse = execute.body()
                        if(newTokenResponse!!.code != 2000 || newTokenResponse!!.data.token == null || newTokenResponse?.data?.token?.isNullOrEmpty() == true) {
                            return response
                        }
                        MMKV.defaultMMKV().encode("newsoft_access_token", newTokenResponse!!.data.token)
                        MMKV.defaultMMKV().encode("newsoft_refresh_token", newTokenResponse!!.data.refresh_token)

                        val newRequestBuilder = chain.request().newBuilder()
                            .addHeader("Accept", "application/json")
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", "${newTokenResponse!!.data.token}")

                        val newBuild = newRequestBuilder.build()
                        response = chain.proceed(newBuild)
//                        logIntercept(request, response)

                        return response
                    }
                }else{
                    return response
                }
            } catch (e: Exception) {
                return response
            }
        }

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