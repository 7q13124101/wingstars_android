package com.wingstars.member.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.member.databinding.ItemMemberIntroductionBinding


class MemberIntroductionAdapter     // -------------------------------------------
    (
    private val context: Context,
    private var dataList: MutableList<Int>?,
    private val listener: OnItemListener,
) : RecyclerView.Adapter<MemberIntroductionAdapter.NormalItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding =
            ItemMemberIntroductionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return NormalItemViewHolder(binding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // -------------------------------------------
    override fun onBindViewHolder(holder: NormalItemViewHolder, position: Int) {
        holder.binding(position)
    }

    // -------------------------------------------
    override fun getItemCount(): Int {
        return if (dataList != null) dataList!!.size else 0
    }

    // -------------------------------------------
    fun setList(list: MutableList<Int>?) {
        dataList = if (dataList == null) {
            ArrayList()
        } else {
            dataList == null
            ArrayList()
        }
        dataList!!.addAll(list!!)
        notifyDataSetChanged()
    }


    fun getData(): MutableList<Int>? {
        if (dataList == null) {
            return null
        }
        return dataList
    }

    // -------------------------------------------
    inner class NormalItemViewHolder(private val binding: ItemMemberIntroductionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun binding(position: Int) {
            binding.root.setOnClickListener {
                listener.onItemClick(position)
            }
        }

        fun onBind(position: Int) {
        }
    }

    interface OnItemListener {
        fun onItemClick(position: Int)
    }
}