package com.wingstars.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.home.R
import com.wingstars.home.adapter.DotIndicatorAdapter.NormalItemViewHolder
import com.wingstars.home.databinding.ItemGuideListBinding

class DotIndicatorAdapter(private val count: Int) : RecyclerView.Adapter<DotIndicatorAdapter.NormalItemViewHolder>() {

    private var selectedPos = 0

    fun setPosition(pos: Int) {
        val prevPos = selectedPos
        selectedPos = pos
        notifyItemChanged(prevPos)
        notifyItemChanged(selectedPos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding =
            ItemGuideListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NormalItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NormalItemViewHolder, position: Int) {
        holder.binding(position)

    }

    override fun getItemCount(): Int = count

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