package com.wingstars.user.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wingstars.base.net.NetworkMonitorNew
import com.wingstars.user.databinding.FragmentApplicationFunctionBinding

class ApplicationFunctionFragment : Fragment() {
    private var _binding: FragmentApplicationFunctionBinding? = null
    private val binding get() = _binding!!
    private var isDataLoaded = false
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
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}