package com.wingstars.member.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wingstars.member.databinding.FragmentPersonalScheduleBinding
import com.wingstars.member.viewmodel.PersonalScheduleViewModel

class PersonalScheduleFragment : Fragment() {

    private lateinit var binding: FragmentPersonalScheduleBinding
    private lateinit var viewModel: PersonalScheduleViewModel

    private var isDataLoaded = false // 标记数据是否加载过
    override fun onResume() {
        super.onResume()

        if (!isDataLoaded) {
            loadData()
            isDataLoaded = true
        }
    }

    private fun loadData() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPersonalScheduleBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    private fun initView() {

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}