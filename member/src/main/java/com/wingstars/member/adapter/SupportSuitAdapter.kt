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
import com.wingstars.base.net.beans.WSFashionResponse
import com.wingstars.base.utils.DPUtils
import com.wingstars.member.R
import com.wingstars.member.databinding.ItemSupportSuitListsBinding
import com.wingstars.member.view.TopRoundedCornersTransformation


class SupportSuitAdapter     // -------------------------------------------
    (
    private val context: Context,
    private var dataList: MutableList<WSFashionResponse>?,
    private var smallwidth:Int,
    private var smallhight:Int,
    private val listener: OnItemListener,
) : RecyclerView.Adapter<SupportSuitAdapter.NormalItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding =
            ItemSupportSuitListsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    inner class NormalItemViewHolder(private val binding: ItemSupportSuitListsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun binding(position: Int) {
            val data = dataList!![position]
            binding.item.setStrokeWidth(DPUtils.dpToPx(1f,context))
            binding.item.setStrokeColor(context.resources.getColor(R.color.color_F3F4F6))
            setImage(binding.item,smallwidth,smallhight,if (position>0&&position%2==1) true else false)
            binding.image.setPadding(DPUtils.dpToPx(1f,context).toInt())
            Glide.with(context)
                .load(data.urlF) //R.mipmap.ic_demo1
                .into(binding.image)
            var imageType =  if (data.type==1){ R.mipmap.ic_member_jersey} else {R.mipmap.ic_member_activity}
            binding.imageType.setImageResource(imageType)
            binding.title.text = "${data.titleF}"
            binding.item.setOnClickListener { listener.onItemClick(data.id) }

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




    interface OnItemListener {
        fun onItemClick(memberId: Int)
    }



}