package com.wingstars.count.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.wingstars.count.databinding.ItemHaveUsedCouponsBinding
import com.wingstars.base.net.beans.CRMCouponsResponse

class HaveUsedCouponAdapter(
    private var list: MutableList<CRMCouponsResponse> = mutableListOf(),
    private val onItemClick: (CRMCouponsResponse) -> Unit
) : RecyclerView.Adapter<HaveUsedCouponAdapter.CouponViewHolder>() {

    fun setData(newList: List<CRMCouponsResponse>?) {
        this.list.clear()
        newList?.let { this.list.addAll(it) }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val binding = ItemHaveUsedCouponsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CouponViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    inner class CouponViewHolder(private val binding: ItemHaveUsedCouponsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: CRMCouponsResponse) {
            val context = binding.root.context

            data.coupon?.eligibleMembersStr?.let {
                if (it.isNotEmpty()) {
                    binding.label.visibility = View.VISIBLE
                    binding.labelTv.text = it
                } else {
                    binding.label.visibility = View.GONE
                }
            }

            Glide.with(context).clear(binding.ivGoodsImage)
            if (!data.coupon?.coverImage.isNullOrEmpty()) {
                Glide.with(context)
                    .load(data.coupon!!.coverImage)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .dontAnimate()
                    .into(binding.ivGoodsImage)
            }

            binding.tvExchangeName.text = data.coupon?.couponName
            binding.tvExchangePeriod1.text = "兌換開始：${data.coupon?.redeemStartAtF}"
//            binding.tvExchangePeriod2.text = "兌換截止：${data.coupon?.redeemEndAtF}"

            // 4. Sự kiện Click
            binding.root.setOnClickListener {
                onItemClick(data)
            }
        }
    }
}