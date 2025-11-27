package com.wingstars.member.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.utils.DPUtils
import com.wingstars.member.R
import com.wingstars.member.adapter.RankingAdapter
import com.wingstars.member.databinding.ActivityPopularityRankingBinding
import com.wingstars.member.view.CircleWithBorderTransformation
import com.wingstars.member.view.PopularityPopupView
import com.wingstars.member.viewmodel.PopularityRankingViewModel

class PopularityRankingActivity : BaseActivity(), View.OnClickListener,
    PopularityPopupView.OnPopupConfirm {
    private lateinit var binding: ActivityPopularityRankingBinding
    private lateinit var viewModel: PopularityRankingViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPopularityRankingBinding.inflate(layoutInflater)
        setTitleFoot(view1 = binding.root, statusBarColor = R.color.color_F3F4F6)
        //setContentView(R.layout.activity_popularity_ranking)
        initView()
        initData()

    }

    private fun initData() {
        viewModel = ViewModelProvider(this)[PopularityRankingViewModel::class.java]
        viewModel.rankinglist.observe(this) {
            var adapter = RankingAdapter(this, it)
            binding.rankList.adapter = adapter
        }
        viewModel.getRankinglist()
    }

    override fun initView() {

        val borderColor = getColor(R.color.color_99A1AF)
        val borderColor1 = getColor(R.color.color_F0B100)
        val borderColor2 = getColor(R.color.color_BB4D00)
        val toInt = DPUtils.dpToPx(4f, this)
        val toInt1 = DPUtils.dpToPx(3f, this)
        Glide.with(this).load(R.mipmap.ic_member_page_background)
            .transform(CircleWithBorderTransformation(toInt1, borderColor))
            .into(binding.second)
        Glide.with(this).load(R.mipmap.ic_member_page_background)
            .transform(CircleWithBorderTransformation(toInt, borderColor1))
            .into(binding.one)
        Glide.with(this).load(R.mipmap.ic_member_page_background)
            .transform(CircleWithBorderTransformation(toInt1, borderColor2))
            .into(binding.three)
        binding.title.setBackClickListener { finish() }
        binding.title.setRightIconClickListener {
            startActivity(
                Intent(
                    this,
                    RankExplanationActivity::class.java
                )
            )
        }
        binding.sort.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        var id = v?.id
        when (id) {
            binding.sort.id -> {
                showPopWindow()
            }
        }
    }

    private fun showPopWindow() {
        var popupWindow = PopularityPopupView(this, this, getNavigationBarHeight())
        popupWindow.show(binding.ranking)
    }

    override fun onPopupConfirm() {

    }
}