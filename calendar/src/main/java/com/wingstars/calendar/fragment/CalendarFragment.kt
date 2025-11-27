package com.wingstars.calendar.fragment

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.GestureDetector
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarLayout
import com.haibin.calendarview.CalendarView
import com.haibin.calendarview.CalendarView.OnCalendarSelectListener
import com.haibin.calendarview.CalendarView.OnViewChangeListener
import com.wingstars.calendar.R
import com.wingstars.calendar.activity.EventDetailsActivity
import com.wingstars.calendar.databinding.FragmentCalendarBinding
import com.wingstars.calendar.viewmodel.CalendarViewModel
import java.util.Locale

class CalendarFragment : Fragment(), OnCalendarSelectListener {

    private val viewModel: CalendarViewModel by viewModels()
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var schemeMap: MutableMap<String, Calendar>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCalendar()
        initData()
        initListener()
    }

    private fun initCalendar() {
        binding.ivPrev.setOnClickListener {
            binding.calendarView.scrollToPre(true)

        }
        binding.ivNext.setOnClickListener {
            binding.calendarView.scrollToNext(true)
        }
        binding.calendarView.setMonthViewScrollable(true)
        binding.calendarView.setWeekViewScrollable(true)
        binding.calendarView.setOnCalendarSelectListener(this)

        // 月份切换时更新标题（月历模式）
        binding.calendarView.setOnMonthChangeListener { year, month ->
            // 只有当月历展开时才更新标题
            binding.tvTitleDate.text = "${year}年${month}月"
        }

    }

    /**
     * 初始化日历数据
     */
    private fun initData() {
        schemeMap = mutableMapOf<String, Calendar>().apply {
            // 1. 带图标和绿色圆点的日期（2025年10月28日）
            val calendar1 = Calendar().apply {  // 明确设置年月
                year = 2025    // 设置年
                month = 10     // 设置月（注意：1表示1月，12表示12月）
                day = 10       // 设置日
                addScheme(Calendar.Scheme().apply {
                    shcemeColor = resources.getColor(R.color.color_007A60, null)
                    scheme = "icon_birthday"
                })
            }
            put(calendar1.toString(),calendar1)

            // 2. 带红+蓝圆点的日期（2025年10月24日）
            val calendar2 = Calendar().apply {  // 明确设置年月
                year = 2025    // 设置年
                month = 11     // 设置月（注意：1表示1月，12表示12月）
                day = 30       // 设置日
                addScheme(Calendar.Scheme().apply {
                    shcemeColor = resources.getColor(R.color.color_DE9DBA, null)
                    scheme = "icon_birthday"
                })
            }
            put(calendar2.toString(),calendar2)

            // 3. 带红+蓝圆点的日期（2025年10月24日）
            val calendar3 = Calendar().apply {  // 明确设置年月
                year = 2025    // 设置年
                month = 11     // 设置月（注意：1表示1月，12表示12月）
                day = 6       // 设置日
                addScheme(Calendar.Scheme().apply {
                    shcemeColor = resources.getColor(R.color.color_DE9DBA, null)
                    scheme = ""
                })
                addScheme(Calendar.Scheme().apply {
                    shcemeColor = resources.getColor(R.color.color_007A60, null)
                    scheme = ""
                })
            }
            put(calendar3.toString(),calendar3)

            // 3. 带红+蓝圆点的日期（2025年10月24日）
            val calendar4 = Calendar().apply {  // 明确设置年月
                year = 2025    // 设置年
                month = 11     // 设置月（注意：1表示1月，12表示12月）
                day = 16       // 设置日
                addScheme(Calendar.Scheme().apply {
                    shcemeColor = resources.getColor(R.color.color_DE9DBA, null)
                    scheme = ""
                })
                addScheme(Calendar.Scheme().apply {
                    shcemeColor = resources.getColor(R.color.color_007A60, null)
                    scheme = ""
                })
            }
            put(calendar4.toString(),calendar4)


            // 3. 带红+蓝圆点的日期（2025年10月24日）
            val calendar5 = Calendar().apply {  // 明确设置年月
                year = 2025    // 设置年
                month = 11     // 设置月（注意：1表示1月，12表示12月）
                day = 18       // 设置日
                addScheme(Calendar.Scheme().apply {
                    shcemeColor = resources.getColor(R.color.color_DE9DBA, null)
                    scheme = "icon_birthday"
                })
                addScheme(Calendar.Scheme().apply {
                    shcemeColor = resources.getColor(R.color.color_007A60, null)
                    scheme = "icon_birthday"
                })
            }
            put(calendar5.toString(),calendar5)
        }
        // 3. 设置数据并滚动到当前日期
        binding.calendarView.setSchemeDate(schemeMap)
        binding.calendarView.scrollToCurrent()
        updateTitle(binding.calendarView.curYear, binding.calendarView.curMonth)

        binding.tvDateItinerary.text = "${binding.calendarView.curMonth}/${binding.calendarView.curDay}"+" 行程"
    }

    /**
     * 初始化监听器
     */
    private fun initListener() {
        // 日期选中监听
        binding.calendarView.setOnCalendarSelectListener(object : CalendarView.OnCalendarSelectListener {
            override fun onCalendarSelect(calendar: Calendar, isClick: Boolean) {
                if (isClick) {
                    binding.calendarView.isSelected=true
                    val dateStr = "${calendar.month}/${calendar.day} 行程"
                    binding.tvDateItinerary.text = dateStr
                    binding.calendarView.invalidate()
                }
            }

            override fun onCalendarOutOfRange(calendar: Calendar) {
                // 日期超出范围时触发（如点击了上个月/下个月不可选的日期）
            }
        })

        // 月份切换监听（用于更新标题）
        binding.calendarView.setOnMonthChangeListener { year, month ->
            updateTitle(year, month)
            resetSelectionOnViewChange()
        }

        binding.clEventDetails.setOnClickListener {
            val intent =Intent(this@CalendarFragment.requireActivity(), EventDetailsActivity::class.java)
            startActivity(intent)
        }
    }

    // 正确的状态重置方法：使用CalendarView的API清除选中
    private fun resetSelectionOnViewChange() {
        binding.calendarView.isSelected=false
        // 刷新数据
        binding.calendarView.setSchemeDate(schemeMap)
//        // 重绘完成后重置标记位
        binding.calendarView.invalidate()
    }
    /**
     * 更新标题栏显示当前年月
     */
    private fun updateTitle(year: Int, month: Int) {
        binding.tvTitleDate.text = "${year}年${month}月"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 移除监听器避免内存泄漏
        binding.calendarView.setOnCalendarSelectListener(null)
        binding.calendarView.setOnMonthChangeListener(null)
    }

    /**
     * 超出范围越界
     *
     * @param calendar calendar
     */
    override fun onCalendarOutOfRange(calendar: Calendar?) {
        TODO("Not yet implemented")
    }

    /**
     * 日期选择事件
     *
     * @param calendar calendar
     * @param isClick  isClick
     */
    override fun onCalendarSelect(
        calendar: Calendar?,
        isClick: Boolean
    ) {

    }
}