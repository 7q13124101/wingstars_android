package com.wingstars.base.net.beans

import java.io.Serializable

data class PhrasesBean(
    var title: String,
    var UniformNo: String = "",
    var isSelected: Boolean = false
) : Serializable

data class ColorData(
    var selectColor: String = "#FF000000",
    var progress: Float = 0f,
    var gradientStartColor: String = "#FF000000"
) : Serializable

data class CheerData(
    var cheerStr: String = "",
    var phrases: String = "",
    var memberName: String = "",
    var fontSizeStr: String = "中",
    var playSpeedStr: String = "1X",
    var fontData: ColorData = ColorData(),
    var backgroundData: ColorData = ColorData()
) : Serializable

data class RosterItem(
    val title: Rendered? = null
)

data class Rendered(
    val rendered: String? = null
)