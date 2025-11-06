package com.wingstars.member.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPageFragmentAdapter(var fragments: MutableList<Fragment>,
                              fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    // 返回 Fragment 数量
    override fun getItemCount(): Int = fragments.size

    // 创建指定位置的 Fragment
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}