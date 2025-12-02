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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingstars.base.base.BaseFragment
import com.wingstars.base.net.beans.LatestNewsResponse
import com.wingstars.home.R
import com.wingstars.home.activity.TodayItineraryDetailsActivity

// Import các Adapter cũ
import com.wingstars.home.adapter.ArticleAdapter
import com.wingstars.home.adapter.ComingSoonAdapter
import com.wingstars.home.adapter.ComingSoonData
import com.wingstars.home.adapter.DotIndicatorAdapter
import com.wingstars.home.adapter.ItineraryBannerAdapter
import com.wingstars.home.adapter.ItineraryData
import com.wingstars.home.adapter.PopularityRankingAdapter
import com.wingstars.home.adapter.NewsAdapter
import com.wingstars.home.adapter.ProductAdapter
import com.wingstars.home.adapter.StyleOutfitsData
import com.wingstars.home.adapter.StylistOutfitsAdapter

// --- THÊM MỚI: Import 2 Adapter mới ---
// --- KẾT THÚC ---

import com.wingstars.home.databinding.FragmentHomeBinding
import com.wingstars.home.viewmodel.HomeViewModel


class HomeFragment : BaseFragment() ,View.OnClickListener{ // Giữ nguyên
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel : HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root = binding.root
        initView()
        return root
    }

    private fun initView() {
        // 1. Khởi tạo ViewModel
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // 2. Xử lý Status Bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM){
            binding.root.setOnApplyWindowInsetsListener{ v, insets ->
                val statusBarHeight = insets.getInsets(WindowInsets.Type.statusBars()).top
                Log.e("statusBarHeight","statusBarHeight=$statusBarHeight")
                setViewTop(binding.title, statusBarHeight)
                binding.root.setOnApplyWindowInsetsListener(null)
                insets
            }
        } else {
            setViewTop(binding.title, getStatusBarHeight())
        }
//        val itineraryList = mutableListOf(
//            ItineraryData(
//                R.drawable.placeholder_person,
//                "JC生日會「Just Connect」維C能量補給站",
//                "2025/09/20 (六) 10:00",
//                "Stars House 門市"
//            ),
//            ItineraryData(R.drawable.placeholder_person, "Event 2...", "2025/10/01", "Taipei Arena"),
//            ItineraryData(R.drawable.placeholder_person, "Event 3...", "2025/11/15", "Kaohsiung")
//        )
//
//        // 2. Setup Adapter cho Banner
//        val bannerAdapter = ItineraryBannerAdapter(itineraryList)
//
//        // Xử lý click vào banner -> Mở màn hình chi tiết
//        bannerAdapter.onItemClickListener = { data ->
//            startActivity(Intent(requireActivity(), TodayItineraryDetailsActivity::class.java))
//        }
//
//        binding.bannerItinerary.apply {
//            adapter = bannerAdapter
//            addBannerLifecycleObserver(this@HomeFragment) // Quan trọng để banner tự chạy/dừng theo lifecycle
//            setBannerGalleryEffect(18, 10) // (Tuỳ chọn) Hiệu ứng Gallery
//            // setIndicator(CircleIndicator(context)) // Nếu muốn dùng Indicator mặc định của thư viện
//        }
        // Trong initView() của HomeFragment

// 1. Tạo dữ liệu (3 item, mỗi item 4 biến)
        val itineraryList = mutableListOf(
            ItineraryData(
                R.drawable.placeholder_person,
                "JC生日會「Just Connect」維C能量補給站",
                "2025/09/20 (六) 10:00",
                "Stars House 門市"
            ),
            ItineraryData(
                R.drawable.placeholder_person,
                "JC生日會「Just Connect」維C能量補給站",
                "2025/09/20 (六) 10:00",
                "Stars House 門市"
            ),
            ItineraryData(
                R.drawable.placeholder_person,
                "JC生日會「Just Connect」維C能量補給站",
                "2025/09/20 (六) 10:00",
                "Stars House 門市"
            )
        )

        val comingSoonList = mutableListOf(
            ComingSoonData(
                R.drawable.placeholder_calendar,
                "25-26 WS女孩應援毛巾｜天鷹款\n",
                "2025/09/20 (六) 10:00",

            ),
            ComingSoonData(
                R.drawable.placeholder_calendar,
                "Event 2 Title",
                "2025/10/01",

            ),
            ComingSoonData(
                R.drawable.placeholder_calendar,
                "Event 3 Title",
                "2025/11/15",
            )
        )

        val bannerAdapter = ItineraryBannerAdapter(itineraryList)
        bannerAdapter.onItemClickListener = { data ->
            startActivity(Intent(requireActivity(), TodayItineraryDetailsActivity::class.java))
        }
        val bannerAdapter2 = ComingSoonAdapter(comingSoonList)
