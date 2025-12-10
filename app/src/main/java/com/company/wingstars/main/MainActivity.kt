package com.company.wingstars.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.company.wingstars.R
import com.company.wingstars.databinding.ActivityMainBinding
import com.wingstars.base.base.BaseActivity
import com.wingstars.calendar.fragment.CalendarFragment
import com.wingstars.count.fragment.CountFragment
import com.wingstars.home.fragment.HomeFragment
import com.wingstars.member.fragment.MemberFragment
import com.wingstars.user.fragment.UserFragment

class MainActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fragments: ArrayList<Fragment>
    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // default status bar trắng
        window.statusBarColor = getColor(R.color.white)
        WindowInsetsControllerCompat(window, window.decorView)
            .isAppearanceLightStatusBars = true
        initView()
    }

    override fun initView() {
        binding.member.setOnClickListener(this)
        binding.tabGroup.setClickIntercepter { positions ->
            Log.e("positions","positions=$positions")
            if (position == 4){
                binding.icnoMember.setImageResource(R.mipmap.icon_main_member_not_select)
            }
            changeTab(positions)
            false
        }
        initFragment()
        defaultFragment()
    }

    private fun defaultFragment() {
        position = 0
        val ft = supportFragmentManager.beginTransaction()
        fragments.forEachIndexed { index, fragment ->
            if (!fragment.isAdded) {
                ft.add(binding.fragmentLayout.id, fragment)
            }
            if (index == position) {
                ft.show(fragment)
            } else {
                ft.hide(fragment)
            }
        }
        ft.commitAllowingStateLoss()
    }


    private fun initFragment() {
        fragments = arrayListOf(
            HomeFragment(),
            CountFragment(),
            CalendarFragment(),
            UserFragment(),
            MemberFragment()
        )
    }

    private fun changeTab(page: Int) {
        if (position != page) {
            val ft = supportFragmentManager.beginTransaction()
            ft.hide(fragments[position])
            ft.show(fragments[page])
            ft.commitAllowingStateLoss()
            position = page
            updateStatusBarForTab(page)
        }
    }

    private fun updateStatusBarForTab(position: Int) {
        when(position) {
            3 -> { // UserFragment
                window.statusBarColor = getColor(R.color.color_E2518D)
                WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
            }
            else -> { // Các tab khác
                window.statusBarColor = getColor(R.color.white)
                WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            binding.member.id -> selectMember()
        }
    }

    private fun selectMember() {
        binding.icnoMember.setImageResource(R.mipmap.icon_main_member_select)
        binding.tabGroup.setPosition(4)
        // Reset các tab khác
        if (position in 0..3) {
            when(position){
                0 -> binding.home.setChecked(false)
                1 -> binding.count.setChecked(false)
                2 -> binding.calendar.setChecked(false)
                3 -> binding.user.setChecked(false)
            }
        }
        changeTab(4)
    }
}
