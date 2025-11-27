package com.wingstars.user.net.beans

import java.io.Serializable

class PrivacyPolicyResponse (
    var top_title: String?,
    var top_title_content: String?,
    var policy_data: List<PrivacyPolicyData>?
){
    data class PrivacyPolicyData(
        val title: String,
        val  content: String
    ): Serializable
}