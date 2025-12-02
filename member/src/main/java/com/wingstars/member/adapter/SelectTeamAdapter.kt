package com.wingstars.member.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView

import com.wingstars.base.utils.DPUtils
import com.wingstars.member.R
import com.wingstars.member.databinding.ItemCategoryListBinding
import com.wingstars.member.viewmodel.TeamType


data class SelectTeamFunBean(
    val teamType: TeamType,
    val image: Int,
    val selImage: Int
) {
    val teamName: String
        get() {
            var name = "全部"
            when (teamType) {
                TeamType.TEAM_ALL -> name = "全部"
                TeamType.TEAM_BASEBALL -> name = "雄鷹"
                TeamType.TEAM_BASKETBALL -> name = "獵鷹"
                TeamType.TEAM_VOLLEYBALL -> name = "天鷹"
            }
            return name
        }
}

class SelectTeamAdapter     // -------------------------------------------
    (
    private val context: Context,
    private var dataList: MutableList<SelectTeamFunBean>?,
    private val listener: OnItemListener
) : RecyclerView.Adapter<SelectTeamAdapter.NormalItemViewHolder>() {
    var selPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding =
            ItemCategoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NormalItemViewHolder(binding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // -------------------------------------------
    override fun onBindViewHolder(holder: NormalItemViewHolder, position: Int) {
        holder.binding(position)
    }

    // -------------------------------------------
    override fun getItemCount(): Int {
        return if (dataList != null) dataList!!.size else 0
    }

    // -------------------------------------------
    fun setList(list: MutableList<SelectTeamFunBean>?) {
        dataList = if (dataList == null) {
            ArrayList()
        } else {
            dataList == null
            ArrayList()
        }
        dataList!!.addAll(list!!)
        notifyDataSetChanged()
    }


    fun getData(): MutableList<SelectTeamFunBean>? {
        if (dataList == null) {
            return null
        }
        return dataList
    }


    // -------------------------------------------
    inner class NormalItemViewHolder(private val binding: ItemCategoryListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun binding(position: Int) {
            var data = dataList!![position]
            binding.name.text = data.teamName
            if (position == selPosition) {
                binding.name.setTextColor(context.getColor(R.color.color_E2518D))
                binding.image.setImageResource(data.selImage)
                binding.item.apply {
                    setStrokeColor(context.getColor(R.color.color_E2518D))
                    setStrokeWidth(DPUtils.dpToPx(1f, context))
                    setLayoutBackground(context.getColor(R.color.white))
                }
            } else {
                binding.name.setTextColor(context.getColor(R.color.color_101828))
                binding.image.setImageResource(data.image)
                binding.item.apply {
                    setStrokeWidth(0f)
                    setLayoutBackground(context.getColor(R.color.color_F3F4F6))
                }
            }
            binding.item.setOnClickListener {
                //Log.e("binding.item", "pos=$selPosition  position=$position")
                if (selPosition != position) {
                    selPosition = position
                    notifyDataSetChanged()
                    listener.onItemClick(data, position)
                }
            }
        }


        fun onBind(position: Int) {

        }

        fun setMarginLeft(view: View, left: Int) {
            var params = view.layoutParams as LinearLayout.LayoutParams
            params.leftMargin = left
            view.layoutParams = params
        }
    }

    interface OnItemListener {
        fun onItemClick(data: SelectTeamFunBean, position: Int)
    }
}