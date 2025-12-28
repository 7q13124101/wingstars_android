package com.wingstars.base.net.beans

data class BeaconListResponse(
    val code: Int,
    val `data`: Data,
    val message: String
) {
    val successed: Boolean
        get() {
            return code == 2000
        }

    data class Data(
        val current: Int,
        val pages: Int,
        val records: List<Record>,
        val size: Int,
        val total: Int
    ) {
        data class Record(
            val address: String,
            val address_description: String,
            val btbName: String,
            val btbUuid: String,
            val createdAt: String,
            val deletedAt: Any,
            val id: String,
            val isDelete: Int,
            val major: Int,
            val minor: Int,
            val signalIntensityThreshold: Int,
            val sub_address: String,
            val sub_address_description: String,
            val updatedAt: String
        )
    }
}