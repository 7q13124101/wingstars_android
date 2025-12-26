package com.wingstars.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.wingstars.base.net.beans.WSMemberResponse
import com.wingstars.base.utils.DPUtils
import com.wingstars.home.databinding.ItemRankingCardBinding
import com.wingstars.member.bean.WSMemberRankBean

class PopularityAdapter(
    private val context: Context,
    // List hiển thị (Rank info)
    private var dataList: MutableList<WSMemberRankBean>?,
    private val listener: onPopularityRankingListener
) : RecyclerView.Adapter<PopularityAdapter.NormalItemViewHolder>() {

    // List chi tiết (Member info dùng để click)
    private var memberDetailList: List<WSMemberResponse> = ArrayList()

    // Interface trả về WSMemberResponse
    interface onPopularityRankingListener {
        fun onPopularityRankingClickItem(data: WSMemberResponse)
    }

    // Hàm cập nhật dữ liệu hiển thị (Rank)
    fun setRankList(list: MutableList<WSMemberRankBean>?) {
        this.dataList = list
        notifyDataSetChanged()
    }

    // Hàm cập nhật dữ liệu chi tiết (Member Detail)
    fun setMemberDetailList(list: List<WSMemberResponse>) {
        this.memberDetailList = list
        // Không cần notifyDataSetChanged vì UI không đổi
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding =
            ItemRankingCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NormalItemViewHolder(binding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: NormalItemViewHolder, position: Int) {
        holder.binding(position, listener)
    }

    override fun getItemCount(): Int {
        return if (dataList != null) dataList!!.size else 0
    }

    inner class NormalItemViewHolder(private val binding: ItemRankingCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun binding(position: Int, listeners: onPopularityRankingListener) {
            val params = binding.item.layoutParams as ViewGroup.MarginLayoutParams
            binding.item.layoutParams = params

            // HIỂN THỊ UI (Dùng dataList - RankBean)
            val bean = dataList!![position]
            binding.tvRankNumber.text = (position + 1).toString()
            binding.tvName.text = "${bean.number} ${bean.name}"
            binding.tvVoteCount.text = "${bean.volume}"

            Glide.with(context)
                .load("${bean.image}")
                .apply(
                    RequestOptions()
                        .transform(RoundedCorners(DPUtils.dpToPx(20f, context).toInt()))
                )
                .into(binding.imgPerson)

            // XỬ LÝ CLICK (Dùng memberDetailList - MemberResponse)
            binding.item.setOnClickListener {
                // Kiểm tra xem đã có dữ liệu chi tiết chưa và vị trí có hợp lệ không
                if (position < memberDetailList.size) {
                    listeners.onPopularityRankingClickItem(memberDetailList[position])
                }
            }
        }
    }
}