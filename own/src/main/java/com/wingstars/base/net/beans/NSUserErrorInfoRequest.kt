package com.wingstars.base.net.beans

data class NSUserErrorInfoRequest(
    var platform: String?,
    var message: String?,
    var app_version:String?,
    var os_version:String?,
    var device_model:String?,
)
