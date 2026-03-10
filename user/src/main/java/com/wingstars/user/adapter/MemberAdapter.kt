package com.wingstars.user.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.user.R

data class MemberInfo(
    val number: String,
    val name: String
)

class MemberAdapter(
    private val memberList: List<MemberInfo>,
    private val selectedIds: Set<String> = emptySet(),
    private val onSelect: (MemberInfo) -> Unit
) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    private val selectedIdsNormalized: Set<String> = selectedIds.map { it.trim() }.toSet()

    inner class MemberViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNumber: TextView = view.findViewById(R.id.tv_number)
        val txtName: TextView = view.findViewById(R.id.tv_name)
        val imgTick: ImageView = view.findViewById(R.id.img_tick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_member_row, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = memberList[position]

        val memberId = member.number.trim()

        holder.txtNumber.text = memberId
        holder.txtName.text = member.name

        holder.imgTick.visibility =
            if (selectedIdsNormalized.contains(memberId)) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
            onSelect(member.copy(number = memberId))
        }
    }

    override fun getItemCount(): Int = memberList.size
}

