package com.wingstars.member.activity

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.net.beans.WSFashionDetailResponse.Acf.Recommend
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
import com.wingstars.member.viewmodel.MemberViewModel
import com.youth.banner.listener.OnPageChangeListener
import kotlin.math.PI

class AtmosphereFashionDetailsActivity : BaseActivity(), SupportSuitAdapter.OnItemListener {
    private lateinit var binding: ActivityAtmosphereFashionDetailsBinding
    private var adapter:GuideAdapter?=null
    private var recommend: MutableList<Recommend>?=null
    private var smallCommodityAdapter:SmallCommodityAdapter?=null
    private lateinit var viewModel: AtmosphereFashionDetailsViewModel
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
                     Log.e("recommend1","$recommend1")
                     if (recommend1!=null){
                         recommend!!.add(recommend1)
                     }
                 }


                if (gallery!=null){
                    var imagesList = mutableListOf<String>()

                    for (i in 1..5) {
                        val image = gallery.image(i)
                        imagesList.add(image!!)
                    }
                    Log.e("recommend","${Gson().toJson(recommend)}")
                    binding.productList.adapter  = ProductListAdapter(this, recommend)
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
                    if (smallCommodityAdapter==null){
                        smallCommodityAdapter = SmallCommodityAdapter(
                            this@AtmosphereFashionDetailsActivity,
                            mutableListOf(recommend!![position])
                        )
                        binding.smallCommodityList.adapter  = smallCommodityAdapter
                    }else{
                        smallCommodityAdapter!!.setList(mutableListOf(recommend!![position]))
                    }

                }

            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
      //  var image = mutableListOf(R.mipmap.ic_demo2,R.mipmap.ic_demo2)

       // var image1 = mutableListOf(R.mipmap.ic_demo2,R.mipmap.ic_demo2,R.mipmap.ic_demo2)



        val list = mutableListOf("1","2")
        var width = ScreenUtils.getWidth(this)
        var smallwidth  = width - DPUtils.dpToPx(50f,this).toInt()
        var smallwidths = smallwidth/2
        var smallhight = smallwidths.toInt()*1.585
     /*   binding.list.adapter = SupportSuitAdapter(this, list,smallwidths.toInt()
            ,smallhight.toInt(),this)*/
    }


    private fun setImageBannerView(v: View?) {
        val width = ScreenUtils.getWidth(this)
        val params = v?.layoutParams
        var hight = width*1.343
        params?.width = width
        params?.height = hight.toInt()
        v?.layoutParams = params
    }

    override fun onItemClick(position: Int) {

    }


}