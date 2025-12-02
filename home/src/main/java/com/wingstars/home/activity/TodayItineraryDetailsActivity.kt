package com.wingstars.home.activity

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide // Import Glide để load ảnh
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.net.beans.IteneraryResponse // Import Model
import com.wingstars.home.R
import com.wingstars.home.databinding.ActivityTodayItineraryDetailsBinding

class TodayItineraryDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityTodayItineraryDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodayItineraryDetailsBinding.inflate(layoutInflater)

        setTitleFoot(view1 = binding.root)
        initView()
        setupListeners()
    }

    override fun initView() {
        // 1. Mặc định ẩn nội dung chi tiết
        binding.tvEventInfoContent.visibility = View.GONE
        binding.tvNoticeContent.visibility = View.GONE

        // 2. Nhận dữ liệu từ Intent (Key là "DATA" như đã gọi ở HomeFragment)
        val data = intent.getSerializableExtra("DATA_ITINERARY") as? IteneraryResponse

        if (data != null) {
            bindData(data)
        }
    }

    private fun bindData(data: IteneraryResponse) {
        // --- A. Hiển thị thông tin cơ bản ---
        binding.tvEventTitle.text = data.titleF
        binding.tvEventDate.text = data.dateF
        binding.tvEventLocation.text = data.locationF
        Log.e("bindData", "bindData=${data.imageF}")


        // --- B. Hiển thị Ảnh Banner ---
        if (data.imageF.isNotEmpty()) {
            Glide.with(this)
                .load(data.imageF)
                .centerCrop()
//                .placeholder(R.drawable.placeholder_banner) // Ảnh chờ
                .into(binding.imgEventBanner)
        } else {
            binding.imgEventBanner.setImageResource(R.drawable.placeholder_banner)
        }

        // --- C. Hiển thị "Thông tin sự kiện" (Lấy từ Content HTML) ---
        // Vì tvEventInfoContent là TextView, ta dùng Html.fromHtml để hiển thị định dạng cơ bản
        if (data.contentRaw.isNotEmpty()) {
            val styledText = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(data.contentRaw, Html.FROM_HTML_MODE_COMPACT)
            } else {
                @Suppress("DEPRECATION")
                Html.fromHtml(data.contentRaw)
            }
            binding.tvEventInfoContent.text = styledText
        } else {
            binding.tvEventInfoContent.text = "暫無資訊"
        }

        // --- D. Hiển thị "Lưu ý" (Lấy từ ACF Precautions) ---
        if (data.precautionsF.isNotEmpty()) {
            binding.tvNoticeContent.text = data.precautionsF
            binding.layoutNoticeHeader.visibility = View.VISIBLE
        } else {
            // Nếu không có lưu ý thì ẩn luôn cả mục này đi cho gọn
            binding.layoutNoticeHeader.visibility = View.GONE
            binding.tvNoticeContent.visibility = View.GONE
        }
    }

    private fun setupListeners() {
        // 1. Nút Back
        binding.btnBack.setOnClickListener {
            finish()
        }

        // 2. Xử lý Đóng/Mở phần "Thông tin sự kiện"
        binding.layoutInfoHeader.setOnClickListener {
            toggleSection(binding.tvEventInfoContent, binding.imgArrowInfo)
        }

        // 3. Xử lý Đóng/Mở phần "Lưu ý"
        binding.layoutNoticeHeader.setOnClickListener {
            toggleSection(binding.tvNoticeContent, binding.imgArrowNotice)
        }
    }

    private fun toggleSection(contentView: View, arrowView: ImageView) {
        if (contentView.visibility == View.VISIBLE) {
            // Đang mở -> Đóng lại
            contentView.visibility = View.GONE
            rotateArrow(arrowView, 0f)
        } else {
            // Đang đóng -> Mở ra
            contentView.visibility = View.VISIBLE
            rotateArrow(arrowView, 180f)
        }
    }

    private fun rotateArrow(view: View, rotation: Float) {
        ObjectAnimator.ofFloat(view, "rotation", rotation).apply {
            duration = 300
            start()
        }
    }
}