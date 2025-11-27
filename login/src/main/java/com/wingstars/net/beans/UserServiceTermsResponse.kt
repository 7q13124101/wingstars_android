package com.wingstars.net.beans

import java.io.Serializable

data class UserServiceTermsResponse(
    var top_title: String?,
    var top_title_content: String?,
    var policy_data: List<UserServiceTermsData>?
){
    data class UserServiceTermsData(
        val title: String,
        val content: String

    ): Serializable
}