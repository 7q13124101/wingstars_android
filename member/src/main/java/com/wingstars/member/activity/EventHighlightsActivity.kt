package com.wingstars.member.activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.view.DynamicWidthIndicatorDrawable
import com.wingstars.member.R
import com.wingstars.member.databinding.ActivityEventHighlightsBinding
import com.wingstars.member.fragment.HighlightsFragment
import com.wingstars.member.viewmodel.EventHighlightsViewModel
import com.wingstars.member.viewmodel.HighlightsType


class EventHighlightsActivity : BaseActivity() {
    private lateinit var binding: ActivityEventHighlightsBinding
    private lateinit var viewModel: EventHighlightsViewModel

    private var tabTitleList = arrayListOf<String>()
    private lateinit var fragmentAdapter: OuterPagerAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var indicatorDrawable: DynamicWidthIndicatorDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEventHighlightsBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[EventHighlightsViewModel::class.java]

        setTitleFoot(view1 = binding.root)
        initData()
        initView()
    }

    private fun initData() {
    }

    override fun initView() {
        initTabLayout(this)
        binding.title.setBackClickListener { finish() }
    }


    private fun initTabLayout(context: Context) {
        tabTitleList.clear()
        tabTitleList.add(getString(R.string.wonderful_videos))
        tabTitleList.add(getString(R.string.flash_short_film))
        tabTitleList.add(getString(R.string.daily_vlog))
        fragmentAdapter = OuterPagerAdapter(supportFragmentManager, lifecycle)
        fragmentAdapter.add(HighlightsFragment(HighlightsType.HT_WONDERFUL_VIDEOS))
        fragmentAdapter.add(HighlightsFragment(HighlightsType.HT_FLASH_SHORT_FILM))
        fragmentAdapter.add(HighlightsFragment(HighlightsType.HT_DAILY_VLOG))

        binding.viewPager.adapter = fragmentAdapter
        binding.viewPager.isUserInputEnabled = true
        binding.viewPager.offscreenPageLimit = 3

        tabLayout = binding.tabLayout
        indicatorDrawable = DynamicWidthIndicatorDrawable(
            context = this,
            tabLayout = tabLayout,
            widthRatio = 1f,  // 内容宽度的100%
            heightDp = 4f,
            color = getColor(R.color.color_E2518D)
        )

        tabLayout.apply {
            // 设置自定义指示器
            setSelectedTabIndicator(indicatorDrawable)

            // 关键设置：指示器不填满整个Tab
            isTabIndicatorFullWidth = false

            val tabLayoutMediator =
                TabLayoutMediator(tabLayout, binding.viewPager, true, true) { tab, position ->
                    when (position) {
                        0 -> {
                            tab.customView = getTabView(context, 0)
                            tab.customView?.findViewById<TextView>(R.id.tv_team_tab)
                                ?.setTextColor(getColor(R.color.color_E2518D))
                        }

                        1 -> {
                            tab.customView = getTabView(context, 1)
                        }

                        2 -> {
                            tab.customView = getTabView(context, 2)
                        }
                    }

                }

            recoverItem()
            tabLayoutMediator.attach()

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.position?.let {
                        indicatorDrawable.updateIndicatorWidth(
                            it,
                            tabLayout.getTabAt(it)?.view?.findViewById<TextView>(R.id.tv_team_tab)
                        )
                    }
                    recoverItem()
                    chooseTab(tab)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
        // 初始化第一个Tab的指示器
        post {
            indicatorDrawable.updateIndicatorWidth(
                0,
                tabLayout.getTabAt(0)?.view?.findViewById<TextView>(R.id.tv_team_tab)
            )
        }
    }

    private fun chooseTab(tab: TabLayout.Tab?) {
        tab?.view?.findViewById<TextView>(R.id.tv_team_tab)
            ?.setTextColor(getColor(R.color.color_E2518D))
    }

    private fun recoverItem() {
        for (i in 0..2) {
            binding.tabLayout.getTabAt(i)?.view?.findViewById<TextView>(R.id.tv_team_tab)
                ?.setTextColor(getColor(R.color.color_4A5565))
        }
    }


    fun getTabView(context: Context, position: Int): View {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_team_main_tab, null)
        var tv_team_tab = view.findViewById<TextView>(R.id.tv_team_tab)
        tv_team_tab.text = tabTitleList[position]
        tv_team_tab.isSingleLine = true
        return view
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

    // 延迟执行确保视图已布局
    private fun post(action: () -> Unit) {
        tabLayout.post(action)
    }
}