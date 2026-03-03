package com.wingstars.member.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.wingstars.base.base.BaseActivity
import com.wingstars.member.R
import com.wingstars.member.adapter.RankExplanationListAdapter
import com.wingstars.member.bean.WSRankBean
import com.wingstars.member.databinding.ActivityRanksExplanationBinding
import com.wingstars.member.viewmodel.RankExplanationViewModel

class RankExplanationActivity : BaseActivity() {
    private lateinit var binding: ActivityRanksExplanationBinding
    private lateinit var viewModel: RankExplanationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRanksExplanationBinding.inflate(layoutInflater)
        setTitleFoot(
            view1 = binding.root,
            statusBarColor = R.color.color_F3F4F6,
            navigationBarColor = R.color.color_F3F4F6
        )
        initView()
        initData()
    }

    private fun initData() {
        // 1. 获取 Serializable 集合（核心代码）
        val serializableList = intent.getSerializableExtra("data")

        // 2. 安全强转并判空（避免 ClassCastException/NullPointerException）
        val productList: List<WSRankBean> = serializableList as? ArrayList<WSRankBean> ?: emptyList()
        //Log.e("productList","${Gson().toJson(productList)}")
        viewModel = ViewModelProvider(this)[RankExplanationViewModel::class.java]
        var adapter = RankExplanationListAdapter(this, productList.toMutableList())
        binding.explanationList.adapter = adapter
    }

    override fun initView() {
        binding.title.setRightIconClickListener { finish() }
    }
}