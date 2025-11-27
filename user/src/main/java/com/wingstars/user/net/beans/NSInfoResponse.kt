package com.wingstars.user.net.beans

class NSInfoResponse (
    val code: Int,
    val message: String,
    val data: String,
) {
    val successed: Boolean
        get() {
            return code == 2000
        }
}