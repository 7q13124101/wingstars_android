package com.wingstars.count.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wingstars.base.net.beans.EvtTaskResponse
import com.wingstars.count.databinding.ItemCountSingleBinding

class CountAdapter : RecyclerView.Adapter<CountAdapter.CountViewHolder>() {

    private val listData = ArrayList<EvtTaskResponse>()

    var onItemClick: ((EvtTaskResponse) -> Unit)? = null

    fun setList(newList: List<EvtTaskResponse>) {
        listData.clear()
        listData.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountViewHolder {
        val binding = ItemCountSingleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CountViewHolder, position: Int) {
        val item = listData[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    inner class CountViewHolder(private val binding: ItemCountSingleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EvtTaskResponse) {

            binding.tvTitle.text = item.topic
            binding.tvInfo.text = item.content
            binding.tvTime.text = item.sectionTime
            binding.tvCount.text = "${item.point} 點"



            binding.root.setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
    }
}