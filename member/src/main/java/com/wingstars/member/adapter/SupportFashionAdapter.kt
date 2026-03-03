package com.wingstars.member.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wingstars.base.net.beans.WSFashionResponse
import com.wingstars.base.utils.DPUtils
import com.wingstars.member.R
import com.wingstars.member.databinding.ItemSupportsFashionListBinding

import com.wingstars.member.view.TopRoundedCornersTransformation


class SupportFashionAdapter     // -------------------------------------------
    (
    private val context: Context,
    private var dataList: MutableList<WSFashionResponse>?,
    private val listener: onSupportFashionListener
) : RecyclerView.Adapter<SupportFashionAdapter.NormalItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding =
            ItemSupportsFashionListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NormalItemViewHolder(binding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // -------------------------------------------
    override fun onBindViewHolder(holder: NormalItemViewHolder, position: Int) {
        holder.binding(position,listener)
    }

    // -------------------------------------------
    override fun getItemCount(): Int {
        return if (dataList != null) dataList!!.size else 0
    }

    // -------------------------------------------
    fun setList(list: MutableList<WSFashionResponse>?) {
        dataList = if (dataList == null) {
            ArrayList()
        } else {
            dataList == null
            ArrayList()
        }
        dataList!!.addAll(list!!)
        notifyDataSetChanged()
    }


    fun getData(): MutableList<WSFashionResponse>? {
        if (dataList == null) {
            return null
        }
        return dataList
    }


    // -------------------------------------------
    inner class NormalItemViewHolder(private val binding: ItemSupportsFashionListBinding,
        ) :
        RecyclerView.ViewHolder(binding.root) {

        fun binding(position: Int,listener: onSupportFashionListener ) {
            val data = dataList!![position]
            if (position==0){
                setMarginLeft(binding.item, DPUtils.dpToPx(20f,context).toInt())
            }else{
                setMarginLeft(binding.item, 0)
            }

            Glide.with(context)
                .load("${data.urlF}")  //R.mipmap.ic_member_page_background
                .into(binding.image)
          var imageType =  if (data.type==1){ R.mipmap.ic_member_jersey} else {R.mipmap.ic_member_activity}
          binding.imageType.setImageResource(imageType)
          binding.title.text = "${data.titleF}"
          binding.item.setOnClickListener { listener.onSupportFashionClickItem(data.id,data.type) }
        }


        fun onBind(position: Int) {

        }

        fun setMarginLeft(view: View,left: Int){
           var params = view.layoutParams as LinearLayout.LayoutParams
            params.leftMargin = left
            view.layoutParams = params
        }
    }


     interface onSupportFashionListener{
         fun onSupportFashionClickItem(memberId: Int,fashionType: Int)
     }





}