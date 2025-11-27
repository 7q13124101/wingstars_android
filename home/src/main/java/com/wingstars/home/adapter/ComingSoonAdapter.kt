package com.wingstars.home.adapter // Hoặc package của bạn

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.home.R // Đảm bảo import R của module :home
import com.wingstars.home.adapter.ItineraryBannerAdapter.BannerViewHolder
import com.youth.banner.adapter.BannerAdapter

data class ComingSoonData(
    val imageRes: Int,
    val title: String,
    val date: String,
)
class ComingSoonAdapter(datas: List<ComingSoonData>) :
    BannerAdapter<ComingSoonData, ComingSoonAdapter.BannerViewHolder>(datas) {
    var onItemClickListener: ((ComingSoonData) -> Unit)? = null

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_coming_soon, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindView(holder: BannerViewHolder, data: ComingSoonData, position: Int, size: Int) {
        holder.bind(data)

        // --- QUAN TRỌNG: Xử lý click ---
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(data)
        }
    }
    // ViewHolder giữ các view từ item_classroom.xml
    class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val comingSoonImage: ImageView = view.findViewById(R.id.imgComingSoon)
        private val comingSoonTitle: TextView = view.findViewById(R.id.tvComingSoonTitle)
        private val comingSoonTime: TextView = view.findViewById(R.id.tvComingSoonTime)

        fun bind(datas: ComingSoonData){
            comingSoonImage.setImageResource(datas.imageRes)
            comingSoonTitle.text = datas.title
            comingSoonTime.text = datas.date
        }
    }
}