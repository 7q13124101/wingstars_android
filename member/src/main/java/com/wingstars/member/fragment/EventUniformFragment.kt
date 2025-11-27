package com.wingstars.member.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingstars.base.base.BaseFragment
import com.wingstars.base.utils.DPUtils
import com.wingstars.base.utils.ScreenUtils
import com.wingstars.member.activity.AtmosphereFashionDetailsActivity
import com.wingstars.member.adapter.CategoryAdapter
import com.wingstars.member.adapter.SupportSuitAdapter
import com.wingstars.member.databinding.FragmentEventUniformBinding
import com.wingstars.member.viewmodel.EventUniformViewModel
import com.wingstars.member.viewmodel.SupportSuitViewModel

class EventUniformFragment : BaseFragment(), SupportSuitAdapter.OnItemListener {
    private lateinit var binding: FragmentEventUniformBinding
    private lateinit var viewModel: EventUniformViewModel

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

    private fun initView() {
        binding.list.layoutManager = GridLayoutManager(requireActivity(),2)
        val list = mutableListOf("1","2","3","4","5","6")
        var width = ScreenUtils.getWidth(requireActivity())
        var smallwidth  = width - DPUtils.dpToPx(50f,requireActivity()).toInt()
        var smallwidths = smallwidth/2
        var smallhight = smallwidths.toInt()*1.585
        binding.list.adapter = SupportSuitAdapter(requireActivity(), list,smallwidths.toInt()
            ,smallhight.toInt(),this)
        viewModel = ViewModelProvider(this)[EventUniformViewModel::class.java]
        viewModel.categorylist.observe(viewLifecycleOwner){ list->
            binding.categoryList.layoutManager = LinearLayoutManager(requireActivity(),
                LinearLayoutManager.HORIZONTAL,false)
            binding.categoryList.adapter = CategoryAdapter(requireActivity(),list)
        }
        viewModel.getCategoryList()
    }

    override fun onItemClick(position: Int) {
        startActivity(Intent(requireActivity(), AtmosphereFashionDetailsActivity::class.java))
    }
}