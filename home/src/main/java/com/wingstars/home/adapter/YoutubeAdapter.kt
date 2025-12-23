package com.wingstars.home.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.wingstars.base.net.beans.YoutubeUiData
import com.wingstars.home.databinding.ItemArticleBinding

class YoutubeAdapter(
    private val context: Context
) : RecyclerView.Adapter<YoutubeAdapter.NormalItemViewHolder>() {

    // 1. Dùng List thường, không dùng LiveData trong Adapter
    private var dataList: List<YoutubeUiData> = ArrayList()

    // Hàm để Fragment cập nhật dữ liệu mới vào đây
    fun setList(newList: List<YoutubeUiData>) {
        this.dataList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NormalItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NormalItemViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class NormalItemViewHolder(private val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: YoutubeUiData) {
            // Set Title
            binding.tvArticleTitle.text = item.title

            // Set Date
            binding.tvArticleDate.text = item.date

            Glide.with(context)
                .load(item.imageUrl)
                .apply(RequestOptions().transform(RoundedCorners(20)))
                .into(binding.imgArticle)

            binding.item.setOnClickListener {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.videoUrl))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}