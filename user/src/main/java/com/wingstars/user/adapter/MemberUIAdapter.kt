package com.wingstars.user.adapter

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.wingstars.base.net.NetBase.WINGSTARS_ACCOUNT_ENC
import com.wingstars.base.net.NetBase.WINGSTARS_PASSWORD_ENC
import com.wingstars.user.R
import com.wingstars.user.activity.ChooseMemberActivity

data class MemberUI(
    val memberId: String,
    val memberName: String,
    val iconImageUrl: String
)

class MemberUIAdapter(
    private val memberList: List<MemberUI>,
    activity: ChooseMemberActivity,
) : RecyclerView.Adapter<MemberUIAdapter.MemberUIViewHolder>() {
    class MemberUIViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgBg: ImageView = view.findViewById(R.id.img_bg_member)
        val imgIcon: ImageView = view.findViewById(R.id.img_member)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberUIViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_member, parent, false)
        return MemberUIViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberUIViewHolder, position: Int) {
        val member = memberList[position]
        holder.imgIcon?.let { iconView ->
            if(!member.iconImageUrl.isNullOrEmpty()){
                loadImageWithAuth(member.iconImageUrl,iconView)
                iconView.visibility = View.VISIBLE
            }else{
                iconView.visibility = View.GONE
            }
        }
        holder.imgBg.setImageResource(R.drawable.bg_image_member)
    }

    override fun getItemCount(): Int = memberList.size
    fun loadImageWithAuth(url: String, imageView: ImageView) {
        val encodedUrl = encodeUrl(url)
        val authHeader = "Basic " + android.util.Base64.encodeToString(
            "${WINGSTARS_ACCOUNT_ENC}:${WINGSTARS_PASSWORD_ENC}".toByteArray(),
            android.util.Base64.NO_WRAP
        )
        val glideUrl = GlideUrl(
            encodedUrl, LazyHeaders.Builder()
                .addHeader("Authorization", authHeader)
                .build()
        )
//        Log.d("GLIDE_MEMBER", "Attempting to load image with Auth: $encodedUrl")
        Glide.with(imageView.context)
            .load(glideUrl)
            .placeholder(R.drawable.bg_rectangle_white)
            .error(R.drawable.bg_rectangle_white)
            .listener(object : com.bumptech.glide.request.RequestListener<Drawable> {

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
//                    Log.e("GLIDE_MEMBER1", "Load FAILED for: $encodedUrl", e)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: com.bumptech.glide.request.target.Target<Drawable?>?,
                    dataSource: com.bumptech.glide.load.DataSource,
                    isFirstResource: Boolean
                ): Boolean {
//                    Log.d("GLIDE_MEMBER1", "Load SUCCESS for: $encodedUrl. Source: $dataSource")
                    return false
                }
            })
            .into(imageView)
    }
    private fun encodeUrl(url: String): String {
        val lastSlash = url.lastIndexOf('/')
        if (lastSlash == -1) return url
        val base = url.substring(0, lastSlash + 1)
        val fileName = url.substring(lastSlash + 1)
        val encodedFileName = Uri.encode(fileName)
        return base + encodedFileName
    }
}
