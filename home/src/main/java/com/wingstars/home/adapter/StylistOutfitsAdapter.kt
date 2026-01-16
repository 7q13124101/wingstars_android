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
    private val listener: onSupportFashionListener
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
        holder.binding(position, listener)
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
        fun binding(position: Int, listener: onSupportFashionListener ) {
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
            var imageType =  if (data.type==1){ com.wingstars.member.R.mipmap.ic_member_jersey} else {
                com.wingstars.member.R.mipmap.ic_member_activity}
            binding.imageType.setImageResource(imageType)
            binding.tittleStylist.text = data.titleF
            binding.item.setOnClickListener { listener.onSupportFashionClickItem(data.id) }
        }
            }
    interface onSupportFashionListener{
        fun onSupportFashionClickItem(memberId: Int)
    }
}
