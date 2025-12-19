package com.wingstars.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.wingstars.base.utils.DPUtils
import com.wingstars.home.databinding.ItemRankingCardBinding
import com.wingstars.member.R
import com.wingstars.member.bean.WSMemberRankBean

class PopularityAdapter     // -------------------------------------------
    (
    private val context: Context,
    private var dataList: MutableList<WSMemberRankBean>?,
    private val listener: onPopularityRankingListener
) : RecyclerView.Adapter<PopularityAdapter.NormalItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding =
            ItemRankingCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NormalItemViewHolder(binding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // -------------------------------------------
    override fun onBindViewHolder(holder: NormalItemViewHolder, position: Int) {
        holder.binding(position, listener)
    }

    // -------------------------------------------
    override fun getItemCount(): Int {
        return if (dataList != null) dataList!!.size else 0
    }

    // -------------------------------------------
    fun setList(list: MutableList<WSMemberRankBean>?) {
        dataList = if (dataList == null) {
            ArrayList()
        } else {
            dataList == null
            ArrayList()
        }
        dataList!!.addAll(list!!)
        notifyDataSetChanged()
    }


    fun getData(): MutableList<WSMemberRankBean>? {
        if (dataList == null) {
            return null
        }
        return dataList
    }


    // -------------------------------------------
    inner class NormalItemViewHolder(private val binding: ItemRankingCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun binding(position: Int, listeners: onPopularityRankingListener) {
            val params = binding.item.layoutParams as ViewGroup.MarginLayoutParams
            if (position == 0) {
                params.leftMargin = DPUtils.Companion.dpToPx(20f, context).toInt()
            } else {
                params.leftMargin = 0
            }
            binding.item.layoutParams = params

            binding.tvRankNumber.text = (position + 1).toString()

            val bean = dataList!![position]

            binding.tvName.text = "${bean.number} ${bean.name}"
            binding.tvVoteCount.text = "${bean.volume}"

            Glide.with(context)
                .load("${bean.image}")
                .apply(
                    RequestOptions()
                        .transform(RoundedCorners(DPUtils.dpToPx(20f, context).toInt()))
                )
                .into(binding.imgPerson)

            binding.item.setOnClickListener { listeners.onPopularityRankingClickItem(position) }
        }


        fun onBind(position: Int) {

        }

        fun setMarginLeft(view: View, leftMargin: Int) {
            if (view.layoutParams is ViewGroup.MarginLayoutParams) {
                val params = view.layoutParams as ViewGroup.MarginLayoutParams
                params.leftMargin = leftMargin
                view.layoutParams = params
            }
        }
    }


    interface onPopularityRankingListener {
        fun onPopularityRankingClickItem(position: Int)
    }
}