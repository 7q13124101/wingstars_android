package com.wingstars.count.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.wingstars.base.net.beans.EvtTaskResponse
import com.wingstars.count.R
import com.wingstars.count.databinding.ItemCountSingleBinding

class CountAdapter(
    private val context: Context,
    private var listData: MutableList<EvtTaskResponse>,
    private val listener: onItemListener? = null
) : RecyclerView.Adapter<CountAdapter.CountViewHolder>() {

    constructor() : this(null!!, mutableListOf(), null)

    interface onItemListener {
        fun onItemClick(data: EvtTaskResponse, position: Int)
        fun onMoreClick()
        fun setViewheight(height: Int)
    }

    var onItemClick: ((EvtTaskResponse) -> Unit)? = null

    fun setList(newList: List<EvtTaskResponse>) {
        listData.clear()
        listData.addAll(newList)
        notifyDataSetChanged()
    }

    fun addList(newList: List<EvtTaskResponse>, isRefresh: Boolean) {
        if (isRefresh) {
            listData.clear()
        }
        listData.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountViewHolder {
        val binding = ItemCountSingleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CountViewHolder, position: Int) {
        val item = listData[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    inner class CountViewHolder(private val binding: ItemCountSingleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EvtTaskResponse, position: Int) {

            binding.tvTitle.text = item.topic
            binding.tvInfo.text = item.content
            binding.tvTime.text =
                if (item.endDate.isNullOrEmpty()) "" else item.endDate.split("T")[0]
            binding.tvCount.text = "${item.point} 點"

//            Glide.with(context)
//                .load(item.image)
//                .transform(CenterCrop(), RoundedCorners(16))
//                .placeholder(R.drawable.ic_default_image)
//                .into(binding.ivIcon)

            binding.root.setOnClickListener {
                if (listener != null) {
                    listener.onItemClick(item, position)
                } else {
                    onItemClick?.invoke(item)
                }
            }

            binding.tvCountStatus.apply {
//                    visibility = View.GONE
//                    when (item.status) {
//                        "unlock" -> {
//                            text = context.getString(R.string.count_unlock)
//                            background = ContextCompat.getDrawable(
//                                context,
//                                R.drawable.bg_rounded_rectangle_gray
//                            )
//                        }
//
//                        "unlockNot" -> {
//                            text = context.getString(R.string.count_unlockNot)
//                            background = ContextCompat.getDrawable(
//                                context,
//                                R.drawable.bg_rounded_rectangle_gray
//                            )
//
//                        }
//
//                        "pending" -> {
//                            text = context.getString(R.string.count_pending)
//                            background = ContextCompat.getDrawable(
//                                context,
//                                R.drawable.bg_rounded_rectangle_gray
//                            )
//
//                        }
//
//                        "expired" -> {
//                            text = context.getString(R.string.count_expired)
//                            background = ContextCompat.getDrawable(
//                                context,
//                                R.drawable.bg_rounded_rectangle_gray
//                            )
//                        }
//
//                        "completed", "reward" -> {
//                            text = context.getString(R.string.count_completed)
//                            background = ContextCompat.getDrawable(
//                                context,
//                                R.drawable.bg_rounded_rectangle_completed
//                            )
//                            setTextColor(ContextCompat.getColor(context, R.color.color_F5F5F5))
//                        }
//                    }

                when (item.status) {
                    "completed", "reward" -> {
                        visibility = View.VISIBLE
                        text = context.getString(R.string.count_completed)
                        background = ContextCompat.getDrawable(
                            context,
                            R.drawable.bg_rounded_rectangle_completed
                        )
                        setTextColor(ContextCompat.getColor(context, R.color.color_F5F5F5))
                    }

                    else -> {
                        visibility = View.GONE
                    }
                }

//            if (item.status != "unlock") {
//                binding.llRoot.setOnClickListener { listener.onItemClick(item, position) }
//            } else {
//                binding.llRoot.setOnClickListener(null)
//            }
            }
        }
    }
}