package com.wingstars.home.activity

import android.os.Bundle
import android.text.Html // Import để hiển thị nội dung HTML nếu cần
import android.view.View
import com.bumptech.glide.Glide
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.net.beans.WSPostResponse
import com.wingstars.home.R
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

        // 2. Nhận dữ liệu từ Intent
        val newsData = intent.getSerializableExtra("ITEM_NEWS_DATA") as? WSPostResponse

        if (newsData != null) {
            // --- HIỂN THỊ DỮ LIỆU ---

            // 1. Tiêu đề
            binding.tvNewsDetailTitle.text = newsData.titleF

            // 2. Nội dung
            // Vì API WordPress trả về HTML trong content.rendered, ta nên xử lý hiển thị
            // Nếu newsData.content.rendered có dữ liệu, dùng Html.fromHtml (đơn giản)
            // Lưu ý: data class của bạn cần có field content. Nếu chưa có, tạm thời hiển thị Link hoặc Title
//            binding.tvNewsContent.text = " ${newsData.content}"
            // Hoặc nếu bạn đã thêm field content vào data class:
            // binding.tvNewsContent.text = Html.fromHtml(newsData.content.rendered, Html.FROM_HTML_MODE_COMPACT)

            // --- XỬ LÝ BANNER & INDICATOR (1/n) ---

            // 3. Lấy danh sách URL ảnh từ newsData
            // yoast_head_json.og_image là một List, ta map nó ra List<String>
//            val imageList = newsData.yoast_head_json.og_image?.map { it.url } ?: emptyList()
//
//            // 4. Hiển thị ảnh đầu tiên lên Banner
//            if (imageList.isNotEmpty()) {
//                Glide.with(this)
//                    .load(imageList[0]) // Load ảnh đầu tiên
//                    .centerCrop()
//                    .into(binding.imgNewsBanner)
//            } else {
//                binding.imgNewsBanner.setImageResource(R.drawable.placeholder_image)
//            }
//
//            // 5. Gọi hàm setupBanner với danh sách ảnh thật
//            setupBanner(imageList)
            val imageList = newsData.yoast_head_json.og_image?.map { it.url } ?: emptyList()
            if (imageList.isNotEmpty()) {
                Glide.with(this).load(imageList[0]).into(binding.imgNewsBanner)
            }
            setupBanner(imageList) // Hàm setup indicator 1/5

            val htmlData = newsData.getContentForWebView()

            binding.webViewContent.settings.javaScriptEnabled = true
            binding.webViewContent.loadDataWithBaseURL(null, htmlData, "text/html", "UTF-8", null)
        }
    }

    /**
     * Hàm xử lý hiển thị Indicator (Icon list + Số trang) trên Banner
     */
    private fun setupBanner(images: List<String>) {
        // Nếu không có ảnh hoặc chỉ có 1 ảnh -> Ẩn indicator
        if (images.size <= 1) {
            binding.layoutImageIndicator.visibility = View.GONE
        } else {
            // Nếu có nhiều hơn 1 ảnh -> Hiện indicator
            binding.layoutImageIndicator.visibility = View.VISIBLE

            // Cập nhật text (Ví dụ: 1/5)
            // Hiện tại Banner là ImageView tĩnh nên luôn là 1/n
            // Nếu sau này đổi Banner thành ViewPager, bạn sẽ update số '1' này khi lướt
            binding.tvImageCount.text = "1/${images.size}"
        }
    }
}