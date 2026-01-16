package com.wingstars.member.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.wingstars.base.net.beans.YoutubeListResponse
import com.wingstars.member.databinding.ItemHighlightsListBinding


class HighlightsAdapter(
    private val context: Context,
    private var dataList: MutableList<YoutubeListResponse.Item>?,
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

    fun setList(list: MutableList<YoutubeListResponse.Item>?) {
        dataList = if (dataList == null) {
            ArrayList()
        } else {
            dataList == null
            ArrayList()
        }
        dataList!!.addAll(list!!)
        notifyDataSetChanged()
    }

    fun getData(): MutableList<YoutubeListResponse.Item>? {
        if (dataList == null) {
            return null
        }
        return dataList
    }

    inner class NormalItemViewHolder(private val binding: ItemHighlightsListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun binding(position: Int) {
            var data = dataList!![position]

            Glide.with(binding.ivVideoImage).clear(binding.ivVideoImage)
            if (data.imageF.isNotEmpty()) {
                Glide.with(context)
                    .load(data.imageF)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .dontAnimate()
                    .into(binding.ivVideoImage)
            }

            binding.tvHighlightsContent.text = data.titleF
            binding.tvDate.text = data.dateF
            if (context is Activity) {
                if (context::class.java.simpleName.equals("ExclusiveSongsListActivity") && position == 0)
                    binding.llPlayNow.visibility = View.VISIBLE
            }


            binding.root.setOnClickListener {
                listener.onItemClick(data, position)
            }
        }

        fun onBind(position: Int) {
        }
    }

    interface OnItemListener {
        fun onItemClick(data: YoutubeListResponse.Item, position: Int)
    }
}