package com.wingstars.member.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.RelativeLayout
import androidx.core.app.ActivityCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.wingstars.base.base.BaseFragment
import com.wingstars.base.utils.DPUtils
import com.wingstars.base.utils.ScreenUtils
import com.wingstars.member.activity.AtmosphereFashionDetailsActivity
import com.wingstars.member.activity.EventHighlightsActivity
import com.wingstars.member.activity.ExclusiveSongsListActivity
import com.wingstars.member.activity.FanInteractionActivity
import com.wingstars.member.activity.FashionableAtmosphereActivity
import com.wingstars.member.activity.MemberIntroductionActivity
import com.wingstars.member.activity.PopularityRankingActivity
import com.wingstars.member.adapter.GirlIntroductionAdapter
import com.wingstars.member.adapter.PopularityAdapter
import com.wingstars.member.adapter.SupportFashionAdapter
import com.wingstars.member.databinding.FragmentMemberBinding
import com.wingstars.member.viewmodel.MemberViewModel
import com.wingstars.base.inter.IPermissionsCallback
import com.wingstars.base.net.NetBase
import com.wingstars.base.net.beans.WSMemberResponse
import com.wingstars.member.R
import com.wingstars.member.activity.MemberDetailsActivity


class MemberFragment : BaseFragment(), View.OnClickListener,
    PopularityAdapter.onPopularityRankingListener, SupportFashionAdapter.onSupportFashionListener {
    private lateinit var viewModel: MemberViewModel
    private lateinit var binding: FragmentMemberBinding
    private var maxHight = 0
    private var currentHeight = 0
    private var minHight = 0
    private lateinit var permissionsCallback: IPermissionsCallback

    private lateinit var girlIntroductionAdapter: GirlIntroductionAdapter

    private var isDataLoaded = false // 标记数据是否加载过
    override fun onResume() {
        super.onResume()
        if (!isDataLoaded) {
            loadData()
            isDataLoaded = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMemberBinding.inflate(inflater, container, false)
        val root = binding.root
        initView()
        return root
    }

    private fun loadData() {
        //成员 > 成员介绍
        viewModel.getWsMembersData()
        //人气排行-名次
        viewModel.getRenderedList()
        val value = viewModel.wsFashionCategorysData.value
        if (value==null){
            //成员 > 氛围时尚-分类
            viewModel.wsFashionCategorys()
        }else{
            // 氛围时尚
            viewModel.wsFashions()
        }

    }

    private fun initView() {
        /*binding.image.post {
            maxHight = binding.image.height
            currentHeight = maxHight
        }*/
        binding.refresh.setColorSchemeResources(R.color.color_E2518D)
        binding.refresh.setOnRefreshListener {
            binding.refresh.isRefreshing = false
            loadData()
        }

        val account = NetBase.decrypt(NetBase.WINGSTARS_ACCOUNT_ENC)
        val password = NetBase.decrypt(NetBase.WINGSTARS_PASSWORD_ENC)

        val authorization =
            NetBase.base64Encode("$account:$password".toByteArray(Charsets.UTF_8))
        //Log.e("authorization", "authorization=$authorization")

        val params = binding.image!!.layoutParams
        var smallwidth = ScreenUtils.getWidth(requireActivity())
        maxHight = (smallwidth * 0.965).toInt()
        currentHeight = maxHight
        params.width = smallwidth
        params.height = maxHight
        binding.image!!.setLayoutParams(params)
        binding.scroll.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener {
            override fun onScrollChange(
                v: NestedScrollView,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {
                val isScrollingDown = scrollY > oldScrollY
                val isScrollingUp = scrollY < oldScrollY
                // Log.e("scrollY","scrollY=$scrollY oldScrollY=$oldScrollY")
                if (isScrollingDown) {
                    val i = Math.abs(scrollY - oldScrollY)
                    if (currentHeight > minHight) {
                        currentHeight =
                            if (currentHeight - i > minHight) currentHeight - i else minHight
                        val params = binding.image!!.layoutParams
                        params.width = ViewGroup.LayoutParams.MATCH_PARENT
                        params.height = currentHeight
                        binding.image!!.setLayoutParams(params)
                    }
                }
                if (isScrollingUp) {
                    if (scrollY <= maxHight - minHight) {
                        val i = Math.abs(scrollY - oldScrollY)
                        if (currentHeight < maxHight) {
                            currentHeight =
                                if (currentHeight + i > maxHight) maxHight else currentHeight + i
                            val params = binding.image!!.layoutParams
                            params.width = ViewGroup.LayoutParams.MATCH_PARENT
                            params.height = currentHeight
                            binding.image!!.setLayoutParams(params)
                        }
                    }

                }
            }

        })
        viewModel = ViewModelProvider(this)[MemberViewModel::class.java]
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            binding.root.setOnApplyWindowInsetsListener { v, insets ->
                val statusBarHeight = insets.getInsets(WindowInsets.Type.statusBars()).top
                minHight = statusBarHeight + DPUtils.dpToPx(64f, requireActivity()).toInt()
                //Log.e("statusBarHeight", "statusBarHeight=$statusBarHeight")
                setViewTop(binding.title, statusBarHeight)
                binding.root.setOnApplyWindowInsetsListener(null)
                insets
            }
        } else {
            minHight = getStatusBarHeight() + DPUtils.dpToPx(64f, requireActivity()).toInt()
            setViewTop(binding.title, getStatusBarHeight())
        }

        /*     viewModel.popularitylist.observe(viewLifecycleOwner) {


                 var adapter1 = SupportFashionAdapter(requireActivity(), it, this)
                 binding.supportFashionList.layoutManager = LinearLayoutManager(
                     requireActivity(),
                     LinearLayoutManager.HORIZONTAL, false
                 )
                 binding.supportFashionList.adapter = adapter1
             }*/

        //[成員] 成員介紹 layout
        girlIntroductionAdapter =
            GirlIntroductionAdapter(requireActivity(), mutableListOf(), object :
                GirlIntroductionAdapter.onItemListener {
                override fun onItemClick(data: WSMemberResponse, position: Int) {
                    val intent = Intent(requireActivity(), MemberDetailsActivity::class.java)
                    intent.putExtra("WSMemberResponse", data)
                    startActivity(intent)
                }
            })
        binding.girlsList.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.HORIZONTAL, false
        )
        binding.girlsList.adapter = girlIntroductionAdapter

        viewModel.wsMembersData.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty())
                girlIntroductionAdapter.setList(it)
        }

       // viewModel.getPopularitylist()
        binding.popularityRanking.setOnClickListener(this)
        binding.take.setOnClickListener(this)
        binding.llEventHighlights.setOnClickListener(this)
        binding.llExclusiveSongs.setOnClickListener(this)

        binding.rlMemberIntroduction.setOnClickListener(this)
        binding.atmosphere.setOnClickListener(this)
        viewModel.loading.observe(viewLifecycleOwner) {
            showLoadingUI(it, requireActivity())
        }
        viewModel.wsRankData.observe(viewLifecycleOwner) {
            //Log.e("wsRankData", "${Gson().toJson(it)}")
            var adapter = PopularityAdapter(requireActivity(), it, this)
            binding.chartList.layoutManager = LinearLayoutManager(
                requireActivity(),
                LinearLayoutManager.HORIZONTAL, false
            )
            binding.chartList.adapter = adapter
        }

        viewModel.wsFashions.observe(viewLifecycleOwner) {
            it.forEach { data ->
                val fashionCategoryf = data.fashion_categoryF
                var wsRankDatalist = viewModel.wsFashionCategorysData.value
                val typeData = wsRankDatalist!!.find { it.id == fashionCategoryf }
                if (typeData != null) {
                    data.type = when (typeData.name.trim()) {
                        "應援服" -> 1
                        "活動服" -> 2
                        else -> 0
                    }

                }
            }
            //Log.e("wsFashions", "${Gson().toJson(it)}")
            var adapter1 = SupportFashionAdapter(requireActivity(), it, this)
            binding.supportFashionList.adapter = adapter1
        }

    }


    public fun setViewTop(view: View, top: Int) {
        val layoutParams = view.layoutParams as RelativeLayout.LayoutParams
        layoutParams.topMargin = top
        view.setLayoutParams(layoutParams);
    }


    override fun onClick(v: View?) {
        val id = v?.id
        when (id) {
            binding.popularityRanking.id -> startActivity(
                Intent(
                    requireActivity(),
                    PopularityRankingActivity::class.java
                )
            )

            binding.take.id -> applyPermission()/*startActivity(
                Intent(
                    requireActivity(),
                    FanInteractionActivity::class.java
                )
            )*/

            binding.llEventHighlights.id -> startActivity(
                Intent(
                    requireActivity(),
                    EventHighlightsActivity::class.java
                )
            )

            binding.llExclusiveSongs.id -> startActivity(
                Intent(
                    requireActivity(),
                    ExclusiveSongsListActivity::class.java
                )
            )

            binding.rlMemberIntroduction.id -> startActivity(
                Intent(
                    requireActivity(),
                    MemberIntroductionActivity::class.java
                )
            )

            binding.atmosphere.id -> startActivity(
                Intent(
                    requireActivity(),
                    FashionableAtmosphereActivity::class.java
                )
            )
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is IPermissionsCallback) {
            permissionsCallback = context
        } else {
            throw RuntimeException("$context must implement IActivityCallback")
        }
    }

    private fun applyPermission() {
        permissionsCallback.setPermissions(1)
        var permission = ArrayList<String>();
        permission.add(Manifest.permission.CAMERA)
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(requireActivity(), permission.toTypedArray(), 1000)
        } else {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                jumpTakePhoto()
            } else {
                applyPermission1()
            }

        }
    }

    private fun applyPermission1() {
        var permission = ArrayList<String>();
        permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(requireActivity(), permission.toTypedArray(), 1001)
        } else {
            jumpTakePhoto()
        }
    }

    private fun jumpTakePhoto() {
        startActivity(Intent(requireActivity(), FanInteractionActivity::class.java))
    }

    override fun onPopularityRankingClickItem(type: String) {
        val intent = Intent(
            requireActivity(),
            PopularityRankingActivity::class.java
        )
        intent.putExtra("type",type)
        startActivity(intent)
    }

    override fun onSupportFashionClickItem(memberId: Int,fashionType: Int) {
        val intent = Intent(
            requireActivity(),
            AtmosphereFashionDetailsActivity::class.java
        )
        intent.putExtra("memberId", memberId)
        intent.putExtra("fashionType", fashionType)
        startActivity(intent)
    }
}