package com.wingstars.member.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.utils.DPUtils
import com.wingstars.member.R
import com.wingstars.member.adapter.RankingAdapter
import com.wingstars.member.bean.WSRankBean
import com.wingstars.member.bean.WSRankBean.ACFBean
import com.wingstars.member.databinding.ActivityPopularityRankingsBinding
import com.wingstars.member.view.CircleWithBorderTransformation
import com.wingstars.member.view.PopularityPopupView
import com.wingstars.member.viewmodel.PopularityRankingViewModel
import java.io.Serializable

class PopularityRankingActivity : BaseActivity(), View.OnClickListener,
    PopularityPopupView.OnPopupConfirm {
    private lateinit var binding: ActivityPopularityRankingsBinding
    private var wsNewRankData = mutableListOf<WSRankBean>()
    private var popupWindow: PopularityPopupView? = null
    private var type = ""
    private var adapter: RankingAdapter? = null
    private var borderColor = 0
    private var borderColor1 = 0
    private var borderColor2 = 0
    private var toInt = 0f
    private var toInt1 = 0f
    private lateinit var viewModel: PopularityRankingViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPopularityRankingsBinding.inflate(layoutInflater)
        setTitleFoot(view1 = binding.root, statusBarColor = R.color.color_F3F4F6)
        //setContentView(R.layout.activity_popularity_ranking)
        initView()
        initData()

    }

    private fun initData() {
        viewModel = ViewModelProvider(this)[PopularityRankingViewModel::class.java]
        viewModel.loading.observe(this) {
            showLoadingUI(it, this)
        }
        viewModel.tip.observe(this){
            Toast.makeText(this,"$it", Toast.LENGTH_SHORT).show()
        }
        viewModel.wsRankData.observe(this) {
            wsNewRankData.addAll(it)
            setScreening(type)
        }

        viewModel.getRenderedList()
        binding.refresh.setColorSchemeResources(R.color.color_E2518D)
        binding.refresh.setOnRefreshListener {
            binding.refresh.isRefreshing = false
            viewModel.getRenderedList()
        }
    }

    private fun setScreening(name: String) {
        if (!wsNewRankData.isNullOrEmpty()) {
            val filter = wsNewRankData.filter { it.title == name }
            Log.e("setScreening", "${Gson().toJson(filter)}")
            if (!filter.isNullOrEmpty()) {
                binding.rankList.visibility = View.VISIBLE
                binding.notData.visibility = View.GONE
                val acf = filter[0].acf
                if (!acf.isNullOrEmpty()) {
                    setThree(acf.take(3).toMutableList())
                    var takeLast = mutableListOf<ACFBean>()
                    if (acf!!.size > 3) {
                        takeLast = acf.takeLast(acf!!.size - 3).toMutableList()
                    }

                    if (adapter == null) {
                        adapter = RankingAdapter(this, takeLast)
                        binding.rankList.adapter = adapter
                    } else {
                        adapter!!.setList(takeLast)
                    }
                } else {
                    setThree(mutableListOf())
                }


            } else {
                binding.rankList.visibility = View.GONE
                binding.notData.visibility = View.VISIBLE
                var data = mutableListOf<ACFBean>()
                setThree(data)
                if (adapter == null) {
                    adapter = RankingAdapter(this, data)
                    binding.rankList.adapter = adapter
                } else {
                    adapter!!.setList(data)
                }
            }
        }
    }

    private fun setHead(resourceId: Int, borderWidth: Float, view: ImageView) {
        Glide.with(this).load(resourceId)
            .transform(CircleWithBorderTransformation(borderWidth, borderColor))
            .into(view)
    }

    private fun setHead(resourceId: String, borderWidth: Float, view: ImageView) {
        Glide.with(this).load(resourceId)
            .transform(CircleWithBorderTransformation(borderWidth, borderColor))
            .into(view)
    }

    private fun setText(
        name1: String = "", volume1: String = "", name2: String = "", volume2: String = "",
        name3: String = "", volume3: String = ""
    ) {
        binding.name1.text = name1
        binding.volume1.text = volume1
        binding.name2.text = name2
        binding.volume2.text = volume2
        binding.name3.text = name3
        binding.volume3.text = volume3
    }


    private fun setThree(data: MutableList<ACFBean>) {

        if (data.size == 0) {
            setHead(R.color.transparent, toInt1, binding.second)
            setHead(R.color.transparent, toInt, binding.one)
            setHead(R.color.transparent, toInt1, binding.three)
            setText()
        } else if (data.size == 1) {
            setHead("${data[0].image}", toInt, binding.one)
            setHead(R.color.transparent, toInt1, binding.second)
            setHead(R.color.transparent, toInt1, binding.three)
            setText(name1 = "${data[0].name}", volume1 = "${data[0].volume}")
        } else if (data.size == 2) {
            setHead("${data[0].image}", toInt, binding.one)
            setHead("${data[1].image}", toInt1, binding.second)
            setHead(R.color.transparent, toInt1, binding.three)
            setText(
                name1 = "${data[0].name}",
                volume1 = "${data[0].volume}",
                name2 = "${data[1].name}",
                volume2 = "${data[1].volume}"
            )
        } else {
            setHead("${data[0].image}", toInt, binding.one)
            setHead("${data[1].image}", toInt1, binding.second)
            setHead("${data[2].image}", toInt1, binding.three)
            setText(
                name1 = "${data[0].name}",
                volume1 = "${data[0].volume}",
                name2 = "${data[1].name}",
                volume2 = "${data[1].volume}",
                name3 = "${data[2].name}",
                volume3 = "${data[2].volume}"
            )
        }


    }

    override fun initView() {
        borderColor = getColor(R.color.transparent)
        borderColor1 = getColor(R.color.transparent)
        borderColor2 = getColor(R.color.transparent)
        toInt = DPUtils.dpToPx(4f, this)
        toInt1 = DPUtils.dpToPx(3f, this)
        type = getString(R.string.support_popularity_list)
        binding.title.setBackClickListener { finish() }
        binding.title.setRightIconClickListener {
           var intent =  Intent(this, RankExplanationActivity::class.java)
            intent.putExtra("data", ArrayList(wsNewRankData) as ArrayList<Serializable>)
            startActivity(intent)
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
        if (popupWindow == null) {
            popupWindow = PopularityPopupView(this, this, getNavigationBarHeight())
        }
        popupWindow?.show(binding.ranking)
        popupWindow!!.setName(type)
    }

    override fun onPopupConfirm(name: String) {
        if (type != name) {
            type = name
            binding.type.text = type
            setScreening(type)
        }

    }
}