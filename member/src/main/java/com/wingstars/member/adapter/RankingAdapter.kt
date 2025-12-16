package com.wingstars.member.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wingstars.base.utils.DPUtils
import com.wingstars.member.R
import com.wingstars.member.bean.WSRankBean.ACFBean
import com.wingstars.member.databinding.ItemRankingListsBinding
import com.wingstars.member.view.CircleWithBorderTransformation

class RankingAdapter     // -------------------------------------------
    (
    private val context: Context,
    private var dataList: MutableList<ACFBean>?
) : RecyclerView.Adapter<RankingAdapter.NormalItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding =
            ItemRankingListsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    fun setList(list: MutableList<ACFBean>?) {
        dataList = if (dataList == null) {
            ArrayList()
        } else {
            dataList == null
            ArrayList()
        }
        dataList!!.addAll(list!!)
        notifyDataSetChanged()
    }


    fun getData(): MutableList<ACFBean>? {
        if (dataList == null) {
            return null
        }
        return dataList
    }


    // -------------------------------------------
    inner class NormalItemViewHolder(private val binding: ItemRankingListsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun binding(position: Int) {
            var data = dataList!![position]
            val borderColor =  context.getColor(R.color.transparent)
            val toInt = DPUtils.dpToPx(1f, context)
            Glide.with(context).load("${data.image}")   //R.mipmap.ic_member_page_background
                .transform(CircleWithBorderTransformation(toInt, borderColor))
                .into(binding.image)
            binding.name.text = "${data.name}"
            binding.volume.text = "${data.volume}"
            binding.nums.text = "${position+4}"


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