package com.wingstars.member.activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.net.beans.WSFashionDetailResponse.Acf.Recommend
import com.wingstars.base.net.beans.WSPhotoFrameResponse
import com.wingstars.base.utils.DPUtils
import com.wingstars.base.utils.ScreenUtils
import com.wingstars.member.R
import com.wingstars.member.adapter.ActivityImagesAdapter
import com.wingstars.member.adapter.GuideAdapter
import com.wingstars.member.adapter.ProductListAdapter
import com.wingstars.member.adapter.SmallCommodityAdapter
import com.wingstars.member.adapter.SupportSuitAdapter
import com.wingstars.member.databinding.ActivityAtmosphereFashionDetailsBinding
import com.wingstars.member.viewmodel.AtmosphereFashionDetailsViewModel
import com.wingstars.member.viewmodel.SupportSuitViewModel
import com.youth.banner.listener.OnPageChangeListener

class AtmosphereFashionDetailsActivity : BaseActivity(), SupportSuitAdapter.OnItemListener {
    private lateinit var binding: ActivityAtmosphereFashionDetailsBinding
    private var adapter:GuideAdapter?=null
    private var recommend: MutableList<Recommend>?=null
    private var smallCommodityAdapter:SmallCommodityAdapter?=null
    private lateinit var viewModel: AtmosphereFashionDetailsViewModel
    private lateinit var viewModelSuit: SupportSuitViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAtmosphereFashionDetailsBinding.inflate(layoutInflater)
        setTitleFoot(view1 = binding.root,navigationBarColor= R.color.color_F3F4F6)
        initView()

    }

    override fun initView() {
        viewModel = ViewModelProvider(this)[AtmosphereFashionDetailsViewModel::class.java]
        setImageBannerView(binding.frameLayout)
        viewModel.loading.observe(this) {
            showLoadingUI(it, this)
        }
        viewModel.wsFashion.observe(this){
            val acf = it.acf
            binding.title.text = "${it.titleF}"
            binding.content.text = Html.fromHtml("${it.contentF}")
            if (acf!=null){
                val gallery = acf.gallery
                 recommend = mutableListOf<Recommend>()
                 for (j in 1..5){
                     val recommend1 = acf.recommend(j)
                     //Log.e("recommend1","$recommend1")
                     if (recommend1!=null){
                         recommend!!.add(recommend1)
                     }
                 }
                if (recommend!!.size>=1){
                    if (smallCommodityAdapter==null){
                        smallCommodityAdapter = SmallCommodityAdapter(
                            this@AtmosphereFashionDetailsActivity,
                            recommend!!.toMutableList()
                        )
                        binding.smallCommodityList.adapter  = smallCommodityAdapter
                    }else{
                        smallCommodityAdapter!!.setList(recommend!!)
                    }
                }


                if (gallery!=null){
                    var imagesList = mutableListOf<String>()

                    for (i in 1..5) {
                        try {
                            val image = gallery.image(i)
                            if (image==null||image is Boolean){
                                imagesList.add("")
                            } else {
                                val fromJson = Gson().fromJson(
                                    Gson().toJson(image),
                                    WSPhotoFrameResponse.ImageBean::class.java
                                )
                                imagesList.add("${fromJson.sizes.`1536x1536`}")
                            }

                        }catch (e: Exception){

                        }

                    }
                    //Log.e("recommend","${Gson().toJson(recommend)}")
                    binding.productList.adapter  = ProductListAdapter(this, recommend,object : ProductListAdapter.OnItemListener{
                        override fun onItemClick(data: Recommend, position: Int) {
                                val intent = Intent(this@AtmosphereFashionDetailsActivity, WebActivity::class.java)
                                intent.putExtra("title",data.product_titleF)
                                intent.putExtra("webUrl",data.product_urlF)
                                startActivity(intent)
                            }
                        })
                    binding.banner.addBannerLifecycleObserver(this)
                        .setAdapter(ActivityImagesAdapter(imagesList, this@AtmosphereFashionDetailsActivity))

                    binding.guideList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
                    adapter = GuideAdapter(this,imagesList)
                    binding.guideList.adapter = adapter
                }
            }


        }
        val memberId = intent.getIntExtra("memberId", 0)
        if (memberId!=0){
            viewModel.wsFashionCategorys(memberId)
        }
        binding.back.setOnClickListener { finish() }


        binding.banner.addOnPageChangeListener(object:OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                adapter!!.setPos(position)
                if (position<=recommend!!.size-1){
                    /*if (smallCommodityAdapter==null){
                        smallCommodityAdapter = SmallCommodityAdapter(
                            this@AtmosphereFashionDetailsActivity,
                            mutableListOf(recommend!![position])
                        )
                        binding.smallCommodityList.adapter  = smallCommodityAdapter
                    }else{
                        smallCommodityAdapter!!.setList(mutableListOf(recommend!![position]))
                    }*/

                }

            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })



        //init 相關服飾 Adapter
        viewModelSuit =ViewModelProvider(this)[SupportSuitViewModel::class.java]
        viewModelSuit.PER_PAGE=4 //最多取4个2x2显示，不用左右滑动
        viewModelSuit.loading.observe(this) {
            showLoadingUI(it, this)
        }
        val fashionType = intent.getIntExtra("fashionType", 1)  //1 應援服  2 活動服
        viewModelSuit.wsFashionCategorys(fashionType,true)

        viewModelSuit.wsFashions.observe(this) {
            it.forEach { data ->
                val fashionCategoryF = data.fashion_categoryF
                val wsRankDataList = viewModelSuit.wsFashionCategorysData.value
                val typeData = wsRankDataList!!.find { it.id == fashionCategoryF }
                if (typeData != null) {
                    data.type = when (typeData.name.trim()) {
                        "應援服" -> 1
                        "活動服" -> 2
                        else -> 0
                    }

                }
            }

            val width = ScreenUtils.getWidth(this)
            val smallWidth = width - DPUtils.dpToPx(50f, this).toInt()
            val smallWidths = smallWidth / 2
            val smallHeight = smallWidths.toInt() * 1.585
            binding.rvRelatedClothing.adapter = SupportSuitAdapter(
                this, it, smallWidths.toInt(), smallHeight.toInt(), this
            )
        }
    }


    private fun setImageBannerView(v: View?) {
        val width = ScreenUtils.getWidth(this)
        val params = v?.layoutParams
        var hight = width*1.343
        params?.width = width
        params?.height = hight.toInt()
        v?.layoutParams = params
    }

    override fun onItemClick(memberId: Int,fashionType: Int) {

    }


}