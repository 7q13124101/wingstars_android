package com.wingstars.base.net.beans

data class EvtCheckinResponse(
    val status: String?,            //"Success" | "AlreadyCheckedIn"
    val point: Int?,                //
    val message: String?,           //error message
) {
    val statusF: Int      //status format
        get() {
            //return status?.equals("Success") ?: false
            return if(status == null) {
                -1
            } else if(status == "Success") {
                0
            } else {
                1
            }
        }
}
