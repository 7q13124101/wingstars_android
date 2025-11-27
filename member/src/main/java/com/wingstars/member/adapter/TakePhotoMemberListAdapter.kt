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
import com.wingstars.member.bean.TakePhotosMembersListBean
import com.wingstars.member.databinding.ItemPoplarityListBinding
import com.wingstars.member.databinding.ItemRankListBinding
import com.wingstars.member.databinding.ItemRankingListBinding
import com.wingstars.member.databinding.ItemTakePhotoMemberListBinding

class TakePhotoMemberListAdapter     // -------------------------------------------
    (
    private val context: Context,
    private var dataList: MutableList<TakePhotosMembersListBean>?
) : RecyclerView.Adapter<TakePhotoMemberListAdapter.NormalItemViewHolder>() {
    private var pos = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding =
            ItemTakePhotoMemberListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    fun setList(list: MutableList<TakePhotosMembersListBean>?) {
        dataList = if (dataList == null) {
            ArrayList()
        } else {
            dataList == null
            ArrayList()
        }
        dataList!!.addAll(list!!)
        notifyDataSetChanged()
    }


    fun getData(): MutableList<TakePhotosMembersListBean>? {
        if (dataList == null) {
            return null
        }
        return dataList
    }


    // -------------------------------------------
    inner class NormalItemViewHolder(private val binding: ItemTakePhotoMemberListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun binding(position: Int) {
            var data = dataList!![position]
            binding.name.text = data.name
            binding.number.text = data.number
            binding.image.visibility = if (pos == position) View.VISIBLE else View.GONE
            binding.item.setOnClickListener {
                if (pos!=position){
                    pos = position
                    notifyDataSetChanged()
                }

            }

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