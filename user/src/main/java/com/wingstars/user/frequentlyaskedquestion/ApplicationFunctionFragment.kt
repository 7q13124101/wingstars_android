package com.wingstars.user.frequentlyaskedquestion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.wingstars.user.BaseApplication
import com.wingstars.user.NetworkMonitorNew
import com.wingstars.user.databinding.FragmentApplicationFunctionBinding
import kotlin.getValue

class ApplicationFunctionFragment : Fragment() {
    private var _binding: FragmentApplicationFunctionBinding? = null
    private val binding get() = _binding!!

    private lateinit var frequentlyAskedQuestionsListAdapter: FrequentlyAskedQuestionsListAdapter
    private var isDataLoaded = false

    private val viewModel: FrequentlyAskedQuestionsViewModel by viewModels()

    override fun onResume() {
        super.onResume()
        if (!isDataLoaded) {
            loadData()
            isDataLoaded = true
        }
    }

    private fun loadData() {
        if (NetworkMonitorNew.getInstance(requireActivity()).currentNetworkState.isConnected) {
            binding.nsvPointsTask.visibility = View.VISIBLE
            binding.llEmpty.visibility = View.GONE
            viewModel.getFrequentlyAskedQuestionsData()
        } else {
            binding.nsvPointsTask.visibility = View.GONE
            binding.llEmpty.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApplicationFunctionBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        frequentlyAskedQuestionsListAdapter = FrequentlyAskedQuestionsListAdapter(requireActivity())
        binding.rvPointsTaskList.setAdapter(frequentlyAskedQuestionsListAdapter)
        binding.rvPointsTaskList.setOnGroupClickListener { _, _, groupPosition, _ ->
            if (binding.rvPointsTaskList.isGroupExpanded(groupPosition)) {
                // gập nhóm
            } else {
                binding.rvPointsTaskList.expandGroup(groupPosition, true)
            }
            true
        }

        viewModel.questionTaskData.observe(viewLifecycleOwner) {
            frequentlyAskedQuestionsListAdapter.setGroupList(it)
            frequentlyAskedQuestionsListAdapter.notifyDataSetChanged()
            for (i in it.indices) {
                binding.rvPointsTaskList.expandGroup(i)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            BaseApplication.shared()?.closeLoadingDialog()
            if (it) BaseApplication.shared()?.showLoadingUI(it, requireActivity())
        }

        binding.srlRefreshLayout.setOnRefreshListener {
            if (NetworkMonitorNew.getInstance(requireActivity()).currentNetworkState.isConnected) {
                binding.nsvPointsTask.visibility = View.VISIBLE
                binding.llEmpty.visibility = View.GONE
                viewModel.getFrequentlyAskedQuestionsData()
            }
            binding.srlRefreshLayout.finishRefresh()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        BaseApplication.shared()?.closeLoadingDialog()
    }
}
