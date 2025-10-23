package com.wingstars.member.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingstars.base.base.BaseFragment
import com.wingstars.member.viewmodel.MemberViewModel
import com.wingstars.member.adapter.PopularityAdapter
import com.wingstars.member.adapter.GirlIntroductionAdapter
import com.wingstars.member.adapter.SupportFashionAdapter
import com.wingstars.member.databinding.FragmentMemberBinding

class MemberFragment : BaseFragment(), PopularityAdapter.onItemListener, View.OnClickListener {
    private lateinit var viewModel : MemberViewModel
    private lateinit var binding: FragmentMemberBinding
    private var statusBarHeight = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMemberBinding.inflate(inflater, container, false)
        val root = binding.root
        initView()
        return root
    }


    private fun initView() {
        viewModel = ViewModelProvider(this)[MemberViewModel::class.java]
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.VANILLA_ICE_CREAM){
            binding.root.setOnApplyWindowInsetsListener{ v, insets ->
                val statusBarHeight = insets.getInsets(WindowInsets.Type.statusBars()).top
                Log.e("statusBarHeight","statusBarHeight=$statusBarHeight")
                setViewTop(binding.title,statusBarHeight)
                binding.root.setOnApplyWindowInsetsListener(null)
                insets
            }
        }else{
            setViewTop(binding.title,getStatusBarHeight())
        }

        viewModel.popularitylist.observe(viewLifecycleOwner){
            var adapter = PopularityAdapter(requireActivity(), it, this)
            binding.chartList.layoutManager = LinearLayoutManager(
                requireActivity(),
                LinearLayoutManager.HORIZONTAL, false
            )
            binding.chartList.adapter = adapter

            var adapter1 = SupportFashionAdapter(requireActivity(), it)
            binding.supportFashionList.layoutManager = LinearLayoutManager(
                requireActivity(),
                LinearLayoutManager.HORIZONTAL, false
            )
            binding.supportFashionList.adapter = adapter1

            var adapter2 = GirlIntroductionAdapter(requireActivity(), it)
            binding.girlsList.layoutManager = LinearLayoutManager(
                requireActivity(),
                LinearLayoutManager.HORIZONTAL, false
            )
            binding.girlsList.adapter = adapter2
        }
        viewModel.getPopularitylist()
        binding.popularityRanking.setOnClickListener(this)

    }


    public fun setViewTop(view: View, top: Int){
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        layoutParams.topMargin = top
        view.setLayoutParams(layoutParams);
    }

    override fun ClickItem(position: Int) {

    }

    override fun onClick(v: View?) {
        val id = v?.id
        when(id){
//            binding.popularityRanking.id-> startActivity(Intent(requireActivity(),
//                PopularityRankingActivity::class.java
//            ))
        }
    }


}