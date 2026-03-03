package com.wingstars.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.home.R
import com.youth.banner.Banner
import com.youth.banner.adapter.BannerAdapter
 class TopBannerAdapter(private val images: List<Int>) :
        RecyclerView.Adapter<TopBannerAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val banner: Banner<Int, ImageBannerAdapter> = view.findViewById(R.id.banner_main)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_top_banner, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.banner.apply {
                setAdapter(ImageBannerAdapter(images))
                isAutoLoop(true)
                setLoopTime(3000)
            }
        }

        override fun getItemCount() = 1

        inner class ImageBannerAdapter(dataList: List<Int>) :
            BannerAdapter<Int, ImageBannerAdapter.ImageHolder>(dataList) {

            override fun onCreateHolder(parent: ViewGroup, viewType: Int): ImageHolder {
                val imageView = ImageView(parent.context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
                return ImageHolder(imageView)
            }

            override fun onBindView(holder: ImageHolder, data: Int, position: Int, size: Int) {
                // Gán ảnh trực tiếp từ tài nguyên Drawable
                holder.imageView.setImageResource(data)
            }

            inner class ImageHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)
        }
    }
