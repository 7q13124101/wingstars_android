package com.wingstars.home.adapter // Hoặc package của bạn

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.home.R // Đảm bảo import R của module :home

class NewsAdapter(private val context: Context, private val dataList: List<Int>) :
    RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
    private val newsImages = listOf(
        R.drawable.img_news_01,
        R.drawable.img_news_02,
        R.drawable.img_news_03,


        )

    // 2. Danh sách Tiêu đề tương ứng (Bạn hãy sửa lại nội dung text ở đây nhé)
    private val newsTitles = listOf(
        "情感滿載！球迷美食應援大力支持台鋼雄鷹及Wing Stars", // Tương ứng img_style_01
        " Stars House 出貨&店休公告",
        "千千生日會-千堡們戰隊集合",
    )
    private val newsDates = listOf(
        "2025.10.06",
        "2025.09.23",
        "2025.09.22",
    )
    // ViewHolder giữ các view từ item_news.xml
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val newsImage: ImageView = view.findViewById(R.id.img_news)
        val newsTitle: TextView = view.findViewById(R.id.tv_news_title)
        val newsDate: TextView = view.findViewById(R.id.tv_news_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Lấy dữ liệu
        val imageIndex = position % newsImages.size
        val titleIndex = position % newsTitles.size
        val dateIndex = position % newsDates.size

        // Gán dữ liệu vào view


        // --- Bind dữ liệu thật của bạn ở đây ---
        holder.newsTitle.text = newsTitles[titleIndex]
        holder.newsDate.text = newsDates[dateIndex]
        holder.newsImage.setImageResource(newsImages[imageIndex])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}