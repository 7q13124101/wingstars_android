package com.company.wingstars.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.company.wingstars.R
import com.company.wingstars.databinding.ActivityMainBinding
import com.tencent.mmkv.MMKV
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.inter.IPermissionsCallback
import com.wingstars.calendar.fragment.CalendarFragment
import com.wingstars.count.fragment.CountFragment
import com.wingstars.home.fragment.HomeFragment
import com.wingstars.member.activity.FanInteractionActivity
import com.wingstars.member.fragment.MemberFragment
import com.wingstars.user.fragment.UserFragment

class MainActivity : BaseActivity(), BaseActivity.OnInitialization, View.OnClickListener,
    IPermissionsCallback {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fragments: ArrayList<Fragment>
    private var position = 0
    private var permission=0

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {

        if (requestCode == 1000) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //  runOnUiThread { startActivity(Intent(this@MainActivity, FanInteractionActivity::class.java)) }
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                    Log.e("permissions","onRequestPermissionsResult permissions=$permissions")
                    if (permission==1){
                        runOnUiThread { startActivity(Intent(this@MainActivity, FanInteractionActivity::class.java)) }
                    }

                } else {
                    applyPermission1()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)

    }

    private fun  applyPermission1(){
        var  permissions = ArrayList<String>();
        permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,permissions.toTypedArray(), 1001)
        } else {
            if (permission==1){
                runOnUiThread { startActivity(Intent(this@MainActivity, FanInteractionActivity::class.java)) }
            }

        }
    }


    override fun setPermissions(permissions: Int) {
        permission = permissions
        Log.e("setPermissions","setPermissions=$permissions")
    }


}