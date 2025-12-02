package com.wingstars.base.net.beans

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.wingstars.base.net.NetBase
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import java.security.SecureRandom
import java.util.Base64
import java.security.MessageDigest

object CRMHashKey {
    private const val AES_ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val KEY_ALGORITHM = "AES"
    private const val IV_LENGTH = 16 // 128 bits for AES block size
    private const val key = "ns#key*!d@h(fq^wz,hg"
    private var os_Version = ""
    private var app_Version = ""

    init {
        //取系统版号、app版号
        try {
            os_Version = Build.VERSION.RELEASE
//            val packageManager: PackageManager = NetBase.shared()!!.packageManager
//            val packageInfo: PackageInfo = packageManager.getPackageInfo(NetBase.shared()!!.packageName, 0)
//            app_Version = packageInfo.versionName!!

//            println("Client: $os_Version, Version: $app_Version")
        } catch(e: Exception){
            println("Exception: ${e.message}")
        }
    }
    fun hashKey(): String {
        var HashKey = ""
        try {
            // 获取HMAC SHA256算法的Mac实例
            val mac = Mac.getInstance("HmacSHA256")
            val key = decrypt(NetBase.CRM_APP_KEY_ENC)
            val secretKeySpec = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "HmacSHA256")
            mac.init(secretKeySpec)

            // 计算哈希值
            val timeP = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
            val timestamp = Instant.now()
            val zoneDateTime = timestamp.atZone(ZoneId.systemDefault())
            val timeF = zoneDateTime.format(timeP)

            val rawHmac = mac.doFinal(timeF.toByteArray(Charsets.UTF_8))

            // 将字节转换为十六进制字符串
            HashKey = rawHmac.fold("") { str, it -> str + "%02x".format(it) }

            Log.d("CRMHashKey", "timestamp: ${timeF}, HashKey: $HashKey")
        } catch (ex: Exception) {
            Log.e("CRMHashKey", "Exception msg:${ex.message}")
        }

        return HashKey
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

//    fun urlEncode(primary: String): String {
//        var encoded = ""
//        try {
//            encoded = URLEncoder.encode(primary, "UTF-8")
//
//            Log.d("urlEncode", "encoded: $encoded")
//        } catch (ex: Exception) {
//            Log.e("urlEncode", "Exception msg:${ex.message}")
//        }
//
//        return encoded
//    }
}