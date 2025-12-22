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
import com.wingstars.user.databinding.FragmentRegisterLoginBinding

class RegisterAndLoginFragment {
    class RegisterAndLoginFragment : Fragment() {
        private var _binding: FragmentRegisterLoginBinding? = null
        private val binding get() = _binding!!
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
            } else {
                binding.nsvRegisterAndLogin.visibility = View.GONE
                binding.llEmpty.visibility = View.VISIBLE
            }
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
        }
        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }
}