package com.wingstars.calendar.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.WeekView
import com.wingstars.calendar.R

class SimpleWeekView @JvmOverloads constructor(
    context: Context,
) : WeekView(context) {
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

    // 记录用户手动选中的日期（初始为null）
    private var userSelectedCalendar: Calendar? = null
    // 触摸相关参数（判断是否为点击事件）
    private var touchX = 0f
    private var touchY = 0f
    private val clickThreshold = dipToPx(10f) // 点击阈值（避免滑动误判为点击）

    init {
        val paddingHorizontal = dipToPx(24f)
        setPadding(paddingHorizontal, 0, paddingHorizontal, 0)

        setBackgroundColor(Color.TRANSPARENT)
        background = ContextCompat.getDrawable(context, R.drawable.calendar_bg_bottom_radius)
    }

    private val customIcon by lazy {
        BitmapFactory.decodeResource(resources, R.drawable.calendar_ic_birthday_cake)
    }

    // 周历仅显示1行（含星期标题），高度为单行高度
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val singleRowHeight = dipToPx(53f) // 与app:calendar_height一致
        val totalHeight = singleRowHeight// 周历仅1行
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), totalHeight)
    }

    // 重写触摸事件，监听用户点击（区分点击和滑动）
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 记录触摸起始位置
                touchX = event.x
                touchY = event.y
            }
            MotionEvent.ACTION_UP -> {
                // 计算触摸偏移量，判断是否为点击（偏移量小于阈值）
                val dx = Math.abs(event.x - touchX)
                val dy = Math.abs(event.y - touchY)
                if (dx < clickThreshold && dy < clickThreshold) {
                    // 是点击事件，获取点击位置对应的日期
                    val clickedCalendar = getCalendarByTouchPosition(event.x, event.y)
                    if (clickedCalendar != null) {
                        // 更新用户选中日期并刷新视图
                        userSelectedCalendar = clickedCalendar
                        invalidate()
                    }
                }
            }
        }
        // 不拦截触摸事件，确保滑动功能正常
        return super.onTouchEvent(event)
    }

    /**
     * 根据触摸位置获取对应的日期
     */
    private fun getCalendarByTouchPosition(x: Float, y: Float): Calendar? {
        // 计算点击的列索引（0-6，对应周日-周六或周一-周日，取决于日历配置）
        val columnIndex = ((x - paddingLeft) / mItemWidth).toInt()
        // 周历仅1行日期，行索引固定为0
        val rowIndex = 0

        // 计算对应的日期索引（mItems中的位置）
        val calendarIndex = rowIndex * 7 + columnIndex
        return if (calendarIndex >= 0 && calendarIndex < mItems.size) {
            mItems[calendarIndex]
        } else {
            null
        }
    }

    override fun onDrawSelected(
        canvas: Canvas,
        calendar: Calendar,
        x: Int,
        hasScheme: Boolean
    ): Boolean {
        // 1. 保留今天日期的背景绘制
        drawTodayBackground(canvas)

        // 2. 仅当用户手动选中该日期，且不是今天时，才绘制选中背景
        if (isUserSelected(calendar) && !isToday(calendar)) {
            val left = x + mItemWidth / 2 - rectWidth / 2
            val top = (mItemHeight / 2 - rectHeight / 2) + dipToPx(4f)
            val right = left + rectWidth
            val bottom = top + rectHeight
            val rectF = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
            canvas.drawRoundRect(rectF, rectCornerRadius.toFloat(), rectCornerRadius.toFloat(), currentDayBgPaint)
        }
        return true
    }

    /**
     * 判断日期是否为用户手动选中
     */
    private fun isUserSelected(calendar: Calendar): Boolean {
        return userSelectedCalendar?.let {
            it.year == calendar.year && it.month == calendar.month && it.day == calendar.day
        } ?: false
    }

    /**
     * 主动计算今天日期在日历中的位置并绘制背景
     */
    private fun drawTodayBackground(canvas: Canvas) {
        // 1. 获取今天的年、月、日
        val systemCalendar = java.util.Calendar.getInstance()
        val todayYear = systemCalendar.get(java.util.Calendar.YEAR)
        val todayMonth = systemCalendar.get(java.util.Calendar.MONTH) + 1 // 1-based
        val todayDay = systemCalendar.get(java.util.Calendar.DAY_OF_MONTH)

        // 2. 手动计算当前显示的“当前周”起止日期
        var currentWeekStart: Calendar? = null
        var currentWeekEnd: Calendar? = null

        // 遍历当前视图的所有日期（mItems），找到今天所在的行（一周）
        var todayIndex = -1 // 记录今天在mItems中的索引
        for ((index, item) in mItems.withIndex()) {
            if (item.year == todayYear && item.month == todayMonth && item.day == todayDay) {
                todayIndex = index // 保存今天的索引
                val rowIndex = index / 7
                val weekStartIndex = rowIndex * 7
                val weekEndIndex = weekStartIndex + 6

                if (weekStartIndex >= 0 && weekEndIndex < mItems.size) {
                    currentWeekStart = mItems[weekStartIndex]
                    currentWeekEnd = mItems[weekEndIndex]
                }
                break
            }
        }

        // 若今天不在当前视图中，取当前视图第一周作为当前周（但不绘制今天背景）
        if (currentWeekStart == null || currentWeekEnd == null) {
            if (mItems.size >= 7) {
                currentWeekStart = mItems[0]
                currentWeekEnd = mItems[6]
            } else {
                return
            }
        }

        // 3. 判断今天是否在当前周范围内
        var isTodayInCurrentWeek = false
        val startIndex = mItems.indexOf(currentWeekStart)
        val endIndex = mItems.indexOf(currentWeekEnd)
        if (startIndex != -1 && endIndex != -1 && todayIndex != -1) {
            isTodayInCurrentWeek = todayIndex in startIndex..endIndex
        }

        // 4. 只有今天在当前周范围内，才绘制背景和特殊文字颜色
        if (!isTodayInCurrentWeek || todayIndex == -1) {
            return
        }

        // 5. 绘制今天的背景（圆角矩形/圆形，保持原逻辑）
        val x = todayIndex % 7 * mItemWidth
        val y = todayIndex / 7 * mItemHeight
        val centerX = x + mItemWidth / 2
        val centerY = y + mItemHeight / 2
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), selectRadius.toFloat(), selectedBgPaint)

        // 关键修复：强制绘制今天的文字颜色（不依赖当前月/选中状态）
        drawTodayText(canvas, x, y)
    }

    /**
     * 单独绘制今天的文字，确保颜色不消失（独立于onDrawText的过滤逻辑）
     */
    private fun drawTodayText(canvas: Canvas, x: Int, y: Int) {
        val todayText = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH).toString()
        val todayTextPaint = mCurDayTextPaint.apply() { color = Color.WHITE }
        todayTextPaint.getTextBounds(todayText, 0, todayText.length, textBounds)
        val baseLineY = y + mItemHeight / 2 + textBounds.height() / 2

        // 强制绘制今天的文字
        canvas.drawText(todayText, (x + mItemWidth / 2).toFloat(), baseLineY.toFloat(), todayTextPaint)
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

    override fun onDrawScheme(canvas: Canvas, calendar: Calendar, x: Int) {
        drawSchemeDots(canvas, calendar, x)
        if (calendar.schemes?.any { it.scheme == "icon_birthday" } == true) {
            drawTopRightIcon(canvas, x)
        }
    }

    override fun onDrawText(
        canvas: Canvas,
        calendar: Calendar,
        x: Int,
        hasScheme: Boolean,
        isSelected: Boolean
    ) {
        // 跳过今天的文字绘制（已在drawTodayBackground中单独绘制）
        if (!isToday(calendar)) {
            val dayText = calendar.day.toString()
            val centerX = x + mItemWidth / 2
            // 优先使用用户手动选中的判断，而非系统的isSelected（避免滑动默认选中）
            val textPaint = getTextPaint(calendar, isUserSelected(calendar), hasScheme)
            textPaint.getTextBounds(dayText, 0, dayText.length, textBounds)
            val baseLineY = mItemHeight / 2 + textBounds.height() / 2
            canvas.drawText(dayText, centerX.toFloat(), baseLineY.toFloat(), textPaint)
        }
    }

    private fun getTextPaint(calendar: Calendar, isSelected: Boolean, hasScheme: Boolean): Paint {
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

    private fun drawSchemeDots(canvas: Canvas, calendar: Calendar, x: Int) {
        if (calendar.schemes.isNullOrEmpty()) return

        val dotCount = calendar.schemes.size
        val totalWidth = (dotCount * 2 * dotRadius) + (dotCount - 1) * dotMargin
        val startX = x + mItemWidth / 2 - totalWidth / 2

        val spacing = dipToPx(8f)  // 8dp转px
        // 使圆点位于日期文字正下方，且不重叠
        val dotY = mItemHeight * 3 / 4 + spacing

        calendar.schemes.forEachIndexed { index, scheme ->
            dotPaint.color = scheme.shcemeColor
            val dotX = startX + index * (2 * dotRadius + dotMargin) + dotRadius
            canvas.drawCircle(dotX.toFloat(), dotY.toFloat(), dotRadius.toFloat(), dotPaint)
        }
    }

    private fun drawTopRightIcon(canvas: Canvas, x: Int) {
        val scaledIcon = customIcon.scale(iconSize, iconSize)
        val iconX = x + mItemWidth - iconSize - dipToPx(2f)
        val iconY = -dipToPx(2f)
        canvas.drawBitmap(scaledIcon, iconX.toFloat(), iconY.toFloat(), iconPaint)
    }

    private fun dipToPx(dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    // 添加Bitmap缩放扩展函数
    private fun Bitmap.scale(width: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(this, width, height, true)
    }
}