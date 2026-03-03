package com.wingstars.member.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnScrollChangeListener
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.wingstars.base.base.BaseFragment
import com.wingstars.base.net.beans.YoutubeListResponse
import com.wingstars.member.adapter.HighlightsAdapter
import com.wingstars.member.databinding.FragmentHighlightsBinding
import com.wingstars.member.viewmodel.HighlightsType
import com.wingstars.member.viewmodel.HighlightsViewModel


class HighlightsFragment(var highlightsType: HighlightsType) : BaseFragment() {
    private lateinit var binding: FragmentHighlightsBinding
    private lateinit var viewModel: HighlightsViewModel
    private lateinit var highlightsAdapter: HighlightsAdapter
    private var isDataLoaded = false // 标记数据是否加载过
    override fun onResume() {
        super.onResume()
        //Log.e("viewModel","onResume=$highlightsType isDataLoaded=$isDataLoaded")
        if (!isDataLoaded) {
            loadData()
            isDataLoaded = true
        }
    }

    private fun loadData() {
        viewModel.getHighlightsList(highlightsType)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[HighlightsViewModel::class.java]
        initView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHighlightsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    private fun initView() {
        highlightsAdapter = HighlightsAdapter(
            requireActivity(),
            mutableListOf(),
            object : HighlightsAdapter.OnItemListener {
                override fun onItemClick(data: YoutubeListResponse.Item, position: Int) {
                    if (data.linkF.isNotEmpty()) {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(data.linkF)
                        startActivity(intent)
                    }
                }
            })
        binding.rvHighlights.adapter = highlightsAdapter
        viewModel.highlightsList.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                binding.llEmpty.visibility = View.GONE
                binding.rvHighlights.visibility = View.VISIBLE
                highlightsAdapter.setList(it)
            } else {
                binding.llEmpty.visibility = View.VISIBLE
                binding.rvHighlights.visibility = View.GONE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoadingUI(it, requireActivity())
        }


        binding.top.setOnClickListener {
            binding.scrollView.smoothScrollTo(0, 0)
        }

        binding.scrollView.setOnScrollChangeListener(object : OnScrollChangeListener {
            override fun onScrollChange(
                v: View?,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int,
            ) {
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
        })



        binding.srlNotUsed.setOnRefreshListener {
            loadData()
            //Log.e("viewModel", "srlNotUsed=$highlightsType")
            binding.srlNotUsed.finishRefresh()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}