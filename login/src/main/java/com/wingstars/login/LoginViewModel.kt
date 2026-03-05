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
import com.wingstars.base.net.beans.CRMMemberContactResponse
import com.wingstars.base.net.beans.CRMResetPasswordRequest
import com.wingstars.base.net.beans.CRMSendOtpRequest
import com.wingstars.base.net.beans.CRMSignInRequest
import com.wingstars.base.net.beans.NSInfoRequest
import com.wingstars.base.utils.MMKVManagement
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Date

class LoginViewModel : ViewModel(){
    var isLoading = MutableLiveData<Boolean>()
    val resetPasswordResult = MutableLiveData<Boolean>()
    val resetPasswordError = MutableLiveData<String>()

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
            //Log.e("LoginDebug", "LỖI: API.shared bị NULL! Bạn chưa khởi tạo API trong BaseApplication?")
            isLoading.postValue(false)
            return
        }

        if (API.shared?.api == null) {
            //Log.e("LoginDebug", "LỖI: API.shared.api bị NULL! Retrofit chưa được tạo?")
            isLoading.postValue(false)
            return
        }

        API.shared?.api?.let { api ->
            Log.d("LoginDebug", "2. API Instance OK. Bắt đầu gọi API crmSignInCheck...")

            // LƯU Ý: Kiểm tra lại Interface xem có cần truyền URL full không?
            // Nếu Interface đã có @POST("...") thì KHÔNG cần truyền URL ở đây.
            // Giả sử Interface của bạn CHẤP NHẬN @Url, code dưới giữ nguyên:
            val observer = api.crmSignInCheck(
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
                            //Log.e("LoginDebug", "4. Login Check Fail: ${next.message}")
                            // Mở lại Toast để xem trên màn hình
                            // ToastUtil.showLongToast(BaseApplication.shared()!!, next.message)
                        }
                    },
                    { error ->
                        isLoading.postValue(false)
                        handleHttpError(error)
                        // Hiển thị thông báo lỗi (Toast hoặc Dialog)
                        // _loginError.postValue(msg)
                    }
                )
        } ?: run {
            // Block này chạy nếu .let bị skip
            //Log.e("LoginDebug", "LỖI: Code không chạy vào trong .let { } do API null")
        }
    }
    private fun handleHttpError(error: Throwable) {
        var msg = error.message.toString()
        if (error is HttpException) {
            try {
                val errorBody = error.response()?.errorBody()?.string()
                if (!errorBody.isNullOrEmpty()) {
                    val gson = Gson()
                    val type = object : TypeToken<CRMBaseFailResponse>() {}.type
                    val failResponse = gson.fromJson<CRMBaseFailResponse>(errorBody, type)

                    if (failResponse?.message == "尚未註冊") {
                        navigator?.showNotRegisteredDialog()
                        return
                    } else {
                        val errorMsg = failResponse?.message ?: "登入失敗"
                        navigator?.showLoginFailDialog(errorMsg)
                        return
                    }


                }
            } catch (e: Exception) {
                //Log.e("LoginDebug", "Error parsing HTTP error body: ${e.message}")
            }
        }
        //Log.e("LoginDebug", "HTTP Error: $msg")
    }

    fun login(request: CRMSignInRequest, isRememberAccount: Boolean) {
        // LOG 5: Đánh số tiếp theo từ hàm userCheck
        Log.d("LoginDebug", "5. Hàm login() bắt đầu chạy. Account: ${request.account}")

        // Kiểm tra API null
        if (API.shared?.api == null) {
            //Log.e("LoginDebug", "LỖI: API Instance bị NULL trong hàm login()")
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
                        if (next.success) {
                            val rd = next.data
                            // LƯU CÁC THÔNG TIN CƠ BẢN ĐỂ CÓ TOKEN GỌI API TIẾP THEO
                            MMKVManagement.setCrmMemberId(rd.id)
                            MMKVManagement.setCrmMemberAccessToken(rd.accessToken)
                            MMKVManagement.setCrmMemberRefreshToken(rd.refreshToken)
                            MMKVManagement.setMemberPhone(request.account)
                            MMKVManagement.setMemberPassword(request.password)
                            MMKVManagement.setIsRememberAccount(isRememberAccount)
                            MMKVManagement.setLogin(true)

                            Log.d("LoginDebug", "8. Login OK. Bắt đầu lấy thông tin chi tiết...")

                            // QUAN TRỌNG: Không gọi loginSuccess() ở đây nữa.
                            // Gọi hàm lấy thông tin chi tiết và truyền vào hành động kết thúc.
                            fetchFullMemberData()
                        } else {
                            isLoading.postValue(false)
                            // ToastUtil.showLongToast(...)
                        }
                    },
                    { error ->
                        isLoading.postValue(false)
                        handleHttpError(error)
                    }
                )
        }
    }
    private fun fetchFullMemberData() {
        val memberId = MMKVManagement.getCrmMemberId()
        API.shared?.api?.let { api ->
            // 1. Lấy thông tin cơ bản (Tên, ngày sinh...)
            val infoUrl = "${NetBase.HOST_CRM}/api/v1/basic/member/$memberId/contact"

            api.crmGetMemberContact(infoUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { infoResponse ->
                    if (infoResponse.success && infoResponse.data != null) {
                        saveMemberToMMKV(infoResponse.data)
                    }
                    // 2. Sau khi lấy xong Info, lấy tiếp Extra Info (Ngày hết hạn...)
                    val extraUrl = "${NetBase.HOST_CRM}/api/v1/basic/member/$memberId"
                    api.crmGetMemberExpiredDate(extraUrl)
                        .subscribeOn(Schedulers.io())
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { extraResponse ->
                        // Lưu nốt ngày hết hạn
                        if (extraResponse.success && extraResponse.data != null) {
                            MMKVManagement.setMemberExpiredDate(extraResponse.data.NextTokenExpiredDate)
                        }

                        Log.d("LoginDebug", "9. Tất cả dữ liệu đã lưu. Kết thúc Login.")
                        isLoading.postValue(false)

                        // CUỐI CÙNG: Mới báo thành công để đóng LoginActivity
                        navigator?.loginSuccess()
                    },
                    { error ->
                        //Log.e("LoginDebug", "Lỗi khi lấy thông tin chi tiết: ${error.message}")
                        isLoading.postValue(false)
                        // Dù lỗi lấy info vẫn nên cho họ vào, nhưng báo loginSuccess
                        navigator?.loginSuccess()
                    }
                )
        }
    }

    private fun saveMemberToMMKV(data: CRMMemberContactResponse) {
        MMKVManagement.setMemberName(data.Name ?: "")
        MMKVManagement.setMemberPhone(data.Phone ?: "")
        MMKVManagement.setCrmMemberCode(data.Code ?: "")
        MMKVManagement.setMemberBirthday(data.Birthday ?: "")
        MMKVManagement.setMemberGender(data.Gender ?: "")
        MMKVManagement.setMemberIdentity(data.Identity ?: "")
        MMKVManagement.setMemberMail(data.Email ?: "")
        MMKVManagement.setCrmMemberBarcode(data.CarrierCode ?: "")
        MMKVManagement.setMemberFavMember(data.ExtraData.favorite_players ?: emptyList())
        Log.d("LoginDebug", "✅ Member info saved to MMKV: ${data.Name}, ${data.Email}")
        Log.d("LoginMemberCode", "✅ Member info saved to MMKV: ${data.CarrierCode}")

    }
    fun sendOtp() {
        val phone = MMKVManagement.getMemberPhone()
        if (phone.isBlank()) {
            //Log.e("LoginDebug", "SendOtp failed: phone is empty")
            return
        }
        val request = CRMSendOtpRequest(phone, "resetPassword")
        val url = "${NetBase.HOST_CRM}/api/v1/client/otp/sms"
        API.shared?.api?.let { api ->
            api.crmSendOtp(url, request )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    if (response.success) {
                        Log.d("LoginDebug", "OTP gửi thành công: ${response.message}")
                    } else {
                        //Log.e("LoginDebug", "OTP gửi thất bại: ${response.message}")
                    }
                }, { error ->
                    //Log.e("LoginDebug", "sendOtp error: ${error.message}")
                })
        }
    }
    fun resetPassword(otp: String, newPassword: String) {
        val memberId = MMKVManagement.getCrmMemberId()
        if (memberId.isBlank()) {
            resetPasswordError.postValue("會員ID不存在")
            return
        }

        val request = CRMResetPasswordRequest(
            oldPassword = "",
            otp = otp,
            password = newPassword
        )

        val url = "${NetBase.HOST_CRM}/api/v1/basic/member/$memberId/reset-password"

        API.shared?.api?.let { api ->
            api.crmResetPassword(url, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    if (response.success) {
                        MMKVManagement.setMemberPassword(newPassword)
                        resetPasswordResult.postValue(true)
                    } else {
                        resetPasswordResult.postValue(false)
                        resetPasswordError.postValue(response.message ?: "OTP 不正確")
                    }
                }, { error ->
                    resetPasswordResult.postValue(false)
                    resetPasswordError.postValue(error.message ?: "系統錯誤")
                })
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
            1,
            1,
            1,
            crmMemberToken = MMKVManagement.getCrmMemberAccessToken(),
            userName = userName,
            loginTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(Date()),
            crmMemberId = MMKVManagement.getCrmMemberId()
        )

        API.shared?.api?.let { api ->
            val observer = api.nsInfo("${NetBase.HOST_BASE}/api/v1/app/mobile_crm/info", request)
            observer?.subscribeOn(Schedulers.io())
                ?.unsubscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { _ -> },
                    { error ->
                        //Log.e("LoginDebug", "updateMemberInfo error: ${error.message}")
                    }
                )
        }
    }
}