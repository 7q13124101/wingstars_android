package com.wingstars.count.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wingstars.count.R
import com.wingstars.count.databinding.ItemGoodsNewDetailBinding
import com.wingstars.count.viewmodel.CountNewDetailViewModel
import java.util.ArrayList

class CountNewDetailAdapter(
    private val context: Context,
    private var dataList: MutableList<CountNewDetailViewModel>?,
    private val onItemClick: (CountNewDetailViewModel) -> Unit
) : RecyclerView.Adapter<CountNewDetailAdapter.CountNewDetailViewHolder>() {

    private var originalList: ArrayList<CountNewDetailViewModel> = ArrayList()

    init {
        if (dataList != null) {
            originalList.addAll(dataList!!)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountNewDetailViewHolder {
        val binding =
            ItemGoodsNewDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CountNewDetailViewHolder(binding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: CountNewDetailViewHolder, position: Int) {
        holder.binding(position)
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    // -------------------------------------------
    fun setList(list: MutableList<CountNewDetailViewModel>?) {
        if (dataList == null) {
            dataList = ArrayList()
        } else {
            dataList!!.clear()
        }

        originalList.clear()

        if (list != null) {
            dataList!!.addAll(list)
            originalList.addAll(list)
        }
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        val text = query.trim()

        if (dataList == null) dataList = ArrayList()
        dataList!!.clear()

        if (text.isEmpty()) {
            dataList!!.addAll(originalList)
        } else {
            for (item in originalList) {
                if (item.title.contains(text, ignoreCase = true) == true) {
                    dataList!!.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    fun getData(): MutableList<CountNewDetailViewModel>? {
        return dataList
    }

    // -------------------------------------------
    inner class CountNewDetailViewHolder(private val binding: ItemGoodsNewDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun binding(position: Int) {
            val item = dataList?.getOrNull(position) ?: return

            binding.tvGoodsName.text = item.title
            binding.tvCountPrice.text = item.count

            Glide.with(context)
                .load(item.image)
                .placeholder(R.drawable.gift_details_image_background)
                .error(R.drawable.gift_details_image_background)
                .into(binding.ivGoodsImage)

            binding.root.setOnClickListener {
                onItemClick(item)
                }
        }
        fun setMarginLeft(view: View, left: Int) {
            val params = view.layoutParams
            if (params is RecyclerView.LayoutParams) {
                params.leftMargin = left
                view.layoutParams = params
            } else if (params is ViewGroup.MarginLayoutParams) {
                // Fallback
                params.leftMargin = left
                view.layoutParams = params
            }
        }
    }
}