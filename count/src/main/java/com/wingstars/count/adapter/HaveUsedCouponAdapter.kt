package com.wingstars.count.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wingstars.count.databinding.ItemHaveUsedCouponsBinding
import com.wingstars.count.viewmodel.ActivityExchangeViewModel

class HaveUsedCouponAdapter (
    private var list: List<ActivityExchangeViewModel> = listOf(),
    private val onItemClick: (ActivityExchangeViewModel) -> Unit
) : RecyclerView.Adapter<HaveUsedCouponAdapter.CouponViewHolder>() {

    fun setData(newList: List<ActivityExchangeViewModel>) {
        this.list = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val binding = ItemHaveUsedCouponsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CouponViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
//        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    inner class CouponViewHolder(private val binding: ItemHaveUsedCouponsBinding) :
        RecyclerView.ViewHolder(binding.root) {

//        fun bind(item: ActivityExchangeViewModel) {
//            binding.tvExchangeName.text = item.title
//            binding.tvExchangePeriod1.text = item.time
//            Glide.with(binding.root.context)
//                .load(item.leftImageRes)
//                .into(binding.ivGoodsImage)
//            binding.root.setOnClickListener {
//                onItemClick(item)
//            }
//        }
    }
}