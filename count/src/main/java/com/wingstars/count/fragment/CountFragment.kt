package com.wingstars.count.fragment

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RadioButton
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tencent.mmkv.MMKV
import com.wingstars.base.base.BaseFragment
import com.wingstars.base.net.NetBase
import com.wingstars.base.net.beans.EvtTaskResponse
import com.wingstars.count.R
import com.wingstars.count.activity.ActivityExchangeActivity
import com.wingstars.count.activity.CountHistoryActivity
import com.wingstars.count.activity.Count_Item_Activity
import com.wingstars.count.activity.ExchangeHistoryActivity
import com.wingstars.count.activity.GiftExchangeActivity
import com.wingstars.count.adapter.CountAdapter
import com.wingstars.count.adapter.CountTitleAdapter
import com.wingstars.count.databinding.DialogPublicPopupBoxBinding
import com.wingstars.count.databinding.DialogPublicPopupSortTypeBinding
import com.wingstars.count.databinding.FragmentCountBinding
import com.wingstars.count.dialog.SortMethod
import com.wingstars.count.viewmodel.CountViewModel


class CountFragment : BaseFragment() {
    private lateinit var viewModel: CountViewModel
    private lateinit var binding: FragmentCountBinding
    private lateinit var countTitleAdapter: CountTitleAdapter
    private lateinit var adapter: CountAdapter

    private var select = 0
    private var eventType = "limited"
    private var limited_more_show = true
    private var daily_more_show = true
    private var exclusive_more_show = true
    private var fullDataList: List<EvtTaskResponse> = ArrayList()

    private var currentSortMethod = SortMethod.SORT_DATE_NEW_TO_OLD

    private var countItemDialog: Dialog? = null

//    private data class CheckInDay(
//        val reward: String,
//        val dayLabel: String
//    )

//    private val daysData = listOf(
//        CheckInDay("+1", "1天"),
//        CheckInDay("+2", "2天"),
//        CheckInDay("+3", "3天"),
//        CheckInDay("+4", "4天"),
//        CheckInDay("+5", "5天"),
//        CheckInDay("+6", "6天"),
//        CheckInDay("+10", "7天")
//    )

