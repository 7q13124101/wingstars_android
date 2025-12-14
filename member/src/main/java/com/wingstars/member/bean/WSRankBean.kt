package com.wingstars.member.bean

import java.io.Serializable

data class WSRankBean(
    var title: String,
    var acf: MutableList<ACFBean>?=null,
    var content: String?=null
): Serializable{
    data class ACFBean(
        var name: String,
        var volume: String,
        var image: String=""
    ): Serializable
}
