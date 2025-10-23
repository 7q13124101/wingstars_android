package com.wingstars.base.base

import android.view.View
import androidx.fragment.app.Fragment
import com.gyf.immersionbar.ImmersionBar

open class BaseFragment : Fragment(){

    public fun getStatusBarHeight(): Int{
       return ImmersionBar.getStatusBarHeight(requireActivity())
    }

}