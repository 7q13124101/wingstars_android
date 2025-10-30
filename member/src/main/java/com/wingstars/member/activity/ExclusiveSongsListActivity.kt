package com.wingstars.member.activity

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.utils.RecyclerViewScrollHelper
import com.wingstars.member.adapter.HighlightsAdapter
import com.wingstars.member.adapter.HighlightsData
import com.wingstars.member.databinding.ActivityExclusiveSongsListBinding
import com.wingstars.member.viewmodel.ExclusiveSongsListViewModel

class ExclusiveSongsListActivity : BaseActivity(), RecyclerViewScrollHelper.onScrollListener {
    private lateinit var binding: ActivityExclusiveSongsListBinding
    private var exclusiveSongsAdapter: HighlightsAdapter? = null
    private lateinit var viewModel: ExclusiveSongsListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExclusiveSongsListBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[ExclusiveSongsListViewModel::class.java]

        setTitleFoot(binding.root)
        initData()
        initView()
    }

    private fun initData() {
        viewModel.getExclusiveSongsListData()
    }

    override fun initView() {
        RecyclerViewScrollHelper.setupScrollListener(
            binding.rvExclusiveSongs,
            this@ExclusiveSongsListActivity
        )

        binding.title.setBackClickListener { finish() }

        exclusiveSongsAdapter = HighlightsAdapter(
            this,
            mutableListOf(), object : HighlightsAdapter.OnItemListener {
                override fun onItemClick(data: HighlightsData, position: Int) {
                }
            }
        )
        binding.rvExclusiveSongs.adapter = exclusiveSongsAdapter

        viewModel.exclusiveSongsListData.observe(this) {
            setData(it)
        }

        binding.srlExclusiveSongs.setOnRefreshListener {
            initData()
            binding.srlExclusiveSongs.finishRefresh()
        }

        binding.top.setOnClickListener {
            binding.rvExclusiveSongs.smoothScrollToPosition(0)
        }
    }

    private fun setData(it: MutableList<HighlightsData>?) {
        if (it == null || it.isEmpty()) {
            binding.llEmpty.visibility = View.VISIBLE
            binding.rvExclusiveSongs.visibility = View.GONE
        } else {
            binding.llEmpty.visibility = View.GONE
            binding.rvExclusiveSongs.visibility = View.VISIBLE
            exclusiveSongsAdapter?.setList(it)
        }
    }

    override fun onScrollTop() {
        if (binding.top.isVisible) {
            binding.top.visibility = View.GONE
        }
    }

    override fun onScrollDown() {
        if (binding.top.isGone) {
            binding.top.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}