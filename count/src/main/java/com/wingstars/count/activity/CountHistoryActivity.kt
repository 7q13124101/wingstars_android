package com.wingstars.count.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var adapter: CountRecordAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCountHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        loadData()
        binding.imgBack.setOnClickListener {
            finish()
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun initView() {

        adapter = CountRecordAdapter(this, null)
        binding.adObtainedList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.adObtainedList.adapter = adapter
    }

    private fun loadData() {
        lifecycleScope.launch {
            val fetchedList = withContext(Dispatchers.IO) {
                fetchRecordsFromRepository()
            }
            adapter.setList(fetchedList.toMutableList())

            handleEmptyState(fetchedList.size)
        }
    }

    private fun handleEmptyState(dataSize: Int) {
        if (dataSize == 0) {
            binding.llEmpty.visibility = View.VISIBLE
            binding.adObtainedList.visibility = View.GONE
        } else {
            binding.llEmpty.visibility = View.GONE
            binding.adObtainedList.visibility = View.VISIBLE
        }
    }

    private suspend fun fetchRecordsFromRepository(): List<CountRecordsItemViewModel> {
        return listOf(
            CountRecordsItemViewModel("每日簽到 獎勵", "", "2025/10/15", "+1"),
            CountRecordsItemViewModel("每日簽到 獎勵", "", "2025/10/15", "+2"),
            CountRecordsItemViewModel("YouTube 星迷，任務達成", "", "2025/10/15", "+1"),
            CountRecordsItemViewModel("IG 星粉，任務達成", "", "2025/10/15", "+1"),
            CountRecordsItemViewModel("WS 中階會員，任務達成", "", "2025/10/15", "+100")
        )
    }
}