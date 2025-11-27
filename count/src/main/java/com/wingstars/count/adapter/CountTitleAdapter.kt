package com.wingstars.count.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.count.R
import com.wingstars.count.databinding.ItemCountTitleBinding


class CountTitleAdapter     // -------------------------------------------
    (
    private val context: Context, private var dataList: MutableList<String>?,
    private val listener: onItemListener,
    private var selectPosition: Int
) : RecyclerView.Adapter<CountTitleAdapter.NormalItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding = ItemCountTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NormalItemViewHolder(binding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // -------------------------------------------
    override fun onBindViewHolder(holder: NormalItemViewHolder, position: Int) {
//        holder.setIsRecyclable(false)
        holder.binding(position, listener)
    }

    // -------------------------------------------
    override fun getItemCount(): Int {
        return if (dataList != null) dataList!!.size else 0
    }

    // -------------------------------------------
    fun setList(list: MutableList<String>?) {
        dataList = if (dataList == null) {
            ArrayList()
        } else {
            dataList == null
            ArrayList()
        }
        if (list != null) {
            dataList!!.addAll(list)
            notifyDataSetChanged()
        }
    }


    fun getData(): MutableList<String>? {
        if (dataList == null) {
            return null
        }
        return dataList
    }

    fun  setSelectPosition(selectPositions:Int){
        if (selectPositions!=selectPosition){
            selectPosition  =  selectPositions
            this@CountTitleAdapter.notifyDataSetChanged()
        }

    }


    // -------------------------------------------
    inner class NormalItemViewHolder(private val binding: ItemCountTitleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun binding(position: Int, listener: onItemListener) {
            var data = dataList!![position]
            binding.name.text = data
            if (selectPosition==position){
                binding.name.setBackgroundResource(R.drawable.count_title_select_text_background)
                binding.name.setTextColor(context.getColor(R.color.white))
            } else{
                binding.name.setBackgroundResource(R.drawable.count_title_text_background)
                binding.name.setTextColor(context.getColor(R.color.black))
            }

            binding.name.setOnClickListener{listener.onItemClick("",position)}
        }

        fun onBind(position: Int) {
        }
    }


    interface onItemListener {
        fun onItemClick(data: String, position: Int)
    }
}