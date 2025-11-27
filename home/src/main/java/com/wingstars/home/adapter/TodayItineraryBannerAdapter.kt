package com.wingstars.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.home.R
import com.youth.banner.adapter.BannerAdapter

// Data class nên để riêng file hoặc top-level nếu nhỏ
data class ItineraryData(
    val imageRes: Int,
    val title: String,
    val date: String,
    val location: String
)

class ItineraryBannerAdapter(datas: List<ItineraryData>) :
    BannerAdapter<ItineraryData, ItineraryBannerAdapter.BannerViewHolder>(datas) {

    // Interface để xử lý click item
    var onItemClickListener: ((ItineraryData) -> Unit)? = null

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_today_itinerary, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindView(holder: BannerViewHolder, data: ItineraryData, position: Int, size: Int) {
        holder.bind(data)

        // --- QUAN TRỌNG: Xử lý click ---
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(data)
        }
    }

    class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Đảm bảo ID khớp với item_today_itinerary.xml
        private val imgPoster: ImageView = view.findViewById(R.id.imgPoster)
        private val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        private val tvTime: TextView = view.findViewById(R.id.tvTime)
        private val tvPlace: TextView = view.findViewById(R.id.tvPlace)

        fun bind(data: ItineraryData) {
            imgPoster.setImageResource(data.imageRes)
            tvTitle.text = data.title
            tvTime.text = data.date
            tvPlace.text = data.location
        }
    }
}