package com.wingstars.count.viewmodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CountNewDetailViewModel (
    val id: Int,
    val image: Int,
    val title: String,
    val count: String,
    val expiryDate: String,
    val exitem:String,
    val limit:String,
    val total:String,
    val location:String,
    val usageRules: String,
    val description: String

): Parcelable