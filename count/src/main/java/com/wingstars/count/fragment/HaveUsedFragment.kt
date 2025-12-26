package com.wingstars.count.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingstars.count.R
import com.wingstars.count.activity.ExchangeDetailsActivity
import com.wingstars.count.adapter.HaveUsedCouponAdapter
import com.wingstars.count.databinding.FragmentHaveUsedBinding
import com.wingstars.count.viewmodel.ActivityExchangeViewModel

class HaveUsedFragment : Fragment() {
    private var _binding: FragmentHaveUsedBinding? = null
    private val binding get() = _binding!!
    private lateinit var usedCouponAdapter: HaveUsedCouponAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHaveUsedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usedCouponAdapter = HaveUsedCouponAdapter(listOf()) { item ->
            val intent = Intent(requireContext(), ExchangeDetailsActivity::class.java)
//            intent.putExtra("EXTRA_GIFT_ITEM", item)
            intent.putExtra("checkButton",2)
            startActivity(intent)
        }

        setupRecyclerView()
        setupRefreshLayout()
        loadData()
    }

    private fun setupRecyclerView() {
        binding.rvHaveUsed.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = usedCouponAdapter
        }
    }

    private fun setupRefreshLayout() {
        binding.srlHaveUsed.setOnRefreshListener { refreshLayout ->
            loadData()
            refreshLayout.finishRefresh(1000)
        }
    }

    private fun loadData() {
//        val mockData = listOf(
//            ActivityExchangeViewModel(
//                1,
//                "有鷹來同樂 TSG Party -  Wing Stars 簽名會（第三梯次）",
//                "2025/11/09 (日)", // Trường time
//                "100",
//                R.drawable.bg_round_image,
//                "所有會員皆適用",
//                "1次",
//                "80",
//                "澄清湖棒球場",
//                "Description...",
//                "aa",
//                ""
//            ),
//            ActivityExchangeViewModel(
//                2,
//                "有鷹來同樂 TSG Party -  Wing Stars 簽名會（第二梯次）",
//                "2025/10/28 (二)",
//                "100",
//                R.drawable.bg_round_image,
//                "所有會員皆適用",
//                "1次",
//                "80",
//                "澄清湖棒球場",
//                "Description...",
//                "aa",
//                ""
//            )
//        )

//        updateUI(mockData)
    }

    private fun updateUI(data: List<ActivityExchangeViewModel>) {
        if (data.isNotEmpty()) {
            usedCouponAdapter.setData(data)
            binding.rvHaveUsed.visibility = View.VISIBLE
            binding.llEmpty.visibility = View.GONE
        } else {
            binding.rvHaveUsed.visibility = View.GONE
            binding.llEmpty.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}