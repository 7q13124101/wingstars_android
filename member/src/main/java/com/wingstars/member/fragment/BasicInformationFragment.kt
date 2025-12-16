package com.wingstars.member.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingstars.base.net.beans.WSMemberResponse
import com.wingstars.base.utils.DPUtils
import com.wingstars.base.utils.ScreenUtils
import com.wingstars.member.adapter.BasicIntroductionAdapter
import com.wingstars.member.adapter.HobbyAdapter
import com.wingstars.member.databinding.FragmentBasicInformationBinding
import com.wingstars.member.viewmodel.BasicInformationViewModel
import java.math.BigDecimal
import java.math.RoundingMode


class BasicInformationFragment : Fragment() {

    private lateinit var binding: FragmentBasicInformationBinding
    private lateinit var viewModel: BasicInformationViewModel

    private lateinit var basicIntroductionAdapter: BasicIntroductionAdapter

    private lateinit var hobbyAdapter: HobbyAdapter
    private var orgHobbyLists = mutableListOf<String>()


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

        val data = arguments?.getSerializable("wsMemberAcf")
        if (data != null) {
            val acf = data as WSMemberResponse.Acf
            binding.tvAboutMeContent.text = acf.about
            binding.tvMottoContent.text = acf.say

            viewModel.getHobbyLists(acf.interest)
            viewModel.getBasicIntroductionList(acf)
        }
        //create base introduction adapter.
        basicIntroductionAdapter = BasicIntroductionAdapter(mutableListOf())
        binding.rvIntroduction.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rvIntroduction.adapter = basicIntroductionAdapter

        //set introduction adapter data.
        viewModel.introductionList.observe(viewLifecycleOwner) {
            basicIntroductionAdapter.setList(it)
        }


        //Create hobby adapter
        hobbyAdapter = HobbyAdapter(mutableListOf())
        // 获取之前设置的 layoutManager
        //val layoutManager = binding.rvHobby.layoutManager as GridLayoutManager
        val srcWidth =
            ScreenUtils.getWidth(requireActivity()) - DPUtils.dpToPx(48f, requireActivity()).toInt()
        val layoutManager = GridLayoutManager(requireActivity(), srcWidth)
        // 设置 SpanSizeLookup
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // 返回值表示该位置的项目占据多少个连续的网格
                var nSpanSize = 0
                if (orgHobbyLists.isNotEmpty() && position < orgHobbyLists.size) {
                    nSpanSize = sum(
                        orgHobbyLists[position].length, DPUtils.sp2px(16f, requireActivity()),
                        DPUtils.dpToPx(32f, requireActivity())
                    )
                }
                return nSpanSize
            }
        }
        binding.rvHobby.layoutManager = layoutManager
        binding.rvHobby.adapter = hobbyAdapter

        //set hobby adapter data.
        viewModel.hobbyLists.observe(viewLifecycleOwner) { lists ->
            orgHobbyLists.clear()
            orgHobbyLists.addAll(lists)
            hobbyAdapter.setList(lists)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun sum(length: Int, textSize: Float, padding: Float): Int {
        val scale = BigDecimal(length.toString()).multiply(BigDecimal(textSize.toString()))
            .setScale(0, RoundingMode.UP)
        val scale1 = scale.add(BigDecimal(padding.toString())).setScale(0, RoundingMode.UP)
        return scale1.toInt()
    }
}