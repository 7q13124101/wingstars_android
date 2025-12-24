package com.wingstars.base.net.beans


data class WSScheduleResponse(
    val work_date: String,
    val location: String,               //地点
    val member_number: String,
    val member_name: String,
)
