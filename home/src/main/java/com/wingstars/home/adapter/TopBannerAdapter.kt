package com.wingstars.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.home.R

class TopBannerAdapter : RecyclerView.Adapter<TopBannerAdapter.ViewHolder>() {
    // Bạn có thể truyền List ảnh vào đây nếu Banner này động

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate cái ImageView Banner hoặc layout chứa Banner
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_top_banner, parent, false)
        // Lưu ý: Tạo file item_home_top_banner.xml chứa cái ImageView id/img_banner
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Load ảnh vào Banner ở đây (dùng Glide)
    }

    override fun getItemCount() = 1
}