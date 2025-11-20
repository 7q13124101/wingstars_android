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
import com.wingstars.member.databinding.ItemGuideListBinding
import com.wingstars.member.view.TopRoundedCornersTransformation


class GuideAdapter     // -------------------------------------------
    (
    private val context: Context,
    private var dataList: MutableList<Int>?
) : RecyclerView.Adapter<GuideAdapter.NormalItemViewHolder>() {
   private var pos = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding =
            ItemGuideListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    public fun setPos(pos:Int){
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
    inner class NormalItemViewHolder(private val binding: ItemGuideListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun binding(position: Int) {
            binding.select.visibility  = if (position==pos) View.VISIBLE else View.GONE
            binding.notSelect.visibility = if (position==pos) View.GONE else View.VISIBLE
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