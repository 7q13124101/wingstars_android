package com.wingstars.count.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wingstars.count.databinding.ItemGoodsListBinding
import com.wingstars.count.viewmodel.CountListItemViewModel
import java.util.ArrayList

class CountListAdapter (
    private val context: Context,
    private var dataList: MutableList<CountListItemViewModel>?
) : RecyclerView.Adapter<CountListAdapter.CountListViewHolder>() {
    private var originalList: ArrayList<CountListItemViewModel> = ArrayList()

    init {
        if (dataList != null) {
            originalList.addAll(dataList!!)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountListViewHolder {
        val binding =
            ItemGoodsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CountListViewHolder(binding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: CountListViewHolder, position: Int) {
        holder.binding(position)
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    // -------------------------------------------
    fun setList(list: MutableList<CountListItemViewModel>?) {
        if (dataList == null) {
            dataList = ArrayList()
        } else {
            dataList!!.clear()
        }
        originalList.clear()

        if (list != null) {
            dataList!!.addAll(list)
            originalList.addAll(list)
        }
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        val text = query.trim()

        if (dataList == null) dataList = ArrayList()
        dataList!!.clear()

        if (text.isEmpty()) {
            dataList!!.addAll(originalList)
        } else {
            for (item in originalList) {
                if (item.title?.contains(text, ignoreCase = true) == true) {
                    dataList!!.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    fun getData(): MutableList<CountListItemViewModel>? {
        return dataList
    }


    // -------------------------------------------
    inner class CountListViewHolder(private val binding: ItemGoodsListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun binding(position: Int) {
            if (dataList == null || position >= dataList!!.size) return

            val item = dataList!![position]

            binding.couponName.text = item.title
            binding.couponStartDate.text = item.time
            binding.tvCount.text = item.count

            Glide.with(context)
                .load(item.leftImageRes)
                .into(binding.image)
        }
    }
}