package com.wingstars.member.activity


import android.content.Intent
import android.os.Bundle
import android.view.View


import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.net.beans.WSMemberResponse
import com.wingstars.base.utils.ItemHotDecoration
import com.wingstars.base.utils.RecyclerViewScrollHelper
import com.wingstars.member.R
import com.wingstars.member.adapter.MemberIntroductionAdapter
import com.wingstars.member.databinding.ActivityMemberIntroductionBinding
import com.wingstars.member.viewmodel.MemberIntroductionViewModel

class MemberIntroductionActivity : BaseActivity(), RecyclerViewScrollHelper.onScrollListener {
    private lateinit var binding: ActivityMemberIntroductionBinding
    private var memberIntroductionAdapter: MemberIntroductionAdapter? = null
    private lateinit var viewModel: MemberIntroductionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMemberIntroductionBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[MemberIntroductionViewModel::class.java]

        setTitleFoot(binding.root)
        initData()
        initView()
    }

    private fun initData() {
        viewModel.getMemberIntroductionListData()
    }

    override fun initView() {
        RecyclerViewScrollHelper.setupScrollListener(
            binding.rvMemberIntroduction,
            this@MemberIntroductionActivity
        )

        binding.title.setBackClickListener { finish() }

        memberIntroductionAdapter = MemberIntroductionAdapter(
            this,
            mutableListOf(), object : MemberIntroductionAdapter.OnItemListener {
                override fun onItemClick(data: WSMemberResponse, position: Int) {
                    val intent =
                        Intent(this@MemberIntroductionActivity, MemberDetailsActivity::class.java)
                    intent.putExtra("WSMemberResponse", data)
                    startActivity(intent)
                }
            }
        )
        binding.rvMemberIntroduction.adapter = memberIntroductionAdapter
        binding.rvMemberIntroduction.addItemDecoration(
            ItemHotDecoration(
                resources.getDimensionPixelSize(
                    R.dimen.dp_10
                )
            )
        )

        viewModel.memberIntroductionListData.observe(this) {
            setData(it)
        }

        binding.srlMemberIntroduction.setOnRefreshListener {
            initData()
            binding.srlMemberIntroduction.finishRefresh()
        }

        binding.top.setOnClickListener {
            binding.rvMemberIntroduction.smoothScrollToPosition(0)
        }

        viewModel.loading.observe(this) {
            showLoadingUI(it, this)
        }
    }

    private fun setData(it: MutableList<WSMemberResponse>?) {
        if (it == null || it.isEmpty()) {
            binding.llEmpty.visibility = View.VISIBLE
            binding.rvMemberIntroduction.visibility = View.GONE
        } else {
            binding.llEmpty.visibility = View.GONE
            binding.rvMemberIntroduction.visibility = View.VISIBLE
            memberIntroductionAdapter?.setList(it)
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