//        bannerAdapter2.onItemClickListener={data ->
//            startActivity(Intent(requireActivity(), TodayItineraryDetailsActivity::class.java))}
        binding.bannerItinerary.apply {
            addBannerLifecycleObserver(this@HomeFragment)
            setAdapter(bannerAdapter) // Gắn adapter vào banner
            // Không cần setIndicator mặc định vì ta dùng RecyclerView bên ngoài làm indicator
        }
        binding.bannerComingSoon.apply {
            addBannerLifecycleObserver(this@HomeFragment)
            setAdapter(bannerAdapter2)
        }

        binding.todayItinerary.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.itemComingSoon.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
// Adapter này vẽ các chấm tròn. Nó chỉ cần biết có bao nhiêu item (itineraryList.size)
        val indicatorAdapter = DotIndicatorAdapter(itineraryList.size)
        val comingSoonAdapter = DotIndicatorAdapter(comingSoonList.size)
        binding.todayItinerary.adapter = indicatorAdapter
        binding.todayItinerary.visibility =
            if (itineraryList.size <= 1) View.GONE else View.VISIBLE
        binding.itemComingSoon.adapter = comingSoonAdapter
        binding.itemComingSoon.visibility =
            if (comingSoonList.size <= 1) View.GONE else View.VISIBLE


