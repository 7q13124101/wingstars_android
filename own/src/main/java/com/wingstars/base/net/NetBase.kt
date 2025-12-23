package com.wingstars.base.net

import android.app.Application
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV
import com.wingstars.base.net.beans.CRMBaseFailResponse
import com.wingstars.base.net.beans.CRMGenQRCodeRequest
import com.wingstars.base.net.beans.CRMVerifyRequest
import com.wingstars.base.net.beans.EvtTaskResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import java.util.Base64
import javax.crypto.spec.SecretKeySpec



object NetBase : Application(){
    private const val AES_ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val KEY_ALGORITHM = "AES"
    private const val IV_LENGTH = 16 // 128 bits for AES block size
    private const val key = "ns#key*!d@h(fq^wz,hg"
    private var os_Version = ""
    private var app_Version = ""

    //测试区
    const val HOST_BASE = "https://61.218.209.209"
    const val HOST_CRM = "https://ws-crm-dev.newretail.tw"
    const val HOST_EVENT = "https://ws-event-dev.newretail.tw/"

    const val CRM_HOST = "https://ws-crm-dev.newretail.tw"
    const val HOST_GOOGLE = "https://www.googleapis.com"
    const val YOUTUBE_CHANNEL_ID = "UCSEI3nk0QSGcQKR75O6vM6Q"
    const val YOUTUBE_API_KEY = "AIzaSyAzRyrgUFVXePO4vfx0RuVKqn2W64RlyQk"
    const val API_KEY = "8e2KeU3Bntw43R09tNE1"
    const val TOKEN_TYPE = "Bearer"
    const val WINGSTARS_ACCOUNT_ENC = "OaAJUXD7ZN20fekfVqN3uJzbbqf4LP8vR7AMXPVlFaU="                        //"newsoftapp"
    const val WINGSTARS_PASSWORD_ENC = "gZR514+qAhvFIRr+eRoQ0Qo5/OVEOrnL4OMd/40ACtKzIvdjNnYFq/vNLe5/Uerm"   //"VU4m E5kG Azeu Rryo JmxT BXAj"
    const val CONSUMER_KEY_ENC = "9humcXmIssTG1JdlihQocOdH6D5tNQDImi7CP5cHvyfkq40DcosNRBXIxd9nFlsFX0QNz6v36iY+vjfMaju7tw=="     //"ck_0de8be632e78d179c2ebcd1215c301198a75944a"
    const val CONSUMER_SECRET_ENC = "XV0kwKPmag27wEZM61zHpFQp6Bjgo2rYdNX6HUycvteJxjVZoD9dcvCpsSQTrwNpYqlCX3UPxAU6Gbjm0yM5Jg=="  //"cs_87db163ae5d871a3913ee961261a37152fd14fe1"
    const val BROADCAST_LOGIN_SUCCESS_INTENT = "com.tsg.wingstar.BROADCAST_LOGIN_SUCCESS_INTENT"
    const val BROADCAST_TASK_REFRESH = "com.tsg.wingstar.BROADCAST_TASK_REFRESH"

    const val HOST_HAWK_EVENT = "https://ws-event-dev.newretail.tw"
    //用户登入
    const val BROADCAST_USER_LOGIN = "com.tsg.wingstar.BROADCAST_USER_LOGIN"

    //用户登出
    const val BROADCAST_USER_LOGOUT = "com.tsg.wingstar.BROADCAST_USER_LOGOUT"

    //正式区
//    const val HOST_BASE = "https://www.tsghawks.com"

//    const val WINGSTARS_ACCOUNT_ENC = "OaAJUXD7ZN20fekfVqN3uJzbbqf4LP8vR7AMXPVlFaU="                        //"newsoftapp"
//    const val WINGSTARS_PASSWORD_ENC = "gZR514+qAhvFIRr+eRoQ0Qo5/OVEOrnL4OMd/40ACtKzIvdjNnYFq/vNLe5/Uerm"   //"VU4m E5kG Azeu Rryo JmxT BXAj"
//    const val CONSUMER_KEY_ENC = "9humcXmIssTG1JdlihQocOdH6D5tNQDImi7CP5cHvyfkq40DcosNRBXIxd9nFlsFX0QNz6v36iY+vjfMaju7tw=="     //"ck_0de8be632e78d179c2ebcd1215c301198a75944a"
//    const val CONSUMER_SECRET_ENC = "XV0kwKPmag27wEZM61zHpFQp6Bjgo2rYdNX6HUycvteJxjVZoD9dcvCpsSQTrwNpYqlCX3UPxAU6Gbjm0yM5Jg=="  //"cs_87db163ae5d871a3913ee961261a37152fd14fe1"


