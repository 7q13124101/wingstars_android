package com.wingstars.home.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.wingstars.base.net.beans.IteneraryResponse
import com.wingstars.home.R
import com.youth.banner.adapter.BannerAdapter
import java.nio.charset.StandardCharsets

// Adapter nhận List<IteneraryResponse> từ API
class ItineraryBannerAdapter(datas: List<IteneraryResponse>) :
    BannerAdapter<IteneraryResponse, ItineraryBannerAdapter.BannerViewHolder>(datas) {

    var onItemClickListener: ((IteneraryResponse) -> Unit)? = null

    // Regex để kiểm tra ký tự đã encode
    private val pctEncoded = Regex("%[0-9a-fA-F]{2}")

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        // Sử dụng layout item_today_itinerary (như bạn đã cung cấp trước đó)
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_today_itinerary, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindView(holder: BannerViewHolder, data: IteneraryResponse, position: Int, size: Int) {
        // Gọi hàm bind và truyền cả đối tượng Adapter vào để dùng hàm encodeBlobLikeUrl
        // Hoặc bạn có thể copy hàm đó vào trong ViewHolder, hoặc để static.
        // Ở đây tôi để hàm encode trong Adapter và gọi từ holder.

        holder.bind(data, this)

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(data)
        }
    }

    class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // ID lấy từ item_today_itinerary.xml
        private val imgPoster: ImageView = view.findViewById(R.id.imgPoster)
        private val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        private val tvTime: TextView = view.findViewById(R.id.tvTime)
        private val tvPlace: TextView = view.findViewById(R.id.tvPlace)

        fun bind(data: IteneraryResponse, adapter: ItineraryBannerAdapter) {
            val rawUrl = data.imageF

            // Xóa ảnh cũ để tránh lỗi hiển thị
            Glide.with(itemView.context).clear(imgPoster)

            if (rawUrl.isNotEmpty()) {
                // --- SỬ DỤNG HÀM ENCODE TỪ ADAPTER ---
                val encodedUrl = adapter.encodeBlobLikeUrl(rawUrl)

                Glide.with(itemView.context)
                    .load(encodedUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache để load nhanh hơn
                    .skipMemoryCache(false)
                    .dontAnimate()
                    .format(DecodeFormat.PREFER_RGB_565) // Giảm bộ nhớ
                    .apply(RequestOptions().disallowHardwareConfig())
//                    .placeholder(R.drawable.placeholder_person) // Ảnh chờ
                    .into(imgPoster)
            } else {
                imgPoster.setImageResource(R.drawable.placeholder_person)
            }

            // Gán Text
            tvTitle.text = data.titleF
            tvTime.text = data.dateF
            tvPlace.text = data.locationF
        }
    }

    // --- CÁC HÀM XỬ LÝ URL (Copy từ LatestNewsAdapter) ---

    fun encodeBlobLikeUrl(url: String): String {
        return try {
            val u = Uri.parse(url)

            // Nếu URI không có scheme/host thì trả về nguyên
            if (u.scheme.isNullOrEmpty() || u.authority.isNullOrEmpty()) return url

            val encodedPath = u.pathSegments.joinToString("/") { seg -> encodePathSegment(seg) }

            u.buildUpon()
                .encodedPath(encodedPath)             // giữ nguyên '/'
                .encodedQuery(u.encodedQuery)         // giữ nguyên query hiện có
                .encodedFragment(u.encodedFragment)   // giữ nguyên fragment nếu có
                .build()
                .toString()
        } catch (_: Exception) {
            url
        }
    }

    private fun encodePathSegment(seg: String): String {
        val sb = StringBuilder(seg.length * 3)
        var i = 0
        while (i < seg.length) {
            val ch = seg[i]

            // Nếu gặp chuỗi đã %HH thì giữ nguyên
            if (ch == '%' && i + 2 < seg.length &&
                seg[i + 1].isLetterOrDigit() && seg[i + 2].isLetterOrDigit() &&
                pctEncoded.matches(seg.substring(i, i + 3))) {
                sb.append(seg, i, i + 3)
                i += 3
                continue
            }

            // Unreserved theo RFC 3986: ALPHA / DIGIT / "-" / "." / "_" / "~"
            val isUnreserved = (ch in 'A'..'Z') || (ch in 'a'..'z') || (ch in '0'..'9') ||
                    ch == '-' || ch == '.' || ch == '_' || ch == '~'

            if (isUnreserved) {
                sb.append(ch)
                i++
            } else {
                // Percent-encode theo UTF-8
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
}