package com.wingstars.base.net.beans

data class CRMUpdateContactRequest(
    val address: String? = null,
    val birthday: String? = null,
    val carrierCode: String? = null,
    val city: String? = null,
    val district: String? = null,
    val email: String? = null,
    val extraData: CRMExtraData? = null,
    val gender: String? = null,
    val identity: String? = null,
    val name: String? = null,
    val newsoftExtraData: CRMNewsoftExtraData? = null,
    val stores: String? = null
)