    init {
        try {
            os_Version = Build.VERSION.RELEASE
//            val packageManager: PackageManager = BaseApplication.shared()!!.packageManager
//            val packageInfo: PackageInfo =
//                packageManager.getPackageInfo(BaseApplication.shared()!!.packageName, 0)
//            app_Version = packageInfo.versionName!!

//            println("Client: $os_Version, Version: $app_Version")
        } catch(e: Exception){
            println("Exception: ${e.message}")
        }
    }
    private var taskListData: ArrayList<EvtTaskResponse> = ArrayList()
    private var checkInListData: ArrayList<EvtTaskResponse> = ArrayList()
    private var taskListIsCompleted = true

    public fun base64Encode(data: ByteArray): String {
        return Base64.getEncoder().encodeToString(data)
    }

    public fun base64Decode(data: String): ByteArray {
        return Base64.getDecoder().decode(data)
    }

    fun clientVersion(): String {
        return "android$os_Version"
    }

    fun appVersion(): String {
        return "$app_Version"
    }

    // 加密函数
    fun encrypt(plainText: String): String {
        val keySpec = generateKey(key)
        val ivBytes = generateIv()
        val cipher = Cipher.getInstance(AES_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, IvParameterSpec(ivBytes))

        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        // 将IV和密文合并后Base64编码
        val combined = ivBytes + encryptedBytes
        return Base64.getEncoder().encodeToString(combined)
    }

