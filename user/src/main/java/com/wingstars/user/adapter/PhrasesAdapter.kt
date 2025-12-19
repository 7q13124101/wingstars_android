package com.wingstars.user.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.base.net.beans.PhrasesBean
import com.wingstars.user.R

class PhrasesAdapter(
    private var list: List<PhrasesBean>,
    private val mode: Int = MODE_SIMPLE,
    private val onItemClick: (PhrasesBean) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val MODE_SIMPLE = 0
        const val MODE_MEMBER = 1
        const val MODE_COLOR_CIRCLE = 2
        const val MODE_COLOR_SQUARE = 3
    }

    inner class SimpleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvContent: TextView = itemView.findViewById(R.id.tv_content)
    }

    inner class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNumber: TextView = itemView.findViewById(R.id.tv_number)
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val ivCheck: ImageView = itemView.findViewById(R.id.iv_check)
    }

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val viewColor: View = itemView.findViewById(R.id.view_color)
        val viewBorder: View = itemView.findViewById(if (mode == MODE_COLOR_CIRCLE) R.id.iv_check else R.id.view_border)
    }

    override fun getItemViewType(position: Int): Int {
        return mode
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MODE_MEMBER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_team_member, parent, false)
                MemberViewHolder(view)
            }
            MODE_COLOR_CIRCLE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_color_circle, parent, false)
                ColorViewHolder(view)
            }
            MODE_COLOR_SQUARE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_color_square, parent, false)
                ColorViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_select_phrase, parent, false)
                SimpleViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        val context = holder.itemView.context

        when (getItemViewType(position)) {
            MODE_MEMBER -> {
                val memberHolder = holder as MemberViewHolder
                memberHolder.tvName.text = item.title
                memberHolder.tvNumber.text = item.UniformNo

                if (item.isSelected) {
                    memberHolder.ivCheck.visibility = View.VISIBLE
                    memberHolder.tvName.setTextColor(ContextCompat.getColor(context, R.color.black))
                } else {
                    memberHolder.ivCheck.visibility = View.GONE
                    memberHolder.tvName.setTextColor(ContextCompat.getColor(context, R.color.color_101828))
                }
            }

            MODE_COLOR_CIRCLE, MODE_COLOR_SQUARE -> {
                val colorHolder = holder as ColorViewHolder
                val colorHex = item.title
                try {
                    val colorInt = Color.parseColor(colorHex)
                    val background = colorHolder.viewColor.background as? GradientDrawable
                    background?.setColor(colorInt) ?: colorHolder.viewColor.setBackgroundColor(colorInt)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                colorHolder.viewBorder.isSelected = item.isSelected
                colorHolder.viewBorder.visibility = if (item.isSelected || mode == MODE_COLOR_SQUARE) View.VISIBLE else View.GONE
            }

            else -> {
                val simpleHolder = holder as SimpleViewHolder
                simpleHolder.tvContent.text = item.title

                simpleHolder.tvContent.setTextColor(
                    ContextCompat.getColorStateList(context, R.color.selector_text_color)
                )
                simpleHolder.tvContent.isSelected = item.isSelected
            }
        }

        holder.itemView.setOnClickListener {
            list.forEach { it.isSelected = false }
            item.isSelected = true
            notifyDataSetChanged()
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateData(newList: List<PhrasesBean>) {
        list = newList
        notifyDataSetChanged()
    }

    fun getSelectedItem(): PhrasesBean? {
        return list.find { it.isSelected }
    }
}