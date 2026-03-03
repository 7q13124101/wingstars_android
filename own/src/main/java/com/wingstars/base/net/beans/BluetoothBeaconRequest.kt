package com.wingstars.base.net.beans

data class BluetoothBeaconRequest(
    val btbId: String,
    val deviceId: String,
    val interactionTime: String,
    val taskUuid: String,
    val beaconUuid: String,
    val beaconMajor: Int,
    val beaconMinor: Int,
    val deviceOtherInfo: String = "",
    val additionalBluetoothInfo: String = ""
)