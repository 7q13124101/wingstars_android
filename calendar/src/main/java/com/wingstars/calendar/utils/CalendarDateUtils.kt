package com.wingstars.calendar.utils

import java.text.SimpleDateFormat
import java.time.DateTimeException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class CalendarDateUtils {
    companion object {
        val BIRTH_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        fun isSameMonthAndDay(birthdate: String, targetMonth: Int, targetDay: Int): Boolean {
            return try {
                val birthDate = LocalDate.parse(birthdate, BIRTH_DATE_FORMATTER)
                birthDate.monthValue == targetMonth && birthDate.dayOfMonth == targetDay
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException(e)
            } catch (e: DateTimeException) {
                throw IllegalArgumentException(e)
            }
        }

        fun formatDateWithWeekday(dateStr: String?): String {
            // 空值校验
            if (dateStr.isNullOrEmpty()) return ""

            val dateTimeParser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.TAIWAN)
            // 分别定义带时间和不带时间的格式化器
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN) // 仅日期
            val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.TAIWAN) // 日期+时间

            return try {
                val date = dateTimeParser.parse(dateStr) ?: return ""

                // 先单独解析时间部分判断是否为 00:00
                val timeFormatter = SimpleDateFormat("HH:mm", Locale.TAIWAN)
                val timeStr = timeFormatter.format(date)

                // 根据时间是否为 00:00 选择不同的格式化方式
                val formattedDate = if (timeStr == "00:00") {
                    dateFormatter.format(date)
                } else {
                    dateTimeFormatter.format(date)
                }

                // 获取星期并拼接（保留你原有的 simplifyWeekday 逻辑）
                val weekDayCn = simplifyWeekday(formattedDate)

                // 如果需要返回 "日期 星期" 的格式，使用这行：
                // "$formattedDate $weekDayCn"
                // 如果只需要返回星期，保留你原来的逻辑：
                weekDayCn

            } catch (e: Exception) {
                // 解析失败返回空字符串
                e.printStackTrace()
                ""
            }
        }

        fun formatCalendarDate(stDate: String?, edDate: String?): String {
            if (stDate.isNullOrEmpty()) return ""

            val dateTimeParser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.TAIWAN)
            val dateOnlyParser = SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN) // 仅解析年月日
//            val fullDateFormatter = SimpleDateFormat("yyyy-MM-dd (E)", Locale.TAIWAN) // 完整日期+单字星期
//            val shortDateFormatter = SimpleDateFormat("MM-dd (E)", Locale.TAIWAN) // 简化日期+单字星期
            val timeFormatter = SimpleDateFormat("HH:mm", Locale.TAIWAN) // 时分格式

            return try {
                val startDateTime = dateTimeParser.parse(stDate) ?: return ""
                var startFullDate = dateOnlyParser.format(startDateTime)
                val startTime = timeFormatter.format(startDateTime)

                startFullDate = simplifyWeekday(startFullDate)

                if (edDate.isNullOrEmpty()) {
                    "$startFullDate $startTime"
                } else {
                    val endDateTime =
                        dateTimeParser.parse(edDate) ?: return "$startFullDate $startTime"
                    val startDate = dateOnlyParser.parse(dateOnlyParser.format(startDateTime))
                        ?: return "$startFullDate $startTime"
                    val endDate = dateOnlyParser.parse(dateOnlyParser.format(endDateTime))
                        ?: return "$startFullDate $startTime"

                    var endShortDate = dateOnlyParser.format(endDateTime)
                    val endTime = timeFormatter.format(endDateTime)

                    endShortDate = simplifyWeekday(endShortDate)

                    if (startDate == endDate) {
                        "$startFullDate $startTime"
                    } else {
                        "$startFullDate $startTime ~ $endShortDate $endTime"
                    }
                }
            } catch (e: Exception) {
                stDate.replace(" ", " ").replace("-", "/")
            }
        }

        private fun simplifyWeekday(dateStr: String): String {
            return dateStr.replace("星期一", "一")
                .replace("星期二", "二")
                .replace("星期三", "三")
                .replace("星期四", "四")
                .replace("星期五", "五")
                .replace("星期六", "六")
                .replace("星期日", "日")
                .replace("週一", "一") // 繁体兜底
                .replace("週二", "二")
                .replace("週三", "三")
                .replace("週四", "四")
                .replace("週五", "五")
                .replace("週六", "六")
                .replace("週日", "日")
        }
    }
}