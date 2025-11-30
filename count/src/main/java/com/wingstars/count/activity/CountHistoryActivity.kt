package com.wingstars.count.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingstars.count.R
import com.wingstars.count.adapter.CountRecordAdapter
// 1. Import class Binding
import com.wingstars.count.databinding.ActivityCountHistoryBinding
import com.wingstars.count.viewmodel.CountRecordsItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CountHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCountHistoryBinding
    private lateinit var obtainedAdapter: CountRecordAdapter
    private lateinit var usageAdapter: CountRecordAdapter
    private var isShowObtained = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCountHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        loadData()
        initListener()
        binding.imgBack.setOnClickListener {
            finish()
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            binding.llHeader.setPadding(0, systemBars.top, 0, 0)

            insets
        }
    }
    private fun initView() {
        obtainedAdapter = CountRecordAdapter(this, null, isUsageRecord = false)
        binding.adObtainedList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.adObtainedList.adapter = obtainedAdapter

        usageAdapter = CountRecordAdapter(this, null, isUsageRecord = true)
        binding.adUsageList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.adUsageList.adapter = usageAdapter

        updateTabUI(true)
    }

    private fun initListener() {
        binding.btnTabObtained.setOnClickListener {
            if (!isShowObtained) {
                updateTabUI(true)
            }
        }

        binding.btnTabUsage.setOnClickListener {
            if (isShowObtained) {
                updateTabUI(false)
            }
        }
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
            binding.srlObtainedRecord.visibility = View.VISIBLE
            binding.srlUsageRecord.visibility = View.GONE

            checkEmptyState(obtainedAdapter.itemCount)

        } else {
            binding.tvObtainedRecords.setTextColor(inactiveColor)
            binding.tvUsageRecords.setTextColor(activeColor)
            binding.viewObtainedRecordsSelect.setBackgroundColor(indicatorInactive)
            binding.viewUsageRecordsSelect.setBackgroundColor(indicatorActive)
            binding.srlObtainedRecord.visibility = View.GONE
            binding.srlUsageRecord.visibility = View.VISIBLE

            checkEmptyState(usageAdapter.itemCount)
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            val obtainedList = withContext(Dispatchers.IO) { fetchObtainedRecords() }
            val usageList = withContext(Dispatchers.IO) { fetchUsageRecords() }

            obtainedAdapter.setList(obtainedList.toMutableList())
            usageAdapter.setList(usageList.toMutableList())

            if (isShowObtained) {
                checkEmptyState(obtainedList.size)
            } else {
                checkEmptyState(usageList.size)
            }
        }
    }
    private fun checkEmptyState(dataSize: Int) {
        if (dataSize == 0) {
            binding.llEmpty.visibility = View.VISIBLE
            binding.srlObtainedRecord.visibility = View.GONE
            binding.srlUsageRecord.visibility = View.GONE
        } else {
            binding.llEmpty.visibility = View.GONE
            if (isShowObtained) {
                binding.srlObtainedRecord.visibility = View.VISIBLE
            } else {
                binding.srlUsageRecord.visibility = View.VISIBLE
            }
        }
    }

    private suspend fun fetchObtainedRecords(): List<CountRecordsItemViewModel> {
        return listOf(
            CountRecordsItemViewModel("每日簽到 獎勵", "Check-in", "2025/10/15", "1"),
            CountRecordsItemViewModel("每日簽到 獎勵", "Check-in", "2025/10/15", "2"),
            CountRecordsItemViewModel("YouTube 星迷", "Task", "2025/10/15", "1"),
            CountRecordsItemViewModel("IG 星粉，任務達成", "Check-in", "2025/10/15", "1"),
            CountRecordsItemViewModel("最喜歡哪位女孩唱《\uD835\uDE3E\uD835\uDE5D\uD835\uDE5A\uD835\uDE5A\uD835\uDE67 \uD835\uDE5E\uD835\uDE69 \uD835\uDE6A\uD835\uDE65 加大》 ？，任務達成", "Check-in", "2025/10/15", "5"),
            CountRecordsItemViewModel("WS 中階會員，任務達成", "Level Up", "2025/10/15", "100")
        )
    }

    private suspend fun fetchUsageRecords(): List<CountRecordsItemViewModel> {
        return listOf(
            CountRecordsItemViewModel("2025 WS單曲寫真女孩貼紙包 兌換", "Gift", "2025/10/16", "-1699"),
            CountRecordsItemViewModel("2025 WS LOGO杯墊 兌換", "Gift", "2025/10/18", "-799"),
            CountRecordsItemViewModel("有鷹來同樂 TSG Party -  Wing Stars 簽名會（第一梯次）兌換", "Gift", "2025/10/15", "-100"),
        )
    }
}