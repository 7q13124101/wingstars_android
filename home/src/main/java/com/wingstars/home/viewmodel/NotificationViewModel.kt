package com.wingstars.home.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tencent.mmkv.MMKV
import com.wingstars.base.net.API
import com.wingstars.base.net.beans.CRMInAppMessageResponse
import com.wingstars.base.net.beans.CRMMessageReadRequest
import com.wingstars.home.paging.NotificationPagingSource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow

class NotificationViewModel : ViewModel() {

    private val disposables = CompositeDisposable()
    var isLoading = MutableLiveData<Boolean>()

    fun getNotificationList(): Flow<PagingData<CRMInAppMessageResponse>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { NotificationPagingSource("") }
        ).flow.cachedIn(viewModelScope)
    }

    fun doSingleRead(messageId: String) {
        API.shared?.api?.let { api ->
            val id = MMKV.defaultMMKV().decodeString("crm_member_id") ?: return@let
            val messageIds = listOf(messageId)

            val disposable = api.crmInAppMessageRead(id, CRMMessageReadRequest(messageIds))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { _ -> },
                    { error ->
                        error.printStackTrace()
                    }
                )
            disposables.add(disposable)
        }
    }

    fun doNotifyAllRead() {
        isLoading.postValue(true)
        API.shared?.api?.let { api ->
            val id = MMKV.defaultMMKV().decodeString("crm_member_id") ?: return@let

            val disposable = api.crmInAppMessageReadAll(id, "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { _ ->
                        isLoading.postValue(false)
                    },
                    { error ->
                        isLoading.postValue(false)
                        error.printStackTrace()
                    }
                )
            disposables.add(disposable)
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
