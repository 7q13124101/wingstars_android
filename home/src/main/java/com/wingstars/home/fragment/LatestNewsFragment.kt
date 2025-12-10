package com.wingstars.home.fragment

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
import com.wingstars.base.net.beans.WSPostResponse
import com.wingstars.home.activity.LatestNewsDetailActivity
import com.wingstars.home.adapter.LatestNewsAdapter
import com.wingstars.home.databinding.FragmentLatestNewsBinding
import com.wingstars.home.viewmodel.HomeViewModel

class LatestNewsFragment : Fragment() {

    private lateinit var binding: FragmentLatestNewsBinding
    private lateinit var viewModel: HomeViewModel
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

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        initView()
        initObserver()

        // Tải dữ liệu
        viewModel.getLatestNewsData()
    }

    private fun initView() {
        val newsListener = object : LatestNewsAdapter.onItemListener {
            override fun onItemClick(data: WSPostResponse, position: Int) {
                val intent = Intent(requireActivity(), LatestNewsDetailActivity::class.java)
                intent.putExtra("ITEM_NEWS_DATA", data)
                startActivity(intent)
            }
        }

        adapter = LatestNewsAdapter(
            requireContext(),
            mutableListOf<WSPostResponse>(),
            newsListener
        )

        binding.rvLatestNew.layoutManager = LinearLayoutManager(context)
        binding.rvLatestNew.adapter = adapter

        // Nút scroll to top
        binding.top.setOnClickListener {
            binding.scrollView.smoothScrollTo(0, 0)
        }

        binding.scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY == 0) {
                if (binding.top.isVisible) binding.top.visibility = View.GONE
            } else {
                if (binding.top.isGone) binding.top.visibility = View.VISIBLE
            }
        }

        binding.srlNotUsed.setEnableRefresh(false)
        binding.srlNotUsed.setEnableLoadMore(false)
    }

    private fun initObserver() {
        viewModel.newsDataList.observe(viewLifecycleOwner) { list ->
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
