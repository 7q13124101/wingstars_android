package com.wingstars.member.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.member.databinding.ItemHighlightsListBinding

data class HighlightsData(
    val title: String,
    val date: String,
    val isPlayNow: Boolean,
) : java.io.Serializable

class HighlightsAdapter(
    private val context: Context,
    private var dataList: MutableList<HighlightsData>?,
    private val listener: OnItemListener
) : RecyclerView.Adapter<HighlightsAdapter.NormalItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding =
            ItemHighlightsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NormalItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NormalItemViewHolder, position: Int) {
        holder.binding(position)
    }

    override fun getItemCount(): Int {
        return if (dataList != null) dataList!!.size else 0
    }

    fun setList(list: MutableList<HighlightsData>?) {
        dataList = if (dataList == null) {
            ArrayList()
        } else {
            dataList == null
            ArrayList()
        }
        dataList!!.addAll(list!!)
        notifyDataSetChanged()
    }

    fun getData(): MutableList<HighlightsData>? {
        if (dataList == null) {
            return null
        }
        return dataList
    }

    inner class NormalItemViewHolder(private val binding: ItemHighlightsListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun binding(position: Int) {
            var data = dataList!![position]

            binding.tvHighlightsContent.text = data.title
            binding.tvDate.text = data.date
            binding.llPlayNow.visibility = if (data.isPlayNow) View.VISIBLE else View.GONE

            binding.root.setOnClickListener {
                listener.onItemClick(data, position)
            }
        }

        fun onBind(position: Int) {
        }
    }

    interface OnItemListener {
        fun onItemClick(data: HighlightsData, position: Int)
    }
}