package com.wingstars.user.net

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class DataConvert {
    @OptIn(ExperimentalEncodingApi::class)
    public fun base64Encode(data: ByteArray): String {
        return Base64.encode(data)
    }

    @OptIn(ExperimentalEncodingApi::class)
    public fun base64Decode(data: String): ByteArray {
        return Base64.decode(data)
    }
}