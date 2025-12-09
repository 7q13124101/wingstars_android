package com.wingstars.count.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wingstars.base.utils.DPUtils
import com.wingstars.count.databinding.ItemCountSingleBinding
import com.wingstars.count.viewmodel.CountSingleItemViewModel
import java.util.ArrayList

class CountSingleAdapter(
    private val context: Context,
    private var dataList: MutableList<CountSingleItemViewModel>?,
    private val onItemClick: (CountSingleItemViewModel) -> Unit
) : RecyclerView.Adapter<CountSingleAdapter.CountSingleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountSingleViewHolder {
        val binding =
            ItemCountSingleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CountSingleViewHolder(binding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // -------------------------------------------
    override fun onBindViewHolder(holder: CountSingleViewHolder, position: Int) {
        holder.binding(position)
    }

    // -------------------------------------------
    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    // -------------------------------------------
    fun setList(list: MutableList<CountSingleItemViewModel>?) {
        if (dataList == null) {
            dataList = ArrayList()
        } else {
            dataList!!.clear()
        }
        if (list != null) {
            dataList!!.addAll(list)
        }
        notifyDataSetChanged()
    }

    fun getData(): MutableList<CountSingleItemViewModel>? {
        if (dataList == null) {
            return null
        }
        return dataList
    }


    // -------------------------------------------
    inner class CountSingleViewHolder(private val binding: ItemCountSingleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun binding(position: Int) {
            val item = dataList?.getOrNull(position) ?: return

            binding.tvTitle.text = item.title
            binding.tvInfo.text = item.info
            binding.tvTime.text = item.time
            binding.tvCount.text = item.count

            Glide.with(context)
                .load(item.leftImageRes)
                .into(binding.ivArrowLeft)

            Glide.with(context)
                .load(item.countIconRes)
                .into(binding.rlIcon)

            binding.root.setOnClickListener {
                onItemClick(item)
            }

        }
    }
}