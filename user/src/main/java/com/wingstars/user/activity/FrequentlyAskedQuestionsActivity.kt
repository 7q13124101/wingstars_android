package com.wingstars.user.activity

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
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
import com.wingstars.user.fragment.ApplicationFunctionFragment
import com.wingstars.user.fragment.DownloadAndInstallFragment
import com.wingstars.user.fragment.RegisterAndLoginFragment
import com.wingstars.base.view.DynamicWidthIndicatorDrawable
import me.jessyan.autosize.AutoSizeCompat

class FrequentlyAskedQuestionsActivity : BaseActivity() {
    private lateinit var binding: ActivityFrequentlyAskedQuestionBinding
    private var tabTitleList = arrayListOf<String>()
    private lateinit var fragmentAdapter: OuterPagerAdapter
    private lateinit var indicatorDrawable: DynamicWidthIndicatorDrawable

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
        
        indicatorDrawable = DynamicWidthIndicatorDrawable(
            context = this,
            tabLayout = binding.tabLayout,
            widthRatio = 1f,
            heightDp = 4f,
            color = getColor(R.color.color_DE9DBA)
        )
        binding.tabLayout.setSelectedTabIndicator(indicatorDrawable)

        val tabLayoutMediator =
            TabLayoutMediator(binding.tabLayout, binding.viewPager, true, true) { tab, position ->
                tab.text = tabTitleList[position]
                tab.customView = getTabView(this, position)
                if (position == 0) {
                    tab.customView?.findViewById<TextView>(R.id.tv_team_tab)
                        ?.setTextColor(getColor(R.color.color_DE9DBA))
                }
            }
        tabLayoutMediator.attach()

        binding.tabLayout.post {
            indicatorDrawable.updateIndicatorWidth(0, binding.tabLayout.getTabAt(0)?.customView?.findViewById(R.id.tv_team_tab))
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                recoverItem()
                chooseTab(tab)
                tab?.let {
                    indicatorDrawable.updateIndicatorWidth(it.position, it.customView?.findViewById(R.id.tv_team_tab))
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.includeTop.imgBack.setOnClickListener {
            finish()
        }
    }

    private fun chooseTab(tab: TabLayout.Tab?) {
        tab?.customView?.findViewById<TextView>(R.id.tv_team_tab)?.setTextColor(getColor(R.color.color_DE9DBA))
    }

    private fun recoverItem() {
        for (i in 0 until tabTitleList.size) {
            binding.tabLayout.getTabAt(i)?.customView?.findViewById<TextView>(R.id.tv_team_tab)
                ?.setTextColor(getColor(R.color.color_101828))
        }
    }

    fun getTabView(context: Context, position: Int): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_team_main_tab, binding.tabLayout, false)
        val tvTeamTab = view.findViewById<TextView>(R.id.tv_team_tab)
        tvTeamTab.text = tabTitleList[position]
        tvTeamTab.isSingleLine = true       
        view.contentDescription = tabTitleList[position]
        return view
    }

    override fun initView() {
        binding.includeTop.imgBack.setOnClickListener {
            finish()
        }
    }

    class OuterPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {
        private val mFragments = mutableListOf<Fragment>()

        override fun createFragment(position: Int): Fragment = mFragments[position]
        override fun getItemCount(): Int = mFragments.size
        fun add(fragment: Fragment) {
            mFragments.add(fragment)
        }
    }
}