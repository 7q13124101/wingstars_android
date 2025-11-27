package com.wingstars.user

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.tencent.mmkv.MMKV

class BaseApplication: Application(){
    companion object{
        @JvmStatic
        lateinit var context: Context
            private set
        private var instance: BaseApplication? = null
        @JvmStatic
        fun shared(): BaseApplication?{
            return instance
        }
        //API Hosts
        const val HOST_TICKET_SKYHAWKS = "https://ticket-platform.tsgb2c.net"
        //        const val HOST_HAWKS = "https://www.tsghawks.com"                         //雄鹰 - 正式環境
        const val HOST_HAWKS = "https://20.189.248.99/"
        //雄鹰 - 正式環境
        const val HOST_HAWKS1 = "https://api.preciser.io/"                        //雄鹰 - 正式環境
        const val HOST_HAWKS_CDN = "https://tsg-hawks-akaqhvfyb7euhdh7.a03.azurefd.net"  //雄鹰 - Azure CDN. 中继服务器对所有数据头信息和授权包装转发
        //const val HOST_CRM = "https://hawk-crm.newretail.app"               //新零售. CRM 会员系統 - 正式環境
//        const val HOST_HAWK_EVENT = "https://hawk-event.newretail.app"      //新零售. CRM 任務系統 - 正式環境
//        const val HOST_NEWSOFT = "https://relay.tsghawks.com:8765"          //中继 UAT 环境

//        const val HOST_HAWKS_TEST = "https://61.218.209.205"              //雄鹰 - 開發環境         //天鹰 - 開發環境

        const val HOST_CRM = "https://tsgskyhawks-crm-dev.newretail.tw"            //新零售. CRM 会员系統 - 開發環境
        const val HOST_HAWK_EVENT = "https://tsgskyhawks-event-dev.newretail.tw"       //新零售. CRM 任務系統 - 開發環境
        //        const val HOST_NEWSOFT = "http://47.236.188.70:8765"              //中继 开发环境
//        const val HOST_NEWSOFT = "http://20.189.240.127:8765"              //中继 开发环境
        const val HOST_NEWSOFT = "http://20.189.248.99:8765"              //中继 开发环境


        //const val HOST_CPBL = "https://statsapi.cpbl.com.tw"              //职棒. 不用

        const val HOST_GOOGLE = "https://www.googleapis.com"                //Google
        const val HOST_YOUTUBE = "https://www.youtube.com"                  //Youtube

        //API keys. 已加密
        const val HAWKS_ACCOUNT_ENC = "E20TFs60WQ88bBFbrU+9aadvJctSNfK8CgpcV6TnzI0="
        //        const val HAWKS_PASSWORD_ENC = "VJ9tFr3JfGCE0QnyszIilevJERa23cXWzKUnYksAiT6bxWuJrt8CHs4d/o/1Go0h"
        const val HAWKS_PASSWORD_ENC = "u3ntfJd9EfP17qTQCGE4SVqZaPP1iEVmc+TysT/Me9m3oBrCeiOiNvYQCH0QuNKr"
        const val HAWKS_CONSUMER_KEY_ENC = "dgmCqYE8Od6YmRIn+7r5ebA1kq0DPLwEyAcbguE9QRIFNZkcUf4eae9wujKwDbve2bq8XVm0vogx66IfqFXETg=="
        const val HAWKS_CONSUMER_SECRET_ENC = "Sfpc9fdIKrV9U9xX920QCemGjeox+XeLPkAjQlVOx7sjWBwpcQQZgGGfw2YFQY6nJWPD+JUK3TC5Km/ushTpnQ=="

        const val CRM_APP_KEY_ENC = "UxoQ+P95F1OY9D4qUFUBfL+CZcBNzV8dzyNrUL3QjrOITZEY+5mBy7EiioeeUicD"      //开发环境1.0
        //const val CRM_APP_KEY_ENC = "uaVwQvZIoffYaLwAG2UYouo2/ZdHm4abkAFrG0isRQCWchDnyQcB5lEeJ5XZ61/M"      //开发环境1.1
//        const val CRM_APP_KEY_ENC = "KPOh9SgFXW/TtTm3b5wTJqKaAzTEbCpsY7oEZyGtA6ESkzbtZSRj/pQ/OKExA1YB"      //正式环境

        const val YOUTUBE_APP_KEY_ENC = "OZz+yBNZBNSuACKLzO7XpCOREOoRmEklZAh9iYW3beXdWupBRypI0oNpbgLfbAPPxPPNtjbC4kk6WDyigoqPOg=="
        const val YOUTUBE_HAWKS_CHANNEL_ENC = "c9c6hQKLugWrO9FC8/ISvYN3mpCD1Nnlj6D5nnu7bkXLSmJiNDDl3Mpu2rqIHLvG"

        const val GOOGLE_MAP_APP_KEY_ENC = "aAJRmwwL2YuMt8MeHHf6L0lRSb6Ld9xIUVjpd431grsq7eIcx022VvfBq/0zfqX+7d18waBpjTCxBL6dtIqoNA=="

        const val NEWSOFT_APPID_ENC = "f94/JPMTYyT6GtQwP+23HTPfNqEyCQ9x1JugXEDwiFdb8lN3/pTO9GWRVt/YfDch61Ho20X8Wa1n9k4/nlNkPg=="        //中继
        const val NEWSOFT_APPSECRET_ENC = "HsV5qFZzCLfDdMYwbdkja51JoO5QB2sXtixKucCRGZf9NddFxj+xZBxnDfzomzz7T+3WmzGB7NMqQd8iWw8ZwA=="    //中继
//        const val NEWSOFT_APPSECRET_ENC = "AIzaSyAHtPSyko-zk2nz7iofQAzCz-KlA8enQ3c"    //中继



        const val URL_FAMITICKET = "https://www.famiticket.com.tw/Home/Activity/Info/WgB5AFAAZwBSAG0ARgBJAFIAVABWAC8AYQBOAFEAdQAvAEkAZQA1AHcAdQBxAEIATgB3ADkAVgBvAG8AZAAzAG0AZgB2AEwARwAyAG4AQwB2AE4AQQA9AA2"
        const val URL_CALENDAR = "https://20.189.240.127/schedule/"
        const val URL_TSGBTICKET = "https://tsg-ticket.tsgb2c.net/"
        //登录之后有界面跳转
        const val BROADCAST_LOGIN_SUCCESS_INTENT = "com.tsg.hawksbaseball.BROADCAST_LOGIN_SUCCESS_INTENT"

        //用户登入
        const val BROADCAST_USER_LOGIN = "com.tsg.hawksbaseball.BROADCAST_USER_LOGIN"

        //用户登出
        const val BROADCAST_USER_LOGOUT = "com.tsg.hawksbaseball.BROADCAST_USER_LOGOUT"

        //任务列表更新成功
        const val BROADCAST_TASK_REFRESH = "com.tsg.hawksbaseball.BROADCAST_TASK_REFRESH"

        //入场通知
        const val BROADCAST_FCM_TASK = "com.tsg.hawksbaseball.BROADCAST_FCM_TASK"
        const val TASK_CODE_JOIN = "1-1"
        const val TASK_CODE_GET_COUNT = "1-2"
        const val TEAM_NAME_KAOHSIUNG_STEELERS = "高雄鋼鐵人"
        const val TEAM_NAME_LIEYING = "臺南台鋼獵鷹"
        const val TEAM_NAME_TAIPEI_FUBON_BRAVES = "臺北富邦勇士"
        const val TEAM_NAME_TAOYUAN_PAUIAN_PILOTS = "桃園璞園領航猿"
        const val TEAM_NAME_FORMOSA_DREAMERS = "福爾摩沙夢想家"
        const val TEAM_NAME_HSINCHU_TOPLUS_LIONEERS = "新竹御頂攻城獅"
        const val TEAM_NAME_NEW_TAIPEI_KINGS = "新北國王"
        const val TEAM_NAME_YANKEE_ENGINEERING = "洋基工程"


        const val TEAM_NAME_TAI_PEI = "臺北富邦勇士"
        const val TEAM_ID_TAI_PEI = "e86394aa-3d88-11ed-8ba8-7f44ba46b0b1"
        const val TEAM_NAME_GAO_XIONG = "高雄鋼鐵人"
        const val TEAM_ID_GAO_XIONG = "e86cac11-3d88-11ed-8637-7f44ba46b0b1"
        const val TEAM_ID_LIE_YING = "3c3a601a-358a-11ed-ab92-711c34ed1298"

        const val TEAM_CODE_XIONG_YING = "AKP011"
        //        const val TEAM_NAME_QUAN_LONG = "味全龍"
//        const val TEAM_CODE_QUAN_LONG = "AAA011"
//        const val TEAM_NAME_HAN_JIANG = "富邦悍將"
//        const val TEAM_CODE_HAN_JIANG = "AEO011"
        const val TEAM_NAME_TAO_YUAN = "桃園璞園領航猿"
        const val TEAM_ID_TAO_YUAN = "e865bb20-3d88-11ed-a956-7f44ba46b0b1"

//        const val TEAM_CODE_TAO_YUAN = "AJL011"
//        const val TEAM_NAME_YI_SHI = "統一獅"
//        const val TEAM_CODE_YI_SHI = "ADD011"

    }
    private var uploadDialog: UpLoadingDialog? = null
    fun showLoadingUI(isShow: Boolean, context: Context) {
        if (isShow) {
            closeLoadingDialog()
            if (uploadDialog == null) {
                uploadDialog = UpLoadingDialog.Builder(context).createDialog()
            }
            uploadDialog!!.show()
        } else {
            closeLoadingDialog()
        }
    }
    fun closeLoadingDialog() {
        if (uploadDialog != null) {
            uploadDialog!!.dismiss()
            uploadDialog = null
        }
    }
    fun dp2px(dp: Float): Float {
        return resources.displayMetrics.density * dp + 0.5f
    }

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        context = applicationContext
        instance = this
    }
}