    // 解密函数
    fun decrypt(encryptedText: String): String {
        val keySpec = generateKey(key)
        val combined = Base64.getDecoder().decode(encryptedText)

        // 提取IV（前16字节）和实际密文
        val ivBytes = combined.copyOfRange(0, IV_LENGTH)
        val cipherBytes = combined.copyOfRange(IV_LENGTH, combined.size)

        val cipher = Cipher.getInstance(AES_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, IvParameterSpec(ivBytes))

        val decryptedBytes = cipher.doFinal(cipherBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }

    // 生成密钥（使用SHA-256哈希确保长度正确）
    private fun generateKey(key: String): SecretKeySpec {
        val digest = MessageDigest.getInstance("SHA-256")
        val keyBytes = digest.digest(key.toByteArray(Charsets.UTF_8))
        return SecretKeySpec(keyBytes, KEY_ALGORITHM)
    }

    // 生成随机IV
    private fun generateIv(): ByteArray {
        val iv = ByteArray(IV_LENGTH)
        SecureRandom().nextBytes(iv)
        return iv
    }
    private var isCrmTokenCompleted = false
    fun getCrmTokenCompleted(): Boolean {
        return isCrmTokenCompleted
    }

    fun setCrmTokenCompleted(value: Boolean) {
        this.isCrmTokenCompleted = value
    }
    fun getCRMQauthToken() {
        //Oauth > 客户端验证
        API?.shared?.api?.let {
            val observer =
                it.crmVerify("${NetBase.HOST_CRM}/api/v1/oauth/verify", CRMVerifyRequest())
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    if (next.success) {
                        setCrmTokenCompleted(true)
                        val rd = next.data
                        MMKV.defaultMMKV().encode("crm_client_id", rd.id)
                        MMKV.defaultMMKV().encode("crm_client_access_token", rd.accessToken)
                        MMKV.defaultMMKV().encode("crm_client_refresh_token", rd.refreshToken)
                    }
                },
                { error ->
                    error.message?.let { it1 ->
                    }
                }
            )
        }
    }
    private fun getEvtMemberTasks(encryptedIdentity: String, bRefreshUI: Boolean) {
        //Event > 会员任务状态列表
        API.shared?.api?.let {

            // encryptedIdentity 的值通过 api crmGenQRCode 取得
            //val encryptedIdentity = "RMafhFbPQedleQ0E6fk9P8gNEoXdwAjZTULb1bLk73Ute9axTtxxSAonuM2jJ3WaXsN4zlpq3SkFZUB8NlNVtNAmX1myKBeOBerbk56Uu+YTKlHNB+/0iCh9R+5wEV+HvRNU7/RU/DKZZf+jU2L88w=="
            val observer = it.evtMemberTasks(
                "${HOST_HAWK_EVENT}/api/v1/public/members/tasks",
                encryptedIdentity
            )
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    if (!next.isNullOrEmpty()) {
                        next.forEach { tr ->
                            val node = taskListData.find { it.id == tr.id }
                            if (node != null) {
                                node.status = tr.personalEventTaskStatus
                                node.isSendApiF = false
                            }
                        }

                        checkInListData.clear()
                        taskListData.forEach {
                            if (it.triggerType == "app" && !it.triggerTag.isNullOrEmpty() && it.triggerTag == "checkin") {
                                checkInListData.add(it)
                            }

                        }

                        if (bRefreshUI)
                            sendBroadcast(Intent(NetBase.BROADCAST_TASK_REFRESH))
                    }
                    taskListIsCompleted = true
                },
                { error ->
                    taskListIsCompleted = true
                    error.message?.let { it1 ->
                    }
                }
            )
        }
    }
    fun refreshEvtTasks(bLogin: Boolean, bRefreshUI: Boolean = true) {
        if(taskListData.isNotEmpty()) {
            if (bLogin && MMKV.defaultMMKV().decodeBool("isLogin")) {
                val id = MMKV.defaultMMKV().decodeString("crm_member_id")
                val phone = MMKV.defaultMMKV().decodeString("member_phone")
                API?.shared?.api?.let {
                    //Member > 会员QRCode
                    val observer =
                        it.crmGenQRCode("${NetBase.HOST_CRM}/api/v1/basic/member/${id}/gen-qrcode",
                            phone?.let { it1 -> CRMGenQRCodeRequest(it1) })
                    observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                        AndroidSchedulers.mainThread()
                    )?.subscribe(
                        { next ->

                            if (next.success && next.data != null) {
                                getEvtMemberTasks(next.data.MEMQRCODE, bRefreshUI)
                            } else {
                                taskListIsCompleted = true
//                            Toast.makeText(BaseApplication.shared()!!,next.message, Toast.LENGTH_LONG).show()
                            }
                        },
                        { error ->
                            taskListIsCompleted = true
                            var msg = error.message.toString()

                            if (error is HttpException) {
                                try {
                                    val gson = Gson()
                                    val type = object : TypeToken<CRMBaseFailResponse>() {}.type
                                    val failResponse = gson.fromJson<CRMBaseFailResponse>(
                                        error.response()?.errorBody()?.string(), type
                                    )
                                    if (failResponse != null) {
                                        failResponse.message?.let {
                                            msg = it
                                        }
                                    }
                                } catch (e: Exception) {

                                }
                            }

                            msg.let { it1 ->
//                            Toast.makeText(BaseApplication.shared()!!,"$it1", Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                }
            } else {
                //清除任务状态，恢复到原始状态
                taskListData.forEach {
                    it.status = "unlock"
                    it.isSendApiF = false
                }
                //退出登录状态，需要重置状态并同步UI中
                if (bRefreshUI)
                    sendBroadcast(Intent(NetBase.BROADCAST_TASK_REFRESH))
            }
        }
    }

    fun ut() {

        //加密 API key
//        var enc = ""
//        var dec = ""
//        enc = encrypt("")
//        println("WINGSTARS_ACCOUNT_ENC. enc: '$enc'")

        API.shared?.api?.let {
            //今日行程
//            val observer = it.wsSchedule(10, 1)
//            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
//                AndroidSchedulers.mainThread())?.subscribe(
//                { next ->
//                    Log.d("API", "[wsSchedule] next.data.size: ${next.size}")
//
//                    for (pd in next) {
//                        println("title: ${pd.titleF}, content: ${pd.contentF}, st_date: ${pd.st_dateF}, map: ${pd.mapF}, Precautions: ${pd.PrecautionsF}, category: ${pd.calendar_categoryF}, urlF: ${pd.urlF}")
//                    }
//                },
//                { error ->
//                    error.message?.let { it1 ->
//                        Log.d("API", "[wsSchedule] error.message: ${it1?.toString()}")
//                    }
//                }
//            )

            //热销商品
//            val observer = it.wsProducts()
//            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
//                AndroidSchedulers.mainThread())?.subscribe(
//                { next ->
//                    Log.d("API", "[wsProducts] next.data.size: ${next.size}")
//
//                    for (rd in next) {
//                        println("id: ${rd.id}, name: ${rd.name}, price: ${rd.price}, url: ${rd.urlF}, permalink: ${rd.permalink}")
//                        println("  image: ${rd.imageF}")
//                    }
//                },
//                { error ->
//                    error.message?.let { it1 ->
//                        Log.d("API", "[wsProducts] error.message: ${it1?.toString()}")
//                    }
//                }
//            )

            //最新消息
//            val observer = it.wsPosts()
//            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
//                AndroidSchedulers.mainThread())?.subscribe(
//                { next ->
//                    Log.d("API", "[wsPosts] next.data.size: ${next.size}")
//
//                    for (rd in next) {
//                        println("title: ${rd.titleF}, date: ${rd.dateF}, url: ${rd.urlF}, link: ${rd.link}")
//                    }
//                },
//                { error ->
//                    error.message?.let { it1 ->
//                        Log.d("API", "[wsPosts] error.message: ${it1?.toString()}")
//                    }
//                }
//            )

            //氛围时尚
//            val  params = HashMap<String, Int>()
//            //params.put("fashion_category", 362) //氛围时尚类别(361:應援服, 362:活動服) //不设 fashion_category 值时查询所有类别
//            val observer = it.wsFashions(params, 100, 1)
//            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
//                AndroidSchedulers.mainThread())?.subscribe(
//                { next ->
//                    Log.d("API", "[wsFashions] next.data.size: ${next.size}")
//
//                    for (rd in next) {
//                        println("title: ${rd.titleF}, fashion_category: ${rd.fashion_categoryF}, url: ${rd.urlF}")
//                    }
//                },
//                { error ->
//                    error.message?.let { it1 ->
//                        Log.d("API", "[wsFashions] error.message: ${it1?.toString()}")
//                    }
//                }
//            )

            //人气排行-名次
//            val observer = it.wsRank()
//            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
//                AndroidSchedulers.mainThread())?.subscribe(
//                { next ->
//                    Log.d("API", "[wsRank] next.data.size: ${next.size}")
//
//                    for (rd in next) {
//                        println("title: ${rd.titleF}, content: ${rd.contentF}")
//                        for (i in 1..10){
//                            val rankBean = rd.acf.rankBean(i)
//                            println("  $i. name: ${rankBean?.name}, volume: ${rankBean?.volume}")
//                        }
//                    }
//                },
//                { error ->
//                    error.message?.let { it1 ->
//                        Log.d("API", "[wsRank] error.message: ${it1?.toString()}")
//                    }
//                }
//            )

            //成员 > 成员介绍
//            val observer = it.wsMembers(100, 1)
//            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
//                AndroidSchedulers.mainThread())?.subscribe(
//                { next ->
//                    Log.d("API", "[wsMembers] next.data.size: ${next.size}")
//
//                    for (rd in next) {
//                        println("title: ${rd.titleF}, number: ${rd.acf.number}, fb_link: ${rd.acf.fb_link}, ig_link: ${rd.acf.ig_link}, about: ${rd.acf.about}, say: ${rd.acf.say}, interest: ${rd.acf.interest}, height: ${rd.acf.height}, weight: ${rd.acf.weight}, birthdate: ${rd.acf.birthdate}, sign: ${rd.acf.sign}, blood_type: ${rd.acf.blood_type}, url: ${rd.urlF}")
//                    }
//                },
//                { error ->
//                    error.message?.let { it1 ->
//                        Log.d("API", "[wsMembers] error.message: ${it1?.toString()}")
//                    }
//                }
//            )

            //人气排行-名次对应成员头贴图片
//            val observer = it.wsPhotos(100, 1)
//            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
//                AndroidSchedulers.mainThread())?.subscribe(
//                { next ->
//                    Log.d("API", "[wsPhotos] next.data.size: ${next.size}")
//
//                    for (rd in next) {
//                        println("title: ${rd.titleF}, url: ${rd.urlF}")
//                    }
//                },
//                { error ->
//                    error.message?.let { it1 ->
//                        Log.d("API", "[wsPhotos] error.message: ${it1?.toString()}")
//                    }
//                }
//            )

            //成员 > 拍照图框
//            val observer = it.wsPhotoFrames(100, 1)
//            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
//                AndroidSchedulers.mainThread())?.subscribe(
//                { next ->
//                    Log.d("API", "[wsPhotoFrames] next.data.size: ${next.size}")
//
//                    for (rd in next) {
//                        println("title: ${rd.titleF}, number: ${rd.numberF}, frameUrl: ${rd.frameUrlF}")
//                    }
//                },
//                { error ->
//                    error.message?.let { it1 ->
//                        Log.d("API", "[wsPhotoFrames] error.message: ${it1?.toString()}")
//                    }
//                }
//            )

            //成员 > 氛围时尚-分类
//            val observer = it.wsFashionCategorys()
//            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
//                AndroidSchedulers.mainThread())?.subscribe(
//                { next ->
//                    Log.d("API", "[wsFashionCategorys] next.data.size: ${next.size}")
//
//                    for (rd in next) {
//                        println("id: ${rd.id}, name: ${rd.name}")
//                    }
//                },
//                { error ->
//                    error.message?.let { it1 ->
//                        Log.d("API", "[wsFashionCategorys] error.message: ${it1?.toString()}")
//                    }
//                }
//            )

            //成员 > 氛围时尚-内页
//            val fashion_id = 71886
//            val observer = it.wsFashion(fashion_id)
//            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
//                AndroidSchedulers.mainThread())?.subscribe(
//                { next ->
//                    Log.d("API", "[wsFashion] next.data.id: ${next.id}")
//
//                    println("title: ${next.titleF}, content: ${next.contentF}")
//                    for (i in 1..5){
//                        val recommend = next.acf.recommend(i)
//                        println("  $i > 商品標題: ${recommend?.product_title}, 商品連結: ${recommend?.product_url}, 商品圖片: ${recommend?.product_image_urlF}")
//                    }
//
//                    for (i in 1..5){
//                        val image = next.acf.gallery_image_urls.image(i)
//                        println("  $i > gallery url: ${image?.image_urlF}")
//                    }
//                },
//                { error ->
//                    error.message?.let { it1 ->
//                        Log.d("API", "[wsFashion] error.message: ${it1?.toString()}")
//                    }
//                }
//            )

            //日历
//            val observer = it.wsCalendar(1, 2)
//            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
//                AndroidSchedulers.mainThread())?.subscribe(
//                { next ->
//                    Log.d("API", "[wsCalendar] next.data.size: ${next.size}")
//
//                    for (pd in next) {
//                        println("title: ${pd.titleF}, content: ${pd.contentF}, date: ${pd.dateF}, map: ${pd.mapF}, Precautions: ${pd.PrecautionsF}, category: ${pd.calendar_categoryF}")
//                        println("  url: ${pd.urlF}")//pd.yoast_head_json类型有误，数组[]还是对象{}？
//                    }
//                },
//                { error ->
//                    error.message?.let { it1 ->
//                        Log.d("API", "[wsCalendar] error.message: ${it1?.toString()}")
//                    }
//                }
//            )

            //日历-分类
//            val observer = it.wsCalendarCategory(100, 1)
//            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
//                AndroidSchedulers.mainThread())?.subscribe(
//                { next ->
//                    Log.d("API", "[wsCalendarCategory] next.data.size: ${next.size}")
//
//                    for (pd in next) {
//                        println("id: ${pd.id}, name: ${pd.name}")
//                    }
//                },
//                { error ->
//                    error.message?.let { it1 ->
//                        Log.d("API", "[wsCalendarCategory] error.message: ${it1?.toString()}")
//                    }
//                }
//            )

            //查询指定客户 customer_id
//            val consumer_key = NetBase.decrypt(NetBase.CONSUMER_KEY_ENC)
//            val consumer_secret = NetBase.decrypt(NetBase.CONSUMER_SECRET_ENC)
//            val email = "sapido@gmail.com"
//            val observer = it.wsCustomer(consumer_key, consumer_secret, email)
//            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
//                AndroidSchedulers.mainThread())?.subscribe(
//                { next ->
//                    Log.d("API", "[wsCustomer] next.data.size: ${next.size}")
//
//                    for (pd in next) {
//                        println("id: ${pd.id}")
//                    }
//                },
//                { error ->
//                    error.message?.let { it1 ->
//                        Log.d("API", "[wsCustomer] error.message: ${it1?.toString()}")
//                    }
//                }
//            )

            //查询指定客户订单
//            val consumer_key = NetBase.decrypt(NetBase.CONSUMER_KEY_ENC)
//            val consumer_secret = NetBase.decrypt(NetBase.CONSUMER_SECRET_ENC)
//            val customer = 961
//            val observer = it.wsOrders(consumer_key, consumer_secret, customer, 100, 1)
//            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
//                AndroidSchedulers.mainThread())?.subscribe(
//                { next ->
//                    Log.d("API", "[wsOrders] next.data.size: ${next.size}")
//
//                    for (pd in next) {
//                        println("id: ${pd.id}, 會員姓名: ${pd.customer_nameF}, 訂單日期: ${pd.date_createdF}, 運費: ${pd.shipping_total}, 總金額: ${pd.total}")
//                        for(li in pd.line_items) {
//                            println("  商品名稱: ${li.name}, 價格: ${li.price}, 數量: ${li.quantity}, 小計: ${li.subtotal}, 圖片: ${li.urlF}")
//                        }
//                    }
//                },
//                { error ->
//                    error.message?.let { it1 ->
//                        Log.d("API", "[wsOrders] error.message: ${it1?.toString()}")
//                    }
//                }
//            )

        }
    }
}