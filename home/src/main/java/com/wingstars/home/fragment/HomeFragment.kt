package com.wingstars.home.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import com.wingstars.base.base.BaseFragment
import com.wingstars.base.net.beans.WSFashionResponse
import com.wingstars.base.net.beans.WSPostResponse
import com.wingstars.base.net.beans.WSProductResponse
import com.wingstars.home.R
import com.wingstars.home.activity.TodayItineraryDetailsActivity
import com.wingstars.home.adapter.*
import com.wingstars.home.databinding.FragmentHomeBinding
import com.wingstars.home.viewmodel.HomeViewModel
import com.wingstars.home.adapter.PopularityAdapter
import com.wingstars.member.activity.PopularityRankingActivity
import com.youth.banner.listener.OnPageChangeListener

class HomeFragment : BaseFragment(), View.OnClickListener, PopularityAdapter.onPopularityRankingListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel

    private lateinit var hotProductAdapter: ProductAdapter
    private lateinit var fashionAdapter: StylistOutfitsAdapter

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
        setupUI()
        setupComingSoonBanner()
        observeData()

        viewModel.getCalendarData()
        viewModel.getHomeData()
        viewModel.getLatestNewsData()
    }

    private fun setupUI() {
        binding.titleProducts.tvSectionTitle.text = "熱銷商品"
        binding.titlePopularRanking.tvSectionTitle.text = "人氣排行"
        binding.titleStylistVibe.tvSectionTitle.text = "氛圍時尚"
        binding.titleHighlights.tvSectionTitle.text = "活動花絮"
        binding.titleNews.tvSectionTitle.text = "最新消息"

        // Set sự kiện click
        binding.icNotification.setOnClickListener(this)
        binding.titlePopularRanking.root.setOnClickListener(this)
        binding.titleHighlights.root.setOnClickListener(this)
        binding.titleStylistVibe.root.setOnClickListener(this)
        binding.titleNews.root.setOnClickListener(this)
        binding.titleProducts.root.setOnClickListener(this)
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
        // --- 1. Lịch trình (Calendar) ---
        viewModel.calendarDataList.observe(viewLifecycleOwner) { list ->
            if (list.isNullOrEmpty()) {
                binding.bannerItinerary.visibility = View.GONE
                binding.todayItinerary.visibility = View.GONE
            } else {
                binding.bannerItinerary.visibility = View.VISIBLE

                val bannerAdapter = ItineraryBannerAdapter(list)
                bannerAdapter.onItemClickListener = { data ->
                    val intent = Intent(requireActivity(), TodayItineraryDetailsActivity::class.java)
                    intent.putExtra("DATA_ITINERARY", data)
                    startActivity(intent)
                }

                binding.bannerItinerary.setAdapter(bannerAdapter)
                binding.bannerItinerary.addBannerLifecycleObserver(this@HomeFragment)

                binding.todayItinerary.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                indicatorAdapterItinerary = DotIndicatorAdapter(list.size)
                binding.todayItinerary.adapter = indicatorAdapterItinerary
                binding.todayItinerary.visibility = if (list.size > 1) View.VISIBLE else View.GONE

                binding.bannerItinerary.addOnPageChangeListener(object : OnPageChangeListener {
                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
                    override fun onPageSelected(position: Int) {
                        indicatorAdapterItinerary.setPosition(position)
                    }
                    override fun onPageScrollStateChanged(state: Int) {}
                })
            }
        }

        // --- 2. Sản phẩm Hot (Products) ---
        hotProductAdapter = ProductAdapter(
            requireActivity(),
            mutableListOf(),
            object : ProductAdapter.OnItemListener {
                override fun onItemClick(data: WSProductResponse, position: Int) {
                    checkLoginAndAction {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(data.permalink))
                            startActivity(intent)
                        } catch (e: Exception) { e.printStackTrace() }
                    }
                }
            })
        binding.rvProducts.adapter = hotProductAdapter
        viewModel.productDataList.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) hotProductAdapter.setList(it)
        }

        // --- 3. Thời trang (Fashion/Stylist) ---
        fashionAdapter = StylistOutfitsAdapter(
            requireActivity(),
            mutableListOf(),
            object : StylistOutfitsAdapter.OnItemListener {
                override fun onItemClick(data: WSFashionResponse, position: Int) {

                    checkLoginAndAction {
                        //
                    }
                }
            }
        )
        binding.rvStylistVibe.adapter = fashionAdapter
        viewModel.fashionDataList.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) fashionAdapter.setList(it)
        }

        // --- 4. youtube ---
//        viewModel.homeDataList.observe(viewLifecycleOwner) { dataList ->
//            val articleAdapter = YoutubeAdapter(requireActivity(), dataList)
//            binding.rvArticles.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
//            binding.rvArticles.adapter = articleAdapter
//        }
        val youtubeAdapter = YoutubeAdapter(requireContext())
        binding.rvArticles.adapter = youtubeAdapter // Gán vào RecyclerView


        viewModel.youtubeVideoList.observe(viewLifecycleOwner) { list ->
            youtubeAdapter.setList(list)
        }

        // --- 5. Bảng xếp hạng (Ranking) ---
//        viewModel.memberDataList.observe(viewLifecycleOwner) { dataList ->
//            val memberAdapter = PopularityRankingAdapter(requireActivity(), dataList)
//            binding.rvPopularityRanking.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
//            binding.rvPopularityRanking.adapter = memberAdapter
//        }
        viewModel.wsRankData.observe(viewLifecycleOwner) {
//            Log.e("wsRankData", "${Gson().toJson(it)}")
            var adapter = PopularityAdapter(requireActivity(), it, this)
            binding.rvPopularityRanking.layoutManager = LinearLayoutManager(
                requireActivity(),
                LinearLayoutManager.HORIZONTAL, false
            )
            binding.rvPopularityRanking.adapter = adapter
        }
        viewModel.getRenderedList()

        // --- 6. Tin tức (News) ---
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

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.icNotification.id,
            binding.titlePopularRanking.root.id,
            binding.titleHighlights.root.id,
            binding.titleStylistVibe.root.id,
            binding.titleProducts.root.id -> {
                checkLoginAndAction {
                    when (v.id) {
                        binding.icNotification.id ->
                            startActivity(Intent(requireActivity(), com.wingstars.home.activity.NotificationActivity::class.java))

                        binding.titlePopularRanking.root.id ->
                            startActivity(Intent(requireActivity(), com.wingstars.member.activity.PopularityRankingActivity::class.java))

                        binding.titleHighlights.root.id ->
                            startActivity(Intent(requireActivity(), com.wingstars.member.activity.EventHighlightsActivity::class.java))

                        binding.titleStylistVibe.root.id ->
                            startActivity(Intent(requireActivity(), com.wingstars.member.activity.FashionableAtmosphereActivity::class.java))

                        binding.titleProducts.root.id -> {
                            try {
                                val url = "https://61.218.209.209/product/"
                                val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))
                                startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }

            binding.titleNews.root.id ->
                startActivity(Intent(requireActivity(), com.wingstars.home.activity.LatestNewsActivity::class.java))
        }
    }
    private fun checkLoginAndAction(action: () -> Unit) {
        val isLogin = MMKV.defaultMMKV().decodeBool("isLogin", false)
        if (isLogin) {
            action()
        } else {
            val intent = Intent(requireActivity(), com.wingstars.login.LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onPopularityRankingClickItem(position: Int) {
        checkLoginAndAction {
            startActivity(
                Intent(
                    requireActivity(),
                    PopularityRankingActivity::class.java
                )
            )
        }
    }
}