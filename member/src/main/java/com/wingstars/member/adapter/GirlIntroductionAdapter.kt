package com.wingstars.member.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.wingstars.base.net.beans.WSMemberResponse
import com.wingstars.base.utils.DPUtils
import com.wingstars.member.R
import com.wingstars.member.databinding.ItemGirlIntoductionListBinding
import com.wingstars.member.view.TopRoundedCornersTransformation


class GirlIntroductionAdapter     // -------------------------------------------
    (
    private val context: Context,
    private var dataList: MutableList<WSMemberResponse>?,
    private val listener: onItemListener
) : RecyclerView.Adapter<GirlIntroductionAdapter.NormalItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding =
            ItemGirlIntoductionListBinding.inflate(
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
        holder.binding(position, listener)
    }

    // -------------------------------------------
    override fun getItemCount(): Int {
        return if (dataList != null) dataList!!.size else 0
    }

    // -------------------------------------------
    fun setList(list: MutableList<WSMemberResponse>?) {
        dataList = if (dataList == null) {
            ArrayList()
        } else {
            dataList == null
            ArrayList()
        }
        dataList!!.addAll(list!!)
        notifyDataSetChanged()
    }


    fun getData(): MutableList<WSMemberResponse>? {
        if (dataList == null) {
            return null
        }
        return dataList
    }


    // -------------------------------------------
    inner class NormalItemViewHolder(private val binding: ItemGirlIntoductionListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun binding(position: Int, listener: onItemListener) {
            var data = dataList!![position]
            if (position == 0) {
                setMarginLeft(binding.item, DPUtils.dpToPx(20f, context).toInt())
            } else {
                setMarginLeft(binding.item, 0)
            }
          /*  Glide.with(context)
                .load(R.mipmap.ic_member_page_background)
                .transform(
                    TopRoundedCornersTransformation(
                        DPUtils.dpToPx(
                            20f,
                            context
                        )
                    )
                ) // 核心：应用自定义变换
                .into(binding.image)*/

            Glide.with(binding.image.context).clear(binding.image)
            //Log.e("data.urlF","data.urlF=${data.urlF}")
            if (data.urlF.isNotEmpty()) {
                Glide.with(context)
                    .load(data.urlF)
                    .error(R.mipmap.ic_member_page_background)
                    .transform(
                        TopRoundedCornersTransformation(
                            DPUtils.dpToPx(
                                20f,
                                context
                            )
                        )
                    ) // 核心：应用自定义变换
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .dontAnimate()
                    .listener(object: RequestListener<Drawable>{
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            //Log.e("Glide","onLoadFailed=${e?.message}")
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            //Log.e("Glide","onResourceReady")
                            return false
                        }

                    })
                    .into(binding.image)

            } else {
                Glide.with(context)
                    .load(R.mipmap.ic_member_page_background)
                    .transform(
                        TopRoundedCornersTransformation(
                            DPUtils.dpToPx(
                                20f,
                                context
                            )
                        )
                    ) // 核心：应用自定义变换
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .dontAnimate()
                    .into(binding.image)
            }

            binding.tvName.text = data.titleF
            binding.nums.text = data.acf.number

            binding.item.setOnClickListener {
                listener.onItemClick(data, position)
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


    interface onItemListener {
        fun onItemClick(data: WSMemberResponse, position: Int)
    }

}