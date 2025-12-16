package com.wingstars.home.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.wingstars.base.net.beans.WSFashionResponse
import com.wingstars.home.R
import com.wingstars.home.databinding.ItemStyleBinding

class StylistOutfitsAdapter(
    private val context: Context,
    private var dataList: MutableList<WSFashionResponse>,
    private val listener: OnItemListener
) : RecyclerView.Adapter<StylistOutfitsAdapter.NormalItemViewHolder>() {

   interface OnItemListener{
       fun onItemClick(data: WSFashionResponse, position: Int)
   }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StylistOutfitsAdapter.NormalItemViewHolder {
        val binding = ItemStyleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NormalItemViewHolder(binding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun onBindViewHolder(holder: NormalItemViewHolder, position: Int) {
        holder.binding(position)
    }

    override fun getItemCount(): Int {
        return dataList?.size?:0
    }
    fun setList(list: MutableList<WSFashionResponse>?) {
        dataList = list ?: ArrayList()
        notifyDataSetChanged()
    }
    inner class NormalItemViewHolder(private val binding: ItemStyleBinding):
            RecyclerView.ViewHolder(binding.root) {
        fun binding(position: Int) {
            if (dataList == null || position >= dataList!!.size) return
            val data = dataList!![position]
            Glide.with(binding.imageStylist.context).clear(binding.imageStylist)
            val imgUrl = data.urlF
            Log.e("imgUrl", "imgUrl=$imgUrl")
            if (!imgUrl.isNullOrEmpty()) {
                Glide.with(binding.imageStylist.context)
                    .load(imgUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.img_style_01)
                    .error(R.drawable.img_style_01)
                    .into(binding.imageStylist)
            } else {
                binding.imageStylist.setImageResource(R.drawable.img_style_01)
            }
            binding.tittleStylist.text = data.titleF
            binding.root.setOnClickListener {
                listener.onItemClick(data, position)
            }
        }
            }
}
