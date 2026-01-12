package com.wingstars.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HorizontalWrapperAdapter(
    private val innerAdapter: RecyclerView.Adapter<*>
) : RecyclerView.Adapter<HorizontalWrapperAdapter.ViewHolder>() {

    class ViewHolder(val recyclerView: RecyclerView) : RecyclerView.ViewHolder(recyclerView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val recyclerView = RecyclerView(parent.context)
        recyclerView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        // Cài đặt Horizontal
        recyclerView.layoutManager =
            LinearLayoutManager(parent.context, LinearLayoutManager.HORIZONTAL, false)
        // Tối ưu
        recyclerView.setRecycledViewPool(RecyclerView.RecycledViewPool())
        return ViewHolder(recyclerView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.recyclerView.adapter = innerAdapter
    }

    override fun getItemCount() = 1
}