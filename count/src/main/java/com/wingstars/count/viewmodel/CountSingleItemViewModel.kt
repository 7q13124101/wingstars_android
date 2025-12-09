package com.wingstars.count.viewmodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CountSingleItemViewModel(
    val id: Int,
    val title: String,
    val info: String,
    val time: String,
    val count: String,
    val leftImageRes: Int,
    val countIconRes: Int,
    val detailContent: String,
    val rules: String
): Parcelable
