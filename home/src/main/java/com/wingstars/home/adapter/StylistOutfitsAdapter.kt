package com.wingstars.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.home.R
import com.wingstars.home.databinding.ItemStyleBinding

data class StyleOutfitsData(
    val tittle: String,
    val isClick: Boolean,
) : java.io.Serializable

class StylistOutfitsAdapter(
    private val context: Context,
    private var dataList: MutableList<StyleOutfitsData>,
    private val listener: OnItemListener
) : RecyclerView.Adapter<StylistOutfitsAdapter.NormalItemViewHolder>() {

    // 1. Danh sách ảnh Style (từ 01 đến 06)
    // Đảm bảo bạn có các ảnh này trong res/drawable, nếu chưa có hãy đổi về placeholder_image tạm
    private val styleImages = listOf(
        R.drawable.img_style_01,
        R.drawable.img_style_02,
        R.drawable.img_style_03,
        R.drawable.img_style_04,
        R.drawable.img_style_05,
        R.drawable.img_style_06
    )

    // 2. Danh sách Tiêu đề
    private val styleTitles = listOf(
        "台鋼雄鷹主場球衣",
        "台鋼雄鷹應援",
        "煞猛拼！好客棒球日",
        "煞猛拼！好客棒球日",
        "鷹TAINAN臺南400紀...",
        "鷹世界冒險法批"
    )


    // ViewHolder dùng ViewBinding
    inner class NormalItemViewHolder(private val binding: ItemStyleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun binding(position: Int) {
            // Kiểm tra an toàn để tránh crash index out of bounds
            if (dataList.isEmpty()) return
            val data = dataList[position]

            // --- LOGIC QUAN TRỌNG: Hiển thị ảnh xoay vòng ---
            if (styleImages.isNotEmpty()) {
                val imageIndex = position % styleImages.size
                binding.imageStylist.setImageResource(styleImages[imageIndex])
            }

            // --- LOGIC HIỂN THỊ TIÊU ĐỀ ---
            // Cách 1: Lấy từ dataList (nếu bạn muốn dùng dữ liệu truyền vào)
            // binding.tittleStylist.text = data.tittle

            // Cách 2: Lấy từ danh sách cứng styleTitles (để khớp với ảnh) -> Tôi dùng cách này cho bạn
            if (styleTitles.isNotEmpty()) {
                val titleIndex = position % styleTitles.size
                binding.tittleStylist.text = styleTitles[titleIndex]
            }

            binding.root.setOnClickListener {
                listener.onItemClick(data, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding = ItemStyleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NormalItemViewHolder(binding)
    }

    // --- SỬA LỖI Ở ĐÂY: Sửa lại tên class Holder và thêm override ---
    override fun onBindViewHolder(holder: NormalItemViewHolder, position: Int) {
        holder.binding(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface OnItemListener {
        fun onItemClick(data: StyleOutfitsData, position: Int)
    }
}