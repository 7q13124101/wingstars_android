package com.wingstars.base.net.beans

data class NSInfoResponse(
    val code: Int,
    val message: String,
    val data: String,
) {
    val successed: Boolean
        get() {
            return code == 2000
        }
}
