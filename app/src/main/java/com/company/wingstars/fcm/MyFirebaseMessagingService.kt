package com.company.wingstars.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.wingstars.base.utils.MMKVManagement
import com.wingstars.base.utils.NotificationHelper

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // 1. Check if user enabled notifications in App Settings
        if (!MMKVManagement.isNotificationOn()) {
            Log.d("FCM", "Notification is OFF in settings, ignoring...")
            return
        }

        // 2. Extract data
        val title = message.notification?.title ?: message.data["title"]
        val content = message.notification?.body ?: message.data["content"]
        val targetUrl = message.data["targetUrl"]

        Log.d("FCM", "Message Received: $title - $content")

        // 3. Show System Notification
        NotificationHelper.showNotification(applicationContext, title, content, targetUrl)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New Token: $token")
        MMKVManagement.setFcmToken(token)
        // Ideally, call updateMemberInfo here if user is logged in
    }
}