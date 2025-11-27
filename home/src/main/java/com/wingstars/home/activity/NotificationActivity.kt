package com.wingstars.home.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingstars.base.base.BaseActivity
import com.wingstars.home.R
import com.wingstars.home.adapter.NotificationAdapter
import com.wingstars.home.adapter.NotificationData
import com.wingstars.home.databinding.ActivityNotificationBinding

class NotificationActivity : BaseActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var adapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setTitleFoot(view1 = binding.root,
        navigationBarColor = R.color.color_F3F4F6,
        statusBarColor = R.color.color_F3F4F6,)

        initView()
        loadData()
    }

    override fun initView() {
        // Nút Back
        binding.btnBack.setOnClickListener { finish() }

        // Nút "Đọc tất cả"
        binding.tvMarkAllRead.setOnClickListener {
            Toast.makeText(this, "已標記為全部已讀", Toast.LENGTH_SHORT).show()
            // Thêm logic đánh dấu đã đọc ở đây (ví dụ gọi API)
        }

        // Setup RecyclerView
        binding.rvNotification.layoutManager = LinearLayoutManager(this)
        adapter = NotificationAdapter(this, listOf())
        binding.rvNotification.adapter = adapter
    }

    private fun loadData() {
        // Giả lập dữ liệu theo yêu cầu của bạn
        val list = listOf(
            NotificationData("2025/10/17", "APP 分享，任務達成！", "獲得星幣 1 點"),
            NotificationData("2025/10/17", "最喜歡哪位女孩唱《𝘾𝙝𝙚𝙚𝙧 𝙞𝙩 𝙪𝙥 加大》 ？，任務達成！", "獲得星幣 10 點"),
            NotificationData("2025/10/17", "星粉登入，任務達成！", "獲得星幣 1 點，勳章 1 枚"),
            NotificationData("2025/10/17", "WS 中階會員，任務達成！", "獲得星幣 1 點"),
            NotificationData("2025/10/17", "2025 WS LOGO卡冊 Get!，任務達成！", "獲得星幣 1 點")
        )

        adapter.updateData(list)
    }
}