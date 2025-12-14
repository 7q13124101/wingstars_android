package com.wingstars.base.base

import android.content.Context
import android.os.Build

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.ktx.immersionBar
import com.wingstars.base.R
import com.wingstars.base.databinding.ActivityBaseBinding
import com.wingstars.base.view.UpLoadingDialog


abstract class BaseActivity : AppCompatActivity() {
    private var uploadDialog: UpLoadingDialog? = null
    private lateinit var binding: ActivityBaseBinding


    private var navigationBarHeights = 0
    private var statusBarHeight = 0
    public fun getNavigationBarHeight(): Int {
        return navigationBarHeights
    }

    fun showLoadingUI(isShow: Boolean, context: Context) {
        if (isShow) {
            closeLoadingDialog()
            if (uploadDialog == null) {
                uploadDialog = UpLoadingDialog.Builder(context).createDialog(this)
            }
            uploadDialog!!.show()
        } else {
            closeLoadingDialog()
        }
    }

    fun closeLoadingDialog() {
        if (uploadDialog != null) {
            uploadDialog!!.dismiss()
            uploadDialog = null
        }
    }


    interface OnInitialization {
        fun onInitializationSuccessful()
    }

    public fun setTitleFoot(
        view1: View,
        navigationBarColor: Int = R.color.white,
        statusBarColor: Int = R.color.white,
        initialization: OnInitialization? = null,
        setHeadAndFoot: Boolean = true
    ) {
        immersionBar {
            statusBarColor(statusBarColor)
            navigationBarColor(navigationBarColor)
            statusBarDarkFont(true)
            fitsSystemWindows(if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) true else false)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            binding = ActivityBaseBinding.inflate(LayoutInflater.from(this))
            setContentView(binding.root)
            binding.root.setOnApplyWindowInsetsListener { v, insets ->
                // 获取状态栏和导航栏高度
                // Log.e("setOnApplyWindowInsetsListener","setOnApplyWindowInsetsListener")

                statusBarHeight = insets.getInsets(WindowInsets.Type.statusBars()).top
                val navigationBarHeight =
                    insets.getInsets(WindowInsets.Type.navigationBars()).bottom

                if (setHeadAndFoot) {
                    binding.headView.setBackgroundColor(getColor(statusBarColor))
                    var params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        statusBarHeight
                    )
                    binding.headView?.layoutParams = params
                } else {
                    binding.headView.visibility = View.GONE
                }


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    getWindow().setDecorFitsSystemWindows(false); // 启用无边框模式‌:ml-citation{ref="4" data="citationList"}
                }
                binding.footView.setBackgroundColor(getColor(navigationBarColor))
                var params1 = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    navigationBarHeight
                )
                navigationBarHeights = navigationBarHeight
                binding.footView?.layoutParams = params1
                var params2 = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )

                if (view1.getParent() != null) {
                    var views = view1.getParent() as ViewGroup
                    views.removeView(view1);
                }
                binding.middle.addView(view1, params2)
                binding.root.setOnApplyWindowInsetsListener(null)
                if (initialization != null) {
                    initialization.onInitializationSuccessful()
                }




                insets

            }

        } else {
            setContentView(view1)
            if (initialization != null) {
                initialization.onInitializationSuccessful()
            }
            navigationBarHeights = ImmersionBar.getNavigationBarHeight(this)
        }
    }

    public fun getStatusBarHeight(): Int {
        return ImmersionBar.getStatusBarHeight(this)
    }

    public fun getStatusBarHeights(): Int {
        return statusBarHeight
    }

    abstract fun initView()

    public fun setImage(view: View, width: Int, height: Int) {
        val params = view.layoutParams
        params?.width = width
        params?.height = height
        view.layoutParams = params
    }


    fun setStatusBarColor() {
        ImmersionBar.with(this)
            .navigationBarColor(R.color.white)
            .statusBarDarkFont(true)
            .init()
    }


    open fun showToast(tip: String) {
        Toast.makeText(this, tip, Toast.LENGTH_LONG).show()
    }


}