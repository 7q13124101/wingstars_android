package com.wingstars.count.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.wingstars.base.net.beans.CRMCouponsAvailableResponse
import com.wingstars.count.R
import com.wingstars.count.databinding.ItemGoodsNewDetailBinding

class CountNewDetailAdapter(
    private val context: Context,
    private var dataList: MutableList<CRMCouponsAvailableResponse> = mutableListOf(),
    private val onItemClick: (CRMCouponsAvailableResponse) -> Unit
) : RecyclerView.Adapter<CountNewDetailAdapter.CountNewDetailViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountNewDetailViewHolder {
        val binding =
            ItemGoodsNewDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CountNewDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CountNewDetailViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        Log.d("Adapter", "ItemCount = ${dataList.size}")
        return dataList.size
    }

    // -------------------------------------------
    fun setList(list: List<CRMCouponsAvailableResponse>?) {
        dataList = if (dataList == null) {
            ArrayList()
        } else {
            dataList == null
            ArrayList()
        }
        dataList!!.addAll(list!!)
        notifyDataSetChanged()
    }

    fun getData(): MutableList<CRMCouponsAvailableResponse> {
        return dataList
    }

    // -------------------------------------------
    inner class CountNewDetailViewHolder(private val binding: ItemGoodsNewDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CRMCouponsAvailableResponse) {


            binding.tvGoodsName.text = item.couponName ?: ""
            binding.tvCountPrice.text = "${item.pointCost}"


            binding.ivGoodsImage.setImageDrawable(null)

            if (!item.coverImage.isNullOrEmpty()) {
                Glide.with(binding.ivGoodsImage)
                    .load(item.coverImage)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .placeholder(R.drawable.gift_details_image_background)
                    .error(R.drawable.gift_details_image_background)
                    .into(binding.ivGoodsImage)
            } else {
                binding.ivGoodsImage.setImageResource(R.drawable.gift_details_image_background)
            }

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}