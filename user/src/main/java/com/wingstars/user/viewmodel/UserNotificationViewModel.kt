package com.wingstars.user.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.tencent.mmkv.MMKV
import com.wingstars.base.net.API
import com.wingstars.base.net.NetBase
import com.wingstars.base.net.beans.NSInfoRequest
import com.wingstars.base.utils.MMKVManagement
import com.wingstars.base.utils.NotificationHelper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class UserNotificationViewModel : ViewModel() {

    /**
     * Đồng bộ cài đặt lên Server
     */
    fun syncNotificationSetting(isOn: Boolean) {
        // Lấy device_id thực tế từ MMKV (đã được lưu trong PermissionRequestActivity)
        val deviceId = MMKV.defaultMMKV().decodeString("device_id", "") ?: ""
        val fcmToken = MMKVManagement.getFcmToken()
        
        val request = NSInfoRequest(
            deviceId = deviceId.ifEmpty { "android_device" },
            fcmToken = fcmToken,
            deviceIsPush = if (isOn) 0 else 1, // 0: Cho phép Push, 1: Không Push
            crmMemberToken = MMKVManagement.getCrmMemberAccessToken(),
            userName = MMKVManagement.getMemberName(),
            crmMemberId = MMKVManagement.getCrmMemberId(),
            memberType = if (MMKVManagement.isLogin()) 1 else 0
        )

        API.shared?.api?.let { api ->
            api.nsInfo("${NetBase.HOST_BASE}/api/v1/app/mobile_crm/info", request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("NotificationSync", "Sync success: $isOn với deviceId: $deviceId")
                }, {
                    Log.e("NotificationSync", "Sync failed", it)
                })
        }
    }

    /**
     * Lấy tin nhắn chưa đọc và hiển thị Local Notification (Chỉ dùng khi vừa bật nút)
     */
    fun pushUnreadMessagesLocally(context: Context) {
        val memberId = MMKVManagement.getCrmMemberId()
        if (memberId == "0" || memberId.isEmpty()) return

        API.shared?.api?.let { api ->
            api.getInAppMessages(memberId, "", 1, 20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    if (response.success) {
                        // Chỉ lọc những tin nhắn thực sự chưa đọc để tránh làm phiền người dùng
                        response.data?.filter { it.status == 0 }?.forEach { msg ->
                            NotificationHelper.showNotification(
                                context,
                                msg.title,
                                msg.content,
                                msg.targetUrl
                            )
                        }
                    }
                }, {
                    Log.e("NotificationLocal", "Fetch unread failed", it)
                })
        }
    }
}