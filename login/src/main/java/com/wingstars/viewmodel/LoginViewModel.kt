package com.wingstars.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wingstars.net.beans.request_respone.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
class LoginViewModel(private val context: Context) : ViewModel() {

    private val disposables = CompositeDisposable()

    val loginSuccess = MutableLiveData<MemberData>()
    val loginError = MutableLiveData<String>()

    fun loginWithToken(apiKey: String, username: String, password: String) {
        val tokenRequest = AccessTokenRequest(apiKey = apiKey, tokenType = "Bearer")
        val tokenDisposable = API.shared.tokenApi.getAccessToken(tokenRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ tokenResp ->
                val accessToken = tokenResp.data?.accessToken
                if (tokenResp.success && !accessToken.isNullOrEmpty()) {
                    context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                        .edit()
                        .putString("access_token", accessToken)
                        .apply()
                    loginUser(username, password, accessToken)
                } else {
                    loginError.value = tokenResp.message ?: "Lấy access token thất bại"
                }
            }, { error ->
                loginError.value = error.message ?: "Lỗi mạng"
            })

        disposables.add(tokenDisposable)
    }

    private fun loginUser(username: String, password: String, token: String) {
        val request = LoginRequest(account = username, password = password)
        val disposable = API.shared.crmApi.logIn(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                val accessToken = response.data?.accessToken
                val userId = response.data?.id
                if (response.success && !accessToken.isNullOrEmpty() && !userId.isNullOrEmpty()) {
                    context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                        .edit()
                        .putString("access_token", accessToken)
                        .putString("user_id", userId)
                        .apply()
                    fetchMemberInfo(accessToken, userId)
                } else {
                    loginError.value = response.message ?: "Login thất bại"
                }
            }, { throwable ->
                loginError.value = throwable.message ?: "Lỗi mạng"
            })

        disposables.add(disposable)
    }

    private fun fetchMemberInfo(accessToken: String, userId: String) {
        val disposable = API.shared.crmApi.getMemberInfo("Bearer $accessToken", userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                if (response.success && response.data != null) {
                    Log.d("LOGIN_DEBUG", "Member info fetched: $response")
                    Log.d(
                        "LOGIN_DEBUG",
                        "Name: ${response.data.Name}, Phone: ${response.data.Phone}, Email: ${response.data.Email}, Birthday: ${response.data.Birthday}, Gender: ${response.data.Gender},EffectiveDate: ${response.data.MemberTier?.EffectiveDate},ExpirationDate: ${response.data.MemberTier?.ExpirationDate }"
                    )
                    val pref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    pref.edit()
                        .putBoolean("is_logged_in", true)
                        .putString("phone", response.data.Phone)
                        .putString("code", response.data.Code)
                        .putString("birthday", response.data.Birthday)
                        .putString("gender", response.data.Gender)
                        .putString("name", response.data.Name)
                        .putString("email", response.data.Email)
                        .putString("name", response.data.Name)
                        .putString("email", response.data.Email)
                        .putString("effective_date", response.data.MemberTier?.EffectiveDate)
                        .apply()
                    loginSuccess.value = response.data
                } else {
                    loginError.value = response.message ?: "Lấy thông tin member thất bại"
                }
            }, { error ->
                Log.d("LOGIN_DEBUG", "Error fetching member info: ${error.message}")
                loginError.value = error.message ?: "Lỗi mạng"
            })

        disposables.add(disposable)
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}

