package com.wingstars.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.base.net.beans.CRMInAppMessageResponse
import com.wingstars.home.databinding.ItemNotificationBinding // Đảm bảo bạn có layout này (hoặc item_notify_list)

class NotificationAdapter(
    private val onItemClick: (CRMInAppMessageResponse) -> Unit
) : PagingDataAdapter<CRMInAppMessageResponse, NotificationAdapter.NotificationViewHolder>(COMPARATOR) {

    // DiffUtil để Paging so sánh dữ liệu cũ/mới
    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<CRMInAppMessageResponse>() {
            override fun areItemsTheSame(oldItem: CRMInAppMessageResponse, newItem: CRMInAppMessageResponse): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: CRMInAppMessageResponse, newItem: CRMInAppMessageResponse): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        // Sử dụng ViewBinding cho item
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    inner class NotificationViewHolder(private val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CRMInAppMessageResponse) {
            binding.tvTitle.text = data.title
            binding.tvContent.text = data.content // Hoặc description tùy tên field trong layout
            binding.tvDate.text = data.CreatedAtF

            // Logic hiển thị chấm đỏ (chưa đọc)
            // status: 0 = chưa đọc, 1 = đã đọc
            binding.viewUnreadDot.visibility = if (data.status == 0) View.VISIBLE else View.GONE

            binding.root.setOnClickListener {
                onItemClick(data)
            }
        }
    }
}