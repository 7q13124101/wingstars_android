package com.wingstars.count.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.wingstars.base.net.beans.CRMCouponsAvailableResponse
import com.wingstars.count.R
import com.wingstars.count.databinding.ItemGoodsListBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ActivityExchangeAdapter(
    private val onItemClick: (CRMCouponsAvailableResponse) -> Unit
) : RecyclerView.Adapter<ActivityExchangeAdapter.ExchangeViewHolder>() {

    private var dataList: MutableList<CRMCouponsAvailableResponse> = mutableListOf()
    companion object {
        private val inputFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.TAIWAN)
        private val outputDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN)
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeViewHolder {
        val binding = ItemGoodsListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExchangeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExchangeViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size

    override fun getItemId(position: Int): Long {
        return dataList[position].id?.hashCode()?.toLong() ?: position.toLong()
    }

    fun submitList(newList: List<CRMCouponsAvailableResponse>?) {
        val oldList = dataList.toList()
        val newData = newList ?: emptyList()

        val diffCallback = CouponDiffCallback(oldList, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        dataList.clear()
        dataList.addAll(newData)

        diffResult.dispatchUpdatesTo(this)
    }
    fun setList(list: MutableList<CRMCouponsAvailableResponse>?) {
        dataList.clear()
        if (!list.isNullOrEmpty()) {
            dataList.addAll(list)
        }
        notifyDataSetChanged()
    }

    fun getData(): MutableList<CRMCouponsAvailableResponse> = dataList

    fun clear() {
        dataList.clear()
        notifyDataSetChanged()
    }

    // ============================================
    // ViewHolder
    // ============================================
    inner class ExchangeViewHolder(
        private val binding: ItemGoodsListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val context: Context = binding.root.context

        init {
            binding.llWingStarsRoot.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(dataList[position])
                }
            }
        }

        fun bind(item: CRMCouponsAvailableResponse) {
            bindDate(item)
            bindCouponName(item)
            bindCouponCost(item)
            bindImage(item)
        }

        private fun bindDate(item: CRMCouponsAvailableResponse) {
            val redeemStart = item.redeemStartAtF

            if (redeemStart.isNullOrEmpty()) {
                binding.couponStartDate.text = "~"
                return
            }

            try {
                val date = inputFormat.parse(redeemStart)

                if (date != null) {
                    val dateStr = outputDateFormat.format(date)
                    val week = getWeekDayString(date)
                    binding.couponStartDate.text = "$dateStr $week"
                } else {
                    binding.couponStartDate.text = redeemStart
                }
            } catch (e: Exception) {
                binding.couponStartDate.text = redeemStart
            }
        }

        private fun getWeekDayString(date: Date): String {
            val cal = Calendar.getInstance()
            cal.time = date

            val weekResId = when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> R.string.week_7
                Calendar.MONDAY -> R.string.week_1
                Calendar.TUESDAY -> R.string.week_2
                Calendar.WEDNESDAY -> R.string.week_3
                Calendar.THURSDAY -> R.string.week_4
                Calendar.FRIDAY -> R.string.week_5
                Calendar.SATURDAY -> R.string.week_6
                else -> 0
            }

            return if (weekResId != 0) "(${context.getString(weekResId)})" else ""
        }

        private fun bindCouponName(item: CRMCouponsAvailableResponse) {
            binding.couponName.text = item.couponName ?: ""
        }

        private fun bindCouponCost(item: CRMCouponsAvailableResponse) {
            binding.tvCount.text = "${item.pointCost}"
        }

        private fun bindImage(item: CRMCouponsAvailableResponse) {
            Glide.with(context)
                .load(item.coverImage)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.image)
        }
    }

    // ============================================
    // DiffUtil Callback
    // ============================================
    private class CouponDiffCallback(
        private val oldList: List<CRMCouponsAvailableResponse>,
        private val newList: List<CRMCouponsAvailableResponse>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            return oldItem.couponName == newItem.couponName &&
                    oldItem.pointCost == newItem.pointCost &&
                    oldItem.redeemStartAt == newItem.redeemStartAt &&
                    oldItem.coverImage == newItem.coverImage
        }
    }
}