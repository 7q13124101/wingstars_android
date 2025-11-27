package com.wingstars.member.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingstars.member.R
import com.wingstars.member.adapter.BasicIntroductionAdapter
import com.wingstars.member.adapter.BasicIntroductionFunBean
import com.wingstars.member.databinding.FragmentBasicInformationBinding
import com.wingstars.member.viewmodel.BasicInformationViewModel


class BasicInformationFragment : Fragment() {

    private lateinit var binding: FragmentBasicInformationBinding
    private lateinit var viewModel: BasicInformationViewModel

    private lateinit var basicIntroductionAdapter: BasicIntroductionAdapter

    private var isDataLoaded = false // 标记数据是否加载过
    override fun onResume() {
        super.onResume()

        if (!isDataLoaded) {
            loadData()
            isDataLoaded = true
        }
    }

    private fun loadData() {
        viewModel.getBasicIntroductionList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[BasicInformationViewModel::class.java]
        initView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBasicInformationBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    private fun initView() {

        //create base introduction adapter.
        basicIntroductionAdapter = BasicIntroductionAdapter(mutableListOf())
        binding.rvIntroduction.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rvIntroduction.adapter = basicIntroductionAdapter

        //set adapter data.
        viewModel.introductionList.observe(viewLifecycleOwner) {
            basicIntroductionAdapter.setList(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}