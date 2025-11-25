package com.wingstars.count.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingstars.count.R
import com.wingstars.count.adapter.UnusedCouponAdapter
import com.wingstars.count.databinding.FragmentNotUsedBinding
import com.wingstars.count.viewmodel.CouponViewModel

class NotUsedFragment : Fragment() {
    private var _binding: FragmentNotUsedBinding? = null
    private val binding get() = _binding!!

    // Khởi tạo adapter
    private val unusedCouponAdapter = UnusedCouponAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotUsedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupRefreshLayout()
        loadData()
    }

    private fun setupRecyclerView() {
        binding.rvNotUsed.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = unusedCouponAdapter
        }
    }

    private fun setupRefreshLayout() {
        binding.srlNotUsed.setOnRefreshListener { refreshLayout ->
            loadData()
            refreshLayout.finishRefresh(1000)
        }
    }

    private fun loadData() {
        val mockData = listOf(
            CouponViewModel("1", "2025 WS單曲寫真壓克力鑰匙圈", "兌換期間：2025/10/28 13:00 ~ 2025/11/09 12:00",R.drawable.ic_count_gift_4),
            CouponViewModel("2", "2025 WS單曲寫真女孩貼紙包", "兌換期間：2025/10/28 13:00 ~ 2025/11/09 12:00",R.drawable.ic_count_gift_2),
            CouponViewModel("3", "有鷹來同樂 TSG Party -  Wing Stars 簽名會（第三梯次）", "兌換期間：2025/10/28 13:00 ~ 2025/11/09 12:00",R.drawable.bg_round_image),
            CouponViewModel("4", "有鷹來同樂 TSG Party -  Wing Stars 簽名會（第二梯次）", "兌換期間：2025/10/28 13:00 ~ 2025/11/09 12:00",R.drawable.bg_round_image),
        )

        updateUI(mockData)
    }

    private fun updateUI(data: List<CouponViewModel>) {
        if (data.isNotEmpty()) {
            unusedCouponAdapter.setData(data)
            binding.rvNotUsed.visibility = View.VISIBLE
            binding.llEmpty.visibility = View.GONE
        } else {
            binding.rvNotUsed.visibility = View.GONE
            binding.llEmpty.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}