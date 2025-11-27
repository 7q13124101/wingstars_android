package com.wingstars.member.activity

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.wingstars.base.base.BaseActivity
import com.wingstars.member.R
import com.wingstars.member.adapter.ViewPageFragmentAdapter
import com.wingstars.member.databinding.ActivityFashionableAtmosphereBinding
import com.wingstars.member.fragment.EventUniformFragment
import com.wingstars.member.fragment.SupportSuitFragment

class FashionableAtmosphereActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityFashionableAtmosphereBinding
    private var pos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFashionableAtmosphereBinding.inflate(layoutInflater)
        setTitleFoot(
            view1 = binding.root,
            navigationBarColor = R.color.color_F3F4F6,
            statusBarColor = R.color.white
        )
        initView()
    }

    override fun initView() {
        binding.title.setBackClickListener { finish() }
        binding.support.setOnClickListener(this)
        binding.event.setOnClickListener(this)
        binding.viewPager.offscreenPageLimit = 2
        var fragments = mutableListOf<Fragment>()
        fragments.add(SupportSuitFragment())
        fragments.add(EventUniformFragment())
        binding.viewPager.adapter = ViewPageFragmentAdapter(fragments, this)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                pos = position
                selectPage(position)
                super.onPageSelected(position)
            }
        })
    }

    private fun selectPage(position: Int) {
        if (position == 0) {
            selectedUI(binding.supportSuitTv, binding.supportSuitView)
            notSelectedUI(binding.eventUniformTv, binding.eventUniformView)
        } else {
            notSelectedUI(binding.supportSuitTv, binding.supportSuitView)
            selectedUI(binding.eventUniformTv, binding.eventUniformView)
        }
    }

    private fun selectedUI(textView: TextView, view: View) {
        textView.apply {
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(getColor(R.color.color_E2518D))
        }
        view.visibility = View.VISIBLE
    }

    private fun notSelectedUI(textView: TextView, view: View) {
        textView.apply {
            typeface = Typeface.DEFAULT
            setTextColor(getColor(R.color.color_4A5565))
        }
        view.visibility = View.INVISIBLE
    }

    override fun onClick(v: View?) {
        var id = v?.id
        when(id){
            binding.support.id-> {
                Log.e("support","support=$pos")
                if (pos!=0){
                    binding.viewPager.currentItem = 0
                }
            }
            binding.event.id-> {
                Log.e("support","event=$pos")
                if (pos!=1){
                    binding.viewPager.currentItem = 1
                }
            }
        }
    }
}