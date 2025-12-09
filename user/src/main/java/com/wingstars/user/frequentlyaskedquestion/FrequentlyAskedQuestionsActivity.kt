package com.wingstars.user.frequentlyaskedquestion

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.wingstars.base.base.BaseActivity
import com.wingstars.user.R
import com.wingstars.user.databinding.ActivityFrequentlyAskedQuestionBinding
import me.jessyan.autosize.AutoSizeCompat

class FrequentlyAskedQuestionsActivity : BaseActivity() {
    private lateinit var binding: ActivityFrequentlyAskedQuestionBinding
    private var tabTitleList = arrayListOf<String>()
    private lateinit var fragmentAdapter: OuterPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFrequentlyAskedQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true
        initData()
    }

    override fun getResources(): Resources {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            AutoSizeCompat.autoConvertDensityOfGlobal(super.getResources())
        }
        return super.getResources()
    }


    private fun initData() {
        tabTitleList.clear()
        tabTitleList.add(getString(R.string.user_points_task))
        tabTitleList.add(getString(R.string.user_register_login))
        tabTitleList.add(getString(R.string.user_download_and_install))

        fragmentAdapter = OuterPagerAdapter(supportFragmentManager, lifecycle)
        fragmentAdapter.add(ApplicationFunctionFragment())
        fragmentAdapter.add(RegisterAndLoginFragment.RegisterAndLoginFragment())
        fragmentAdapter.add(DownloadAndInstallFragment())

        binding.viewPager.adapter = fragmentAdapter
        binding.viewPager.isUserInputEnabled = true
        binding.viewPager.offscreenPageLimit = 3

        val tabLayoutMediator =
            TabLayoutMediator(binding.tabLayout, binding.viewPager, true, true) { tab, position ->

                when (position) {
                    0 -> {
                        tab.customView = getTabView(this, 0)
                        tab.customView?.findViewById<TextView>(R.id.tv_team_tab)
                            ?.setTextColor(getColor(R.color.color_DE9DBA))
                    }

                    1 -> {
                        tab.customView = getTabView(this, 1)
                    }

                    2 -> {
                        tab.customView = getTabView(this, 2)
                    }

                }

            }

        recoverItem()
        tabLayoutMediator.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                recoverItem()
                chooseTab(tab)

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        binding.includeTop.imgBack.setOnClickListener {
            finish()
        }
    }


    private fun chooseTab(tab: TabLayout.Tab?) {
        tab?.view?.findViewById<TextView>(R.id.tv_team_tab)?.setTextColor(getColor(R.color.color_101828))
    }

    private fun recoverItem() {
        for (i in 0..2) {
            binding.tabLayout.getTabAt(i)?.view?.findViewById<TextView>(R.id.tv_team_tab)
                ?.setTextColor(getColor(R.color.color_101828))
        }
    }


    fun getTabView(context: Context, position: Int): View {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_team_main_tab, null)
        var tv_team_tab = view.findViewById<TextView>(R.id.tv_team_tab)
        tv_team_tab.text = tabTitleList[position]
        tv_team_tab.isSingleLine=true
        return view
    }

    override fun initView() {
        binding.includeTop.imgBack.setOnClickListener {
            finish()
        }
    }

    inner class OuterPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

        private val mFragments = mutableListOf<Fragment>()

        @NonNull
        override fun createFragment(position: Int): Fragment = mFragments[position]

        override fun getItemCount(): Int = mFragments.size

        fun add(fragment: Fragment) {
            mFragments.add(fragment)
        }

        fun allFragments(): MutableList<Fragment> {
            return mFragments
        }

    }
}