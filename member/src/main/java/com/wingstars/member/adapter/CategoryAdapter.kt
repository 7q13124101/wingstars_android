package com.wingstars.member.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wingstars.base.utils.DPUtils
import com.wingstars.member.R
import com.wingstars.member.databinding.ItemCategoryListBinding
import com.wingstars.member.databinding.ItemGirlIntoductionListBinding
import com.wingstars.member.view.TopRoundedCornersTransformation


class CategoryAdapter     // -------------------------------------------
    (
    private val context: Context,
    private var dataList: MutableList<String>?
) : RecyclerView.Adapter<CategoryAdapter.NormalItemViewHolder>() {
    var pos = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding =
            ItemCategoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    fun setList(list: MutableList<String>?) {
        dataList = if (dataList == null) {
            ArrayList()
        } else {
            dataList == null
            ArrayList()
        }
        dataList!!.addAll(list!!)
        notifyDataSetChanged()
    }


    fun getData(): MutableList<String>? {
        if (dataList == null) {
            return null
        }
        return dataList
    }


    // -------------------------------------------
    inner class NormalItemViewHolder(private val binding: ItemCategoryListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun binding(position: Int) {

           binding.name.text  = dataList!![position]
           if (position == pos){
               binding.name.setTextColor(context.getColor(R.color.color_E2518D))
               binding.item.apply {
                   setStrokeColor(context.getColor(R.color.color_E2518D))
                   setStrokeWidth(DPUtils.dpToPx(1f, context))
                   setLayoutBackground(context.getColor(R.color.white))
               }
           }else{
               binding.name.setTextColor(context.getColor(R.color.color_101828))
               binding.item.apply {
                   setStrokeWidth(0f)
                   setLayoutBackground(context.getColor(R.color.color_F3F4F6))
               }
           }
            binding.item.setOnClickListener {
                Log.e("binding.item","pos=$pos  position=$position")
                if (pos!=position){
                    pos = position
                    notifyDataSetChanged()
                }
            }
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