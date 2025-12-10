package com.wingstars.user.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.user.R
data class MemberUI(
    val bgRes: Int,     // drawable background
    val iconRes: Int    // drawable icon
)

class MemberUIAdapter(
    private val memberList: List<MemberUI>
) : RecyclerView.Adapter<MemberUIAdapter.MemberUIViewHolder>() {

    private var selectedPosition = -1 // chỉ chọn 1

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

        holder.imgBg.setImageResource(member.bgRes)
        holder.imgIcon.setImageResource(member.iconRes)

//        // Hiệu ứng chọn
//        holder.itemView.alpha = if (position == selectedPosition) 1f else 0.5f
//
//        // Click để chọn 1 item duy nhất
//        holder.itemView.setOnClickListener {
//            val oldPos = selectedPosition
//            selectedPosition = holder.adapterPosition
//
//            if (oldPos != -1) notifyItemChanged(oldPos)
//            notifyItemChanged(selectedPosition)
//        }
    }

    override fun getItemCount(): Int = memberList.size
}
