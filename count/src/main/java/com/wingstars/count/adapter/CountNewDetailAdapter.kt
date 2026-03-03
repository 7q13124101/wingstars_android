package com.wingstars.count.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View // Đừng quên import View
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
        dataList.clear()
        if (!list.isNullOrEmpty()) {
            dataList.addAll(list)
        }
        notifyDataSetChanged()
    }

    fun getData(): MutableList<CRMCouponsAvailableResponse> {
        return dataList
    }

    // -------------------------------------------
    inner class CountNewDetailViewHolder(private val binding: ItemGoodsNewDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CRMCouponsAvailableResponse) {

            val totalIssued = item.totalIssued
            val totalQuantity = item.totalQuantity
            if (totalQuantity != -1 && totalIssued >= totalQuantity) {
                binding.ivMaskImage.visibility = View.VISIBLE
                binding.tvExchangeCompleted.visibility = View.VISIBLE
            } else {
                binding.ivMaskImage.visibility = View.GONE
                binding.tvExchangeCompleted.visibility = View.GONE
            }

            // 1. Bind thông tin cơ bản
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

//            val eligibleMembersStr = item.eligibleMembersStr
//            if (!eligibleMembersStr.isNullOrEmpty() && eligibleMembersStr != context.getString(R.string.all_members)) {
//                binding.label.visibility = View.VISIBLE
//                binding.labelTv.text = eligibleMembersStr
//            } else {
//                binding.label.visibility = View.GONE
//            }
            // --------------------------------

            // 4. Click Listener
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}