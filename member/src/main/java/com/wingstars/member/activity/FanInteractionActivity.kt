package com.wingstars.member.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.utils.ScreenUtils
import com.wingstars.member.R
import com.wingstars.member.databinding.ActivityFanInteractionBinding
import com.wingstars.member.view.TakePhotosMemberPopupView
import com.wingstars.member.viewmodel.FanInteractionViewModel


class FanInteractionActivity : BaseActivity(), View.OnClickListener {
    private val viewModel: FanInteractionViewModel by viewModels()
    private lateinit var binding: ActivityFanInteractionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFanInteractionBinding.inflate(layoutInflater)

        setTitleFoot(binding.root, navigationBarColor = R.color.color_F8EBF1)
        initView()
    }



    override fun initView() {
        binding.title.setBackClickListener { finish() }
        var width = ScreenUtils.getWidth(this@FanInteractionActivity)
        setImage(width, width)
        binding.selectMember.setOnClickListener(this)
    }

    private fun setImage(width: Int, height: Int) {

        Log.e("width", "width=" + width)
        val params = binding.frameLayout.layoutParams
        params.width = width
        params.height = height
        binding.frameLayout.layoutParams = params
    }

    override fun onClick(v: View?) {
        var id = v?.id
        when (id) {
            binding.selectMember.id -> showPopupWindow()
        }
    }

    private fun showPopupWindow() {
        val takePhotosMembersList = viewModel.getTakePhotosMembersList()
        var popupWindow = TakePhotosMemberPopupView(this,getNavigationBarHeight(),
            takePhotosMembersList)
        popupWindow.show(binding.main)
    }
}