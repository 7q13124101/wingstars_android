package com.wingstars.user.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.wingstars.user.net.BaseApplication
import com.wingstars.user.net.NetworkMonitorNew
import com.wingstars.user.adapter.FrequentlyAskedQuestionsListAdapter
import com.wingstars.user.databinding.FragmentRegisterLoginBinding
import com.wingstars.user.viewmodel.FrequentlyAskedQuestionsViewModel

class RegisterAndLoginFragment {
    class RegisterAndLoginFragment : Fragment() {
        private var _binding: FragmentRegisterLoginBinding? = null
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
                binding.nsvRegisterAndLogin.visibility = View.VISIBLE
                binding.llEmpty.visibility = View.GONE
                viewModel.getFrequentlyAskedQuestionsData()
            } else {
                binding.nsvRegisterAndLogin.visibility = View.GONE
                binding.llEmpty.visibility = View.VISIBLE
            }
        }

        companion object {
            fun newInstance() = RegisterAndLoginFragment()
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
            _binding = FragmentRegisterLoginBinding.inflate(inflater, container, false)
            val root: View = binding.root

            initView()

            return root
        }

        private fun initView() {
            frequentlyAskedQuestionsListAdapter =
                FrequentlyAskedQuestionsListAdapter(requireActivity())
            binding.rvRegisterAndLoginList.setAdapter(frequentlyAskedQuestionsListAdapter)
            binding.rvRegisterAndLoginList.setOnGroupClickListener(ExpandableListView.OnGroupClickListener { parent, v, groupPosition, id ->
                if (binding.rvRegisterAndLoginList.isGroupExpanded(groupPosition)) {
                    // 如果该组已展开，则折叠它

                } else {
                    // 如果该组未展开，则展开它
                    binding.rvRegisterAndLoginList.expandGroup(groupPosition, true)
                }
                true // 返回 true 表示消费该事件，防止自动处理展开
            })

            viewModel.questionRegisterData.observe(viewLifecycleOwner) {
                frequentlyAskedQuestionsListAdapter.setGroupList(it)
                frequentlyAskedQuestionsListAdapter.notifyDataSetChanged()
                for (i in 0 until it.size) {
                    binding.rvRegisterAndLoginList.expandGroup(i)
                }
            }

            viewModel.isLoading.observe(viewLifecycleOwner) {
                BaseApplication.Companion.shared()!!.closeLoadingDialog()
                if (it) {
                    BaseApplication.Companion.shared()!!.showLoadingUI(it, requireActivity())
                }
            }
            binding.srlRefreshLayout.setOnRefreshListener {
                if (NetworkMonitorNew.getInstance(requireActivity()).currentNetworkState.isConnected) {
                    binding.nsvRegisterAndLogin.visibility = View.VISIBLE
                    binding.llEmpty.visibility = View.GONE
                    viewModel.getFrequentlyAskedQuestionsData()
                }
                binding.srlRefreshLayout.finishRefresh()
            }
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
            BaseApplication.Companion.shared()?.closeLoadingDialog()
        }

    }
}