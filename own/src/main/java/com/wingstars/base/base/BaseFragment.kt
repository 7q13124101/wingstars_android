package com.wingstars.base.base

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.gyf.immersionbar.ImmersionBar
import com.wingstars.base.view.UpLoadingDialog

open class BaseFragment : Fragment(){
    private var uploadDialog: UpLoadingDialog? = null
    public fun getStatusBarHeight(): Int{
       return ImmersionBar.getStatusBarHeight(requireActivity())
    }

    fun showLoadingUI(isShow: Boolean, context: Context) {
        if (isShow) {
            closeLoadingDialog()
            if (uploadDialog == null) {
                uploadDialog = UpLoadingDialog.Builder(context).createDialog(requireActivity())
            }
            uploadDialog!!.show()
        } else {
            closeLoadingDialog()
        }
    }


     fun showTip(tip: String){
        Toast.makeText(requireActivity(),tip, Toast.LENGTH_SHORT).show()
    }

    fun closeLoadingDialog() {
        if (uploadDialog != null) {
            uploadDialog!!.dismiss()
            uploadDialog = null
        }
    }

}