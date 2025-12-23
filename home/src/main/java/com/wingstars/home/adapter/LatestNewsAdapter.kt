package com.wingstars.home.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.wingstars.base.net.beans.WSPostResponse
import com.wingstars.home.databinding.ItemNewsBinding // Đảm bảo ViewBinding được bật
import java.nio.charset.StandardCharsets
class LatestNewsAdapter(
    private val context: Context,
    private var dataList: MutableList<WSPostResponse>?,
    private val listener: onItemListener
) : RecyclerView.Adapter<LatestNewsAdapter.NormalItemViewHolder>() {
    private val pctEncoded = Regex("%[0-9a-fA-F]{2}")
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

    fun String.encodeBlobLikeUrl(): String {
        return try {
            val u = Uri.parse(this)

            // Nếu URI không có scheme/host thì trả về nguyên
            if (u.scheme.isNullOrEmpty() || u.authority.isNullOrEmpty()) return this

            val encodedPath = u.pathSegments.joinToString("/") { seg -> encodePathSegment(seg) }

            u.buildUpon()
                .encodedPath(encodedPath)             // giữ nguyên '/'
                .encodedQuery(u.encodedQuery)         // giữ nguyên query hiện có
                .encodedFragment(u.encodedFragment)   // giữ nguyên fragment nếu có
                .build()
                .toString()
        } catch (_: Exception) {
            this
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        binding.lifecycleOwner = parent.context as LifecycleOwner
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
    fun setList(list: MutableList<WSPostResponse>?) {
        if (dataList == null) dataList = ArrayList()             // <-- gán đúng
        else dataList!!.clear()                                   // <-- clear cũ
        if (list != null) {
            dataList!!.addAll(list)
            notifyDataSetChanged()
        }
    }



    fun getData(): MutableList<WSPostResponse>? {
        if (dataList == null) {
            return null
        }
        return dataList
    }


    // -------------------------------------------
    inner class NormalItemViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun binding(position: Int, listener: onItemListener) {
            var data = dataList!![position]
            val rawUrl = data.urlF

            Log.d("url img", rawUrl.encodeBlobLikeUrl())

            Glide.with(binding.imgNews.context).clear(binding.imgNews)
            if (!data.urlF.isNullOrEmpty()) {
                val encodedUrl = rawUrl.encodeBlobLikeUrl()

                Glide.with(binding.imgNews)
                    .load(encodedUrl)
                    //  .centerCrop() // 裁剪以适应ImageView大小
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .dontAnimate()
                    .format(DecodeFormat.PREFER_RGB_565)
                    .apply(RequestOptions().disallowHardwareConfig())
//                    .error(R.drawable.ic_06)
//                    .placeholder(R.drawable.ic_00)
                    .into(binding.imgNews)

            } else {
//                Glide.with(BaseApplication.context!!)
//                    .load(R.drawable.ic_hot_product1)
//                    .fitCenter()
//                    //.centerCrop()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .skipMemoryCache(false)
//                    .dontAnimate()
//                    .into(binding.ivNewsImage)
            }
            binding.tvNewsTitle.text = data.titleF
            binding.tvNewsDate.text = data.dateF
            val commonClickListener = android.view.View.OnClickListener {
                listener.onItemClick(data, position)
            }
            binding.llNewsRoot.setOnClickListener (commonClickListener)

            binding.shadowImg.setOnClickListener (commonClickListener)
            binding.imgNews.setOnClickListener (commonClickListener)
        }

        fun onBind(position: Int) {
        }
    }

    interface onItemListener {
        fun onItemClick(data: WSPostResponse, position: Int)
    }
}