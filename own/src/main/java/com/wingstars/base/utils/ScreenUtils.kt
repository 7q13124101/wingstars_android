package com.wingstars.base.utils

import android.app.Activity

import android.util.DisplayMetrics



class ScreenUtils {
    companion object {
        fun  getWidth( activity: Activity):Int{
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels
            return width
        }

        fun  getHeight( activity: Activity):Int{
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val height = displayMetrics.heightPixels
            return height
        }
    }
}