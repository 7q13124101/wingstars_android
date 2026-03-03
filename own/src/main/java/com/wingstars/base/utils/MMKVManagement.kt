package com.wingstars.base.utils

import android.content.Context
import com.tencent.mmkv.MMKV

class MMKVManagement {
    companion object {
        //定义存储的字段名
        private var FONT_SCALE = "font_scale" //字体大小倍数的字段名
        private val SAVE_LANGUAGE = "save_language"   //系统语言信息
        private val IS_LOGIN = "isLogin"       //是否登录的字段名
        private val CRMMEMBERID = "crm_member_id"       //会员id
        private val MEMBER_PHONE = "member_phone"        //用户账号的字段名
        private val FIRST_OPEN = "first_open"           //用户是否首次打开app的字段名
        private val NS_USER_ID = "ns_user_id"           //中继登录返回的用户id
        private val NS_ACCESS_TOKEN = "ns_access_token" //中继登录返回的用户token
        private val NS_REFRESH_TOKEN = "ns_refresh_token" //中继登录返回的用户refresh_token
        private val CRM_CLIENT_ACCESS_TOKEN = "crm_client_access_token"  //CRM的clientToken
        private val CRM_MEMBER_ACCESS_TOKEN = "crm_member_access_token"  //CRM的会员token

        private val IS_REMEMBER_ACCOUNT = "isRememberAccount"   //是否记住账号的标记

        private val CRM_MEMBER_REFRESH_TOKEN = "crm_member_refresh_token" //CRM用户的刷新Token

        private val CRM_MEMBER_USER_TYPE = "crm_member_user_type"   //CRM的会员用户类型

        private val CRM_MEMBER_CODE = "crm_member_code"

        private val MEMBER_PSD = "member_psd"   //用户密码

        private val CRM_CLIENT_ID = "crm_client_id"

        private val CRM_CLIENT_REFRESH_TOKEN = "crm_client_refresh_token"

        private val MEMBER_BIRTHDAY = "member_birthday"

        private val MEMBER_GENDER = "member_gender"

        private val MEMBER_NAME = "member_name"

        private val MEMBER_MAIL = "member_mail"

        private val MEMBER_IDENTITY = "member_identity"

        private val CRM_MEMBER_EXPIRED_DATE = "crm_expired_date"

        //---------------------------请西安同事在下面新增MMKV相关set/get方法---------------------

        public fun init(context: Context) {
            MMKV.initialize(context)
        }

        public fun getCrmClientRefreshToken(): String {
            return MMKV.defaultMMKV().decodeString(CRM_CLIENT_REFRESH_TOKEN, "")!!
        }

        public fun setCrmClientRefreshToken(crm_client_refresh_token: String) {
            MMKV.defaultMMKV().encode(CRM_CLIENT_REFRESH_TOKEN, crm_client_refresh_token)
        }

        public fun getCrmClientId(): String {
            return MMKV.defaultMMKV().decodeString(CRM_CLIENT_ID, "")!!
        }

        public fun setCrmClientId(crm_client_id: String) {
            MMKV.defaultMMKV().encode(CRM_CLIENT_ID, crm_client_id)
        }

        public fun getCrmMemberCode(): String {
            return MMKV.defaultMMKV().decodeString(CRM_MEMBER_CODE, "")!!
        }

        public fun setCrmMemberCode(crm_member_code: String) {
            MMKV.defaultMMKV().encode(CRM_MEMBER_CODE, crm_member_code)
        }
        public fun getCrmMemberBarcode(): String {
            return MMKV.defaultMMKV().decodeString(CRM_MEMBER_CODE, "")!!
        }

        public fun setCrmMemberBarcode(crm_member_code: String) {
            MMKV.defaultMMKV().encode(CRM_MEMBER_CODE, crm_member_code)
        }

        public fun getCrmMemberUserType(): String {
            return MMKV.defaultMMKV().decodeString(CRM_MEMBER_USER_TYPE, "")!!
        }

        public fun setCrmMemberUserType(crm_member_user_type: String) {
            MMKV.defaultMMKV().encode(CRM_MEMBER_USER_TYPE, crm_member_user_type)
        }

        public fun getCrmMemberRefreshToken(): String {
            return MMKV.defaultMMKV().decodeString(CRM_MEMBER_REFRESH_TOKEN, "")!!
        }

        public fun setCrmMemberRefreshToken(crm_member_refresh_token: String) {
            MMKV.defaultMMKV().encode(CRM_MEMBER_REFRESH_TOKEN, crm_member_refresh_token)
        }

        public fun getIsRememberAccount(): Boolean {
            return MMKV.defaultMMKV().decodeBool(IS_REMEMBER_ACCOUNT, false)
        }

        public fun setIsRememberAccount(isRememberAccount: Boolean) {
            MMKV.defaultMMKV().encode(IS_REMEMBER_ACCOUNT, isRememberAccount)
        }


        public fun getCrmMemberAccessToken(): String {
            return MMKV.defaultMMKV().decodeString(CRM_MEMBER_ACCESS_TOKEN, "")!!
        }

        public fun setCrmMemberAccessToken(crm_member_access_token: String) {
            MMKV.defaultMMKV().encode(CRM_MEMBER_ACCESS_TOKEN, crm_member_access_token)
        }

        public fun getCrmClientAccessToken(): String {
            return MMKV.defaultMMKV().decodeString(CRM_CLIENT_ACCESS_TOKEN, "")!!
        }

        public fun setCrmClientAccessToken(crm_client_access_token: String) {
            MMKV.defaultMMKV().encode(CRM_CLIENT_ACCESS_TOKEN, crm_client_access_token)
        }

        public fun getNsRefreshToken(): String {
            return MMKV.defaultMMKV().decodeString(NS_REFRESH_TOKEN, "")!!
        }

        public fun setNsRefreshToken(ns_refresh_token: String) {
            MMKV.defaultMMKV().encode(NS_REFRESH_TOKEN, ns_refresh_token)
        }

        public fun getNsAccessToken(): String {
            return MMKV.defaultMMKV().decodeString(NS_ACCESS_TOKEN, "")!!
        }

        public fun setNsAccessToken(ns_access_token: String) {
            MMKV.defaultMMKV().encode(NS_ACCESS_TOKEN, ns_access_token)
        }

        public fun getNsUserId(): Int {
            return MMKV.defaultMMKV().decodeInt(NS_USER_ID, 0)
        }

        public fun setNsUserId(ns_user_id: Int) {
            MMKV.defaultMMKV().encode(NS_USER_ID, ns_user_id)
        }

        public fun getFontScale(): Float {
            return MMKV.defaultMMKV().decodeFloat(FONT_SCALE, 1f)
        }

        public fun setFontScale(fontScale: Float) {
            MMKV.defaultMMKV().encode(FONT_SCALE, fontScale)
        }

        public fun getFirstOpen(): Boolean {
            return MMKV.defaultMMKV().decodeBool(FIRST_OPEN, true)
        }

        public fun setFirstOpen(first_open: Boolean) {
            MMKV.defaultMMKV().encode(FIRST_OPEN, first_open)
        }

        public fun getLanguage(): Int {
            return MMKV.defaultMMKV().decodeInt(SAVE_LANGUAGE, 3)
        }

        public fun setLanguage(language: Int) {
            MMKV.defaultMMKV().encode(SAVE_LANGUAGE, language)
        }

        public fun isLogin(): Boolean {
            return MMKV.defaultMMKV().decodeBool(IS_LOGIN, false)
        }

        public fun setLogin(islogin: Boolean) {
            MMKV.defaultMMKV().encode(IS_LOGIN, islogin)
        }

        public fun getCrmMemberId(): String {
            return MMKV.defaultMMKV().decodeString(CRMMEMBERID, "0")!!
        }

        public fun setCrmMemberId(crm_member_id: String) {
            MMKV.defaultMMKV().encode(CRMMEMBERID, crm_member_id)
        }

        public fun getMemberPhone(): String {
            return MMKV.defaultMMKV().decodeString(MEMBER_PHONE, "")!!
        }

        public fun setMemberPhone(member_phone: String) {
            MMKV.defaultMMKV().encode(MEMBER_PHONE, member_phone)
        }

        public fun getMemberName(): String {
            return MMKV.defaultMMKV().decodeString(MEMBER_NAME, "")!!
        }

        public fun setMemberName(member_name: String) {
            MMKV.defaultMMKV().encode(MEMBER_NAME, member_name)
        }

        public fun getMemberPassword(): String {
            return MMKV.defaultMMKV().decodeString(MEMBER_PSD, "")!!
        }

        public fun setMemberPassword(member_psd: String) {
            MMKV.defaultMMKV().encode(MEMBER_PSD, member_psd)
        }
        fun getMemberBirthday(): String {
            return MMKV.defaultMMKV().decodeString(MEMBER_BIRTHDAY, "")!!
        }

        fun setMemberBirthday(birthday: String) {
            MMKV.defaultMMKV().encode(MEMBER_BIRTHDAY, birthday)
        }

        fun getMemberGender(): String {
            return MMKV.defaultMMKV().decodeString(MEMBER_GENDER, "")!!
        }

        fun setMemberGender(gender: String) {
            MMKV.defaultMMKV().encode(MEMBER_GENDER, gender)
        }

        fun getMemberMail(): String {
            return MMKV.defaultMMKV().decodeString(MEMBER_MAIL, "")!!
        }

        fun setMemberMail(mail: String) {
            MMKV.defaultMMKV().encode(MEMBER_MAIL, mail)
        }

        fun getMemberIdentity(): String {
            return MMKV.defaultMMKV().decodeString(MEMBER_IDENTITY, "")!!
        }

        fun setMemberIdentity(identity: String) {
            MMKV.defaultMMKV().encode(MEMBER_IDENTITY, identity)
        }

        fun getMemberExpiredDate(): String {
            return MMKV.defaultMMKV().decodeString(CRM_MEMBER_EXPIRED_DATE, "")!!
        }

        fun setMemberExpiredDate(expireddate: String) {
            MMKV.defaultMMKV().encode(CRM_MEMBER_EXPIRED_DATE, expireddate)
        }

        //---------------------------请台湾同事在下面新增MMKV相关set/get方法---------------------
        //举例set(存储)方法格式
        fun setTest(test: String) {
            MMKV.defaultMMKV().encode("test", test)
        }

        //举例get(获取)方法格式
        fun getTest(): String {
            return MMKV.defaultMMKV().decodeString("test", "")!!
        }


    }


}