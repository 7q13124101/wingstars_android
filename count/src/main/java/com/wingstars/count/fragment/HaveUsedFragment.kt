package com.wingstars.count.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingstars.count.R
import com.wingstars.count.activity.ExchangeDetailsActivity
import com.wingstars.count.activity.GiftDetailsActivity
import com.wingstars.count.adapter.HaveUsedCouponAdapter
import com.wingstars.count.databinding.FragmentHaveUsedBinding
import com.wingstars.count.viewmodel.HaveUsedViewModel
import com.wingstars.base.net.NetBase
import com.wingstars.base.net.NetworkMonitorNew
import com.wingstars.count.Repository.ActivityStatusEnum

class HaveUsedFragment : Fragment() {
    private var _binding: FragmentHaveUsedBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HaveUsedViewModel
    private lateinit var usedCouponAdapter: HaveUsedCouponAdapter

    private var isDataLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHaveUsedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo ViewModel
        viewModel = ViewModelProvider(requireActivity())[HaveUsedViewModel::class.java]

        setupRecyclerView()
        setupRefreshLayout()
        initScrollListener()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        if (!isDataLoaded) {
            loadData()
            isDataLoaded = true
        }
    }

    private fun setupRecyclerView() {
        usedCouponAdapter = HaveUsedCouponAdapter(mutableListOf()) { data ->
            if (data.coupon?.couponType == 1) {
                val intent = Intent(requireActivity(), GiftDetailsActivity::class.java)
                intent.putExtra("status", ActivityStatusEnum.USED_REDEMPTION.name)
                intent.putExtra("title", getString(R.string.exchange_details))
                intent.putExtra("coupon_data", data.coupon)
                startActivity(intent)
            } else if (data.coupon?.couponType == 2) {
                val intent = Intent(requireActivity(), ExchangeDetailsActivity::class.java)
                intent.putExtra("status", ActivityStatusEnum.USED_REDEMPTION.name)
                intent.putExtra("coupon_data", data.coupon)
                startActivity(intent)
            }
        }

        binding.rvHaveUsed.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = usedCouponAdapter
        }

        binding.top.setOnClickListener {
            binding.scrollView.smoothScrollTo(0, 0)
        }
    }

    private fun setupRefreshLayout() {
        binding.srlHaveUsed.setOnRefreshListener {
            loadData()
        }
    }

    private fun initScrollListener() {
        binding.scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY == 0) {
                if (binding.top.isVisible) {
                    binding.top.visibility = View.GONE
                }
            } else {
                if (binding.top.isGone) {
                    binding.top.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun loadData() {
        if (NetworkMonitorNew.getInstance(requireActivity()).currentNetworkState.isConnected) {
            viewModel.getHaveUsedCouponsData()
        } else {
            if (binding.srlHaveUsed.state.isOpening) binding.srlHaveUsed.finishRefresh(false)
            binding.llEmpty.visibility = View.VISIBLE
            binding.rvHaveUsed.visibility = View.GONE
//            binding.tvHaveUsedRemind.visibility = View.GONE
        }
    }

    private fun setupObservers() {
        viewModel.haveUsedCouponsData.observe(viewLifecycleOwner) { list ->
            binding.srlHaveUsed.finishRefresh(true)

            if (!list.isNullOrEmpty()) {
                // Có dữ liệu
                binding.llEmpty.visibility = View.GONE
                binding.rvHaveUsed.visibility = View.VISIBLE
//                binding.tvHaveUsedRemind.visibility = View.VISIBLE
                usedCouponAdapter.setData(list)
            } else {
                binding.llEmpty.visibility = View.VISIBLE
                binding.rvHaveUsed.visibility = View.GONE
//                binding.tvHaveUsedRemind.visibility = View.GONE
                usedCouponAdapter.setData(emptyList())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}