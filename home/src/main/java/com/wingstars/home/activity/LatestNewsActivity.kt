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

        // Vì thiết kế chỉ có 1 màn hình danh sách, ta ẩn TabLayout đi cho giống ảnh
        // Hoặc nếu bạn muốn giữ Tab thì để lại dòng này
        binding.tabLayout.visibility = android.view.View.GONE

        // Setup ViewPager hiển thị Fragment
        val adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 1
            override fun createFragment(position: Int): Fragment = LatestNewsFragment()
        }
        binding.viewPager.adapter = adapter
    }
}