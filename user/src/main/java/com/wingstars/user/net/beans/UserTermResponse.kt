package com.wingstars.user.net.beans

import java.io.Serializable

class UserTermResponse (
    var top_title: String?,
    var top_title_content: String?,
    var policy_data: List<UserTermsData>?
){
    data class UserTermsData(
        val title: String,
        val  content: String
    ): Serializable
}