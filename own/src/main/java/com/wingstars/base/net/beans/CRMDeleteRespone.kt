package com.wingstars.base.net.beans

class CRMDeleteRespone (
    val success: Boolean,
    val message: String,
    val time: String,
    var data: Any? = null
)