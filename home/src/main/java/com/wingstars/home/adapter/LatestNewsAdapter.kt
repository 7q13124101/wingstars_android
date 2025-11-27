package com.wingstars.home.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.home.R
import com.wingstars.home.databinding.ItemNewsBinding // Đảm bảo ViewBinding được bật
import java.io.Serializable

data class NewsData(
    val title: String,
    val date: String,
    val imageUrl: Int // Tạm thời dùng Int (Resource ID) để test ảnh local
) : Serializable
class LatestNewsAdapter(
    private val context: Context,
    private var dataList: MutableList<NewsData>
) : RecyclerView.Adapter<LatestNewsAdapter.ViewHolder>() {

    // Hàm cập nhật dữ liệu
    fun setList(list: List<NewsData>?) {
        if (list == null) return
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NewsData) {
            binding.tvNewsTitle.text = item.title
            binding.tvNewsDate.text = item.date

            // Load ảnh (Dùng thư viện như Glide/Coil nếu load từ URL)
            // Ở đây dùng ảnh local resource
            binding.imgNews.setImageResource(item.imageUrl)

            // Xử lý click (nếu cần)
            binding.root.setOnClickListener {
                val intent =
                    Intent(context, com.wingstars.home.activity.LatestNewsDetailActivity::class.java)

                // Truyền dữ liệu sang màn hình chi tiết (nếu cần)
                intent.putExtra("NEWS_DATA", item)

                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size
}