// 4. Kết nối Banner và Indicator (Logic đồng bộ y hệt ví dụ mẫu)
        binding.bannerItinerary.addOnPageChangeListener(object : com.youth.banner.listener.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                // Khi banner lướt đến vị trí nào, bảo indicator update sang vị trí đó
                // (Tương đương adapter.setPos(position) trong ví dụ mẫu)
                indicatorAdapter.setPosition(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
        binding.bannerComingSoon.addOnPageChangeListener(object : com.youth.banner.listener.OnPageChangeListener{
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }
            override fun onPageSelected(position: Int) {
                comingSoonAdapter.setPosition(position)
            }
            override fun onPageScrollStateChanged(state: Int) {}
        })
        binding.titleProducts.tvSectionTitle.text = "熱銷商品"
        binding.titlePopularRanking.tvSectionTitle.text = "人氣排行"

// 2. Stylist Vibe
        binding.titleStylistVibe.tvSectionTitle.text = "氛圍時尚"
// binding.titleHighlights.tvMore.setOnClickListener { ... }

// 3. Bài viết
        binding.titleHighlights.tvSectionTitle.text = "活動花絮"

// 4. Tin tức
        binding.titleNews.tvSectionTitle.text = "最新消息"
//        binding.todayItinerary.setOnClickListener {
//            val intent = Intent(context, TodayItineraryActivity::class.java)
//
//            context?.startActivity(intent)
//        }

        viewModel.homeDataList.observe(viewLifecycleOwner) { dataList ->
            // Setup rvProducts (Grid)
            val productAdapter = ProductAdapter(requireActivity(), dataList)
            binding.rvProducts.layoutManager = GridLayoutManager(requireActivity(), 2)
            binding.rvProducts.adapter = productAdapter

//            // Setup rvMembers (Horizontal)
//            val memberAdapter = MemberAdapter(requireActivity(), dataList)
//            binding.rvMembers.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
//            binding.rvMembers.adapter = memberAdapter

            // Setup rvHighlights (Grid)
            val styleList = mutableListOf<StyleOutfitsData>()
            dataList.forEach {
                styleList.add(StyleOutfitsData(tittle = "Item $it", isClick = false))
            }
            val styleListener = object : StylistOutfitsAdapter.OnItemListener {
                override fun onItemClick(data: StyleOutfitsData, position: Int) {
                    // Xử lý khi click vào item (nếu cần)
                    // Ví dụ: Toast.makeText(requireActivity(), "Click $position", Toast.LENGTH_SHORT).show()
                }
            }
            val highlightAdapter = StylistOutfitsAdapter(requireActivity(), styleList, styleListener)
            binding.rvStylistVibe.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            binding.rvStylistVibe.adapter = highlightAdapter
            // Setup rvArticles (Horizontal)
            val articleAdapter = ArticleAdapter(requireActivity(), dataList)
            binding.rvArticles.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            binding.rvArticles.adapter = articleAdapter

        }
        viewModel.memberDataList.observe(viewLifecycleOwner){dataList ->
            val memberAdapter = PopularityRankingAdapter(requireActivity(), dataList)
        binding.rvPopularityRanking.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            binding.rvPopularityRanking.adapter = memberAdapter
        }
        viewModel.newsDataList.observe(viewLifecycleOwner) { dataList ->
            // 1. Tạo Listener để xử lý sự kiện click
            val newsListener = object : NewsAdapter.OnItemListener {
                override fun onItemClick(data: LatestNewsResponse, position: Int) {
                    val intent = Intent(requireActivity(), com.wingstars.home.activity.LatestNewsDetailActivity::class.java)

                    // 2. Đóng gói dữ liệu vào Intent (key là "NEWS_DATA")
                    intent.putExtra("NEWS_DATA", data)

                    // 3. Khởi chạy Activity
                    startActivity(intent)
                }
            }
            val limitedList = dataList.take(3).toMutableList()
            // 2. Khởi tạo Adapter với ĐỦ 3 THAM SỐ (Context, List, Listener)
            val newsAdapter = NewsAdapter(
                requireActivity(),
                limitedList, // Chuyển List thành MutableList
                newsListener
            )

            // 3. Gán vào RecyclerView
            binding.rvNews.layoutManager = LinearLayoutManager(requireActivity())
            binding.rvNews.adapter = newsAdapter
        }
        viewModel.getHomeData()




        // 7. Setup Click Listener (Mở comment)
        binding.icNotification.setOnClickListener(this)
//        binding.todayItinerary.setOnClickListener(this)
        binding.titleProducts.root.setOnClickListener(this)
        binding.titlePopularRanking.root.setOnClickListener(this)
        binding.titleHighlights.root.setOnClickListener(this)
        binding.titleNews.root.setOnClickListener(this)
        binding.titleStylistVibe.root.setOnClickListener(this)
    }

    // Hàm này đã đúng, giữ nguyên
    fun setViewTop(view: View, top: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        layoutParams.topMargin = top
        view.setLayoutParams(layoutParams);
    }

    // --- THÊM MỚI: Hàm onClick bị thiếu ---
    override fun onClick(v: View?) {
        val id = v?.id
        when(id) {
            // Dùng .root.id để so sánh
            binding.icNotification.id -> startActivity(Intent(requireActivity(),
                com.wingstars.home.activity.NotificationActivity::class.java
            ))
//            binding.todayItinerary.id -> startActivity(Intent(requireActivity(),
//                TodayItineraryDetailsActivity::class.java
//            ))
            binding.titlePopularRanking.root.id-> startActivity(Intent(requireActivity(),
                com.wingstars.member.activity.PopularityRankingActivity::class.java
            ))
//            binding.titleProducts.root.id -> startActivity(Intent(requireActivity(),
//                PopularityRankingActivity::class.java
//            ))
            binding.titleHighlights.root.id -> startActivity(Intent(requireActivity(),
                com.wingstars.member.activity.EventHighlightsActivity::class.java
            ))
            binding.titleStylistVibe.root.id -> startActivity(
                Intent(
                    requireActivity(),
                    com.wingstars.member.activity.FashionableAtmosphereActivity::class.java
                )
            )
//            binding.titleHighlights.root.id -> {
////                Toast.makeText(requireActivity(), "Mở trang Hoa hậu", Toast.SHORT).show()
//            }
//            binding.titleArticles.root.id -> {
////                Toast.makeText(requireActivity(), "Mở trang Bài viết", Toast.SHORT).show()
//            }
            binding.titleNews.root.id -> startActivity(
                Intent(
                    requireActivity(),
                    com.wingstars.home.activity.LatestNewsActivity::class.java
                )
            )
        }
    }
    // --- KẾT THÚC ---
}