package com.wingstars.user.net

import android.app.Application
import android.content.Context
import com.wingstars.user.dialog.UpLoadingDialog

class BaseApplication: Application(){
    companion object{
        @JvmStatic
        lateinit var context: Context
            private set
        private var instance: BaseApplication? = null
        @JvmStatic
        fun shared(): BaseApplication?{
            return instance
        }
        //API Hosts
        const val CRM_MEMBER_LIST_URL = "https://61.218.209.209"
    }
    private var uploadDialog: UpLoadingDialog? = null
    fun closeLoadingDialog() {
        if (uploadDialog != null) {
            uploadDialog!!.dismiss()
            uploadDialog = null
        }
    }
    fun dp2px(dp: Float): Float {
        return resources.displayMetrics.density * dp + 0.5f
    }

}