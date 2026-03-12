package com.wingstars.calendar.fragment

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView.OnCalendarSelectListener
import com.wingstars.base.base.BaseFragment
import com.wingstars.base.net.beans.WSCalendarNResponse
import com.wingstars.base.net.beans.WSMemberResponse
import com.wingstars.calendar.R
import com.wingstars.calendar.activity.EventDetailsActivity
import com.wingstars.calendar.adapter.CalendarAdapter
import com.wingstars.calendar.adapter.CalendarMemberAdapter
import com.wingstars.calendar.databinding.FragmentCalendarBinding
import com.wingstars.calendar.utils.CalendarDateUtils
import com.wingstars.calendar.utils.CalendarDateUtils.Companion.BIRTH_DATE_FORMATTER
import com.wingstars.calendar.viewmodel.CalendarViewModel
import com.wingstars.calendar.viewmodel.DailyCalendarData
import com.wingstars.login.LoginActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import com.tencent.mmkv.MMKV
import java.util.*

class CalendarFragment : BaseFragment(), OnCalendarSelectListener {
    private val viewModel: CalendarViewModel by viewModels()
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var schemeMap: MutableMap<String, Calendar>

    // 缓存重组后的每日日历数据，按年份分组
    private var allDailyCalendarList = mutableListOf<DailyCalendarData>()
    // 缓存生日用户数据（独立API返回）
    private val birthdayUserList = mutableListOf<WSMemberResponse>()
    // 缓存选中日期的生日用户列表
    private val selectedBirthdayUsers = mutableListOf<WSMemberResponse>()
    // 缓存选中日期的日历活动列表（用于空视图判断）
    private var selectedCalendarActivities = mutableListOf<WSCalendarNResponse>()

