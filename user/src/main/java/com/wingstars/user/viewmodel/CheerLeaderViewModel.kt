package com.wingstars.user.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wingstars.base.net.NetBase
import com.wingstars.user.adapter.MemberUI
import com.wingstars.user.net.API
import com.wingstars.user.net.BaseApplication
import com.wingstars.user.net.beans.CRMMemberRespone
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException

class CheerLeaderViewModel: ViewModel() {
    private  val compositeDisposable = CompositeDisposable()
    val memberListUI = MutableLiveData<List<MemberUI>>()
    val isLoading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()

    fun fetchCheerLeaderList(){
        isLoading.value = true
        errorMessage.value = null
        val api = API.shared?.api
        if(api == null){
            isLoading.value = false
            errorMessage.value = "API尚未初始化."
            return
        }
        val disposable = api.getMemberList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    respones ->
                    isLoading.value = false
                    memberListUI.value = mapApiResponseToMemberUI(respones)
                },
                {
                    error ->
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
    private fun mapApiResponseToMemberUI(list: List<CRMMemberRespone>): List<MemberUI>{
        return list.mapNotNull { item ->
            val number = item.acf?.number?: return@mapNotNull null
            val name = item.title.rendered
            val iconUrl = item.yoast_head_json
                ?.og_image
                ?.firstOrNull()
                ?.url
            if (iconUrl.isNullOrEmpty()){
                null
            }else{
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