package com.wingstars.member.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingstars.base.base.BaseFragment
import com.wingstars.member.adapter.CategoryAdapter
import com.wingstars.member.databinding.FragmentEventUniformBinding
import com.wingstars.member.viewmodel.EventUniformViewModel
import com.wingstars.member.viewmodel.SupportSuitViewModel

class EventUniformFragment : BaseFragment() {
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
        viewModel = ViewModelProvider(this)[EventUniformViewModel::class.java]
        viewModel.categorylist.observe(viewLifecycleOwner){ list->
            binding.categoryList.layoutManager = LinearLayoutManager(requireActivity(),
                LinearLayoutManager.HORIZONTAL,false)
            binding.categoryList.adapter = CategoryAdapter(requireActivity(),list)
        }
        viewModel.getCategoryList()
    }
}