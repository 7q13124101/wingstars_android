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
import com.wingstars.user.databinding.FragmentDownloadAndInstallBinding
import kotlin.getValue

class DownloadAndInstallFragment : Fragment() {
    private var _binding: FragmentDownloadAndInstallBinding? = null
    private val binding get() = _binding!!

    private lateinit var frequentlyAskedQuestionsListAdapter: FrequentlyAskedQuestionsListAdapter

    private var isDataLoaded = false // 标记数据是否加载过
    override fun onResume() {
        super.onResume()
        if (!isDataLoaded) {
            loadData()
            isDataLoaded = true
        }
    }

    private fun loadData() {
        if (NetworkMonitorNew.getInstance(requireActivity()).currentNetworkState.isConnected) {
            binding.nsvDownloadAndInstall.visibility = View.VISIBLE
            binding.llEmpty.visibility = View.GONE
            viewModel.getFrequentlyAskedQuestionsData()
        } else {
            binding.nsvDownloadAndInstall.visibility = View.GONE
            binding.llEmpty.visibility = View.VISIBLE
        }
    }

    companion object {
        fun newInstance() = DownloadAndInstallFragment()
    }

    private val viewModel: FrequentlyAskedQuestionsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDownloadAndInstallBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initView()

        return root
    }

    private fun initView() {
        frequentlyAskedQuestionsListAdapter = FrequentlyAskedQuestionsListAdapter(requireActivity())
        binding.rvDownloadAndInstallList.setAdapter(frequentlyAskedQuestionsListAdapter)
        binding.rvDownloadAndInstallList.setOnGroupClickListener(ExpandableListView.OnGroupClickListener { parent, v, groupPosition, id ->
            if (binding.rvDownloadAndInstallList.isGroupExpanded(groupPosition)) {
                // 如果该组已展开，则折叠它

            } else {
                // 如果该组未展开，则展开它
                binding.rvDownloadAndInstallList.expandGroup(groupPosition, true)
            }
            true // 返回 true 表示消费该事件，防止自动处理展开
        })

        viewModel.questionDownloadData.observe(viewLifecycleOwner) {
            frequentlyAskedQuestionsListAdapter.setGroupList(it)
            frequentlyAskedQuestionsListAdapter.notifyDataSetChanged()
            for (i in 0 until it.size) {
                binding.rvDownloadAndInstallList.expandGroup(i)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            BaseApplication.shared()!!.closeLoadingDialog()
            if (it) {
                BaseApplication.shared()!!.showLoadingUI(it, requireActivity())
            }
        }
        binding.srlRefreshLayout.setOnRefreshListener {
            if (NetworkMonitorNew.getInstance(requireActivity()).currentNetworkState.isConnected) {
                binding.nsvDownloadAndInstall.visibility = View.VISIBLE
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