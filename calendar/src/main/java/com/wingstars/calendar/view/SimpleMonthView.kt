package com.wingstars.calendar.view

import android.content.Context
import android.graphics.*
import androidx.core.content.ContextCompat
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.MonthView
import com.wingstars.calendar.R

class SimpleMonthView @JvmOverloads constructor(
    context: Context,
) : MonthView(context) {

    // 画笔初始化
    private val dotPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    private val iconPaint = Paint().apply { isAntiAlias = true }
    private val selectedBgPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.parseColor("#E2518D") // 选中背景色（粉色）
    }

    // 当前日期专用背景画笔
    private val currentDayBgPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.parseColor("#F8EBF1") // 当前日期背景色（粉色）
    }

    // 尺寸参数（dp转px）
    private val dotRadius = dipToPx(3f)  // 标记点半径
    private val dotMargin = dipToPx(6f)  // 点间距
    private val selectRadius = dipToPx(12f)  // 选中圆圈半径
    private val iconSize = dipToPx(24f)  // 图标大小
    private val textBounds = Rect()  // 存储文字边界

    private val rectCornerRadius = dipToPx(16f) // 圆角矩形的圆角半径
    private val rectWidth = dipToPx(48f) // 圆角矩形宽度
    private val rectHeight = dipToPx(48f) // 圆角矩形高度
    private var rowCount = 4

    init {
        setBackgroundColor(Color.TRANSPARENT)
        background = ContextCompat.getDrawable(context, R.drawable.calendar_bg_bottom_radius)
    }
    // 图标资源（需在res/drawable添加ic_custom.png）
    private val customIcon by lazy {
        BitmapFactory.decodeResource(resources, R.drawable.calendar_ic_birthday_cake)
    }

    // 月历默认显示6行（含星期标题），高度应为单行高度的6倍
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val singleRowHeight = dipToPx(53f)
        calculateRowCount()
        val totalHeight = singleRowHeight * rowCount // 月历6行（高度 > 月历）
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), totalHeight)
    }

    // 动态计算当前月需要显示的行数（5或6）
    private fun calculateRowCount() {
        val currentYear = mYear
        val currentMonth = mMonth

        // 1. 获取当月第一天是星期几（1=周日，2=周一，...，7=周六）
        val firstDayOfWeek = getFirstDayOfWeek(currentYear, currentMonth)
        // 2. 获取当月总天数
        val totalDays = getTotalDaysInMonth(currentYear, currentMonth)

        // 3. 计算当月第一天的偏移天数（距离每周第一天的偏移，例如：若第一天是周日（1），偏移0天）
        // 注意：若你的日历以周一为每周第一天，此处需调整计算方式
        val offsetDays = firstDayOfWeek - 1 // 周日（1）→ 0，周一（2）→1，...，周六（7）→6

        // 4. 计算当月所有日期占用的总天数（偏移天数 + 当月总天数）
        val totalOccupiedDays = offsetDays + totalDays

        // 5. 根据总占用天数计算行数（向上取整）
        rowCount = when {
            totalOccupiedDays <= 28 -> 4    // 4行（4×7=28）
            totalOccupiedDays <= 35 -> 5    // 5行（5×7=35）
            else -> 6                       // 6行（6×7=42）
        }
    }

    // 工具方法：获取当月第一天是星期几（1=周日，7=周六）
    private fun getFirstDayOfWeek(year: Int, month: Int): Int {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(year, month - 1, 1) // 月份从0开始（1月=0）
        return calendar.get(java.util.Calendar.DAY_OF_WEEK) // 1=周日，7=周六
    }

    // 工具方法：获取当月总天数
    private fun getTotalDaysInMonth(year: Int, month: Int): Int {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(year, month - 1, 1)
        return calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
    }

    /**
     * 绘制选中日期的背景圆圈
     */
    override fun onDrawSelected(
        canvas: Canvas,
        calendar: Calendar,
        x: Int,
        y: Int,
        hasScheme: Boolean
    ): Boolean {
        // 1. 关键修改：强制绘制今天日期的背景（无论当前遍历的是哪个日期）
        drawTodayBackground(canvas)

        if (isSelected(calendar) && !isToday(calendar)) {
            if(isSelected) {
                val left = x + mItemWidth / 2 - rectWidth / 2
                val top = (y + mItemHeight / 2 - rectHeight / 2) + dipToPx(4f)
                val right = left + rectWidth
                val bottom = top + rectHeight
                val rectF = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
                canvas.drawRoundRect(rectF,rectCornerRadius.toFloat(), rectCornerRadius.toFloat(),currentDayBgPaint)
            }
        }
        return true
    }

    /**
     * 判断日期是否为今天（辅助方法，用于选中背景的排除判断）
     */
    private fun isToday(calendar: Calendar): Boolean {
        val systemCalendar = java.util.Calendar.getInstance()
        val todayYear = systemCalendar.get(java.util.Calendar.YEAR)
        val todayMonth = systemCalendar.get(java.util.Calendar.MONTH) + 1
        val todayDay = systemCalendar.get(java.util.Calendar.DAY_OF_MONTH)

        return calendar.year == todayYear && calendar.month == todayMonth && calendar.day == todayDay
    }

    /**
     * 主动计算今天日期在日历中的位置并绘制背景
     */
    private fun drawTodayBackground(canvas: Canvas) {
        // 关键修改：通过系统Calendar获取今天的年、月、日（不依赖库的方法）
        val systemCalendar = java.util.Calendar.getInstance()
        val todayYear = systemCalendar.get(java.util.Calendar.YEAR)
        val todayMonth = systemCalendar.get(java.util.Calendar.MONTH) + 1 // 系统月份是0-based，转为1-based
        val todayDay = systemCalendar.get(java.util.Calendar.DAY_OF_MONTH)

        // 关键判断：今天是否属于当前显示的月份
        if (todayYear != mYear || todayMonth != mMonth) {
            return // 今天不在当前显示的月份，不绘制背景
        }

        // 遍历日历中的所有日期项，找到今天的位置
        for ((index, item) in mItems.withIndex()) {
            // 匹配年、月、日，确保是今天
            if (item.year == todayYear && item.month == todayMonth && item.day == todayDay) {
                // 计算今天日期在网格中的坐标
                val x = index % 7 * mItemWidth // 列索引 * 单元格宽度
                val y = index / 7 * mItemHeight // 行索引 * 单元格高度

                // 绘制今天的背景（圆角矩形）
                val centerX = x + mItemWidth / 2
                val centerY = y + mItemHeight / 2
                canvas.drawCircle(centerX.toFloat(),centerY.toFloat(),selectRadius.toFloat(),selectedBgPaint)

                drawTodayText(canvas, x, y)
            }
        }
    }
    /**
     * 单独绘制今天的文字，确保颜色不消失（独立于onDrawText的过滤逻辑）
     */
    private fun drawTodayText(canvas: Canvas, x: Int, y: Int) {
        val todayText = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH).toString()
        // 今天的文字颜色（自定义，如粉色，与背景区分）
        val todayTextPaint =mCurDayTextPaint
        // 计算文字基线（居中显示）
        todayTextPaint.getTextBounds(todayText, 0, todayText.length, textBounds)
        val baseLineY = y + mItemHeight / 2 + textBounds.height() / 2

        // 强制绘制今天的文字
        canvas.drawText(todayText, (x + mItemWidth / 2).toFloat(), baseLineY.toFloat(), todayTextPaint)

    }
    /**
     * 绘制日期标记（底部圆点+右上角图标）
     */
    override fun onDrawScheme(canvas: Canvas, calendar: Calendar, x: Int, y: Int) {
        // 1. 绘制底部彩色圆点
        drawSchemeDots(canvas, calendar, x, y)
        // 2. 绘制右上角图标（通过tag判断是否显示）
        if (calendar.schemes?.any { it.scheme == "icon_birthday" } == true) {
            drawTopRightIcon(canvas, x, y)
        }
    }

    /**
     * 绘制日期文字
     */
    override fun onDrawText(
        canvas: Canvas,
        calendar: Calendar,
        x: Int,
        y: Int,
        hasScheme: Boolean,
        isSelected: Boolean
    ) {
        val dayText = calendar.day.toString()
        val centerX = x + mItemWidth / 2  // 水平中心

        // 计算文字垂直居中的基线
        val textPaint = getTextPaint(calendar, isSelected)  // 获取当前文字画笔
        textPaint.getTextBounds(dayText, 0, dayText.length, textBounds)  // 测量文字宽高
        // 垂直中心 = 单元格高度/2 + 文字高度/2（抵消基线偏移）
        val baseLineY = y + mItemHeight / 2 + textBounds.height() / 2

        canvas.drawText(calendar.day.toString(), centerX.toFloat(), baseLineY.toFloat(), textPaint)
    }

    override fun onDrawScheduling(
        canvas: Canvas?,
        calendar: Calendar?,
        x: Int,
        y: Int,
        hasScheme: Boolean,
        isSelected: Boolean
    ) {
        TODO("Not yet implemented")
    }

    // 提取文字画笔逻辑
    private fun getTextPaint(calendar: Calendar, isSelected: Boolean): Paint {
        return when {
            isSelected -> mSelectTextPaint.apply {
                if (calendar.isCurrentDay) {
                    color = Color.WHITE
                } else {
                    color = Color.BLACK
                }
            }

            calendar.isCurrentDay -> mCurDayTextPaint.apply { color = Color.WHITE }
            else -> mCurMonthTextPaint
        }
    }

    /**
     * 修复圆点颜色逻辑，添加选中状态参数
     */
    private fun drawSchemeDots(
        canvas: Canvas,
        calendar: Calendar,
        x: Int,
        y: Int
    ) {
        if (calendar.schemes.isNullOrEmpty()) return

        val dotCount = calendar.schemes.size
        val totalWidth = (dotCount * 2 * dotRadius) + (dotCount - 1) * dotMargin
        val startX = x + mItemWidth / 2 - totalWidth / 2

        val spacing = dipToPx(8f)
        val dotY = y + mItemHeight * 3 / 4 + spacing

        calendar.schemes.forEachIndexed { index, scheme ->
            dotPaint.color = scheme.shcemeColor
            val dotX = startX + index * (2 * dotRadius + dotMargin) + dotRadius
            canvas.drawCircle(dotX.toFloat(), dotY.toFloat(), dotRadius.toFloat(), dotPaint)
        }
    }

    /**
     * 绘制右上角图标
     */
    private fun drawTopRightIcon(canvas: Canvas, x: Int, y: Int) {
        val scaledIcon = customIcon.scale(iconSize, iconSize)
        val iconX = x + mItemWidth - iconSize - dipToPx(2f)  // 右边缘间距
        val iconY = y - dipToPx(2f)  // 上边缘间距
        canvas.drawBitmap(scaledIcon, iconX.toFloat(), iconY.toFloat(), iconPaint)
    }

    /**
     * dp转px工具方法
     */
    private fun dipToPx(dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    // 添加Bitmap缩放扩展函数
    private fun Bitmap.scale(width: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(this, width, height, true)
    }
}