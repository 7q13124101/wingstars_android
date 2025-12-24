package com.wingstars.user.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wingstars.base.net.NetBase
import com.wingstars.base.net.beans.WSMemberResponse
import com.wingstars.user.adapter.MemberUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException
class CheerLeaderViewModel: ViewModel() {
    private  val compositeDisposable = CompositeDisposable()
    val memberListUI = MutableLiveData<List<MemberUI>>()
    val isLoading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()
    fun fetchCheerLeaderList(
        perPage: Int = 20,
        page: Int = 1
    ) {
        isLoading.value = true
        errorMessage.value = null

        val api = com.wingstars.base.net.API.shared?.api
        if (api == null) {
            isLoading.value = false
            errorMessage.value = "API尚未初始化."
            return
        }
        val disposable = api.wsMembers(perPage, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    isLoading.value = false
                    memberListUI.value = mapWsResponseToMemberUI(response)
                },
                { error ->
                    isLoading.value = false
                    errorMessage.value = parseError(error)
                    memberListUI.value = emptyList()
                }
            )
        compositeDisposable.add(disposable)
    }
    override fun onCleared() {
        compositeDisposable.clear()
    }
    private fun mapWsResponseToMemberUI(
        list: List<WSMemberResponse>
    ): List<MemberUI> {
        return list.mapNotNull { item ->
            val number = item.acf?.number ?: return@mapNotNull null
            val name = item.title?.rendered ?: return@mapNotNull null
            val iconUrl = item.yoast_head_json
                ?.og_image
                ?.firstOrNull()
                ?.url
            if (iconUrl.isNullOrEmpty()) {
                null
            } else {
                MemberUI(
                    memberId = number,
                    memberName = name,
                    iconImageUrl = iconUrl
                )
            }
        }
    }
    private fun parseError(error: Throwable): String {
        return if (error is HttpException) {
            "HTTP ${error.code()} – server error 伺服器錯誤"
        } else {
            error.message ?: "未知錯誤"
        }
    }

}