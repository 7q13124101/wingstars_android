package com.wingstars.home.adapter // Hoặc package của bạn

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.home.R // Đảm bảo import R của module :home

// Adapter này nhận List<Int> từ HomeViewModel
class ScheduleAdapter(private val context: Context, private val dataList: List<Int>) :
    RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    // ViewHolder giữ các view từ item_schedule.xml
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Bạn hãy đảm bảo ID của các view này khớp với file item_schedule.xml
        val scheduleImage: ImageView = view.findViewById(R.id.imgSchedule)
        val scheduleTitle: TextView = view.findViewById(R.id.tvScheduleTitle)
        val scheduleDate: TextView = view.findViewById(R.id.tvScheduleDate)
        val scheduleLocation: TextView = view.findViewById(R.id.tvScheduleLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Dùng layout item_schedule.xml
        val view = LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Lấy dữ liệu (hiện tại chỉ là số Int)
        val item = dataList[position]

        // --- Bind dữ liệu thật của bạn ở đây ---
        holder.scheduleTitle.text = "Tiêu đề lịch trình $item"
        holder.scheduleDate.text = "Ngày: 2025.11.1$position"
        holder.scheduleLocation.text = "Địa điểm: ..."

        // Gán ảnh placeholder
        holder.scheduleImage.setImageResource(R.drawable.placeholder_person)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}