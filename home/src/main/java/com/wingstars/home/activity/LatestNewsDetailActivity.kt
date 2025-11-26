package com.wingstars.home.activity

import android.os.Bundle
import android.view.View
import com.wingstars.base.base.BaseActivity
import com.wingstars.home.databinding.ActivityNewsDetailBinding

class LatestNewsDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityNewsDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsDetailBinding.inflate(layoutInflater)
        setTitleFoot(view1 = binding.root)

        initView()
    }

    override fun initView() {
        // 1. Xử lý nút Back
        binding.btnBack.setOnClickListener {
            finish()
        }

        // 2. Giả lập dữ liệu ảnh để test giao diện (Ví dụ có 5 ảnh)
        // Sau này bạn sẽ lấy list này từ API hoặc Intent
        val dummyImages = listOf("img1", "img2", "img3", "img4", "img5")

        // Gọi hàm setup banner
        setupBanner(dummyImages)

        // --- Phần nhận dữ liệu thật (khi nào có API thì mở ra) ---
        /*
        val newsData = intent.getSerializableExtra("NEWS_DATA") as? NewsData
        if (newsData != null) {
            binding.tvNewsDetailTitle.text = newsData.title
            binding.imgNewsBanner.setImageResource(newsData.imageUrl)
            // setupBanner(newsData.listImages) // Truyền list ảnh thật vào đây
        }
        */
    }

    /**
     * Hàm xử lý hiển thị Indicator (Icon list + Số trang) trên Banner
     */
    private fun setupBanner(images: List<String>?) {
        // Nếu danh sách null hoặc rỗng -> Ẩn indicator
        if (images.isNullOrEmpty()) {
            binding.layoutImageIndicator.visibility = View.GONE
            return
        }

        // Nếu có NHIỀU HƠN 1 ảnh -> Hiện indicator (Ví dụ: 1/5)
        if (images.size > 1) {
            binding.layoutImageIndicator.visibility = View.VISIBLE
            // Set text mặc định là ảnh đầu tiên
            binding.tvImageCount.text = "1/${images.size}"

            // TODO: Nếu sau này bạn dùng ViewPager cho banner,
            // hãy lắng nghe sự kiện onPageSelected để cập nhật số "1" thành trang hiện tại.
        } else {
            // Nếu chỉ có 1 ảnh -> Ẩn indicator
            binding.layoutImageIndicator.visibility = View.GONE
        }
    }
}