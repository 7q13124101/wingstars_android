package com.wingstars.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingstars.home.adapter.LatestNewsAdapter
import com.wingstars.home.databinding.FragmentLatestNewsBinding
import com.wingstars.home.viewmodel.LatestNewsViewModel

class LatestNewsFragment : Fragment() {

    private lateinit var binding: FragmentLatestNewsBinding
    private lateinit var viewModel: LatestNewsViewModel
    private lateinit var adapter: LatestNewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLatestNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[LatestNewsViewModel::class.java]

        initView()
        initObserver()

        // Tải dữ liệu
        viewModel.getLatestNews()
    }

    private fun initView() {
        // Khởi tạo Adapter với list rỗng ban đầu
        adapter = LatestNewsAdapter(requireContext(), mutableListOf())

        binding.rvLatestNew.layoutManager = LinearLayoutManager(context)
        binding.rvLatestNew.adapter = adapter

        // Xử lý nút Scroll to Top
        binding.top.setOnClickListener {
            binding.scrollView.smoothScrollTo(0, 0)
        }

        // Logic hiện/ẩn nút Top khi cuộn
        binding.scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY == 0) {
                if (binding.top.isVisible) binding.top.visibility = View.GONE
            } else {
                if (binding.top.isGone) binding.top.visibility = View.VISIBLE
            }
        }

        // Tắt chức năng refresh (vì layout bạn đặt id là srl_not_used)
        binding.srlNotUsed.setEnableRefresh(false)
        binding.srlNotUsed.setEnableLoadMore(false)
    }

    private fun initObserver() {
        viewModel.newsList.observe(viewLifecycleOwner) { list ->
            if (list.isNullOrEmpty()) {
                binding.llEmpty.visibility = View.VISIBLE
                binding.rvLatestNew.visibility = View.GONE
            } else {
                binding.llEmpty.visibility = View.GONE
                binding.rvLatestNew.visibility = View.VISIBLE
                adapter.setList(list)
            }
        }
    }
}