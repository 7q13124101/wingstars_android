package com.wingstars.member.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.utils.ScreenUtils
import com.wingstars.member.R
import com.wingstars.member.adapter.ActivityImagesAdapter
import com.wingstars.member.adapter.GuideAdapter
import com.wingstars.member.adapter.SmallCommodityAdapter
import com.wingstars.member.databinding.ActivityAtmosphereFashionDetailsBinding
import com.youth.banner.listener.OnPageChangeListener

class AtmosphereFashionDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityAtmosphereFashionDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAtmosphereFashionDetailsBinding.inflate(layoutInflater)
       //  setContentView(binding.root)
        setTitleFoot(view1 = binding.root,navigationBarColor= R.color.color_F3F4F6)
        initView()

    }

    override fun initView() {
        binding.back.setOnClickListener { finish() }
        setImageBannerView(binding.frameLayout)
        var images = mutableListOf(R.mipmap.ic_demo2,R.mipmap.ic_demo2,R.mipmap.ic_demo2,R.mipmap.ic_demo2,R.mipmap.ic_demo2)
        binding.banner.addBannerLifecycleObserver(this)
            .setAdapter(ActivityImagesAdapter(images, this@AtmosphereFashionDetailsActivity))

        binding.guideList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        var adapter = GuideAdapter(this,images)
        binding.guideList.adapter = adapter
        binding.banner.addOnPageChangeListener(object:OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                adapter.setPos(position)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
        var image = mutableListOf(R.mipmap.ic_demo2,R.mipmap.ic_demo2)
        binding.smallCommodityList.adapter  = SmallCommodityAdapter(this,image)
    }


    private fun setImageBannerView(v: View?) {
        val width = ScreenUtils.getWidth(this)
        val params = v?.layoutParams
        var hight = width*1.343
        params?.width = width
        params?.height = hight.toInt()
        v?.layoutParams = params
    }


}