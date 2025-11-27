package com.wingstars.count.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.count.databinding.ItemCountRecordsBinding
import com.wingstars.count.viewmodel.CountRecordsItemViewModel
import java.util.ArrayList

class CountRecordAdapter(
    private val context: Context,
    private var dataList: MutableList<CountRecordsItemViewModel>?
) : RecyclerView.Adapter<CountRecordAdapter.CountRecordViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountRecordViewHolder {
        val binding =
            ItemCountRecordsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CountRecordViewHolder(binding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // -------------------------------------------
    override fun onBindViewHolder(holder: CountRecordViewHolder, position: Int) {
        holder.binding(position)
    }

    // -------------------------------------------
    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    // -------------------------------------------
    fun setList(list: MutableList<CountRecordsItemViewModel>?) {
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

    fun getData(): MutableList<CountRecordsItemViewModel>? {
        if (dataList == null) {
            return null
        }
        return dataList
    }


    // -------------------------------------------
    inner class CountRecordViewHolder(private val binding: ItemCountRecordsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun binding(position: Int) {
            val item = dataList!![position]

            binding.tvTitle.text = item.title
            binding.tvInfo.text = item.info
            binding.tvTime.text = item.time
            binding.tvObtained.text = item.count

        }

        fun setMarginLeft(view: View, left: Int) {
            val params = view.layoutParams
            if (params is RecyclerView.LayoutParams) {
                params.leftMargin = left
                view.layoutParams = params
            } else if (params is ViewGroup.MarginLayoutParams) {
                // Fallback
                params.leftMargin = left
                view.layoutParams = params
            }
        }
    }
}