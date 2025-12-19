package com.company.wingstars.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import com.company.wingstars.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.tencent.mmkv.MMKV
import com.wingstars.base.base.BaseActivity
import java.util.UUID

class PermissionRequestActivity : BaseActivity() {

    // Mã request code hằng số
    private val PERMISSION_REQUEST_CODE = 9008

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission_request)

        // Thiết lập trong suốt thanh trạng thái (API 21+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        }

        val device_id = MMKV.defaultMMKV().decodeString("device_id")
        if (device_id.isNullOrEmpty()) {
            getPhoneDeviceId()
        }

        checkPermission()
    }

    private fun checkPermission() {
        val permissionList: MutableList<String> = ArrayList()

        // 1. Quyền vị trí
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // 2. Quyền Thông báo (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // Đã có quyền -> Lấy Token
                fetchFcmToken()
            }
        } else {
            // Android thấp hơn 13 mặc định có quyền -> Lấy Token
            fetchFcmToken()
        }

        // 3. Quyền Bluetooth (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }

        // 4. Quyền Lịch
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_CALENDAR)
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_CALENDAR)
        }

        // Xử lý logic
        if (permissionList.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toTypedArray(), PERMISSION_REQUEST_CODE)
        } else {
            // Nếu đã đủ quyền hết rồi -> Chuyển trang
            navigateToNextPage()
        }
    }

    /**
     * Hàm lấy FCM Token tách riêng cho gọn
     */
    private fun fetchFcmToken() {
        val fcmToken = MMKV.defaultMMKV().decodeString("FCM_Token")
        Log.d("FCM_Token", fcmToken.toString())

        if (fcmToken.isNullOrEmpty()) {
            try {
                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result
                        MMKV.defaultMMKV().encode("FCM_Token", token)
                        // BaseApplication.shared()?.checkNsInfoHandler()
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            // BaseApplication.shared()?.checkNsInfoHandler()
        }
    }

    /**
     * Hàm điều hướng thống nhất: Luôn chuyển sang WelcomeActivity
     */
    private fun navigateToNextPage() {
        val intent = Intent(this, WelcomeActivity::class.java)

        // Truyền tiếp fcmTag nếu có
        this.intent.getStringExtra("fcmTag")?.let {
            intent.putExtra("fcmTag", it)
        }

        startActivity(intent)
        finish()
    }

    @SuppressLint("MissingPermission")
    private fun getPhoneDeviceId() {
        var deviceId: String? = null
        try {
            deviceId = if (Build.VERSION.SDK_INT <= 29) {
                val tm = getSystemService(TELEPHONY_SERVICE) as? TelephonyManager
                if (tm != null && !TextUtils.isEmpty(tm.deviceId)) {
                    tm.deviceId
                } else {
                    Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                }
            } else {
                Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (deviceId.isNullOrEmpty()) {
            deviceId = UUID.randomUUID().toString()
        }
        MMKV.defaultMMKV().encode("device_id", deviceId)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            // Kiểm tra quyền Notification (Android 13+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                for ((index, permission) in permissions.withIndex()) {
                    if (permission == Manifest.permission.POST_NOTIFICATIONS) {
                        if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                            // Nếu người dùng đồng ý quyền thông báo -> Lấy token
                            fetchFcmToken()
                        }
                    }
                }
            }

            // Dù người dùng Đồng ý hay Từ chối quyền -> Vẫn chuyển sang trang Welcome
            navigateToNextPage()
        }
    }

    override fun initView() {
        TODO("Not yet implemented")
    }
}