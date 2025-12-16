package com.wingstars.home.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wingstars.base.base.BaseActivity
import com.wingstars.home.databinding.ActivityLatestNewsBinding
import com.wingstars.home.fragment.LatestNewsFragment

class LatestNewsActivity : BaseActivity() {

    private lateinit var binding: ActivityLatestNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLatestNewsBinding.inflate(layoutInflater)
        // setTitleFoot(view1 = binding.root) // Nếu BaseActivity của bạn hỗ trợ set view này
        setTitleFoot(view1 = binding.root)

        initView()
    }

    override fun initView() {
        // Setup nút Back trên TitleView
        binding.title.setBackClickListener { finish() }

        // Setup ViewPager hiển thị Fragment
        val adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 1
            override fun createFragment(position: Int): Fragment = LatestNewsFragment()
        }
        binding.viewPager.adapter = adapter
    }
}