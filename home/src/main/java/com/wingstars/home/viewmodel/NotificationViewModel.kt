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
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow

class NotificationViewModel : ViewModel() {

    var isLoading = MutableLiveData<Boolean>()

    // --- 1. Lấy danh sách (Dùng Paging 3) ---
    fun getNotificationList(): Flow<PagingData<CRMInAppMessageResponse>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { NotificationPagingSource("") } // Category "" lấy tất cả
        ).flow.cachedIn(viewModelScope)
    }

    // --- 2. Đọc 1 tin (Dùng RxJava) ---
    fun doSingleRead(messageId: String) {
        // Không bật loading để tránh giật màn hình khi user click
        API.shared?.api?.let { api ->
            val id = MMKV.defaultMMKV().decodeString("crm_member_id") ?: return@let
            val messageIds = listOf(messageId)

            api.crmInAppMessageRead(id, CRMMessageReadRequest(messageIds))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        // Đọc thành công
                    },
                    { error ->
                        error.printStackTrace()
                    }
                )
        }
    }

    // --- 3. Đọc tất cả (Dùng RxJava) ---
    fun doNotifyAllRead() {
        isLoading.postValue(true)
        API.shared?.api?.let { api ->
            val id = MMKV.defaultMMKV().decodeString("crm_member_id") ?: return@let

            // category = "" để đọc hết
            api.crmInAppMessageReadAll(id, "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        isLoading.postValue(false)
                        // Bắn sự kiện hoặc thông báo UI reload nếu cần
                    },
                    { error ->
                        isLoading.postValue(false)
                        error.printStackTrace()
                    }
                )
        }
    }
}