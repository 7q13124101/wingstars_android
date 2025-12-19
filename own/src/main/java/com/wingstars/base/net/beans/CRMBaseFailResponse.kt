package com.wingstars.base.net.beans

data class CRMBaseFailResponse(
    val success: Boolean,
    val message: String,
    val time: String,
    var code: String,
    var data: Any
)
