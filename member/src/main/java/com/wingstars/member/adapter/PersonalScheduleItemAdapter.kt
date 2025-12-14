package com.wingstars.member.adapter

import android.content.Context
import android.view.LayoutInflater

import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.wingstars.member.R
import com.wingstars.member.databinding.ItemMonthPersonalScheduleBinding


data class ScheduleFunBean(
    var date: String,
    var teamName: String
)


class PersonalScheduleItemAdapter
    (
    private val context: Context,
    private var dataList: MutableList<ScheduleFunBean>?

) : RecyclerView.Adapter<PersonalScheduleItemAdapter.NormalItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding = ItemMonthPersonalScheduleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
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
    fun setList(list: MutableList<ScheduleFunBean>?) {
        dataList = if (dataList == null) {
            ArrayList()
        } else {
            dataList == null
            ArrayList()
        }
        if (list != null) {
            dataList!!.addAll(list)
            notifyDataSetChanged()
        }
    }


    fun getData(): MutableList<ScheduleFunBean>? {
        if (dataList == null) {
            return null
        }
        return dataList
    }


    // -------------------------------------------
    inner class NormalItemViewHolder(private val binding: ItemMonthPersonalScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun binding(position: Int) {
            var data = dataList!![position]

            if (position % 2 != 0) {
                binding.tvMonthWeekly.setBackgroundColor(context.getColor(R.color.color_F9FAFB))
                binding.tvTeamName.setBackgroundColor(context.getColor(R.color.color_F9FAFB))
            } else {
                binding.tvMonthWeekly.setBackgroundColor(context.getColor(R.color.white))
                binding.tvTeamName.setBackgroundColor(context.getColor(R.color.white))
            }

            binding.tvMonthWeekly.text = data.date
            binding.tvTeamName.text = data.teamName
        }

        fun onBind(position: Int) {
        }
    }
}