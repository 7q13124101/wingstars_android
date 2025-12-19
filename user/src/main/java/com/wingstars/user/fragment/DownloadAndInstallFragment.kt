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
import com.wingstars.user.databinding.FragmentDownloadAndInstallBinding

class DownloadAndInstallFragment : Fragment() {
    private var _binding: FragmentDownloadAndInstallBinding? = null
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
            binding.nsvDownloadAndInstall.visibility = View.VISIBLE
            binding.llEmpty.visibility = View.GONE
        } else {
            binding.nsvDownloadAndInstall.visibility = View.GONE
            binding.llEmpty.visibility = View.VISIBLE
        }
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

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}