    // 时间格式化工具
    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.TAIWAN)
    private val dayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN)
    private var isDataLoaded = false

    // 缓存所有生日的月日组合（去重）
    private val birthdayMonthDaySet = mutableSetOf<String>()

    // 生日用户列表适配器
    private lateinit var birthdayUserAdapter: CalendarMemberAdapter

    // 保存当前选中的日期
    private var currentSelectedCalendar: Calendar? = null

    // 用于防止重复加载的标志
    private var isFromDetailsReturn = false

    // 当前显示的年份和月份
    private var currentDisplayYear: Int = getCurrentYear()
    private var currentDisplayMonth: Int = getCurrentMonth()

    private var currentDisplayDay: Int = getCurrentDay()
    // 记录哪些年份的数据已经加载过，避免重复加载
    private val loadedYears = mutableSetOf<Int>()

    override fun onResume() {
        super.onResume()

        if (MMKV.defaultMMKV().decodeBool("isLogin")) {
            binding.llLoginState.visibility = View.VISIBLE
            binding.llNoLogin.visibility = View.GONE
        } else {
            binding.llLoginState.visibility = View.GONE
            binding.llNoLogin.visibility = View.VISIBLE
        }

        // 如果不是从详情页返回，才重新加载数据
        if (!isFromDetailsReturn && !isDataLoaded) {
            loadData()
            isDataLoaded = true
        }
        // 重置标志
        isFromDetailsReturn = false
    }

    /**
     * 加载数据：独立调用日历数据和生日API
     */
    private fun loadData() {
        // 1. 独立调用生日API
        viewModel.getWsMembersBirthdayData()
        // 2. 调用当前显示年份的日历主数据API
        viewModel.getWsCalendar(currentDisplayYear)
        // 3. 观察生日数据
        observeBirthdayData()

        // 标记当前年份已加载
        loadedYears.add(currentDisplayYear)
    }

    /**
     * 独立观察生日API返回数据并缓存
     */
    private fun observeBirthdayData() {
        viewModel.wsMembersBirthdayData.observe(viewLifecycleOwner) { data ->
            birthdayUserList.clear()
            birthdayMonthDaySet.clear() // 清空月日缓存
            if (data != null && data.isNotEmpty()) {
                // 转换为生日用户模型
                birthdayUserList.addAll(data)
                // 提取所有生日的月日组合（格式：month-day，去重）
                data.forEach { member ->
                    val birthdate = member.acf.birthdate
                    if (birthdate.isNotEmpty()) {
                        try {
                            val birthDate = LocalDate.parse(birthdate, BIRTH_DATE_FORMATTER)
                            val month = birthDate.monthValue
                            val day = birthDate.dayOfMonth
                            birthdayMonthDaySet.add("$month-$day")
                        } catch (e: Exception) {
                            // Log.e("CalendarFragment", "解析生日日期失败: ${member.acf.birthdate}", e)
                        }
                    }
                }
                // 生日数据更新后刷新日历标记（仅初始化/数据变更时调用）
                if (allDailyCalendarList.isNotEmpty()) {
                    refreshCalendarScheme(false)
                }
                // 生日数据更新后检查空视图（当前选中日期可能有生日）
                checkEmptyViewVisibility()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCalendar()
        initData()
        initListener()
        initCalendarAdapter()
        initBirthdayUserAdapter()
    }

    /**
     * 初始化生日用户列表适配器
     */
    private fun initBirthdayUserAdapter() {
        birthdayUserAdapter = CalendarMemberAdapter(requireContext(), selectedBirthdayUsers)
        binding.rvBirthdayList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBirthdayList.adapter = birthdayUserAdapter
        // 默认隐藏生日列表
        binding.rvBirthdayList.visibility = View.GONE
    }

    fun getCurrentYear(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now().year
        } else {
            java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        }
    }

    fun getCurrentMonth(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now().monthValue
        } else {
            java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1
        }
    }

    fun getCurrentDay(): Int {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now().dayOfMonth
        } else {
            java.util.Calendar.getInstance().get(java.util.Calendar.DATE)
        }
    }

    private fun initCalendar() {
        binding.ivPrev.setOnClickListener {
            currentDisplayDay = 1
            binding.calendarView.scrollToPre(true)
            binding.tvDateItinerary.text = "${currentDisplayMonth}/${currentDisplayDay} ${getString(R.string.calendar_itinerary)}"

            binding.calendarView.isSelected = true
        }
        binding.ivNext.setOnClickListener {
            currentDisplayDay = 1
            binding.calendarView.scrollToNext(true)
            binding.tvDateItinerary.text = "${currentDisplayMonth}/${currentDisplayDay} ${getString(R.string.calendar_itinerary)}"

            binding.calendarView.isSelected = true
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(requireActivity(), LoginActivity::class.java))
        }
        binding.calendarView.setMonthViewScrollable(true)
        binding.calendarView.setWeekViewScrollable(true)
        binding.calendarView.setOnCalendarSelectListener(this)

        // 月份切换监听器
        binding.calendarView.setOnMonthChangeListener { year, month ->
            // 记录旧的显示年份
            val oldYear = currentDisplayYear
            // 更新当前显示的年份和月份
            currentDisplayYear = year
            currentDisplayMonth = month

            // 更新标题
            binding.tvTitleDate.text = "${year}年${month}月"

            // 检查年份是否发生变化
            if (year != oldYear) {
                // 年份发生变化，需要检查是否需要加载新年份的数据
                if (!loadedYears.contains(year)) {
                    // 新年份未加载过，加载数据
                    viewModel.getWsCalendar(year)
                    loadedYears.add(year)
                } else {
                    // 新年份已加载过，直接刷新日历标记
                    refreshCalendarScheme(false)
                }
            }
            // 月份滑动切换时重置选中状态
            resetSelectionOnViewChange()

            currentDisplayDay = 1
            binding.tvDateItinerary.text = "${month}/${currentDisplayDay} ${getString(R.string.calendar_itinerary)}"
        }

        // 初始化时设置标题
        binding.tvTitleDate.text = "${currentDisplayYear}年${currentDisplayMonth}月"
        binding.tvDateItinerary.text = "${currentDisplayMonth}/${currentDisplayDay} ${getString(R.string.calendar_itinerary)}"
    }

    private fun initData() {
        // 观察日历主数据
        viewModel.wSCalendarData.observe(viewLifecycleOwner) { data ->
            if (data != null && data.isNotEmpty()) {
                // 处理新加载的数据
                processCalendarData(data)
                // 刷新日历标记，但不滚动到当前月
                refreshCalendarScheme(false)
                // 恢复显示当前月份
                scrollToCurrentDisplayMonth()
                binding.calendarView.postDelayed({automaticallyDisplayEvents()}, 100)
            }
        }
    }
    private fun automaticallyDisplayEvents() {
        if (allDailyCalendarList.isEmpty()) {
            binding.calendarView.postDelayed({automaticallyDisplayEvents()}, 100)
            return
        }

        val today = Calendar().apply {
            year = currentDisplayYear
            month = currentDisplayMonth
            day = currentDisplayDay
        }

        binding.calendarView.post {
            binding.calendarView.scrollToCalendar(today.year, today.month, today.day, true)
            currentSelectedCalendar = today
            onCalendarSelect(today, true)
        }
    }

    /**
     * 处理日历数据
     */
    private fun processCalendarData(data: List<WSCalendarNResponse>) {
        val newDailyCalendarList = mutableListOf<DailyCalendarData>()

        data.forEach { item ->
            val startDateStr = item.st_dateF
            val endDateStr = item.ed_dateF

            if (startDateStr.isEmpty() && endDateStr.isEmpty()) {
                return@forEach
            } else if (startDateStr.isEmpty()) {
                val endDate = apiDateFormat.parse(endDateStr) ?: return@forEach
                val endCalendar = java.util.Calendar.getInstance().apply {
                    time = endDate
                    set(java.util.Calendar.HOUR_OF_DAY, 0)
                    set(java.util.Calendar.MINUTE, 0)
                    set(java.util.Calendar.SECOND, 0)
                    set(java.util.Calendar.MILLISECOND, 0)
                }
                addDailyCalendarItem(endCalendar, item, newDailyCalendarList)
            } else if (endDateStr.isEmpty()) {
                val startDate = apiDateFormat.parse(startDateStr) ?: return@forEach
                val startCalendar = java.util.Calendar.getInstance().apply {
                    time = startDate
                    set(java.util.Calendar.HOUR_OF_DAY, 0)
                    set(java.util.Calendar.MINUTE, 0)
                    set(java.util.Calendar.SECOND, 0)
                    set(java.util.Calendar.MILLISECOND, 0)
                }
                addDailyCalendarItem(startCalendar, item, newDailyCalendarList)
            } else {
                try {
                    val startDate = apiDateFormat.parse(startDateStr) ?: return@forEach
                    val endDate = apiDateFormat.parse(endDateStr) ?: return@forEach

                    val startCalendar = java.util.Calendar.getInstance().apply {
                        time = startDate
                        set(java.util.Calendar.HOUR_OF_DAY, 0)
                        set(java.util.Calendar.MINUTE, 0)
                        set(java.util.Calendar.SECOND, 0)
                        set(java.util.Calendar.MILLISECOND, 0)
                    }
                    val endCalendar = java.util.Calendar.getInstance().apply {
                        time = endDate
                        set(java.util.Calendar.HOUR_OF_DAY, 0)
                        set(java.util.Calendar.MINUTE, 0)
                        set(java.util.Calendar.SECOND, 0)
                        set(java.util.Calendar.MILLISECOND, 0)
                    }

                    while (!startCalendar.after(endCalendar)) {
                        addDailyCalendarItem(startCalendar, item, newDailyCalendarList)
                        startCalendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
                    }
                } catch (e: ParseException) {
                    // Log.e("CalendarFragment", "时间解析异常", e)
                }
            }
        }

        // 移除旧年份的数据，添加新年份的数据
        allDailyCalendarList.removeAll { it.year == currentDisplayYear }
        allDailyCalendarList.addAll(newDailyCalendarList)
    }

    private fun addDailyCalendarItem(
        calendar: java.util.Calendar,
        item: WSCalendarNResponse,
        list: MutableList<DailyCalendarData>
    ) {
        val year = calendar.get(java.util.Calendar.YEAR)
        val month = calendar.get(java.util.Calendar.MONTH) + 1
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        list.add(
            DailyCalendarData(
                date = calendar.time,
                year = year,
                month = month,
                day = day,
                originalItem = item
            )
        )
    }

    /**
     * 滚动到当前显示的月份
     */
    private fun scrollToCurrentDisplayMonth() {
        // 使用post确保在UI更新后执行滚动
        binding.calendarView.post {
            binding.calendarView.scrollToCalendar(
                currentDisplayYear,
                currentDisplayMonth,
                binding.calendarView.curDay,
                true
            )
        }
    }

    /**
     * 统一刷新日历标记（包含活动+生日）
     * @param scrollToCurrent 是否滚动到当前月份（仅初始化时为true）
     */
    private fun refreshCalendarScheme(scrollToCurrent: Boolean) {
        schemeMap = mutableMapOf()
        addBirthdaySchemeToAllBirthdayDates()

        val dailyGroup = allDailyCalendarList.groupBy { "${it.year}-${it.month}-${it.day}" }
        dailyGroup.forEach { (dateKey, sameDayItems) ->
            val uniqueTypeItems = mutableListOf<DailyCalendarData>()
            val addedCategoryIds = mutableSetOf<Int>()
            sameDayItems.forEach { item ->
                val categoryId = item.originalItem.categoryF
                if (!addedCategoryIds.contains(categoryId)) {
                    uniqueTypeItems.add(item)
                    addedCategoryIds.add(categoryId)
                }
            }
            val displayItems = uniqueTypeItems.take(CalendarViewModel.CalendarCategory.MAX_DISPLAY_COUNT)

            val (year, month, day) = dateKey.split("-").map { it.toInt() }
            val calendar = createCalendarWithActivityScheme(year, month, day, displayItems)
            schemeMap[calendar.toString()] = calendar
        }

        // 刷新日历UI
        viewModel.setIsLoading(false)
        binding.calendarView.setSchemeDate(schemeMap)

        if (scrollToCurrent) {
            // 仅在初始化时滚动到当前月
            binding.calendarView.scrollToCurrent()
            updateTitle(binding.calendarView.curYear, binding.calendarView.curMonth)
            binding.tvDateItinerary.text =
                "${binding.calendarView.curMonth}/${binding.calendarView.curDay} ${getString(R.string.calendar_itinerary)}"

            if (currentSelectedCalendar != null) {
                restoreSelection()
            } else {
                filterDataByDate(
                    binding.calendarView.curYear,
                    binding.calendarView.curMonth,
                    binding.calendarView.curDay
                )
            }
        } else {
            // 非初始化刷新，保持当前位置
            binding.calendarView.invalidate()
        }
    }

    /**
     * 恢复选中状态
     */
    private fun restoreSelection() {
        currentSelectedCalendar?.let { calendar ->
            binding.calendarView.isSelected = true
            binding.tvDateItinerary.text =
                "${calendar.month}/${calendar.day} ${getString(R.string.calendar_itinerary)}"
            binding.calendarView.invalidate()

            filterDataByDate(calendar.year, calendar.month, calendar.day)
            filterBirthdayUsersByDate(calendar.month, calendar.day)
        }
    }

    /**
     * 创建包含活动标记的Calendar对象
     */
    private fun createCalendarWithActivityScheme(
        year: Int,
        month: Int,
        day: Int,
        displayItems: List<DailyCalendarData>
    ): Calendar {
        return Calendar().apply {
            this.year = year
            this.month = month
            this.day = day

            displayItems.forEach { item ->
                addSchemeByCategory(item.originalItem.categoryF, requireContext())
            }

            if (birthdayMonthDaySet.contains("$month-$day")) {
                val hasBirthdayIcon = schemes?.any { it.scheme == "icon_birthday" } ?: false
                if (!hasBirthdayIcon) {
                    addBirthdayScheme(requireContext())
                }
            }
        }
    }

    /**
     * 为所有生日日期添加纯生日图标标记
     */
    private fun addBirthdaySchemeToAllBirthdayDates() {
        val currentYear = currentDisplayYear
        val years = listOf(currentYear - 1, currentYear, currentYear + 1)

        years.forEach { year ->
            birthdayMonthDaySet.forEach { monthDay ->
                val (month, day) = monthDay.split("-").map { it.toInt() }
                val calendarKey = "$year-$month-$day"

                if (schemeMap.containsKey(calendarKey)) {
                    val existingCalendar = schemeMap[calendarKey]
                    if (existingCalendar != null) {
                        val hasBirthdayIcon = existingCalendar.schemes?.any {
                            it.scheme == "icon_birthday"
                        } ?: false

                        if (!hasBirthdayIcon) {
                            existingCalendar.addBirthdayScheme(requireContext())
                        }
                    }
                } else {
                    val birthdayCalendar = Calendar().apply {
                        this.year = year
                        this.month = month
                        this.day = day
                        addBirthdayScheme(requireContext())
                    }
                    schemeMap[birthdayCalendar.toString()] = birthdayCalendar
                }
            }
        }
    }

    /**
     * 添加纯生日Scheme - 仅作为标识，不添加颜色（没有圆点）
     */
    private fun Calendar.addBirthdayScheme(context: Context) {
        val birthdayScheme = Calendar.Scheme().apply {
            scheme = "icon_birthday" // 仅保留scheme标识
        }
        addScheme(birthdayScheme)
    }

    private fun Calendar.addSchemeByCategory(categoryId: Int, context: Context) {
        val scheme = Calendar.Scheme().apply {
            when (categoryId) {
                CalendarViewModel.CalendarCategory.GENERAL_ACTIVITY -> {// 一般活动
                    shcemeColor = ContextCompat.getColor(context, R.color.color_DE9DBA)
                    scheme = "general"
                }
                CalendarViewModel.CalendarCategory.BIRTHDAY -> { // 生日活动
//                    shcemeColor = ContextCompat.getColor(context, R.color.color_DE9DBA)
                    scheme = "birthday"
                }
                CalendarViewModel.CalendarCategory.MALE_EAGLE -> {// 雄鹰
                    shcemeColor = ContextCompat.getColor(context, R.color.color_007A60)
                    scheme = "male_eagle"
                }
                CalendarViewModel.CalendarCategory.HUNT_EAGLE -> {// 猎鹰
                    shcemeColor = ContextCompat.getColor(context, R.color.color_B81B30)
                    scheme = "hunt_eagle"
                }
                CalendarViewModel.CalendarCategory.SKY_EAGLE -> {// 天鹰
                    shcemeColor = ContextCompat.getColor(context, R.color.color_0089D3)
                    scheme = "sky_eagle"
                }
                else -> return
            }
        }
        addScheme(scheme)
    }

    private fun initListener() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoadingUI(it, requireActivity())
        }

        // 观察选中的日历活动数据
        viewModel.selectedData.observe(viewLifecycleOwner) { selectedList ->
            selectedCalendarActivities.clear()
            if (selectedList != null) {
                selectedCalendarActivities.addAll(selectedList)
            }
            val adapter = binding.rvCardClassificationList.adapter as CalendarAdapter
            adapter.updateData(selectedCalendarActivities)

            binding.rvCardClassificationList.visibility = if(selectedCalendarActivities.isNotEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }

            checkEmptyViewVisibility()
        }
    }

    private fun initCalendarAdapter() {
        binding.rvCardClassificationList.layoutManager = LinearLayoutManager(requireContext())
        val adapter = CalendarAdapter(
            requireActivity(),
            mutableListOf(),
            object : CalendarAdapter.onItemClickListener {
                override fun onItemClick(data: WSCalendarNResponse, position: Int) {
                    isFromDetailsReturn = true

                    val intent = Intent(requireActivity(), EventDetailsActivity::class.java)
                    val bundle = Bundle()
                    bundle.putSerializable("WSCalendar_Details", data)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            })
        binding.rvCardClassificationList.adapter = adapter
    }

    /**
     * 重置日历选中状态和视图显示
     */
    private fun resetSelectionOnViewChange() {
        binding.calendarView.isSelected = false
        binding.calendarView.setSchemeDate(schemeMap)
        binding.calendarView.invalidate()

        selectedCalendarActivities.clear()
        selectedBirthdayUsers.clear()

        binding.rvBirthdayList.visibility = View.GONE
        binding.rvCardClassificationList.visibility = View.GONE

        binding.llEvemtEmpty.visibility = View.VISIBLE

        currentSelectedCalendar = null
    }

    /**
     * 仅当活动列表和生日列表都为空时显示空视图
     */
    private fun checkEmptyViewVisibility() {
        val isActivityEmpty = selectedCalendarActivities.isEmpty()
        val isBirthdayEmpty = selectedBirthdayUsers.isEmpty()

        binding.llEvemtEmpty.visibility = if (isActivityEmpty && isBirthdayEmpty) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun updateTitle(year: Int, month: Int) {
        binding.tvTitleDate.text = "${year}年${month}月"
    }

    override fun onPause() {
        super.onPause()
        if (!isFromDetailsReturn) {
            resetSelectionOnViewChange()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        resetSelectionOnViewChange()
        binding.calendarView.setOnCalendarSelectListener(null)
        binding.calendarView.setOnMonthChangeListener(null)
    }

    override fun onCalendarOutOfRange(calendar: Calendar?) {}

    /**
     * 日历点击事件 - 匹配生日并显示用户列表
     */
    override fun onCalendarSelect(calendar: Calendar?, isClick: Boolean) {
        if (isClick && calendar != null) {
            currentSelectedCalendar = calendar

            binding.calendarView.isSelected = true
            binding.tvDateItinerary.text =
                "${calendar.month}/${calendar.day} ${getString(R.string.calendar_itinerary)}"
            binding.calendarView.invalidate()

            filterDataByDate(calendar.year, calendar.month, calendar.day)
            filterBirthdayUsersByDate(calendar.month, calendar.day)
        }
    }

    /**
     * 筛选选中日期的生日用户并显示列表
     */
    private fun filterBirthdayUsersByDate(month: Int, day: Int) {
        selectedBirthdayUsers.clear()
        selectedBirthdayUsers.addAll(
            birthdayUserList.filter {
                CalendarDateUtils.isSameMonthAndDay(it.acf.birthdate, month, day)
            }
        )
        try {
            birthdayUserAdapter.setList(selectedBirthdayUsers)
        } catch (e: Exception) {
            birthdayUserAdapter.updateData(selectedBirthdayUsers)
        }
        binding.rvBirthdayList.visibility = if (selectedBirthdayUsers.isNotEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
        checkEmptyViewVisibility()
    }

    private fun filterDataByDate(year: Int, month: Int, day: Int) {
        val categoryPriority = mapOf(
            CalendarViewModel.CalendarCategory.GENERAL_ACTIVITY to 1,
            CalendarViewModel.CalendarCategory.BIRTHDAY to 2,
            CalendarViewModel.CalendarCategory.MALE_EAGLE to 3,
            CalendarViewModel.CalendarCategory.SKY_EAGLE to 4,
            CalendarViewModel.CalendarCategory.HUNT_EAGLE to 5
        )
        val selectedItems = allDailyCalendarList.filter {
            it.year == year && it.month == month && it.day == day
        }
        val sortedList = selectedItems
            .map { it.originalItem }
            .filter { it.categoryF != CalendarViewModel.CalendarCategory.BIRTHDAY }
            .sortedBy { categoryPriority[it.categoryF] ?: 6 }
            .toMutableList()

        selectedCalendarActivities.clear()
        selectedCalendarActivities.addAll(sortedList)

        viewModel.selectedData.postValue(sortedList)
    }
}