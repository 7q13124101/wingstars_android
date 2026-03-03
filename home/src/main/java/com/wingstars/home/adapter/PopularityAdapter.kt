package com.wingstars.home.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.wingstars.base.net.beans.WSMemberResponse
import com.wingstars.base.utils.DPUtils
import com.wingstars.home.databinding.ItemRankingCardBinding
import com.wingstars.member.bean.WSMemberRankBean

class PopularityAdapter(
    private val context: Context,
    // List hiển thị (Rank info)
    private var dataList: MutableList<WSMemberRankBean>?,
    private val listener: onPopularityRankingListener
) : RecyclerView.Adapter<PopularityAdapter.NormalItemViewHolder>() {

    private var memberDetailList: List<WSMemberResponse> = ArrayList()

    interface onPopularityRankingListener {
        fun onPopularityRankingClickItem(data: WSMemberResponse)
    }

    fun setRankList(list: MutableList<WSMemberRankBean>?) {
        this.dataList = list
        notifyDataSetChanged()
    }

    fun setMemberDetailList(list: List<WSMemberResponse>) {
        this.memberDetailList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding =
            ItemRankingCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NormalItemViewHolder(binding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: NormalItemViewHolder, position: Int) {
        holder.binding(position, listener)
    }

    override fun getItemCount(): Int {
        return if (dataList != null) dataList!!.size else 0
    }

    inner class NormalItemViewHolder(private val binding: ItemRankingCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun binding(position: Int, listeners: onPopularityRankingListener) {
            val params = binding.item.layoutParams as ViewGroup.MarginLayoutParams
            binding.item.layoutParams = params

            val bean = dataList!![position]
            binding.tvRankNumber.text = (position + 1).toString()
            binding.tvName.text = "${bean.number} ${bean.name}"
            binding.tvVoteCount.text = "${bean.volume}"

            Glide.with(binding.imgPerson.context)
                .load("${bean.image}")
                .apply(
                    RequestOptions()
                        .transform(CenterCrop(), RoundedCorners(DPUtils.dpToPx(20f, context).toInt()))
                )
                .into(binding.imgPerson)

            binding.item.setOnClickListener {
                val detail = memberDetailList.getOrNull(position)
                if (detail != null) {
                    listeners.onPopularityRankingClickItem(detail)
                } else {
                     Log.d("PopularityAdapter", "No detail for position=$position, memberDetailList.size=${memberDetailList.size}")
                }
            }
        }
    }
}