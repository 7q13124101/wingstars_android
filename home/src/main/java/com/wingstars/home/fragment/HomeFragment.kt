package com.wingstars.home.fragment

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.base.base.BaseFragment
import com.wingstars.base.net.beans.WSCalendarNResponse
import com.wingstars.base.net.beans.WSMemberResponse
import com.wingstars.base.net.beans.WSProductResponse
import com.wingstars.base.utils.DPUtils
import com.wingstars.base.utils.MMKVManagement
import com.wingstars.home.R
import com.wingstars.home.activity.LatestNewsDetailActivity
import com.wingstars.home.activity.TodayItineraryDetailsActivity
import com.wingstars.home.adapter.*
import com.wingstars.home.databinding.FragmentHomeBinding
import com.wingstars.home.viewmodel.HomeViewModel
import com.wingstars.member.activity.AtmosphereFashionDetailsActivity
import com.wingstars.member.activity.MemberDetailsActivity
import com.wingstars.member.activity.PopularityRankingActivity

class HomeFragment : BaseFragment(), View.OnClickListener,
    SupportFashionAdapter.onSupportFashionListener,
    PopularityAdapter.onPopularityRankingListener,
    StylistOutfitsAdapter.onSupportFashionListener{

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private var minHight = 0

    // Các Adapter con
    private lateinit var topBannerAdapter: TopBannerAdapter
    private lateinit var itineraryAdapter: ItineraryBannerAdapter
    private lateinit var itinerarySection: SectionWrapperAdapter

    private lateinit var comingSoonSection: SectionWrapperAdapter
    private lateinit var comingSoonAdapter: ComingSoonAdapter
    private lateinit var productAdapter: ProductAdapter
    private lateinit var popularityAdapter: PopularityAdapter
    private lateinit var stylistOutfitsAdapter: StylistOutfitsAdapter
    private lateinit var supportFashionAdapter: SupportFashionAdapter
    private lateinit var highlightsAdapter: YoutubeAdapter
    private lateinit var newsAdapter: NewsAdapter
    private var type = ""

    private lateinit var mainConcatAdapter: ConcatAdapter
    private var loadedHome = false
    private var loadedNews = false
    private var isDataLoaded = false
    private fun stopLoadingIfReady() {
        if (loadedHome) {
            binding.refresh.isRefreshing = false
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isDataLoaded) {
            binding.refresh.isRefreshing = true
            loadData()
            isDataLoaded = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            binding.root.setOnApplyWindowInsetsListener { v, insets ->
                val statusBarHeight = insets.getInsets(WindowInsets.Type.statusBars()).top
                minHight = statusBarHeight + DPUtils.dpToPx(64f, requireActivity()).toInt()
                //Log.e("statusBarHeight", "statusBarHeight=$statusBarHeight")
                setViewTop(binding.rlTopBar, statusBarHeight)
                binding.root.setOnApplyWindowInsetsListener(null)
                insets
            }
        } else {
            minHight = getStatusBarHeight() + DPUtils.dpToPx(64f, requireActivity()).toInt()
            setViewTop(binding.rlTopBar, getStatusBarHeight())
        }

        initView()
        observeData()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (!isDataLoaded) {
            loadData()
            isDataLoaded = true
        }
    }

    private fun loadData() {
        viewModel.getRenderedList()
        if (viewModel.wsFashionCategorysData.value == null) {
            viewModel.wsFashionCategorys()
        } else {
            viewModel.wsFashions()
        }
//        viewModel.getCalendarData()
//        viewModel.getNewCalendarData()
        viewModel.getHomeData()
        viewModel.getLatestNewsData()
    }

    private fun initView() {
        val localImages = listOf(R.drawable.placeholder_banner)
        topBannerAdapter = TopBannerAdapter(localImages)
        binding.refresh.setColorSchemeResources(com.wingstars.member.R.color.color_E2518D)
        binding.refresh.setOnRefreshListener {
            binding.refresh.isRefreshing = false
            loadData()
        }
        setupTopBarClicks()
        val roundingOffsetAdapter = SingleViewAdapter(R.layout.item_home_rounding_offset)

        // 2. 今日行程
        itineraryAdapter = ItineraryBannerAdapter(mutableListOf()).apply {
            setOnItemListener(object : ItineraryBannerAdapter.OnItemListener {
                override fun onItemClick(data: WSCalendarNResponse) {
                    checkLoginAndAction {
                        startActivity(Intent(requireActivity(), TodayItineraryDetailsActivity::class.java).apply {
                            putExtra("DATA_ITINERARY", data)
                        })
                    }
                }
            })
        }

        val itinerarySection = SectionWrapperAdapter(
            title = getString(R.string.today_schedule_title),
            innerAdapter = itineraryAdapter,
            onMoreClick = null,
            showIndicator = true
        )

        comingSoonAdapter = ComingSoonAdapter(mutableListOf()).apply {
            setOnItemListener(object : ComingSoonAdapter.OnItemListener {
            override fun onItemClick(data: WSProductResponse, position: Int) {
                checkLoginAndAction { openWebUrl(data.permalink)
                   }
               }
           })
       }
        comingSoonSection = SectionWrapperAdapter(
            title = getString(R.string.coming_soon_title),
            innerAdapter = comingSoonAdapter,
            onMoreClick = null,
        )

        productAdapter = ProductAdapter(requireActivity(), mutableListOf(), object : ProductAdapter.OnItemListener {
            override fun onItemClick(data: WSProductResponse, position: Int) {
                checkLoginAndAction {
                    openWebUrl(data.permalink)
                }
            }
        })
        val productsSection = SectionWrapperAdapter(
            title = getString(R.string.products_title),
            innerAdapter = productAdapter,
            isGrid = true,
            onMoreClick = { checkLoginAndAction {
                openWebUrl("https://61.218.209.209/product/")
            } },
            contentPadding = SectionWrapperAdapter.SectionPadding(
                startDp = 20,
                topDp = 20,
                endDp = 20,
                bottomDp = 0
            )
        )

        // 5. 人氣排行
        popularityAdapter = PopularityAdapter(requireActivity(), mutableListOf(), object : PopularityAdapter.onPopularityRankingListener {
            override fun onPopularityRankingClickItem(data: WSMemberResponse) {
                checkLoginAndAction {
                    val intent = Intent(requireActivity(), MemberDetailsActivity::class.java)
                    intent.putExtra("WSMemberResponse", data)
                    startActivity(intent)
                }

            }
        })
        val popularitySection = SectionWrapperAdapter(
            title = getString(R.string.popularity_ranking_title),
            innerAdapter = popularityAdapter,
            onMoreClick = {
                checkLoginAndAction {  startActivity(Intent(requireActivity(), PopularityRankingActivity::class.java))} },
            contentPadding = SectionWrapperAdapter.SectionPadding(
                startDp = 20,
                topDp = 20,
                endDp = 20,
                bottomDp = 0
            ),
        )
        // 6. 人氣排行
        stylistOutfitsAdapter = StylistOutfitsAdapter(requireActivity(), mutableListOf(), object : StylistOutfitsAdapter.onSupportFashionListener {
            override fun onSupportFashionClickItem(memberId: Int,fashionType: Int) {
                checkLoginAndAction {
                    val intent = Intent(
                        requireActivity(),
                        AtmosphereFashionDetailsActivity::class.java
                    )
                    intent.putExtra("memberId", memberId)
                    intent.putExtra("fashionType", fashionType)
                    startActivity(intent)
                }
            }
        })
        val stylistsSection = SectionWrapperAdapter(
            title = getString(R.string.stylist_vibe_title),
            innerAdapter = stylistOutfitsAdapter,
            onMoreClick = {
                checkLoginAndAction { startActivity(Intent(requireActivity(), com.wingstars.member.activity.FashionableAtmosphereActivity::class.java)) }},
            contentPadding = SectionWrapperAdapter.SectionPadding(
                startDp = 20,
                topDp = 20,
                endDp = 20,
                bottomDp = 0
            )
        )
        // 7. 活動花絮 (Event Highlights)
        highlightsAdapter = YoutubeAdapter(requireContext())
        val highlightsSection = SectionWrapperAdapter(
            title = getString(R.string.event_highlights),
            innerAdapter = highlightsAdapter,
            onMoreClick = { startActivity(Intent(requireActivity(), com.wingstars.member.activity.EventHighlightsActivity::class.java)) },
            contentPadding = SectionWrapperAdapter.SectionPadding(
                startDp = 20,
                topDp = 20,
                endDp = 20,
                bottomDp = 0
            )
        )

        // 7. 最新消息
        newsAdapter = NewsAdapter(requireActivity(), mutableListOf(), object : NewsAdapter.OnItemListener {
            override fun onItemClick(data: com.wingstars.base.net.beans.WSPostResponse, position: Int) {
                val intent = Intent(requireActivity(), LatestNewsDetailActivity::class.java)
                intent.putExtra("ITEM_NEWS_DATA", data)
                startActivity(intent)
            }
        })
        val newsSection = SectionWrapperAdapter(
            title = getString(R.string.news_title),
            innerAdapter = newsAdapter,
            onMoreClick = { startActivity(Intent(requireActivity(), com.wingstars.home.activity.LatestNewsActivity::class.java)) },
            contentPadding = SectionWrapperAdapter.SectionPadding(
                startDp = 20,
                topDp = 20,
                endDp = 20,
                bottomDp = 0
            ),
            orientation = RecyclerView.VERTICAL
        )

        mainConcatAdapter = ConcatAdapter(
            topBannerAdapter,
            roundingOffsetAdapter,
            itinerarySection,
            comingSoonSection,
            productsSection,
            popularitySection,
            stylistsSection,
            highlightsSection,
            newsSection
        )

        binding.rvHomeContent.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mainConcatAdapter
        }

        binding.refresh.setOnRefreshListener {
            loadData()
            binding.refresh.isRefreshing = false
        }
    }

    private fun observeData() {
        viewModel.calendarDataList.observe(viewLifecycleOwner) { list ->
            val todayList = (list ?: emptyList()).toMutableList()
            itineraryAdapter.setList(todayList)
//            itinerarySection.setVisible(todayList.isNotEmpty())
            loadedHome = true
            stopLoadingIfReady()
        }

        viewModel.comingSoonDataList.observe(viewLifecycleOwner) { list ->
            val comingSoonList = (list ?: emptyList()).toMutableList()
            if (!comingSoonList.isNullOrEmpty()) {
                comingSoonAdapter.setList(comingSoonList)
            }
            comingSoonSection.setVisible(comingSoonList.isNotEmpty())
        }

        viewModel.productDataList.observe(viewLifecycleOwner) { list ->
            if (!list.isNullOrEmpty()) {
                productAdapter.setList(list.take(4).toMutableList())
            }
        }

        // Cập nhật Rank
        viewModel.wsRankData.observe(viewLifecycleOwner) { list ->
            if (!list.isNullOrEmpty()) {
                type = getString(R.string.support_popularity_list)
                val filteredList = list.filter { it.title == type }.toMutableList()
                if (filteredList.isNotEmpty()) {
                    popularityAdapter.setRankList(filteredList)
                    viewModel.getWsMembersData(filteredList)
                }
            }

        }
        viewModel.wsMembersData.observe(viewLifecycleOwner) { memberDetails ->
            if (!memberDetails.isNullOrEmpty()) {
                popularityAdapter.setMemberDetailList(memberDetails)
            }
        }

        viewModel.fashionDataList.observe(viewLifecycleOwner){rawList ->
            if (rawList.isNullOrEmpty()) return@observe

            val categoryList = viewModel.wsFashionCategorysData.value
            Log.d("rawList", "rawList: $categoryList")
            if (!categoryList.isNullOrEmpty()) {
                rawList.forEach { data ->
                    val fashionCategoryf = data.fashion_categoryF
                    val typeData = categoryList.find { it.id == fashionCategoryf }
                    if (typeData != null) {
                        data.type = when (typeData.name.trim()) {
                            "應援服" -> 1
                            "活動服" -> 2
                            else -> 0
                        }
                    }
                }
            } else {
                Log.w("HomeFragment", "Chưa tải xong Category, hiển thị mặc định.")
            }
            stylistOutfitsAdapter.setList(rawList)
            supportFashionAdapter = SupportFashionAdapter(requireActivity(), rawList, this)

        }

        // Cập nhật Youtube Highlights
        viewModel.youtubeVideoList.observe(viewLifecycleOwner) { list ->
            if (!list.isNullOrEmpty()) {
                highlightsAdapter.setList(list)
            }
        }

        // Cập nhật Tin tức
        viewModel.newsDataList.observe(viewLifecycleOwner) { list ->
            if (!list.isNullOrEmpty()) {
                newsAdapter.setList(list.take(3))
            }
//            loadedNews = true
            stopLoadingIfReady()
        }
    }
    public fun setViewTop(view: View, top: Int) {
        val lp = view.layoutParams as ViewGroup.MarginLayoutParams
        lp.topMargin = top
        view.layoutParams = lp
    }
    private fun setupTopBarClicks() {
        binding.icNotification.setOnClickListener {
            checkLoginAndAction {
                startActivity(
                    Intent(requireActivity(), com.wingstars.home.activity.NotificationActivity::class.java)
                )
            }
        }
    }


    private fun openWebUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun checkLoginAndAction(action: () -> Unit) {
        val isLogin = MMKVManagement.isLogin()
        if (isLogin) {
            action()
        } else {
            val intent = Intent(requireActivity(), com.wingstars.login.LoginActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onClick(v: View?) {}

    override fun onPopularityRankingClickItem(data: WSMemberResponse) {
        checkLoginAndAction {
            val intent = Intent(requireActivity(), MemberDetailsActivity::class.java)
            intent.putExtra("WSMemberResponse", data)
            startActivity(intent)
        }
    }

    override fun onSupportFashionClickItem(memberId: Int,fashionType: Int) {
        checkLoginAndAction {
            val intent = Intent(requireActivity(), AtmosphereFashionDetailsActivity::class.java)
            intent.putExtra("memberId", memberId)
            intent.putExtra("fashionType", fashionType)
            startActivity(intent)
        }
    }
}
