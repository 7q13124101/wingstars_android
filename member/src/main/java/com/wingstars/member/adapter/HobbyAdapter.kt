package com.wingstars.member.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.member.databinding.ItemHobbyListBinding


class HobbyAdapter(
    private var dataList: MutableList<String>?
) : RecyclerView.Adapter<HobbyAdapter.NormalItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding =
            ItemHobbyListBinding.inflate(
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
    fun setList(list: MutableList<String>?) {
        dataList = if (dataList == null) {
            ArrayList()
        } else {
            dataList == null
            ArrayList()
        }
        dataList!!.addAll(list!!)
        notifyDataSetChanged()
    }


    fun getData(): MutableList<String>? {
        if (dataList == null) {
            return null
        }
        return dataList
    }


    // -------------------------------------------
    inner class NormalItemViewHolder(private val binding: ItemHobbyListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun binding(position: Int) {
            var data = dataList!![position]
            binding.tvItemHobby.text =data

        }


        fun onBind(position: Int) {
        }
    }

}