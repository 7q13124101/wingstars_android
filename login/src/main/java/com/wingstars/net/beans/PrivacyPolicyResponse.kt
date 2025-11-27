package com.wingstars.net.beans

import java.io.Serializable

data class PrivacyPolicyResponse(
    var top_title: String?,
    var top_title_content: String?,
    var policy_data: List<PrivacyPolicyData>?
){
    data class PrivacyPolicyData(
        val title: String,
        val content: String

    ): Serializable
}