package com.wingstars.user.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.wingstars.base.net.NetworkMonitorNew
import com.wingstars.base.net.beans.FrequentlyQuestionsResponse
import com.wingstars.user.adapter.FaqExpandableAdapter
import com.wingstars.user.databinding.FragmentRegisterLoginBinding
import java.io.InputStreamReader

class RegisterAndLoginFragment {
    class RegisterAndLoginFragment : Fragment() {
        private var _binding: FragmentRegisterLoginBinding? = null
        private val binding get() = _binding!!

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = FragmentRegisterLoginBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            loadData()
            
            binding.srlRefreshLayout.setOnRefreshListener {
                loadData()
                binding.srlRefreshLayout.finishRefresh()
            }
        }

        private fun loadData() {
            if (!isAdded) return
            
            if (NetworkMonitorNew.getInstance(requireActivity()).currentNetworkState.isConnected) {
                binding.nsvRegisterAndLogin.visibility = View.VISIBLE
                binding.llEmpty.visibility = View.GONE

                val faqData = readFaqJson()
                faqData?.let { response ->
                    val filteredData = response.data.find { it.partName == "註冊登入" }
                    filteredData?.let {
                        val adapter = FaqExpandableAdapter(requireContext(), it.outData)
                        binding.rvRegisterAndLoginList.setAdapter(adapter)
                        
                        for (i in 0 until adapter.groupCount) {
                            binding.rvRegisterAndLoginList.expandGroup(i)
                        }
                    }
                }
            } else {
                binding.nsvRegisterAndLogin.visibility = View.GONE
                binding.llEmpty.visibility = View.VISIBLE
            }
        }

        private fun readFaqJson(): FrequentlyQuestionsResponse? {
            return try {
                val assetManager = requireContext().assets
                val inputStream = assetManager.open("frequently_questions.json")
                val reader = InputStreamReader(inputStream)
                Gson().fromJson(reader, FrequentlyQuestionsResponse::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }
}