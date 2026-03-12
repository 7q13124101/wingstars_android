package com.wingstars.member.adapter
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.base.net.beans.WSScheduleResponse
import com.wingstars.member.R
import com.wingstars.member.databinding.ItemMonthPersonalScheduleBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class PersonalScheduleItemAdapter
    (
    private val context: Context,
    private var dataList: MutableList<WSScheduleResponse>?

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
    fun setList(list: MutableList<WSScheduleResponse>?) {
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


    fun getData(): MutableList<WSScheduleResponse>? {
        if (dataList == null) {
            return null
        }
        return dataList
    }


    // -------------------------------------------
    inner class NormalItemViewHolder(private val binding: ItemMonthPersonalScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun binding(position: Int) {
            val data = dataList!![position]

            if (position % 2 != 0) {
                binding.tvMonthWeekly.setBackgroundColor(context.getColor(R.color.color_F9FAFB))
                binding.llTeamName.setBackgroundColor(context.getColor(R.color.color_F9FAFB))
            } else {
                binding.tvMonthWeekly.setBackgroundColor(context.getColor(R.color.white))
                binding.llTeamName.setBackgroundColor(context.getColor(R.color.white))
            }

//            val dateFormatWeek = SimpleDateFormat("MM/dd", Locale.TAIWAN)
//            var dateFormatWeek = SimpleDateFormat("M/d(E)", Locale.TAIWAN)
            var dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val dateUtil = dateFormat.parse(data.work_date)
            binding.tvMonthWeekly.text = formatTaiwanDate(dateUtil)
            binding.tvTeamName.text = data.location
//            Log.d("data ngay thang nam: ", data.work_date)
//            binding.tvTeamName.text = "Halo"
        }

        fun onBind(position: Int) {
        }
    }

    fun formatTaiwanDate(date: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = date

        val dateFormat = SimpleDateFormat("MM/dd", Locale.TAIWAN)

        val weekNumber = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "一"
            Calendar.TUESDAY -> "二"
            Calendar.WEDNESDAY -> "三"
            Calendar.THURSDAY -> "四"
            Calendar.FRIDAY -> "五"
            Calendar.SATURDAY -> "六"
            Calendar.SUNDAY -> "日"
            else -> ""
        }

        return "${dateFormat.format(date)}($weekNumber)"
    }
}