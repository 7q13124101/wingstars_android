package com.wingstars.member.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wingstars.base.utils.DPUtils
import com.wingstars.member.R
import com.wingstars.member.databinding.ItemSupportFashionListBinding
import com.wingstars.member.databinding.ItemSupportSuitListBinding
import com.wingstars.member.view.TopRoundedCornersTransformation


class SupportSuitAdapter     // -------------------------------------------
    (
    private val context: Context,
    private var dataList: MutableList<String>?,
    private var smallwidth:Int,
    private var smallhight:Int
) : RecyclerView.Adapter<SupportSuitAdapter.NormalItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding =
            ItemSupportSuitListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    inner class NormalItemViewHolder(private val binding: ItemSupportSuitListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun binding(position: Int) {
            binding.item.setStrokeWidth(DPUtils.dpToPx(1f,context))
            binding.item.setStrokeColor(context.resources.getColor(R.color.color_F3F4F6))
            setImage(binding.item,smallwidth,smallhight,if (position>0&&position%2==1) true else false)
           // var smallhights = smallhight - DPUtils.dpToPx(44f,context).toInt()
           // setImage(binding.image,smallwidth,smallhights)
            binding.image.setPadding(DPUtils.dpToPx(1f,context).toInt())
/*
            if (position==0){
                setMarginLeft(binding.item, DPUtils.dpToPx(20f,context).toInt())
            }else{
                setMarginLeft(binding.item, 0)
            }
*/

            Glide.with(context)
                .load(R.mipmap.ic_demo1)
                .transform(TopRoundedCornersTransformation(DPUtils.dpToPx(20f, context))) // 核心：应用自定义变换
                .into(binding.image)

        }


        fun onBind(position: Int) {

        }

        fun setImage(view: View,width: Int,hight:Int,gravity: Boolean=false){
           var params = view.layoutParams as LinearLayout.LayoutParams
            params.width = width
            params.height = hight
            if (gravity){
                params.gravity = Gravity.RIGHT
            }else{
                params.gravity = Gravity.LEFT
            }
            view.layoutParams = params
        }
    }








}