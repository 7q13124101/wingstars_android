package com.wingstars.base.net.beans

data class EvtCheckinRequest(
    val encryptedIdentity: String,          //
    val taskId: String,
) {

}
