package com.wingstars.home.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.net.beans.CRMInAppMessageResponse
import com.wingstars.home.R
import com.wingstars.home.adapter.NotificationAdapter
import com.wingstars.home.databinding.ActivityNotificationBinding
import com.wingstars.home.viewmodel.NotificationViewModel
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
        setTitleFoot(
            view1 = binding.root,
            navigationBarColor = R.color.color_F3F4F6,
            statusBarColor = R.color.color_F3F4F6,
        )

        initView()
        loadData()
    }

    override fun initView() {
        binding.btnBack.setOnClickListener { finish() }

        binding.tvMarkAllRead.setOnClickListener {
            viewModel.doNotifyAllRead()

            val currentItems = adapter.snapshot().items
            currentItems.forEachIndexed { index, item ->
                if (item.status == 0) {
                    item.status = 1
                    adapter.notifyItemChanged(index)
                }
            }

            binding.tvMarkAllRead.setTextColor("#999999".toColorInt())
            Toast.makeText(this, "已全部標示為已讀", Toast.LENGTH_SHORT).show()
        }

        adapter = NotificationAdapter { data ->
            if (data.status == 0) {
                data.status = 1
                val position = adapter.snapshot().items.indexOf(data)
                if (position != -1) {
                    adapter.notifyItemChanged(position)
                }
                viewModel.doSingleRead(data.id)
            }
            switchView(data)
        }

        binding.rvNotification.layoutManager = LinearLayoutManager(this)
        binding.rvNotification.adapter = adapter

        adapter.addOnPagesUpdatedListener {
            val allItems = adapter.snapshot().items
            val hasUnread = allItems.any { it.status == 0 }

            if (hasUnread) {
                binding.tvMarkAllRead.setTextColor("#E2518D".toColorInt())
            } else {
                binding.tvMarkAllRead.setTextColor("#999999".toColorInt())
            }
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            viewModel.getNotificationList().collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                val isLoading = loadStates.refresh is androidx.paging.LoadState.Loading
                val isNotLoading = loadStates.refresh is androidx.paging.LoadState.NotLoading
                val isListEmpty = isNotLoading && adapter.itemCount == 0

                when {
                    isLoading -> {
                        binding.layoutEmpty.visibility = android.view.View.GONE
                    }
                    isListEmpty -> {
                        binding.rvNotification.visibility = android.view.View.GONE
                        binding.layoutEmpty.visibility = android.view.View.VISIBLE
                    }
                    isNotLoading -> {
                        binding.rvNotification.visibility = android.view.View.VISIBLE
                        binding.layoutEmpty.visibility = android.view.View.GONE
                    }
                }
            }
        }
    }

    private fun switchView(data: CRMInAppMessageResponse) {
        val route = data.targetUrl
        val taskPattern = "^task\\.([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})$".toRegex()

        when {
            route.isEmpty() -> { }
            route == "home" -> {
                navigateToMain()
            }
            route == "point" || route == "task" -> {
                navigateToMain()
            }
            route == "ticket" -> {
                navigateToMain()
            }
            route.startsWith("http") -> {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, route.toUri())
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            route.matches(taskPattern) -> { }
            else -> { }
        }
    }

    private fun navigateToMain() { }
}
