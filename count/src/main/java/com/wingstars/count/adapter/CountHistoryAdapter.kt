package com.wingstars.count.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.count.R
import com.wingstars.count.databinding.ItemCountRecordsBinding
import com.wingstars.base.net.beans.CRMJournalHistoryResponse

class CountHistoryAdapter(
    private val context: Context,
    private val isUsageRecord: Boolean = false
) : PagingDataAdapter<CRMJournalHistoryResponse.Journal, CountHistoryAdapter.CountRecordViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CRMJournalHistoryResponse.Journal>() {
            override fun areItemsTheSame(
                oldItem: CRMJournalHistoryResponse.Journal,
                newItem: CRMJournalHistoryResponse.Journal
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: CRMJournalHistoryResponse.Journal,
                newItem: CRMJournalHistoryResponse.Journal
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountRecordViewHolder {
        val binding =
            ItemCountRecordsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CountRecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CountRecordViewHolder, position: Int) {
        // Lấy item từ PagingDataAdapter
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    // ViewHolder
    inner class CountRecordViewHolder(private val binding: ItemCountRecordsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CRMJournalHistoryResponse.Journal) {

            binding.tvTitle.text = item.Description ?: ""
//            binding.tvInfo.text = item.Content ?: ""
            binding.tvTime.text = item.CreditedAtF ?: ""

            val pointValue = item.Points ?: 0

            if (isUsageRecord) {
                binding.tvObtained.setTextColor(ContextCompat.getColor(context, R.color.color_101828))

                val pointText = if (pointValue.toString().startsWith("-")) "$pointValue" else "-$pointValue"
                binding.tvObtained.text = pointText

            } else {
                binding.tvObtained.setTextColor(ContextCompat.getColor(context, R.color.color_E2518D))
                val pointText = if (pointValue.toString().startsWith("+")) "$pointValue" else "+$pointValue"
                binding.tvObtained.text = pointText
            }
        }
    }
}