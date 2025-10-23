package com.company.wingstars.main

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.company.wingstars.R
import com.company.wingstars.databinding.ActivityMainBinding
import com.wingstars.base.base.BaseActivity
import com.wingstars.calendar.CalendarFragment
import com.wingstars.count.CountFragment
import com.wingstars.home.HomeFragment
import com.wingstars.member.fragment.MemberFragment
import com.wingstars.user.UserFragment

class MainActivity : BaseActivity(), BaseActivity.OnInitialization, View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fragments: ArrayList<Fragment>
    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM){
            setContentView(binding.root)
            setStatusBarColor()
            initView()
        }else{
            setTitleFoot(view1=binding.root,initialization=this,setHeadAndFoot=false)
        }
    }

    override fun initView() {
        binding.member.setOnClickListener(this)
        binding.tabGroup.setClickIntercepter { positions ->
            Log.e("positions","positions=$positions")
            if (position==4){
                binding.icnoMember.setImageResource(R.mipmap.icon_main_member_not_select)
            }
            changeTab(positions)
            false
        }
        initFragment()
        defaultFragment()
    }

    private fun defaultFragment() {
        position = 0;
        var fragment = fragments[0]
        val beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.replace(binding.fragmentLayout.id, fragment)
        beginTransaction.commit()
    }

    private fun initFragment() {
        fragments = ArrayList()
        fragments.add(HomeFragment())
        fragments.add(CountFragment())
        fragments.add(CalendarFragment())
        fragments.add(UserFragment())
        fragments.add(MemberFragment())
    }

    private fun changeTab(page: Int) {
        if (position != page) {
            val fragment = fragments[page]
            val ft = supportFragmentManager.beginTransaction()
            if (!fragment.isAdded) {

                ft.add(binding.fragmentLayout.id, fragment)
            }
            ft.hide(fragments[position])
            ft.show(fragments[page])
            position = page
            if (!this.isFinishing) {
                ft.commitAllowingStateLoss()
            }
        }
    }

    override fun onInitializationSuccessful() {
        initView()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            binding.member.id->{
                selectMember()
            }
        }
    }

    private fun selectMember() {
        binding.icnoMember.setImageResource(R.mipmap.icon_main_member_select)
        binding.tabGroup.setPosition(4)
        when(position){
            0-> binding.home.setChecked(false)
            1-> binding.count.setChecked(false)
            2-> binding.calendar.setChecked(false)
            3-> binding.user.setChecked(false)
        }
        changeTab(4)
    }


}