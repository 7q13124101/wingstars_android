package com.wingstars.member.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.net.beans.WSMemberResponse
import com.wingstars.base.utils.DPUtils
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

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {  //小于Android 15
            setContentView(binding.root)
            setStatusBarColor()
            onInitializationSuccessful()
        } else {
            setTitleFoot(view1 = binding.root, initialization = this, setHeadAndFoot = false)
        }
    }

    fun initLayoutsMarginTop() {
        //状态栏高度
        var nStatusHeight = getStatusBarHeights()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM)
            nStatusHeight = getStatusBarHeight()

        //设置标题栏的MarginTop
        setTitleMarginTop(nStatusHeight)

        //设置成员简介的MarginTop
        //64f标题栏(rlTitle)高度
        val nTitleMarginTop = nStatusHeight + DPUtils.dpToPx(64f, this).toInt()
        setMemerProfileMarginTop(nTitleMarginTop)

        //设置成员详情的MarginTop
        //325f=人物简介（rlMemberProfile）高度348- slMemberDetail本身高度向上重叠后的高度23
        val nDetailMarginTop = nTitleMarginTop + DPUtils.dpToPx(325f, this).toInt()
        setMemberDetailMarginTop(nDetailMarginTop, nStatusHeight)
    }


    private fun setTitleMarginTop(marginTop: Int) {
        val params = binding.rlTitle.layoutParams as FrameLayout.LayoutParams
        params.topMargin = marginTop
    }

    private fun setMemerProfileMarginTop(marginTop: Int) {
        val params = binding.rlMemberProfile.layoutParams as LinearLayout.LayoutParams
        params.topMargin = marginTop
    }

    private fun setMemberDetailMarginTop(marginTop: Int, nStatusHeight: Int) {
        binding.main.post {
            val params = binding.slMemberDetail.layoutParams as LinearLayout.LayoutParams
            params.topMargin = marginTop
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            //布局整体的高度 - 64f标题栏高度 - 状态栏高度
            params.height = binding.main.height - DPUtils.dpToPx(64f, this).toInt() - nStatusHeight
        }
    }

    private fun initData() {

    }

    override fun initView() {
        var wsMemberResponse: WSMemberResponse
        var acf: WSMemberResponse.Acf? = null
        val data = intent.getSerializableExtra("WSMemberResponse")
        if (data != null) {
            wsMemberResponse = data as WSMemberResponse
            acf = wsMemberResponse.acf

            binding.tvTitle.text = wsMemberResponse.titleF
            binding.tvWingNumber.text = wsMemberResponse.acf.number
            binding.tvWingNickname.text = wsMemberResponse.titleF
            //facebook
            if (wsMemberResponse.acf.fb_link.isNotEmpty()) {
                binding.llFacebook.visibility = View.VISIBLE
                binding.llFacebook.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(wsMemberResponse.acf.fb_link)
                    startActivity(intent)
                }
            } else {
                binding.llFacebook.visibility = View.GONE
            }
            //Instagram
            if (wsMemberResponse.acf.ig_link.isNotEmpty()) {
                binding.llInstagram.visibility = View.VISIBLE
                binding.llInstagram.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(wsMemberResponse.acf.ig_link)
                    startActivity(intent)
                }
            } else {
                binding.llInstagram.visibility = View.GONE
            }

            Glide.with(binding.ivImage.context).clear(binding.ivImage)
            if (wsMemberResponse.urlF.isNotEmpty()) {
                Glide.with(this)
                    .load(wsMemberResponse.urlF)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .dontAnimate()
                    .into(binding.ivImage)
            }
        }
        initTabLayout(this, acf)
        binding.imgBack.setOnClickListener {
            finish()
        }
    }

    private fun initTabLayout(context: Context, wsMemberAcf: WSMemberResponse.Acf?) {
        tabTitleList.clear()
        tabTitleList.add(getString(R.string.basic_information))
        //tabTitleList.add(getString(R.string.personal_schedule))
        fragmentAdapter = OuterPagerAdapter(supportFragmentManager, lifecycle)


        val basicInformationFragment = BasicInformationFragment()
        val basicInformationBundle = Bundle().apply {
            putSerializable("wsMemberAcf", wsMemberAcf)
        }
        intent.putExtras(basicInformationBundle)
        basicInformationFragment.arguments = basicInformationBundle
        fragmentAdapter.add(basicInformationFragment)


        /*val personalScheduleFragment = PersonalScheduleFragment()
        val personalBundle = Bundle().apply {
            putString(
                "wing_stars_month",
                SimpleDateFormat("yyyy/MM").format(Date())
            )
        }
        intent.putExtras(personalBundle)
        personalScheduleFragment.arguments = personalBundle
        fragmentAdapter.add(personalScheduleFragment)
         */

        binding.viewPager.adapter = fragmentAdapter
        binding.viewPager.isUserInputEnabled = true
        binding.viewPager.offscreenPageLimit = 1

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

                        /*1 -> {
                            tab.customView = getTabView(context, 1)
                        }*/
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
        for (i in 0..0) {
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
        initLayoutsMarginTop()
        initData()
        initView()
    }
}