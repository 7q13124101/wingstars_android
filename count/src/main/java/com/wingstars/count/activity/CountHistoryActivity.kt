package com.wingstars.count.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingstars.count.R
import com.wingstars.count.adapter.CountHistoryAdapter
import com.wingstars.count.databinding.ActivityCountHistoryBinding
import com.wingstars.count.viewmodel.CountHistoryViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CountHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCountHistoryBinding
    private lateinit var viewModel: CountHistoryViewModel
    private lateinit var obtainedAdapter: CountHistoryAdapter
    private lateinit var usageAdapter: CountHistoryAdapter
    private var obtainedLoadStates: CombinedLoadStates? = null
    private var usageLoadStates: CombinedLoadStates? = null

    private var isShowObtained = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCountHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Khởi tạo ViewModel
        viewModel = ViewModelProvider(this)[CountHistoryViewModel::class.java]

        initView()
        initData()
        initListener()

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            binding.llHeader.setPadding(0, systemBars.top, 0, 0)
            insets
        }
    }

    private fun initView() {
        // Setup Adapters (Không truyền list null nữa vì Adapter mới không cần)
        obtainedAdapter = CountHistoryAdapter(this, isUsageRecord = false)
        binding.adObtainedList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.adObtainedList.adapter = obtainedAdapter

        usageAdapter = CountHistoryAdapter(this, isUsageRecord = true)
        binding.adUsageList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.adUsageList.adapter = usageAdapter

        updateTabUI(true)

        // Lấy điểm tổng truyền từ màn hình trước (nếu có)
        val countIntent = intent.getStringExtra("count")
        viewModel.setWinStarCount(countIntent)
    }

    private fun initData() {
        // Lắng nghe Điểm tổng từ ViewModel
        viewModel.countWinStar.observe(this) { count ->
            binding.tvCountWS.text = count
        }

        // 5. Thu thập dữ liệu Paging cho tab "Nhận điểm" (Obtained)
        lifecycleScope.launch {
            viewModel.getCountHistoryList(true).collectLatest { pagingData ->
                obtainedAdapter.submitData(pagingData)
            }
        }

        // Thu thập dữ liệu Paging cho tab "Tiêu điểm" (Usage)
        lifecycleScope.launch {
            viewModel.getCountHistoryList(false).collectLatest { pagingData ->
                usageAdapter.submitData(pagingData)
            }
        }
    }

    private fun initListener() {
        binding.imgBack.setOnClickListener { finish() }

        binding.btnTabObtained.setOnClickListener {
            if (!isShowObtained) updateTabUI(true)
        }

        binding.btnTabUsage.setOnClickListener {
            if (isShowObtained) updateTabUI(false)
        }


        obtainedAdapter.addLoadStateListener { loadStates ->
            obtainedLoadStates = loadStates
            if (isShowObtained) {
                handleLoadState(loadStates, obtainedAdapter.itemCount)
            }
        }

        usageAdapter.addLoadStateListener { loadStates ->
            usageLoadStates = loadStates
            if (!isShowObtained) {
                handleLoadState(loadStates, usageAdapter.itemCount)
            }
        }

        // --------------------

        binding.srlObtainedRecord.setOnRefreshListener { obtainedAdapter.refresh() }
        binding.srlUsageRecord.setOnRefreshListener { usageAdapter.refresh() }
    }

    private fun handleLoadState(loadStates: CombinedLoadStates, itemCount: Int) {
        val isListLoading = loadStates.refresh is LoadState.Loading
        val isNotLoading = loadStates.refresh is LoadState.NotLoading

        if (isListLoading) {
            binding.llEmpty.visibility = View.GONE
            toggleListVisibility(false)
        }
        else if (isNotLoading) {
            if (itemCount == 0) {
                showEmptyView(true)
            } else {
                showEmptyView(false)
            }

            if (isShowObtained) binding.srlObtainedRecord.finishRefresh()
            else binding.srlUsageRecord.finishRefresh()
        }
    }

    private fun toggleListVisibility(show: Boolean) {
        val visibility = if (show) View.VISIBLE else View.GONE
        if (isShowObtained) binding.srlObtainedRecord.visibility = visibility
        else binding.srlUsageRecord.visibility = visibility
    }

    private fun updateTabUI(isObtained: Boolean) {
        isShowObtained = isObtained
        val activeColor = ContextCompat.getColor(this, R.color.color_E2518D)
        val inactiveColor = ContextCompat.getColor(this, R.color.color_4A5565)
        val indicatorActive = ContextCompat.getColor(this, R.color.color_E2518D)
        val indicatorInactive = ContextCompat.getColor(this, R.color.white)

        if (isObtained) {
            binding.tvObtainedRecords.setTextColor(activeColor)
            binding.tvUsageRecords.setTextColor(inactiveColor)
            binding.viewObtainedRecordsSelect.setBackgroundColor(indicatorActive)
            binding.viewUsageRecordsSelect.setBackgroundColor(indicatorInactive)
            binding.srlUsageRecord.visibility = View.GONE
            obtainedLoadStates?.let {
                checkStateForTab(it, obtainedAdapter.itemCount)
            }

        } else {
            binding.tvObtainedRecords.setTextColor(inactiveColor)
            binding.tvUsageRecords.setTextColor(activeColor)
            binding.viewObtainedRecordsSelect.setBackgroundColor(indicatorInactive)
            binding.viewUsageRecordsSelect.setBackgroundColor(indicatorActive)
            binding.srlObtainedRecord.visibility = View.GONE
            usageLoadStates?.let {
                checkStateForTab(it, usageAdapter.itemCount)
            }
        }
    }

    private fun checkStateForTab(loadStates: CombinedLoadStates, itemCount: Int) {
        if (loadStates.refresh is LoadState.Loading) {
            binding.llEmpty.visibility = View.GONE
            toggleListVisibility(false)
        } else {
            if (itemCount == 0) {
                showEmptyView(true)
            } else {
                showEmptyView(false)
            }
        }
    }

    private fun showEmptyView(isEmpty: Boolean) {
        if (isEmpty) {
            binding.llEmpty.visibility = View.VISIBLE
            if (isShowObtained) binding.srlObtainedRecord.visibility = View.GONE
            else binding.srlUsageRecord.visibility = View.GONE
        } else {
            binding.llEmpty.visibility = View.GONE
            if (isShowObtained) binding.srlObtainedRecord.visibility = View.VISIBLE
            else binding.srlUsageRecord.visibility = View.VISIBLE
        }
    }
}