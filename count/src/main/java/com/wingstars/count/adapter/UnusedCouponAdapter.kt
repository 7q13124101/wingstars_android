package com.wingstars.count.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.wingstars.count.R
import com.wingstars.count.databinding.ItemUnusedCouponsBinding
import com.wingstars.base.net.beans.CRMCouponsResponse
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class UnusedCouponAdapter(
    private var list: MutableList<CRMCouponsResponse> = mutableListOf(),
    private val onItemClick: (CRMCouponsResponse) -> Unit
) : RecyclerView.Adapter<UnusedCouponAdapter.CouponViewHolder>() {
    var onBarcodeClick: ((CRMCouponsResponse) -> Unit)? = null

    fun setData(newList: List<CRMCouponsResponse>?) {
        this.list = ArrayList()
        newList?.let { this.list.addAll(it) }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val binding = ItemUnusedCouponsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CouponViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    inner class CouponViewHolder(private val binding: ItemUnusedCouponsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun formatDate(dateStr: String?): String {
            if (dateStr.isNullOrEmpty()) return ""

            return try {
                val date = OffsetDateTime.parse(dateStr)
                val outputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
                date.format(outputFormatter)
            } catch (e: Exception) {
                ""
            }
        }

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

            val start = formatDate(data.coupon?.couponStartDate)
            val end = formatDate(data.coupon?.couponEndDate)
            binding.tvExchangeName.text = data.coupon?.couponName
            binding.tvExchangePeriod1.text = "兌換時間：$start~$end"
//            binding.tvExchangePeriod2.text = "兌換截止：${data.coupon?.redeemEndAtF}"

            val endAt = data.coupon?.couponEndDate
//            android.util.Log.d("UnusedCouponAdapter", "EndAt String: $endAt")
            if (endAt.isNullOrEmpty()) {
                binding.tvExchangePeriod1.text = "兌換時間：~"
//                binding.tvExchangePeriod2.text = "兌換截止：~"
                binding.llActivityBarcode.visibility = View.VISIBLE
                binding.llRoot.setBackgroundResource(R.drawable.bg_item_unused)
//            } else {
//                val expired = try {
//                    val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
//                    val endDate = sdf.parse(endAt)
//                    val now = Date()
//                    endDate != null && endDate.before(now)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    true
//                }

//                if (expired) {
//                    binding.llActivityBarcode.visibility = View.GONE
//                    binding.llRoot.setBackgroundResource(R.drawable.bg_item_unused_notpr)
//                } else {
//                    binding.llActivityBarcode.visibility = View.VISIBLE
//                    binding.llRoot.setBackgroundResource(R.drawable.bg_item_unused)
//                    binding.llActivityBarcode.alpha = 1.0f
//                }
            }

            binding.root.setOnClickListener {
                onItemClick(data)
            }

            binding.llActivityBarcode.setOnClickListener {
                onBarcodeClick?.invoke(data)
            }
        }
    }
}