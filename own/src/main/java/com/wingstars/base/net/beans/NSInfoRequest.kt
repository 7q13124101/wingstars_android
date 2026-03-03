package com.wingstars.base.net.beans

import android.util.Log
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class NSInfoRequest(
    var deviceId: String?,		        //设备ID
    var fcmToken: String?,		        //Firebase消息推送令牌
    var deviceIsPush: Int,			    //是否推送（推送：0、不推送：1）
    var deviceType: Int = 1,			//设备类型（IOS：0、Android：1）
    var memberType: Int = 0,			//登录用户类型（游客: 0，正式: 1）
    var crmMemberToken: String? = null,	//CRM 会员登录令牌
    var crmClientToken: String? = null,	//CRM client令牌
    var userName: String? = null,			//用户名称
    var loginTime: String = "",	        //时间戳
    var crmMemberId: String? = null
) {
    init {
        try {
            val now = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
            loginTime = now.format(formatter)

//            Log.d("NSInfoRequest", "loginTime: ${loginTime}")
        } catch (ex: Exception) {
            //Log.e("NSInfoRequest", "Exception msg:${ex.message}")
        }
    }
}
