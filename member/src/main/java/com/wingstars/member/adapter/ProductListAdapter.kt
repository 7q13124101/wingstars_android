package com.wingstars.member.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.wingstars.base.utils.DPUtils
import com.wingstars.member.R
import com.wingstars.member.databinding.ItemCategoryListBinding
import com.wingstars.member.databinding.ItemGirlIntoductionListBinding
import com.wingstars.member.databinding.ItemGuideListBinding
import com.wingstars.member.databinding.ItemProductListBinding
import com.wingstars.member.databinding.ItemSmallCommodityListBinding
import com.wingstars.member.view.CircleWithBorderTransformation
import com.wingstars.member.view.TopRoundedCornersTransformation


class ProductListAdapter     // -------------------------------------------
    (
    private val context: Context,
    private var dataList: MutableList<Int>?
) : RecyclerView.Adapter<ProductListAdapter.NormalItemViewHolder>() {
    private var pos = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding =
            ItemProductListBinding.inflate(
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

    public fun setPos(pos: Int) {
        this.pos = pos
        notifyDataSetChanged()
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
    inner class NormalItemViewHolder(private val binding: ItemProductListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun binding(position: Int) {
            if (position==0){
                setMarginLeft(binding.item, DPUtils.dpToPx(20f,context).toInt())
            }else{
                setMarginLeft(binding.item, 0)
            }
            Glide.with(context)
                .load(R.mipmap.ic_demo5)
                .transform(RoundedCorners(DPUtils.dpToPx(20f,context).toInt()))
                .into(binding.image)

        }


        fun onBind(position: Int) {

        }

        fun setMarginLeft(view: View, left: Int) {
            var params = view.layoutParams as LinearLayout.LayoutParams
            params.leftMargin = left
            view.layoutParams = params
        }
    }


}