    private val loginSuccessReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val tag = intent?.getStringExtra("intentTag")
            if (tag == "countWS") {
                checkLoginStatus()
                viewModel.getEvtTasks()
                viewModel.getMemberPointFromDetailsData()
            }
        }
    }

    private var currentDayIndex = 0
    private var hasCheckedInToday = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(NetBase.BROADCAST_LOGIN_SUCCESS_INTENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().registerReceiver(loginSuccessReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            requireActivity().registerReceiver(loginSuccessReceiver, filter)
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            requireActivity().unregisterReceiver(loginSuccessReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
//        if (fullDataList.isNotEmpty()) {
//            updateListDisplay()
//        }

        checkLoginStatus()
        if (MMKV.defaultMMKV().decodeBool("isLogin")) {
            viewModel.getMemberPointFromDetailsData(false)
//            viewModel.getEvtTasks()
        } else {
            binding.tvCountWinstar.text = "0"
            fullDataList = ArrayList()
            updateListDisplay()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        closeCountItemDialog()
    }

    private fun initView() {
        viewModel = ViewModelProvider(this)[CountViewModel::class.java]

        val titleList = mutableListOf(
            getString(R.string.count_limited_time_task),
            getString(R.string.count_birthday_celebration),
            getString(R.string.count_star_fan_shopping)
        )

        binding.titleList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        countTitleAdapter = CountTitleAdapter(requireActivity(), titleList, object :
            CountTitleAdapter.onItemListener {
            override fun onItemClick(data: String, position: Int) {
                if (select != position) {
                    countTitleAdapter.setSelectPosition(position)
                    select = position
                    eventType = when (position) {
                        0 -> "limited"
                        1 -> "daily"
                        else -> "exclusive"
                    }
                    updateListDisplay()
                }
            }
        }, 0)
        binding.titleList.adapter = countTitleAdapter

        adapter = CountAdapter(requireActivity(), mutableListOf(), object : CountAdapter.onItemListener {
            override fun onItemClick(data: EvtTaskResponse, position: Int) {
                closeCountItemDialog()
                if (MMKV.defaultMMKV().decodeBool("isLogin")) {
                    val intent = Intent(requireActivity(), Count_Item_Activity::class.java)
                    val jsonString = com.google.gson.Gson().toJson(data)
                    intent.putExtra("EXTRA_ITEM_JSON", jsonString)
                    startActivity(intent)
                } else {
                    openLoginActivity()
                }
            }
            override fun onMoreClick() {}
            override fun setViewheight(height: Int) {}
        })

        binding.girlsList.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.girlsList.adapter = adapter

        viewModel.getEvtTasks()
        setupObservers()
//        setupCheckInUI()

//        binding.btnCheckIn.setOnClickListener {
//            checkLoginAndAction {
//                if (!hasCheckedInToday) {
//                    hasCheckedInToday = true
//                    setupCheckInUI()
//                    showCheckInSuccessDialog()
//                }
//            }
//        }

        binding.btnCheckLogin.setOnClickListener { openLoginActivity() }
        binding.tvCountWinstar.setOnClickListener { openCountHistoryActivity() }

        binding.llGiftExchange.setOnClickListener {
            checkLoginAndAction {
                val intent = Intent(requireActivity(), GiftExchangeActivity::class.java)
                startActivity(intent)
            }
        }

        binding.llActivityExchange.setOnClickListener {
            checkLoginAndAction {
                startActivity(Intent(requireActivity(), ActivityExchangeActivity::class.java))
            }
        }

        binding.llExchangeHistory.setOnClickListener {
            checkLoginAndAction {
                startActivity(Intent(requireActivity(), ExchangeHistoryActivity::class.java))
            }
        }

        binding.tvList.setOnClickListener { showSortDialog() }

        binding.tvViewMore.setOnClickListener {
            when(eventType) {
                "limited" -> limited_more_show = false
                "daily" -> daily_more_show = false
                else -> exclusive_more_show = false
            }
            updateListDisplay()
        }

//        binding.top.setOnClickListener { binding.scrollView.smoothScrollTo(0, 0) }
//
//        binding.scrollView.setOnScrollChangeListener { _: NestedScrollView, _: Int, scrollY: Int, _: Int, _: Int ->
//            val scrollThreshold = 300
//            if (scrollY > scrollThreshold) {
//                if (binding.top.visibility != View.VISIBLE || binding.top.alpha < 1f) {
//                    binding.top.animate().cancel()
//                    binding.top.visibility = View.VISIBLE
//                    binding.top.alpha = 0f
//                    binding.top.animate().alpha(1f).setDuration(200).start()
//                }
//            } else {
//                if (binding.top.visibility == View.VISIBLE && binding.top.alpha > 0f) {
//                    binding.top.animate().cancel()
//                    binding.top.animate().alpha(0f).setDuration(200).withEndAction {
//                        binding.top.visibility = View.GONE
//                    }.start()
//                }
//            }
//        }
//
//        binding.scrollView.setOnScrollChangeListener(
//            NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
//                val headerHeight = binding.ivHeaderBg.height.toFloat()
//                val ratio = (scrollY / headerHeight).coerceIn(0f, 1f)
//                binding.ivHeaderBg.apply {
//                    alpha = 1f - ratio
//                    scaleX = 1f + ratio * 0.1f
//                    scaleY = 1f + ratio * 0.1f
//                }
//            }
//        )

        binding.top.setOnClickListener { binding.scrollView.smoothScrollTo(0, 0) }

        binding.scrollView.setOnScrollChangeListener { _: NestedScrollView, _: Int, scrollY: Int, _: Int, _: Int ->
            val scrollThreshold = 300
            if (scrollY > scrollThreshold) {
                if (binding.top.visibility != View.VISIBLE || binding.top.alpha < 1f) {
                    binding.top.animate().cancel()
                    binding.top.visibility = View.VISIBLE
                    binding.top.alpha = 0f
                    binding.top.animate().alpha(1f).setDuration(200).start()
                }
            } else {
                if (binding.top.visibility == View.VISIBLE && binding.top.alpha > 0f) {
                    binding.top.animate().cancel()
                    binding.top.animate().alpha(0f).setDuration(200).withEndAction {
                        binding.top.visibility = View.GONE
                    }.start()
                }
            }
            val headerHeight = binding.ivHeaderBg.height.toFloat()
            if (headerHeight > 0f) {
                val ratio = (scrollY / headerHeight).coerceIn(0f, 1f)
                binding.ivHeaderBg.apply {
                    alpha = 1f - ratio
                    scaleX = 1f + ratio * 0.1f
                    scaleY = 1f + ratio * 0.1f
                }
            }
        }



        binding.srlCountsRecord.setOnRefreshListener {
            viewModel.getEvtTasks()
            if (MMKV.defaultMMKV().decodeBool("isLogin")) {
                viewModel.getMemberPointFromDetailsData()
            }
            binding.srlCountsRecord.finishRefresh()
        }

        checkLoginStatus()
    }

//    private fun handleTaskAction(data: EvtTaskResponse) {
//        if (data.triggerTag != null) {
//            var url = ""
//            var bAlwaysOpenUrl = false
//            when (data.triggerTag) {
//                "fb" -> url = "https://www.facebook.com/tsgwingstars/"
//                "instagram" -> url = "https://www.instagram.com/wing_stars_official/"
//                "yt" -> url = "https://www.youtube.com/@WingStars-TSG/"
//                "survey" -> {
//                    bAlwaysOpenUrl = true
//                    url = ""
//                }
//            }
//            if (url.isNotEmpty()) {
//                if (bAlwaysOpenUrl || data.status == "pending") {
//                    val intent = Intent(Intent.ACTION_VIEW)
//                    intent.data = Uri.parse(url)
//                    startActivity(intent)
//                }
//                if (!data.isSendApiF && data.status == "pending") {
//                    data.isSendApiF = true
//                    viewModel.grantMemberPoint(data)
//                }
//            }
//        }
//    }

    private fun setupObservers() {
        viewModel.taskList.observe(viewLifecycleOwner) { list ->
            fullDataList = list ?: ArrayList()
            limited_more_show = true
            daily_more_show = true
            exclusive_more_show = true
            updateListDisplay()
        }
        viewModel.points.observe(viewLifecycleOwner) { pointStr ->
            //Log.e("CountFragment", "Observer received points: $pointStr")
            binding.tvCountWinstar.text = pointStr
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { }
    }

        private fun updateListDisplay() {
        if (!MMKV.defaultMMKV().decodeBool("isLogin")) {
            return
        }
        val filteredList = fullDataList.filter { it.eventType == eventType }
        if (filteredList.isEmpty()) {
            binding.llEmpty.visibility = View.VISIBLE
            adapter.setList(emptyList())
            binding.tvViewMore.visibility = View.GONE
        } else {
            binding.llEmpty.visibility = View.GONE
            val isCollapsed = when(eventType) {
                "limited" -> limited_more_show
                "daily" -> daily_more_show
                else -> exclusive_more_show
            }
            val displayList = if (isCollapsed && filteredList.size > 3) {
                binding.tvViewMore.visibility = View.VISIBLE
                filteredList.take(3)
            } else {
                binding.tvViewMore.visibility = View.GONE
                filteredList
            }
            adapter.setList(displayList)
        }
    }

    private fun checkLoginStatus() {
        val isLogin = MMKV.defaultMMKV().decodeBool("isLogin")
        if (isLogin) {
            binding.clCheckLogin.visibility = View.GONE
            binding.rlCount.visibility = View.VISIBLE
            binding.girlsList.visibility = View.VISIBLE
        } else {
            binding.clCheckLogin.visibility = View.VISIBLE
            binding.rlCount.visibility = View.GONE
            binding.girlsList.visibility = View.GONE
            binding.llEmpty.visibility = View.VISIBLE
        }
    }

    private fun checkLoginAndAction(action: () -> Unit) {
        if (MMKV.defaultMMKV().decodeBool("isLogin")) {
            action()
        } else {
            openLoginActivity()
        }
    }

    private fun openLoginActivity() {
        try {
            val loginClass = Class.forName("com.wingstars.login.LoginActivity")
            val intent = Intent(requireActivity(), loginClass)
            intent.putExtra("tag", "countWS")
            startActivity(intent)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun openCountHistoryActivity() {
        val intent = Intent(requireActivity(), CountHistoryActivity::class.java)
        intent.putExtra("count", binding.tvCountWinstar.text.toString())
        startActivity(intent)
    }

//    private fun setupCheckInUI() {
//        val dayBindings = listOf(
//            binding.day1, binding.day2, binding.day3,
//            binding.day4, binding.day5, binding.day6, binding.day7
//        )
//        val colorPink = ContextCompat.getColor(requireContext(), R.color.color_EE97BB)
//        val colorGray = ContextCompat.getColor(requireContext(), R.color.color_4A5565)
//
//        daysData.forEachIndexed { index, data ->
//            val dayBinding = dayBindings[index]
//            val tvReward = dayBinding.tvReward
//            val ivStar = dayBinding.ivStar
//            val tvDayLabel = dayBinding.tvDayLabel
//            val contentBox = dayBinding.llContentBox
//            tvReward.text = data.reward
//            tvDayLabel.text = if (index == currentDayIndex) "今天" else data.dayLabel
//
//            when {
//                index < currentDayIndex -> {
//                    contentBox.setBackgroundResource(R.drawable.bg_checkin_item_future)
//                    tvReward.setTextColor(colorGray)
//                    tvDayLabel.setTextColor(colorGray)
//                    ivStar.setImageResource(R.drawable.ic_star_outline)
//                }
//                index == currentDayIndex -> {
//                    contentBox.setBackgroundResource(R.drawable.bg_checkin_item_today)
//                    tvReward.setTextColor(colorPink)
//                    tvDayLabel.setTextColor(colorPink)
//                    if (hasCheckedInToday) {
//                        ivStar.setImageResource(R.drawable.ic_star_filled)
//                    } else {
//                        ivStar.setImageResource(R.drawable.ic_star_filled)
//                    }
//                }
//                else -> {
//                    contentBox.setBackgroundResource(R.drawable.bg_checkin_item_future)
//                    tvReward.setTextColor(colorGray)
//                    tvDayLabel.setTextColor(colorGray)
//                    ivStar.setImageResource(R.drawable.ic_star_outline)
//                }
//            }
//        }
//        if (hasCheckedInToday) {
//            binding.btnCheckIn.text = "已簽到"
//            binding.btnCheckIn.isEnabled = false
//            binding.btnCheckIn.alpha = 0.5f
//        } else {
//            binding.btnCheckIn.text = "點擊簽到"
//            binding.btnCheckIn.isEnabled = true
//            binding.btnCheckIn.alpha = 1.0f
//        }
//    }

//    private fun showCheckInSuccessDialog() {
//        val dialog = Dialog(requireContext())
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        val dialogBinding = DialogPublicPopupBoxBinding.inflate(LayoutInflater.from(context))
//        dialog.setContentView(dialogBinding.root)
//        dialog.window?.apply {
//            setGravity(Gravity.BOTTOM)
//            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        }
//        val fullText = "你獲得 1 星幣！"
//        val spannableString = SpannableString(fullText)
//        val startIndex = fullText.indexOf("1")
//        if (startIndex >= 0) {
//            spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#FFC0CB")), startIndex, startIndex + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        }
//        dialogBinding.tvDialogContent.text = spannableString
//        dialogBinding.tvDialogConfirm.setOnClickListener { dialog.dismiss() }
//        dialog.show()
//    }

    private fun showSortDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = DialogPublicPopupSortTypeBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(dialogBinding.root)
        dialog.window?.apply {
            val displayMetrics = resources.displayMetrics
            val screenHeight = displayMetrics.heightPixels
            val halfScreenHeight = (screenHeight * 0.5).toInt()
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, halfScreenHeight)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setGravity(Gravity.BOTTOM)
        }

        val checkedId = when (currentSortMethod) {
            SortMethod.SORT_DATE_NEW_TO_OLD -> R.id.rb_sort_date_new_to_old
            SortMethod.SORT_DATE_OLD_TO_NEW -> R.id.rb_sort_date_old_to_new
            SortMethod.SORT_POINTS_HIGH_TO_LOW -> R.id.rb_sort_points_high_to_low
            SortMethod.SORT_POINTS_LOW_TO_HIGH -> R.id.rb_sort_points_low_to_high
            else -> R.id.rb_sort_date_new_to_old
        }
        dialogBinding.rgSort.check(checkedId)
        dialogBinding.ivCloseDialog.setOnClickListener { dialog.dismiss() }
        dialogBinding.rgSort.setOnCheckedChangeListener { group, checkedId ->

                val selectedRadioButton = group.findViewById<RadioButton>(checkedId)
                binding.tvList.text = selectedRadioButton.text.toString()

                currentSortMethod = when (checkedId) {
                    R.id.rb_sort_date_new_to_old -> SortMethod.SORT_DATE_NEW_TO_OLD
                    R.id.rb_sort_date_old_to_new -> SortMethod.SORT_DATE_OLD_TO_NEW
                    R.id.rb_sort_points_high_to_low -> SortMethod.SORT_POINTS_HIGH_TO_LOW
                    R.id.rb_sort_points_low_to_high -> SortMethod.SORT_POINTS_LOW_TO_HIGH
                    else -> currentSortMethod
                }

                viewModel.sortTaskListData(currentSortMethod)

                dialogBinding.root.postDelayed({
                    if (dialog.isShowing) dialog.dismiss()
                }, 300)
        }
        dialog.show()
    }

    private fun closeCountItemDialog() {
        if (countItemDialog != null) {
            if (countItemDialog!!.isShowing) countItemDialog!!.dismiss()
            countItemDialog = null
        }
    }
}