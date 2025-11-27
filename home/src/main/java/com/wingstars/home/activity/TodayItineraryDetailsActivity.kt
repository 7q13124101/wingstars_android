package com.wingstars.home.activity

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.wingstars.base.base.BaseActivity
import com.wingstars.home.databinding.ActivityTodayItineraryDetailsBinding // Đảm bảo ViewBinding được tạo

class TodayItineraryDetailsActivity   : BaseActivity() {

    private lateinit var binding: ActivityTodayItineraryDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodayItineraryDetailsBinding.inflate(layoutInflater)

        setTitleFoot(view1 = binding.root)
        initView()
        setupListeners()
    }

    override fun initView() {
        // Mặc định ẩn nội dung chi tiết để màn hình gọn gàng (tùy bạn chọn)
        // Nếu muốn mặc định mở thì comment 2 dòng này lại
        binding.tvEventInfoContent.visibility = View.GONE
        binding.tvNoticeContent.visibility = View.GONE

        // Set dữ liệu mẫu (nếu chưa có API)
        // binding.tvEventTitle.text = "JC Birthday Party..."
    }

    private fun setupListeners() {
        // 1. Nút Back
        binding.btnBack.setOnClickListener {
            finish() // Đóng Activity hiện tại
        }

        // 2. Xử lý Đóng/Mở phần "Thông tin sự kiện" (Event Info)
        binding.layoutInfoHeader.setOnClickListener {
            toggleSection(binding.tvEventInfoContent, binding.imgArrowInfo)
        }

        // 3. Xử lý Đóng/Mở phần "Lưu ý" (Notices)
        binding.layoutNoticeHeader.setOnClickListener {
            toggleSection(binding.tvNoticeContent, binding.imgArrowNotice)
        }
    }

    /**
     * Hàm helper để xử lý logic ẩn/hiện và xoay mũi tên
     * @param contentView: View nội dung cần ẩn/hiện (TextView)
     * @param arrowView: Icon mũi tên cần xoay
     */
    private fun toggleSection(contentView: View, arrowView: ImageView) {
        if (contentView.visibility == View.VISIBLE) {
            // Đang mở -> Đóng lại
            contentView.visibility = View.GONE
            rotateArrow(arrowView, 0f) // Xoay về vị trí cũ (mũi tên lên hoặc xuống tùy icon gốc)
        } else {
            // Đang đóng -> Mở ra
            contentView.visibility = View.VISIBLE
            rotateArrow(arrowView, 180f) // Xoay ngược lại
        }
    }

    private fun rotateArrow(view: View, rotation: Float) {
        ObjectAnimator.ofFloat(view, "rotation", rotation).apply {
            duration = 300 // Thời gian xoay (ms)
            start()
        }
    }
}