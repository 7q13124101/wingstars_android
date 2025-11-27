package com.wingstars.member.adapter

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
import com.wingstars.member.R
import com.wingstars.member.databinding.ItemPoplarityListBinding

class PopularityAdapter     // -------------------------------------------
    (
    private val context: Context,
    private var dataList: MutableList<Int>?,
    private val listener: onItemListener
) : RecyclerView.Adapter<PopularityAdapter.NormalItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding =
            ItemPoplarityListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    inner class NormalItemViewHolder(private val binding: ItemPoplarityListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun binding(position: Int, listeners: onItemListener) {
            if (position==0){
                setMarginLeft(binding.item, DPUtils.Companion.dpToPx(20f,context).toInt())
            }else{
                setMarginLeft(binding.item, 0)
            }
            Glide.with(context)
                .load(R.mipmap.ic_demo)
                .apply(
                    RequestOptions.bitmapTransform(
                        RoundedCorners(
                            DPUtils.Companion.dpToPx(20f, context).toInt()
                        )
                    ))
                .into(binding.image)
        }


        fun onBind(position: Int) {

        }

        fun setMarginLeft(view: View, left: Int){
           var params = view.layoutParams as LinearLayout.LayoutParams
            params.leftMargin = left
            view.layoutParams = params
        }
    }







    interface onItemListener {
        fun ClickItem(position: Int)
    }
}