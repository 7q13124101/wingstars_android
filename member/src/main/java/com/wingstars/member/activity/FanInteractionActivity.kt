package com.wingstars.member.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.utils.ScreenUtils
import com.wingstars.member.R
import com.wingstars.member.databinding.ActivityFanInteractionBinding


class FanInteractionActivity : BaseActivity() {
    private lateinit var binding: ActivityFanInteractionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFanInteractionBinding.inflate(layoutInflater)

        setTitleFoot(binding.root,navigationBarColor=R.color.color_F8EBF1)
        initView()
    }

    override fun initView() {
        binding.title.setBackClickListener { finish() }
        var width = ScreenUtils.getWidth(this@FanInteractionActivity)
        setImage(width, width)
    }

    private fun setImage(width: Int, height: Int) {

        Log.e("width", "width=" + width)
        val params = binding.frameLayout.layoutParams
        params.width = width
        params.height = height
        binding.frameLayout.layoutParams = params
    }
}