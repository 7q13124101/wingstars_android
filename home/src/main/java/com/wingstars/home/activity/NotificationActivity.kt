package com.wingstars.home.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingstars.base.base.BaseActivity
import com.wingstars.home.R
import com.wingstars.home.adapter.NotificationAdapter
import com.wingstars.home.adapter.NotificationData
import com.wingstars.home.databinding.ActivityNotificationBinding
import com.wingstars.home.viewmodel.NotificationViewModel
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.wingstars.base.net.beans.CRMInAppMessageResponse
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NotificationActivity : BaseActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private val viewModel: NotificationViewModel by viewModels()
    private lateinit var adapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitleFoot(view1 = binding.root,
        navigationBarColor = R.color.color_F3F4F6,
        statusBarColor = R.color.color_F3F4F6,)

        initView()
        loadData()
    }

    override fun initView() {
        binding.btnBack.setOnClickListener { finish() }

        binding.tvMarkAllRead.setOnClickListener {
            viewModel.doNotifyAllRead()

            // 1. Cập nhật UI tạm thời: Xóa chấm đỏ của tất cả item đang hiển thị
            val currentItems = adapter.snapshot().items
            currentItems.forEach { it?.status = 1 }
            adapter.notifyDataSetChanged()

            // 2. Đổi màu nút thành màu xám
            binding.tvMarkAllRead.setTextColor(android.graphics.Color.parseColor("#999999"))
            Toast.makeText(this, "已全部標示為已讀", Toast.LENGTH_SHORT).show()
        }

        adapter = NotificationAdapter { data ->
            // Khi click vào 1 item chưa đọc
            if (data.status == 0) {
                data.status = 1
                adapter.notifyDataSetChanged()
                viewModel.doSingleRead(data.id)
            }
            switchView(data)
        }

        binding.rvNotification.layoutManager = LinearLayoutManager(this)
        binding.rvNotification.adapter = adapter

        adapter.addOnPagesUpdatedListener {
            val allItems = adapter.snapshot().items
            val hasUnread = allItems.any { it?.status == 0 }
            val totalItems = adapter.itemCount

            // 1. Chỉ cập nhật màu chữ của nút "Đọc tất cả"
            if (hasUnread) {
                binding.tvMarkAllRead.setTextColor(android.graphics.Color.parseColor("#E2518D"))
            } else {
                binding.tvMarkAllRead.setTextColor(android.graphics.Color.parseColor("#999999"))
            }

        }
    }
    private fun loadData() {
        // 1. Load data vào adapter
        lifecycleScope.launch {
            viewModel.getNotificationList().collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        // 2. Lắng nghe trạng thái tải để xử lý nhấp nháy
        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                // Kiểm tra các trạng thái của lần tải đầu tiên (refresh)
                val isLoading = loadStates.refresh is androidx.paging.LoadState.Loading
                val isNotLoading = loadStates.refresh is androidx.paging.LoadState.NotLoading

                // Danh sách trống khi đã tải XONG và số lượng = 0
                val isListEmpty = isNotLoading && adapter.itemCount == 0

                when {
                    isLoading -> {
                        // 1. ĐANG TẢI: Giấu ngay màn hình trống đi để khỏi nhấp nháy
                        binding.layoutEmpty.visibility = android.view.View.GONE
                        // (Tùy chọn: Bạn có thể bật 1 cái ProgressBar xoay xoay ở đây)
                    }
                    isListEmpty -> {
                        // 2. TẢI XONG VÀ TRỐNG: Giờ mới chắc chắn là không có thông báo nào
                        binding.rvNotification.visibility = android.view.View.GONE
                        binding.layoutEmpty.visibility = android.view.View.VISIBLE
                    }
                    isNotLoading -> {
                        // 3. TẢI XONG VÀ CÓ DỮ LIỆU: Hiện danh sách bình thường
                        binding.rvNotification.visibility = android.view.View.VISIBLE
                        binding.layoutEmpty.visibility = android.view.View.GONE
                    }
                }
            }
        }
    }
    private fun switchView(data: CRMInAppMessageResponse) {
        val route = data.targetUrl ?: ""

        // Regex patterns (Giữ nguyên từ dự án cũ)
        val taskPattern     = "^task\\.([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})$".toRegex()
        val couponPattern   = "^coupon\\.([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})$".toRegex()
        val activityPattern = "^activity\\.([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})$".toRegex()

        when {
            route.isEmpty() -> {
                // Không có link -> Mở màn hình chi tiết tin nhắn (nếu có)
                // Hoặc chỉ show nội dung
            }
            route == "home" -> {
                // Về trang chủ (Dùng EventBus hoặc Intent về MainActivity clear top)
                navigateToMain(0)
            }
            route == "point" || route == "task" -> {
                // Mở tab Điểm/Nhiệm vụ
                navigateToMain(2) // Giả sử tab 2 là Point
            }
            route == "ticket" -> {
                navigateToMain(3) // Giả sử tab 3 là Ticket
            }
            // ... Copy các case khác tương tự ...

            // Ví dụ mở link ngoài (Product URL như HomeFragment)
            route.startsWith("http") -> {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(route))
                    startActivity(intent)
                } catch (e: Exception) { e.printStackTrace() }
            }

            // Xử lý Task/Coupon specific UUID
            route.matches(taskPattern) -> {
                val uuid = taskPattern.find(route)?.groupValues?.get(1)
                // Gọi API lấy chi tiết task rồi hiện Dialog (như trong EventNotifyFragment cũ)
                // viewModel.getTaskInfo(uuid)
            }

            else -> {
                // Mặc định
            }
        }
    }

    private fun navigateToMain(tabIndex: Int) {
        // Code chuyển tab MainActivity (thay cho EventBus nếu muốn đơn giản)
        // val intent = Intent(this, MainActivity::class.java)
        // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        // intent.putExtra("TAB_INDEX", tabIndex)
        // startActivity(intent)
    }
}