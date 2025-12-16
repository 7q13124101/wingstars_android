package com.wingstars.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.wingstars.base.net.beans.WSProductResponse
import com.wingstars.home.R
import com.wingstars.home.databinding.ItemProductBinding

class ProductAdapter(
    private val context: Context,
    private var dataList: MutableList<WSProductResponse>?,
    private val listener: OnItemListener
) : RecyclerView.Adapter<ProductAdapter.NormalItemViewHolder>() {

    interface OnItemListener {
        fun onItemClick(data: WSProductResponse, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NormalItemViewHolder(binding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: NormalItemViewHolder, position: Int) {
        holder.binding(position)
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    fun setList(list: MutableList<WSProductResponse>?) {
        dataList = list ?: ArrayList()
        notifyDataSetChanged()
    }

    inner class NormalItemViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun binding(position: Int) {
            // Check null an toàn
            if (dataList == null || position >= dataList!!.size) return

            val data = dataList!![position]

            // Load ảnh
            Glide.with(binding.imgProduct.context).clear(binding.imgProduct)

            val imageUrl = data.imageF
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(binding.imgProduct.context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.img_product_01)
                    .error(R.drawable.img_product_01)
                    .into(binding.imgProduct)
            } else {
                binding.imgProduct.setImageResource(R.drawable.img_product_01)
            }

            binding.tvProductName.text = data.name
            binding.tvProductPrice.text = "$" + data.price

            binding.root.setOnClickListener {
                listener.onItemClick(data, position)
            }
        }
    }
}