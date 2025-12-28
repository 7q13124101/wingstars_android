
package com.wingstars.count.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tencent.mmkv.MMKV
import com.wingstars.base.net.API
import com.wingstars.base.net.NetBase
import com.wingstars.base.net.beans.CRMGenQRCodeRequest
import com.wingstars.base.net.beans.EvtCheckinRequest
import com.wingstars.base.net.beans.EvtTaskResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class CountItemViewModel : ViewModel() {
    val claimSuccess = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>()
    private val compositeDisposable = CompositeDisposable()
    fun claimPoint(data: EvtTaskResponse) {
        if (!MMKV.defaultMMKV().decodeBool("isLogin")) {
            errorMessage.value = "請先登入"
            return
        }

        isLoading.value = true
        val id = MMKV.defaultMMKV().decodeString("crm_member_id")
        val phone = MMKV.defaultMMKV().decodeString("member_phone")

        if (id.isNullOrEmpty() || phone.isNullOrEmpty()) {
            isLoading.value = false
            errorMessage.value = "User Info Error"
            return
        }
        val genQrApi = API.shared?.api?.crmGenQRCode(
            "${NetBase.HOST_CRM} /api/v1/basic/member/${id}/gen-qrcode",
            CRMGenQRCodeRequest(phone)
        )

        genQrApi?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({ qrResponse ->
                if (qrResponse.success && qrResponse.data != null) {
                    performReward(data, qrResponse.data.MEMQRCODE)
                } else {
                    isLoading.value = false
                    errorMessage.value = qrResponse.message ?: "GenQRCode Failed"
                    data.isSendApiF = false
                }
            }, { error ->
                isLoading.value = false
                errorMessage.value = error.message
                data.isSendApiF = false
            })?.let { compositeDisposable.add(it) }
    }

    private fun performReward(data: EvtTaskResponse, encryptedIdentity: String) {
        val rewardApi = API.shared?.api?.evtReward(
            EvtCheckinRequest(encryptedIdentity, data.id)
        )

        rewardApi?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({ response ->
                isLoading.value = false
                if (response.statusF >= 0) {
                    claimSuccess.value = true
                } else {
                    errorMessage.value = response.message ?: "Reward Failed"
                    data.isSendApiF = false
                }
            }, { error ->
                isLoading.value = false
                errorMessage.value = error.message
                data.isSendApiF = false
            })?.let { compositeDisposable.add(it) }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}