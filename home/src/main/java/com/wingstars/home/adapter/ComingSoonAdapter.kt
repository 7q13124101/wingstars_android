package com.wingstars.home.adapter // Hoặc package của bạn


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.wingstars.base.net.beans.WSProductResponse
import com.wingstars.home.R // Đảm bảo import R của module :home
import com.youth.banner.adapter.BannerAdapter

class ComingSoonAdapter(dataList: MutableList<WSProductResponse>) :
    BannerAdapter<WSProductResponse, ComingSoonAdapter.BannerViewHolder>(dataList) {
    interface OnItemListener {
        fun onItemClick(data: WSProductResponse, position: Int)
    }

    private var listener: OnItemListener? = null

    fun setOnItemListener(l: OnItemListener?) {
        listener = l
    }

    fun setList(list: MutableList<WSProductResponse>?) {
        setDatas(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return getRealCount()
    }

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_coming_soon, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindView(holder: BannerViewHolder, data: WSProductResponse, position: Int, size: Int) {
        holder.bind(data,this)
        holder.itemView.setOnClickListener { listener?.onItemClick(data,position) }
    }
    // ViewHolder giữ các view từ item_classroom.xml
    class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val comingSoonImage: ImageView = view.findViewById(R.id.imgComingSoon)
        private val comingSoonTitle: TextView = view.findViewById(R.id.tvComingSoonTitle)
        private val comingSoonTime: TextView = view.findViewById(R.id.tvComingSoonTime)

        fun bind(data: WSProductResponse,adapter: ComingSoonAdapter){
            Glide.with(itemView.context).clear(comingSoonImage)
            val imageUrl = data.imageF
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.img_product_01)
                    .error(R.drawable.img_product_01)
                    .into(comingSoonImage)
            } else {
                comingSoonImage.setImageResource(R.drawable.img_product_01)
            }
            comingSoonTitle.text = data.name
            comingSoonTime.text = data.dateF //"${itemView.context.getString(R.string.pre_order_time)}${data.dateF}"
        }
    }
}