package com.wingstars.user.utils

import com.tencent.mmkv.MMKV

object MemberStorage {
    private val kv = MMKV.defaultMMKV()

    fun saveSelectedMembers(name1: String?, name2: String?, name3: String?) {
        kv.encode("member_name1", name1 ?: "")
        kv.encode("member_name2", name2 ?: "")
        kv.encode("member_name3", name3 ?: "")
    }

    fun getSelectedMembers(): Array<String> {
        val name1 = kv.decodeString("member_name1", "")
        val name2 = kv.decodeString("member_name2", "")
        val name3 = kv.decodeString("member_name3", "")
        return arrayOf(name1.toString(), name2.toString(), name3.toString()) as Array<String>
    }

    fun clear() {
        kv.remove("member_name1")
        kv.remove("member_name2")
        kv.remove("member_name3")
    }
}