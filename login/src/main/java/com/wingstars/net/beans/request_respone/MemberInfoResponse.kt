package com.wingstars.net.beans.request_respone

data class MemberInfoResponse(
    val success: Boolean,
    val message: String,
    val data: MemberData
)

data class MemberData(
    val Phone: String?,
    val Id: String?,
    val Birthday: String?,
    val Gender: String?,
    val Name: String?,
    val Email: String?,
    val Code: String?,
    val MemberTier:membertier?
)
data class membertier(
    val EffectiveDate: String?,
    val ExpirationDate: String?,
    val TierName: String
)
