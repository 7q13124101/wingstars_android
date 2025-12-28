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
import com.google.gson.Gson
import com.wingstars.base.net.beans.WSFashionDetailResponse
import com.wingstars.base.net.beans.WSFashionDetailResponse.Acf.Recommend
import com.wingstars.base.utils.DPUtils
import com.wingstars.member.R
import com.wingstars.member.databinding.ItemSmallCommoditysListBinding


class SmallCommodityAdapter     // -------------------------------------------
    (
    private val context: Context,
    private var dataList: MutableList<Recommend>?
) : RecyclerView.Adapter<SmallCommodityAdapter.NormalItemViewHolder>() {
    private var pos = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding =
            ItemSmallCommoditysListBinding.inflate(
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
    fun setList(list: MutableList<Recommend>?) {
        dataList = if (dataList == null) {
            ArrayList()
        } else {
            dataList == null
            ArrayList()
        }
        dataList!!.addAll(list!!)
        notifyDataSetChanged()
    }


    fun getData(): MutableList<Recommend>? {
        if (dataList == null) {
            return null
        }
        return dataList
    }


    // -------------------------------------------
    inner class NormalItemViewHolder(private val binding: ItemSmallCommoditysListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun binding(position: Int) {
            val recommend = dataList!![position]
            var product_image_url_fullF = ""
            val productImage = recommend.product_image
            if (productImage is String) {
                product_image_url_fullF = "$productImage"
            } else if (productImage is Boolean) {
                product_image_url_fullF = ""
            } else {
                try {
                    val fromJson = Gson().fromJson(
                        Gson().toJson(productImage),
                        WSFashionDetailResponse.ProductImage::class.java
                    )
                    val sizes = fromJson.sizes
                    if (sizes != null) {
                        product_image_url_fullF = "${sizes.`1536x1536`}"
                    }

                } catch (e: Exception) {

                }
            }
            Glide.with(context).load(product_image_url_fullF)  //R.mipmap.ic_demo3
                .transform(CircleCrop())
                .into(binding.image)
            binding.title.text = recommend.product_titleF
            binding.item.visibility =
                if (recommend.isTitleAndImageThumbnailEmpty) View.GONE else View.VISIBLE
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