package com.wingstars.member.activity

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
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
import com.wingstars.member.databinding.ActivityMemberDetailsBinding
import com.wingstars.member.fragment.BasicInformationFragment
import com.wingstars.member.fragment.PersonalScheduleFragment
import com.wingstars.member.viewmodel.MemberDetailsViewModel

class MemberDetailsActivity : BaseActivity(), BaseActivity.OnInitialization {

    private lateinit var binding: ActivityMemberDetailsBinding
    private lateinit var viewModel: MemberDetailsViewModel

    private var tabTitleList = arrayListOf<String>()
    private lateinit var fragmentAdapter: OuterPagerAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var indicatorDrawable: DynamicWidthIndicatorDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMemberDetailsBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[MemberDetailsViewModel::class.java]

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            setContentView(binding.root)
            setStatusBarColor()
            setMarginTop(binding.rlTop, getStatusBarHeight())
            initData()
            initView()
        } else {
            setTitleFoot(view1 = binding.root, initialization = this, setHeadAndFoot = false)
        }


    }

    fun setMarginTop(view: View, top: Int) {
        var params = view.layoutParams as LinearLayout.LayoutParams
        params.topMargin = top
        view.layoutParams = params
    }

    private fun initData() {

    }

    override fun initView() {
        initTabLayout(this)
        binding.imgBack.setOnClickListener {
            finish()
        }
    }

    private fun initTabLayout(context: Context) {
        tabTitleList.clear()
        tabTitleList.add(getString(R.string.basic_information))
        tabTitleList.add(getString(R.string.personal_schedule))
        fragmentAdapter = OuterPagerAdapter(supportFragmentManager, lifecycle)
        fragmentAdapter.add(BasicInformationFragment())
        fragmentAdapter.add(PersonalScheduleFragment())

        binding.viewPager.adapter = fragmentAdapter
        binding.viewPager.isUserInputEnabled = true
        binding.viewPager.offscreenPageLimit = 2

        tabLayout = binding.tabLayout
        indicatorDrawable = DynamicWidthIndicatorDrawable(
            context = this, tabLayout = tabLayout, widthRatio = 1f,  // 内容宽度的100%
            heightDp = 4f, color = getColor(R.color.color_E2518D)
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
                0, tabLayout.getTabAt(0)?.view?.findViewById<TextView>(R.id.tv_team_tab)
            )
        }
    }

    private fun chooseTab(tab: TabLayout.Tab?) {
        tab?.view?.findViewById<TextView>(R.id.tv_team_tab)
            ?.setTextColor(getColor(R.color.color_E2518D))
    }

    private fun recoverItem() {
        for (i in 0..1) {
            binding.tabLayout.getTabAt(i)?.view?.findViewById<TextView>(R.id.tv_team_tab)
                ?.setTextColor(getColor(R.color.color_4A5565))
        }
    }


    fun getTabView(context: Context, position: Int): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_team_main_tab, null)
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

    override fun onInitializationSuccessful() {
        initData()
        initView()
        setMarginTop(binding.rlTop, getStatusBarHeights())
    }
}