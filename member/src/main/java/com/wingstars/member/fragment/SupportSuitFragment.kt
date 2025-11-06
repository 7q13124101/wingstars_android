package com.wingstars.member.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingstars.base.base.BaseFragment
import com.wingstars.member.adapter.CategoryAdapter
import com.wingstars.member.databinding.FragmentSupportSuitBinding
import com.wingstars.member.viewmodel.RankExplanationViewModel
import com.wingstars.member.viewmodel.SupportSuitViewModel

class SupportSuitFragment : BaseFragment() {

    private lateinit var viewModel: SupportSuitViewModel
    private lateinit var binding: FragmentSupportSuitBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSupportSuitBinding.inflate(inflater, container, false)
        val root = binding.root
        initView()
        return root
    }

    private fun initView() {
        viewModel = ViewModelProvider(this)[SupportSuitViewModel::class.java]
        viewModel.categorylist.observe(viewLifecycleOwner){ list->
            binding.categoryList.layoutManager = LinearLayoutManager(requireActivity(),
                LinearLayoutManager.HORIZONTAL,false)
            binding.categoryList.adapter = CategoryAdapter(requireActivity(),list)
        }
        viewModel.getCategoryList()
    }
}