package com.wingstars.home.adapter // Hoặc package của bạn

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.home.R // Đảm bảo import R của module :home

class ArticleAdapter(private val context: Context, private val dataList: List<Int>) :
    RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {
    private val styleImages = listOf(
        R.drawable.img_yt_01,
        R.drawable.img_yt_02,
        R.drawable.img_yt_03,
        R.drawable.img_yt_04,

    )

    // 2. Danh sách Tiêu đề tương ứng (Bạn hãy sửa lại nội dung text ở đây nhé)
    private val styleTitles = listOf(
        "Wing Stars - 恬魚生日會 活動花絮", // Tương ứng img_style_01
        "Wing Stars - \uD83C\uDF1F 夢幻聯動\uD83C\uDF1F 女孩們與街舞天團的國際交流",
        "Wing Stars - 小安 X Mingo Stars House 一日店長華麗 ...",
        "Wing Stars - 玩客瘋探班女孩拍攝!!",
    )

    // ViewHolder giữ các view từ item_article.xml
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val articleImage: ImageView = view.findViewById(R.id.img_article)
        val articleTitle: TextView = view.findViewById(R.id.tv_article_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Lấy dữ liệu
        val imageIndex = position % styleImages.size
        val titleIndex = position % styleTitles.size
        // --- Bind dữ liệu thật của bạn ở đây ---
        holder.articleTitle.text = styleTitles[titleIndex]
        holder.articleImage.setImageResource(styleImages[imageIndex])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}