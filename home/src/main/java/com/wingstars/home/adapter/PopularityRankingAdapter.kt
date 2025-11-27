package com.wingstars.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.home.R

// Bạn có thể tạo một data class riêng để chứa thông tin đầy đủ (Ảnh, Tên, Vote)
// Tạm thời mình dùng List<Int> như cũ để demo logic
class PopularityRankingAdapter(private val context: Context, private val dataList: List<Int>) :
    RecyclerView.Adapter<PopularityRankingAdapter.ViewHolder>() {

    // Danh sách tên giả định (để khớp với ảnh demo)
    private val memberNames = listOf("安芝儇", "一粒", "朴旻曙", " 瑈0", "恬魚")

    // Danh sách số vote giả định
    private val voteCounts = listOf("1221", "1200", "1989", "1890", "1795")

    // Danh sách ảnh thành viên (Placeholder)
    // Bạn nên thay bằng ảnh thật của các thành viên đã tách nền
    private val memberImages = listOf(
        R.drawable.img_card_an_zhi_xuan,
        R.drawable.img_card_yi_li,
        R.drawable.img_card_pu_min_shu,
        R.drawable.img_card_xin_0,
        R.drawable.img_card_tian_yu
    )

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Ánh xạ các view từ layout item_ranking_card.xml
        val imgBackground: ImageView = view.findViewById(R.id.imgBackground)
        val imgPerson: ImageView = view.findViewById(R.id.imgPerson)
        val tvRankNumber: TextView = view.findViewById(R.id.tvRankNumber)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvVoteCount: TextView = view.findViewById(R.id.tvVoteCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // SỬA: Dùng layout mới item_ranking_card
        val view = LayoutInflater.from(context).inflate(R.layout.item_ranking_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 1. Set số thứ tự (Ranking)
        // Position bắt đầu từ 0, nên rank = position + 1
        holder.tvRankNumber.text = (position + 1).toString()

        // 2. Set Tên thành viên
        if (position < memberNames.size) {
            holder.tvName.text = memberNames[position]
        } else {
            holder.tvName.text = "Member ${position + 1}"
        }

        // 3. Set Số lượt vote
        if (position < voteCounts.size) {
            holder.tvVoteCount.text = voteCounts[position]
        } else {
            holder.tvVoteCount.text = "0"
        }

        // 4. Set Ảnh thành viên
        // Lưu ý: Ảnh này nên là ảnh PNG nền trong suốt để đè lên background đẹp nhất
        if (position < memberImages.size) {
            holder.imgPerson.setImageResource(memberImages[position])
        }

        // 5. Set Background (Nếu muốn đổi màu nền theo thứ hạng)
        // Hiện tại XML đang set cứng 1 ảnh nền gradient.
        // Nếu muốn hạng 1 màu vàng, hạng 2 màu bạc... bạn có thể xử lý ở đây:
        /*
        when (position) {
            0 -> holder.imgBackground.setImageResource(R.drawable.bg_rank_gold)
            1 -> holder.imgBackground.setImageResource(R.drawable.bg_rank_silver)
            else -> holder.imgBackground.setImageResource(R.drawable.bg_rank_normal)
        }
        */
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}