package com.wingstars.user.cheer

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
    private val onSelect: (MemberInfo) -> Unit,
    initialSelectedName: String? = null,
) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    private var selectedPosition = memberList.indexOfFirst { it.name == initialSelectedName }

    class MemberViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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

        holder.txtNumber.text = member.number
        holder.txtName.text = member.name

        // Hiển thị tick nếu item đang được chọn
        holder.imgTick.visibility = if (position == selectedPosition) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener

            val oldPos = selectedPosition
            selectedPosition = pos

            if (oldPos != -1) notifyItemChanged(oldPos)
            notifyItemChanged(selectedPosition)

            onSelect(memberList[pos])
        }
    }

    override fun getItemCount(): Int = memberList.size
}

