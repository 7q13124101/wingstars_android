package com.wingstars.home.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingstars.base.base.BaseFragment
import com.wingstars.base.net.beans.WSFashionCategoryResponse
import com.wingstars.base.net.beans.WSFashionResponse
import com.wingstars.base.net.beans.WSPostResponse
import com.wingstars.base.net.beans.WSProductResponse
import com.wingstars.base.utils.ItemHotDecoration
import com.wingstars.home.R
import com.wingstars.home.activity.TodayItineraryDetailsActivity
import com.wingstars.home.adapter.* // Import hết adapter cho gọn
import com.wingstars.home.databinding.FragmentHomeBinding
import com.wingstars.home.viewmodel.HomeViewModel
import com.youth.banner.listener.OnPageChangeListener

class HomeFragment : BaseFragment(), View.OnClickListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel

    // Adapter cho các banner, khai báo ở đây để dùng chung
    private lateinit var hotProductAdapter: ProductAdapter
    private lateinit var  fashionAdapter: StylistOutfitsAdapter

    private lateinit var indicatorAdapterItinerary: DotIndicatorAdapter
    private lateinit var indicatorAdapterComingSoon: DotIndicatorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        val window = requireActivity().window

        window.statusBarColor = requireContext().getColor(android.R.color.white)

        androidx.core.view.WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        handleStatusBar()
        setupUI()
        setupComingSoonBanner()
        observeData()

        viewModel.getCalendarData()
        viewModel.getHomeData()
        viewModel.getLatestNewsData()
    }

    private fun handleStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            binding.root.setOnApplyWindowInsetsListener { _, insets ->
                val statusBarHeight = insets.getInsets(WindowInsets.Type.statusBars()).top
                Log.e("statusBarHeight", "statusBarHeight=$statusBarHeight")
                setViewTop(binding.title, statusBarHeight)
                binding.root.setOnApplyWindowInsetsListener(null)
                insets
            }
        } else {
            setViewTop(binding.title, getStatusBarHeight())
        }
    }

    private fun setupUI() {
        binding.titleProducts.tvSectionTitle.text = "熱銷商品"
        binding.titlePopularRanking.tvSectionTitle.text = "人氣排行"
        binding.titleStylistVibe.tvSectionTitle.text = "氛圍時尚"
        binding.titleHighlights.tvSectionTitle.text = "活動花絮"
        binding.titleNews.tvSectionTitle.text = "最新消息"

        binding.icNotification.setOnClickListener(this)
        binding.titlePopularRanking.root.setOnClickListener(this)
        binding.titleHighlights.root.setOnClickListener(this)
        binding.titleStylistVibe.root.setOnClickListener(this)
        binding.titleNews.root.setOnClickListener(this)
    }

    private fun setupComingSoonBanner() {
        val comingSoonList = mutableListOf(
            ComingSoonData(R.drawable.placeholder_calendar, "25-26 WS女孩應援毛巾｜天鷹款\n", "2025/09/20 (六) 10:00"),
            ComingSoonData(R.drawable.placeholder_calendar, "Event 2 Title", "2025/10/01"),
            ComingSoonData(R.drawable.placeholder_calendar, "Event 3 Title", "2025/11/15")
        )

        val bannerAdapter2 = ComingSoonAdapter(comingSoonList)

        binding.bannerComingSoon.apply {
            addBannerLifecycleObserver(this@HomeFragment)
            setAdapter(bannerAdapter2)
        }

        binding.itemComingSoon.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        indicatorAdapterComingSoon = DotIndicatorAdapter(comingSoonList.size)
        binding.itemComingSoon.adapter = indicatorAdapterComingSoon
        binding.itemComingSoon.visibility = if (comingSoonList.size > 1) View.VISIBLE else View.GONE

        binding.bannerComingSoon.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                indicatorAdapterComingSoon.setPosition(position)
            }
            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun observeData() {
        viewModel.calendarDataList.observe(viewLifecycleOwner) { list ->
            if (list.isNullOrEmpty()) {
                binding.bannerItinerary.visibility = View.GONE
                binding.todayItinerary.visibility = View.GONE
            } else {
                binding.bannerItinerary.visibility = View.VISIBLE

                // Setup Banner Adapter
                val bannerAdapter = ItineraryBannerAdapter(list)
                bannerAdapter.onItemClickListener = { data ->
                    val intent = Intent(requireActivity(), TodayItineraryDetailsActivity::class.java)
                     intent.putExtra("DATA_ITINERARY", data)
                    startActivity(intent)
                }

                binding.bannerItinerary.setAdapter(bannerAdapter)
                binding.bannerItinerary.addBannerLifecycleObserver(this@HomeFragment)

                // Setup Indicator (RecyclerView)
                binding.todayItinerary.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                indicatorAdapterItinerary = DotIndicatorAdapter(list.size)
                binding.todayItinerary.adapter = indicatorAdapterItinerary
                binding.todayItinerary.visibility = if (list.size > 1) View.VISIBLE else View.GONE // Chỉ hiện khi > 1 item

                binding.bannerItinerary.addOnPageChangeListener(object : OnPageChangeListener {
                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
                    override fun onPageSelected(position: Int) {
                        indicatorAdapterItinerary.setPosition(position)
                    }
                    override fun onPageScrollStateChanged(state: Int) {}
                })
            }
        }

            hotProductAdapter = ProductAdapter(
            requireActivity(),
            mutableListOf(),
            object : ProductAdapter.OnItemListener {
                override fun onItemClick(data: WSProductResponse, position: Int) {
//                    val intent = Intent(
//                        this@HomeFragment.requireActivity(),
//                        CommonWebViewActivity::class.java
//                    )
//                    intent.putExtra(
//                        "webTitle",
//                        getResources().getString(R.string.title_hot_product)
//                    )
//                    intent.putExtra("webUrl", data.linkF)
//                    intent.putExtra("isXShow", true)
//                    startActivity(intent)
                }
            })

        binding.rvProducts.adapter = hotProductAdapter


        viewModel.productDataList.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty())
                hotProductAdapter.setList(it)
        }

        fashionAdapter = StylistOutfitsAdapter(
            requireActivity(),
            mutableListOf(),
            object : StylistOutfitsAdapter.OnItemListener {
                override fun onItemClick(data: WSFashionResponse, position: Int) {

                }
            }
        )
        binding.rvStylistVibe.adapter = fashionAdapter
        viewModel.fashionDataList.observe(viewLifecycleOwner){
            if (!it.isNullOrEmpty())
                fashionAdapter.setList(it)
        }

        // --- 2. Home Data (Products, Stylist, Articles...) ---
        viewModel.homeDataList.observe(viewLifecycleOwner) { dataList ->
//            // Sản phẩm (Products)
//            val productAdapter = ProductAdapter(requireActivity(), dataList)
//            binding.rvProducts.layoutManager = GridLayoutManager(requireActivity(), 2)
//            binding.rvProducts.adapter = productAdapter

            // Stylist Vibe
//            val styleList = mutableListOf<StyleOutfitsData>()
//            dataList.forEach { styleList.add(StyleOutfitsData(tittle = "Item $it", isClick = false)) }
//
//            val styleListener = object : StylistOutfitsAdapter.OnItemListener {
//                override fun onItemClick(data: StyleOutfitsData, position: Int) {
//                    // Xử lý click
//                }
//            }
////            val highlightAdapter = StylistOutfitsAdapter(requireActivity(), styleList, styleListener)
//            binding.rvStylistVibe.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
//            binding.rvStylistVibe.adapter = highlightAdapter

            // Articles (Hoạt động hoa絮)
            val articleAdapter = ArticleAdapter(requireActivity(), dataList)
            binding.rvArticles.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            binding.rvArticles.adapter = articleAdapter
        }

        // --- 3. Ranking (Thành viên) ---
        viewModel.memberDataList.observe(viewLifecycleOwner) { dataList ->
            val memberAdapter = PopularityRankingAdapter(requireActivity(), dataList)
            binding.rvPopularityRanking.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            binding.rvPopularityRanking.adapter = memberAdapter
        }

        // --- 4. Tin tức (Latest News) ---
        viewModel.newsDataList.observe(viewLifecycleOwner) { dataList ->
            val newsListener = object : NewsAdapter.OnItemListener {
                override fun onItemClick(data: WSPostResponse, position: Int) {
                    val intent = Intent(requireActivity(), com.wingstars.home.activity.LatestNewsDetailActivity::class.java)
                    intent.putExtra("ITEM_NEWS_DATA", data)
                    startActivity(intent)
                }
            }

            val limitedList = dataList.take(3).toMutableList()
            val newsAdapter = NewsAdapter(requireActivity(), limitedList, newsListener)

            binding.rvNews.layoutManager = LinearLayoutManager(requireActivity())
            binding.rvNews.adapter = newsAdapter
        }
    }

    // Hàm helper set margin status bar
    fun setViewTop(view: View, top: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        layoutParams.topMargin = top
        view.layoutParams = layoutParams
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.icNotification.id -> startActivity(Intent(requireActivity(), com.wingstars.home.activity.NotificationActivity::class.java))
            binding.titlePopularRanking.root.id -> startActivity(Intent(requireActivity(), com.wingstars.member.activity.PopularityRankingActivity::class.java))
            binding.titleHighlights.root.id -> startActivity(Intent(requireActivity(), com.wingstars.member.activity.EventHighlightsActivity::class.java))
            binding.titleStylistVibe.root.id -> startActivity(Intent(requireActivity(), com.wingstars.member.activity.FashionableAtmosphereActivity::class.java))
            binding.titleNews.root.id -> startActivity(Intent(requireActivity(), com.wingstars.home.activity.LatestNewsActivity::class.java))
        }
    }
}