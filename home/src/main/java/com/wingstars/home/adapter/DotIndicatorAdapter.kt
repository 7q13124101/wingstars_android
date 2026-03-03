package com.wingstars.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.home.R
import com.wingstars.home.adapter.DotIndicatorAdapter.NormalItemViewHolder
import com.wingstars.home.databinding.ItemGuideListBinding

class DotIndicatorAdapter(count: Int = 0) : RecyclerView.Adapter<DotIndicatorAdapter.NormalItemViewHolder>() {

    private var count: Int = count
    private var selectedPos: Int = 0
    fun submitCount(newCount: Int) {
        count = newCount.coerceAtLeast(0)
        if (count <= 1) selectedPos = 0
        if (selectedPos >= count) selectedPos = 0
        notifyDataSetChanged()
    }
    fun setPosition(pos: Int) {
        if (count <= 0) return
        val newPos = pos.coerceIn(0, count - 1)
        if (newPos == selectedPos) return
        val prevPos = selectedPos
        selectedPos = newPos
        notifyItemChanged(prevPos)
        notifyItemChanged(selectedPos)
    }

    override fun getItemCount(): Int = count


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding =
            ItemGuideListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NormalItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NormalItemViewHolder, position: Int) {
        holder.binding(position)

    }

    inner class NormalItemViewHolder(private val binding: ItemGuideListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun binding(position: Int) {
            binding.select.visibility  = if (position==selectedPos) View.VISIBLE else View.GONE
            binding.notSelect.visibility = if (position==selectedPos) View.GONE else View.VISIBLE
        }


        fun onBind(position: Int) {

        }

        fun setMarginLeft(view: View,left: Int){
            var params = view.layoutParams as LinearLayout.LayoutParams
            params.leftMargin = left
            view.layoutParams = params
        }
    }

}