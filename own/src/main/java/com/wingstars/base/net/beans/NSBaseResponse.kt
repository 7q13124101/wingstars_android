package com.wingstars.base.net.beans

data class NSBaseResponse<T>(
    val code: Int,
    val message: String,
    val data: T,
) {
    val successed: Boolean
        get() {
            return code == 2000
        }
}
