package com.wingstars.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV
import com.wingstars.base.net.API
import com.wingstars.base.net.NetBase
import com.wingstars.base.net.beans.CRMBaseFailResponse
import com.wingstars.base.net.beans.CRMSignInRequest
import com.wingstars.base.net.beans.NSInfoRequest
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Date

class LoginViewModel : ViewModel(){
    var isLoading = MutableLiveData<Boolean>()

    init {

    }


    private var navigator: LoginNavigator? = null
    fun setNavigator(navigator: LoginNavigator){
        this.navigator = navigator
    }
    fun userCheck(request: CRMSignInRequest, isRememberAccount: Boolean) {
        // 1. Log bắt đầu chạy
        Log.d("LoginDebug", "1. Hàm userCheck đã được gọi. Account: ${request.account}")

        isLoading.postValue(true)

        // Kiểm tra xem API có null không
        if (API.shared == null) {
            Log.e("LoginDebug", "LỖI: API.shared bị NULL! Bạn chưa khởi tạo API trong BaseApplication?")
            isLoading.postValue(false)
            return
        }

        if (API.shared?.api == null) {
            Log.e("LoginDebug", "LỖI: API.shared.api bị NULL! Retrofit chưa được tạo?")
            isLoading.postValue(false)
            return
        }

        API.shared?.api?.let { api ->
            Log.d("LoginDebug", "2. API Instance OK. Bắt đầu gọi API crmSignInCheck...")

            // LƯU Ý: Kiểm tra lại Interface xem có cần truyền URL full không?
            // Nếu Interface đã có @POST("...") thì KHÔNG cần truyền URL ở đây.
            // Giả sử Interface của bạn CHẤP NHẬN @Url, code dưới giữ nguyên:
            val observer = api.crmSignInCheck(
                "${NetBase.HOST_CRM}/api/v1/client/sign-in/check",
                request.account
            )

            observer
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { next ->
                        Log.d("LoginDebug", "3. API Trả về kết quả: Success=${next.success}")
                        if (next.success) {
                            Log.d("LoginDebug", "4. Login OK -> Gọi hàm login()")
                            login(request, isRememberAccount)
                        } else {
                            isLoading.postValue(false)
                            Log.e("LoginDebug", "4. Login Check Fail: ${next.message}")
                            // Mở lại Toast để xem trên màn hình
                            // ToastUtil.showLongToast(BaseApplication.shared()!!, next.message)
                        }
                    },
                    { error ->
                        isLoading.postValue(false)
                        var msg = error.message.toString()

                        // Check lỗi HTTP
                        if (error is HttpException) {
                            try { // <--- THÊM TRY-CATCH LỚN NÀY
                                val errorBody = error.response()?.errorBody()?.string()

                                if (!errorBody.isNullOrEmpty()) {
                                    // Thử parse JSON
                                    try {
                                        val gson = Gson()
                                        val type = object : TypeToken<CRMBaseFailResponse>() {}.type
                                        val failResponse = gson.fromJson<CRMBaseFailResponse>(errorBody, type)

                                        // Kiểm tra logic nghiệp vụ
                                        if (failResponse?.message == "尚未註冊") {
                                            navigator?.showNotRegisteredDialog()
                                            return@subscribe
                                        }

                                        // Lấy message từ JSON trả về
                                        if (!failResponse?.message.isNullOrEmpty()) {
                                            msg = failResponse.message!!
                                        }
                                    } catch (e: Exception) {
                                        // NẾU PARSE JSON THẤT BẠI (Do lỗi 404 trả về HTML/String)
                                        // Ta sẽ không làm gì cả, giữ nguyên biến 'msg' mặc định hoặc gán bằng errorBody
                                        Log.e("LoginDebug", "Lỗi không phải JSON: $errorBody")
                                        // msg = "Lỗi kết nối: ${error.code()}" // Tùy chọn
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        // Hiển thị thông báo lỗi (Toast hoặc Dialog)
                        // _loginError.postValue(msg)
                    }
                )
        } ?: run {
            // Block này chạy nếu .let bị skip
            Log.e("LoginDebug", "LỖI: Code không chạy vào trong .let { } do API null")
        }
    }
    fun login(request: CRMSignInRequest, isRememberAccount: Boolean) {
        // LOG 5: Đánh số tiếp theo từ hàm userCheck
        Log.d("LoginDebug", "5. Hàm login() bắt đầu chạy. Account: ${request.account}")

        // Kiểm tra API null
        if (API.shared?.api == null) {
            Log.e("LoginDebug", "LỖI: API Instance bị NULL trong hàm login()")
            isLoading.postValue(false)
            return
        }

        API.shared?.api?.let { api ->
            Log.d("LoginDebug", "6. Bắt đầu gọi API crmSignIn...")

            // LƯU Ý: Vẫn cảnh báo về tham số URL này. Nếu Interface không có @Url thì code này sai.
            val observer = api.crmSignIn("${NetBase.HOST_CRM}/api/v1/client/sign-in", request)

            observer
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { next ->
                        isLoading.postValue(false)
                        Log.d("LoginDebug", "7. API Login trả về: Success=${next.success}")

                        if (next.success) {
                            val rd = next.data
                            Log.d("LoginDebug", "8. Login OK. Member ID: ${rd.id}")
                            Log.d("LoginDebug", "Access Token: ${rd.accessToken}")

                            // Lưu MMKV
                            MMKV.defaultMMKV().encode("crm_member_id", rd.id)
                            MMKV.defaultMMKV().encode("crm_member_access_token", rd.accessToken)
                            MMKV.defaultMMKV().encode("crm_member_refresh_token", rd.refreshToken)
                            MMKV.defaultMMKV().encode("crm_member_user_type", rd.userType)
                            MMKV.defaultMMKV().encode("crm_member_code", rd.code)
                            MMKV.defaultMMKV().encode("member_phone", request.account)
                            MMKV.defaultMMKV().encode("member_account", request.account)
                            MMKV.defaultMMKV().encode("member_psd", request.password)

                            MMKV.defaultMMKV().encode("isRememberAccount", isRememberAccount)
                            MMKV.defaultMMKV().encode("isLogin", true)

                            Log.d("LoginDebug", "9. Đã lưu MMKV. Gọi navigator.loginSuccess()")

                            if (navigator != null) {
                                navigator!!.loginSuccess()
                            } else {
                                Log.e("LoginDebug", "LỖI: Navigator bị NULL! Không thể chuyển màn hình.")
                            }

                            if (MMKV.defaultMMKV().decodeString("crm_client_access_token").isNullOrEmpty() ||
                                MMKV.defaultMMKV().decodeString("newsoft_access_token").isNullOrEmpty()
                            ) {
                                Log.d("LoginDebug", "10. Thiếu QAuthToken -> Gọi lại getCRMQauthToken")
                                NetBase.getCRMQauthToken()
                                // BaseApplication.shared()!!.getNSQauthToken()
                                // BaseApplication.shared()!!.checkNsInfoHandler()
                            } else {
                                Log.d("LoginDebug", "10. Đã có Token -> Gọi getMemberInfo")
                                getMemberInfo()
                            }
                        } else {
                            Log.e("LoginDebug", "7. Login Thất Bại (Logic): ${next.message}")
                            // ToastUtil.showLongToast(BaseApplication.shared()!!,next.message)
                        }

                        // loginTicket(request)
                    },
                    { error ->
                        isLoading.postValue(false)
                        val msg = error.message.toString()
                        Log.e("LoginDebug", "7. Login Exception: $msg")
                        error.printStackTrace()

                        if (error is HttpException) {
                            Log.e("LoginDebug", "HTTP Code: ${error.code()}")
                            try {
                                val errorBody = error.response()?.errorBody()?.string()
                                Log.e("LoginDebug", "HTTP Error Body: $errorBody")

                                // Parse JSON lỗi nếu cần (đã mở comment để debug)
                                if (!errorBody.isNullOrEmpty()) {
                                    val gson = Gson()
                                    val type = object : TypeToken<CRMBaseFailResponse>() {}.type
                                    val failResponse = gson.fromJson<CRMBaseFailResponse>(errorBody, type)
                                    if (failResponse?.message != null) {
                                        Log.e("LoginDebug", "Server Message: ${failResponse.message}")
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("LoginDebug", "Lỗi khi parse Error Body: ${e.message}")
                            }
                        }
                    }
                )
        }
    }
    fun getMemberInfo(){
        val device_id = MMKV.defaultMMKV().decodeString("device_id")
        val fcm_token = MMKV.defaultMMKV().decodeString("FCM_Token")

        API?.shared?.api?.let {
            //Member > 查询会员联络资料
            val id = MMKV.defaultMMKV().decodeString("crm_member_id")
            val observer =
                it.crmGetMemberContact("${NetBase.HOST_CRM}/api/v1/basic/member/${id}/contact")
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())
                ?.observeOn(
                    AndroidSchedulers.mainThread()
                )?.subscribe(
                    { next ->
                        if (next.success) {
                            updateMemberInfo(device_id, fcm_token, next.data.Name)
                        }
                    },
                    { error ->
                        error.message?.let { it1 ->

                        }
                    }
                )
        }
    }
    private fun updateMemberInfo(
        device_id: String?,
        fcm_token: String?,
        userName: String?
    ) {

        val request = NSInfoRequest(
            device_id,
            fcm_token,
            MMKV.defaultMMKV().decodeInt("isPush", 0),
            1,
            1,
            crmMemberToken = MMKV.defaultMMKV().decodeString("crm_member_access_token")!!,
            userName = userName,
            loginTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(Date()),
            crmMemberId = MMKV.defaultMMKV().decodeString("crm_member_id")!!
        )

        API.shared?.api?.let {
            //中继 > 记录手机设备信息、CRM会员信息
            val observer =
                it.nsInfo("${NetBase.HOST_BASE}/api/v1/app/mobile_crm/info", request)
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->

                },
                { error ->
                    error.message?.let { it1 ->
                    }
                }
            )
        }
    }
}