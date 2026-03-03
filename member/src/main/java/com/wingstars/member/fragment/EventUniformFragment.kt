package com.wingstars.member.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.wingstars.base.base.BaseFragment
import com.wingstars.base.net.beans.WSFashionResponse
import com.wingstars.base.utils.DPUtils
import com.wingstars.base.utils.ScreenUtils
import com.wingstars.member.R
import com.wingstars.member.activity.AtmosphereFashionDetailsActivity
import com.wingstars.member.adapter.CategoryAdapter
import com.wingstars.member.adapter.SupportSuitAdapter
import com.wingstars.member.databinding.FragmentEventUniformBinding
import com.wingstars.member.viewmodel.EventUniformViewModel
import com.wingstars.member.viewmodel.SupportSuitViewModel

class EventUniformFragment : BaseFragment(), SupportSuitAdapter.OnItemListener {
    private lateinit var binding: FragmentEventUniformBinding
    private lateinit var viewModel: SupportSuitViewModel
    private var initShow = false
    var isMore = false

    private var adapter1: SupportSuitAdapter? = null
    private var dataList: MutableList<WSFashionResponse> = mutableListOf()

    private var loading_completed = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventUniformBinding.inflate(inflater, container, false)
        val root = binding.root
        initView()
        return root
    }

    override fun onResume() {
        super.onResume()
        if (!initShow) {
            initShow = true
            initData()
        }
    }

    private fun initData() {
        var width = ScreenUtils.getWidth(requireActivity())
        var smallwidth = width - DPUtils.dpToPx(50f, requireActivity()).toInt()
        var smallwidths = smallwidth / 2
        var smallhight = smallwidths.toInt() * 1.585
        viewModel = ViewModelProvider(this)[SupportSuitViewModel::class.java]
        viewModel.wsFashions.observe(viewLifecycleOwner) {
            loading_completed = true
            if (it.size == viewModel.PER_PAGE) {
                isMore = true
            }
            binding.notData.visibility = View.GONE
            binding.list.visibility = View.VISIBLE
            it.forEach { data ->
                val fashionCategoryf = data.fashion_categoryF
                var wsRankDatalist = viewModel.wsFashionCategorysData.value
                val typeData = wsRankDatalist!!.find { it.id == fashionCategoryf }
                if (typeData != null) {
                    data.type = when (typeData.name.trim()) {
                        "應援服" -> 1
                        "活動服" -> 2
                        else -> 0
                    }

                }
            }
            //Log.e("wsFashions", "${Gson().toJson(it)}")
            /* var adapter1 = SupportSuitAdapter(requireActivity(), it, smallwidths.toInt()
                 ,smallhight.toInt(),this)
             binding.list.adapter = adapter1*/
            dataList.clear()
            dataList.addAll(it)
            //Log.e("wsFashions", "${Gson().toJson(it)}")
            if (adapter1 == null) {
                adapter1 = SupportSuitAdapter(
                    requireActivity(), dataList, smallwidths.toInt(), smallhight.toInt(), this
                )
                binding.list.adapter = adapter1
            } else {
                adapter1!!.notifyDataSetChanged()
            }
        }

        viewModel.wsMoreFashions.observe(viewLifecycleOwner) {
            it.forEach { data ->
                val fashionCategoryf = data.fashion_categoryF
                var wsRankDatalist = viewModel.wsFashionCategorysData.value
                val typeData = wsRankDatalist!!.find { it.id == fashionCategoryf }
                if (typeData != null) {
                    data.type = when (typeData.name.trim()) {
                        "應援服" -> 1
                        "活動服" -> 2
                        else -> 0
                    }

                }
            }
            dataList.addAll(it)
            adapter1!!.notifyDataSetChanged()

        }
        viewModel.tip.observe(viewLifecycleOwner){
            showTip(if (it=="rest_post_invalid_page_number") getString(R.string.no_more_data) else it)
        }
        viewModel.loading.observe(viewLifecycleOwner) {
            showLoadingUI(it, requireActivity())
        }

        binding.srlMemberIntroduction.setOnRefreshListener {
            binding.srlMemberIntroduction.finishRefresh()
            viewModel.PAGE = 1
            isMore = false
            loading_completed= false
            viewModel.wsFashionCategorys(2)
        }
        binding.srlMemberIntroduction.setOnLoadMoreListener {
            binding.srlMemberIntroduction.finishLoadMore()
            //Log.e("isMore", "isMore=$isMore")
            if (isMore) {
                viewModel.wsFashions(isShowLoading = true, isLoadMore = true)
            }else{
                if (loading_completed){
                    showTip(getString(R.string.no_more_data))
                }

            }
        }
        viewModel.wsFashionCategorys(2)
    }

    private fun initView() {
        //   val list = mutableListOf("1","2","3","4","5","6")

        /* binding.list.adapter = SupportSuitAdapter(requireActivity(), list,smallwidths.toInt()
             ,smallhight.toInt(),this)*/


        /* viewModel.categorylist.observe(viewLifecycleOwner){ list->
             binding.categoryList.layoutManager = LinearLayoutManager(requireActivity(),
                 LinearLayoutManager.HORIZONTAL,false)
             binding.categoryList.adapter = CategoryAdapter(requireActivity(),list)
         }*/
        //viewModel.getCategoryList()
    }

    override fun onItemClick(memberId: Int,fashionType: Int) {
        val intent = Intent(
            requireActivity(),
            AtmosphereFashionDetailsActivity::class.java
        )
        intent.putExtra("memberId", memberId)
        intent.putExtra("fashionType", fashionType)
        startActivity(intent)
    }
}