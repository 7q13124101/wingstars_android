package com.wingstars.member.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.wingstars.base.base.BaseActivity
import com.wingstars.member.R
import com.wingstars.member.adapter.RankExplanationListAdapter
import com.wingstars.member.databinding.ActivityPopularityRankingBinding
import com.wingstars.member.databinding.ActivityRankExplanationBinding
import com.wingstars.member.viewmodel.PopularityRankingViewModel
import com.wingstars.member.viewmodel.RankExplanationViewModel

class RankExplanationActivity : BaseActivity() {
    private lateinit var binding: ActivityRankExplanationBinding
    private lateinit var viewModel: RankExplanationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankExplanationBinding.inflate(layoutInflater)
        setTitleFoot(
            view1 = binding.root,
            statusBarColor = R.color.color_F3F4F6,
            navigationBarColor = R.color.color_F3F4F6
        )
        initView()
        initData()
    }

    private fun initData() {
        viewModel = ViewModelProvider(this)[RankExplanationViewModel::class.java]
        viewModel.explanationlist.observe(this) {
            var adapter = RankExplanationListAdapter(this, it)
            binding.explanationList.adapter = adapter
        }
        viewModel.getExplanationlist()
    }

    override fun initView() {
        binding.title.setRightIconClickListener { finish() }
    }
}