package com.wingstars.base.net.beans

import java.io.Serializable

data class CRMBaseResponse<T>(
    val success: Boolean,
    val message: String,
    val time: String,
    val meta: Meta,
    val data: T,
) {
    data class Meta(
        val totalPages: Int,    //总页数
        val total: Int,         //总笔数
        val page: Int,          //当前页
        val size: Int,          //每页笔数
    ): Serializable
}
