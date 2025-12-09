package com.wingstars.count.viewmodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize

data class CountListItemViewModel (
    val id:Int,
    val title: String,
    val time: String,
    val count: String,
    val leftImageRes: Int,
    val exitem:String,
    val limit:String,
    val total:String,
    val location:String,
    val usageRules: String,
    val information: String,
    val description: String
): Parcelable