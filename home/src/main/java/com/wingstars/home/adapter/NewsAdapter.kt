package com.wingstars.home.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.wingstars.base.net.beans.WSPostResponse
import com.wingstars.home.R
import com.wingstars.home.databinding.ItemNewsBinding
import java.nio.charset.StandardCharsets

class NewsAdapter(
    private val context: Context,
    private var dataList: MutableList<WSPostResponse>, // Đổi sang var để có thể gán lại danh sách
    private val listener: OnItemListener
) : RecyclerView.Adapter<NewsAdapter.NormalItemViewHolder>() {

    private val pctEncoded = Regex("%[0-9a-fA-F]{2}")

    interface OnItemListener {
        fun onItemClick(data: WSPostResponse, position: Int)
    }
    fun setList(newList: List<WSPostResponse>) {
        this.dataList.clear()
        this.dataList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NormalItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NormalItemViewHolder, position: Int) {
        holder.binding(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    // --- Các hàm xử lý chuỗi ---
    private fun encodePathSegment(seg: String): String {
        val sb = StringBuilder(seg.length * 3)
        var i = 0
        while (i < seg.length) {
            val ch = seg[i]
            if (ch == '%' && i + 2 < seg.length &&
                seg[i + 1].isLetterOrDigit() && seg[i + 2].isLetterOrDigit() &&
                pctEncoded.matches(seg.substring(i, i + 3))) {
                sb.append(seg, i, i + 3)
                i += 3
                continue
            }
            val isUnreserved = (ch in 'A'..'Z') || (ch in 'a'..'z') || (ch in '0'..'9') ||
                    ch == '-' || ch == '.' || ch == '_' || ch == '~'

            if (isUnreserved) {
                sb.append(ch)
                i++
            } else {
                val bytes = ch.toString().toByteArray(StandardCharsets.UTF_8)
                for (b in bytes) {
                    val v = b.toInt() and 0xFF
                    sb.append('%')
                    val hi = "0123456789ABCDEF"[v ushr 4]
                    val lo = "0123456789ABCDEF"[v and 0x0F]
                    sb.append(hi).append(lo)
                }
                i++
            }
        }
        return sb.toString()
    }

    fun String.encodeBlobLikeUrl(): String {
        return try {
            val u = Uri.parse(this)
            if (u.scheme.isNullOrEmpty() || u.authority.isNullOrEmpty()) return this
            val encodedPath = u.pathSegments.joinToString("/") { seg -> encodePathSegment(seg) }
            u.buildUpon()
                .encodedPath(encodedPath)
                .encodedQuery(u.encodedQuery)
                .encodedFragment(u.encodedFragment)
                .build()
                .toString()
        } catch (_: Exception) {
            this
        }
    }

    inner class NormalItemViewHolder(val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun binding(position: Int) {
            val data = dataList[position]
            val rawUrl = data.urlF

            Glide.with(binding.imgNews.context).clear(binding.imgNews)

            if (!rawUrl.isNullOrEmpty()) {
                val encodedUrl = rawUrl.encodeBlobLikeUrl()

                Glide.with(binding.imgNews)
                    .load(encodedUrl)
                    .placeholder(R.drawable.ws_logo) // Thêm ảnh chờ
                    .error(R.drawable.ws_logo)       // Thêm ảnh khi lỗi
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .dontAnimate()
                    .format(DecodeFormat.PREFER_RGB_565)
                    .apply(RequestOptions().disallowHardwareConfig())
                    .into(binding.imgNews)
            } else {
                // Xử lý khi không có URL ảnh: Hiện ảnh mặc định
                binding.imgNews.setImageResource(R.drawable.ws_logo)
            }

            binding.tvNewsTitle.text = data.titleF
            binding.tvNewsDate.text = data.dateF

            val commonClickListener = android.view.View.OnClickListener {
                listener.onItemClick(data, position)
            }

            binding.llNewsRoot.setOnClickListener(commonClickListener)
            binding.shadowImg.setOnClickListener(commonClickListener)
            binding.imgNews.setOnClickListener(commonClickListener)
        }
    }
}
