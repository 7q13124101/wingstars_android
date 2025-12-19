package com.wingstars.count.fragment

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingstars.base.base.BaseFragment
import com.wingstars.count.R
import com.wingstars.count.adapter.CountSingleAdapter
import com.wingstars.count.adapter.CountTitleAdapter
import com.wingstars.count.databinding.FragmentCountBinding
import com.wingstars.count.viewmodel.CountViewModel
import com.wingstars.count.viewmodel.CountSingleItemViewModel
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.Window
import android.widget.RadioButton
import androidx.core.widget.NestedScrollView
import com.wingstars.base.net.beans.EvtTaskResponse
import com.wingstars.count.activity.ActivityExchangeActivity
import com.wingstars.count.activity.CountHistoryActivity
import com.wingstars.count.activity.Count_Item_Activity
import com.wingstars.count.activity.ExchangeHistoryActivity
import com.wingstars.count.activity.GiftExchangeActivity
import com.wingstars.count.adapter.CountAdapter
import com.wingstars.count.databinding.DialogPublicPopupBoxBinding
import com.wingstars.count.databinding.DialogPublicPopupSortTypeBinding

class CountFragment : BaseFragment(){
    private lateinit var viewModel : CountViewModel
    private lateinit var binding: FragmentCountBinding
    private var statusBarHeight = 0
    private lateinit var countTitleAdapter: CountTitleAdapter
    private var select =0
    private var eventType ="limited"
    private lateinit var adapter: CountAdapter
    private var fullDataList: List<EvtTaskResponse> = ArrayList()
    private var isExpanded = false

    private data class CheckInDay(
        val reward: String,
        val dayLabel: String
    )

    private val daysData = listOf(
        CheckInDay("+1", "1天"),
        CheckInDay("+2", "2天"),
        CheckInDay("+3", "3天"),
        CheckInDay("+4", "4天"),
        CheckInDay("+5", "5天"),
        CheckInDay("+6", "6天"),
        CheckInDay("+10", "7天")
    )

    private var currentDayIndex = 0
    private var hasCheckedInToday = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCountBinding.inflate(inflater, container, false)
        val root = binding.root
        initView()
        return root
    }

    override fun onResume() {
        super.onResume()
        isExpanded = false
        if (fullDataList.isNotEmpty()) {
            updateListDisplay()
        }
    }


    private fun initView() {
        viewModel = ViewModelProvider(this)[CountViewModel::class.java]
        viewModel.getEvtTasks()
        val titleList = mutableListOf(
            getString(R.string.count_limited_time_task),
            getString(R.string.count_birthday_celebration),
            getString(R.string.count_star_fan_shopping),
//            getString(R.string.count_qa)
        )

        binding.titleList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        countTitleAdapter = CountTitleAdapter(requireActivity(), titleList, object :
            CountTitleAdapter.onItemListener {
            override fun onItemClick(data: String, position: Int) {
                if (select != position) {
                    countTitleAdapter.setSelectPosition(position)
                    eventType = when (position) {
                        0 -> "limited"
                        1 -> "daily"
                        2 -> ""
                        else -> "exclusive"
                    }
                    select = position
                }
            }
        }, 0)

        adapter = CountAdapter()
        adapter.onItemClick = { item ->
            val intent = Intent(requireActivity(), Count_Item_Activity::class.java)
            startActivity(intent)
        }

        binding.girlsList.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL, false
        )
        binding.girlsList.adapter = adapter

        viewModel.getEvtTasks()
        setupObservers()

        setupCheckInUI()
        binding.btnCheckIn.setOnClickListener {
            if (!hasCheckedInToday) {
                hasCheckedInToday = true
                setupCheckInUI()
                showCheckInSuccessDialog()
            }
        }

        binding.rlCount.setOnClickListener {
            val intent = Intent(requireActivity(), CountHistoryActivity::class.java)
            startActivity(intent)
        }

        binding.llGiftExchange.setOnClickListener {
            val intent = Intent(requireActivity(), GiftExchangeActivity::class.java)
            startActivity(intent)
        }

        binding.llActivityExchange.setOnClickListener {
            val intent = Intent(requireActivity(), ActivityExchangeActivity::class.java)
            startActivity(intent)
        }

        binding.llExchangeHistory.setOnClickListener {
            val intent = Intent(requireActivity(), ExchangeHistoryActivity::class.java)
            startActivity(intent)
        }

        binding.tvList.setOnClickListener {
            showSortDialog()
        }

        binding.tvViewMore.setOnClickListener {
            isExpanded = true
            updateListDisplay()
        }

        binding.top.setOnClickListener{
            binding.flTop.smoothScrollTo(0, 0)
        }

        binding.flTop.setOnScrollChangeListener { _: NestedScrollView, _: Int, scrollY: Int, _: Int, _: Int ->
            if (scrollY > 300) {
                if (binding.top.visibility != View.VISIBLE) {
                    binding.top.visibility = View.VISIBLE
                    binding.top.alpha = 0f
                    binding.top.animate().alpha(1f).setDuration(200).start()
                }
            } else {
                if (binding.top.visibility == View.VISIBLE && binding.top.alpha == 1f) {
                    binding.top.animate()
                        .alpha(0f)
                        .setDuration(200)
                        .withEndAction { binding.top.visibility = View.GONE }
                        .start()
                }
            }
        }


        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.VANILLA_ICE_CREAM){
            binding.root.setOnApplyWindowInsetsListener{ v, insets ->
                val statusBarHeight = insets.getInsets(WindowInsets.Type.statusBars()).top
                Log.e("statusBarHeight","statusBarHeight=$statusBarHeight")
                setViewTop(binding.title,statusBarHeight)
                binding.root.setOnApplyWindowInsetsListener(null)
                insets
            }
        }else{
            setViewTop(binding.title,getStatusBarHeight())
        }


    }

    private fun setupObservers() {
        viewModel.taskList.observe(viewLifecycleOwner) { list ->
            fullDataList = list ?: ArrayList()
            isExpanded = false // Reset trạng thái mở rộng khi có dữ liệu mới
            updateListDisplay()
        }
    }

    private fun setupCheckInUI() {
        val dayBindings = listOf(
            binding.day1, binding.day2, binding.day3,
            binding.day4, binding.day5, binding.day6, binding.day7
        )
        val colorPink = ContextCompat.getColor(requireContext(), R.color.color_EE97BB)
        val colorGray = ContextCompat.getColor(requireContext(), R.color.color_4A5565)
        val colorWhite = ContextCompat.getColor(requireContext(), R.color.white)

        daysData.forEachIndexed { index, data ->
            val dayBinding = dayBindings[index]
            val tvReward = dayBinding.tvReward
            val ivStar = dayBinding.ivStar
            val tvDayLabel = dayBinding.tvDayLabel
            val contentBox = dayBinding.llContentBox
            tvReward.text = data.reward
            tvDayLabel.text = if (index == currentDayIndex) "今天" else data.dayLabel

            when {
                // --- Ngày quá khứ ---
                index < currentDayIndex -> {
                    contentBox.setBackgroundResource(R.drawable.bg_checkin_item_future)
                    tvReward.setTextColor(colorGray)
                    tvDayLabel.setTextColor(colorGray)
                    ivStar.setImageResource(R.drawable.ic_star_outline)
                }

                // --- Ngày hôm nay ---
                index == currentDayIndex -> {
                    contentBox.setBackgroundResource(R.drawable.bg_checkin_item_today)
                    tvReward.setTextColor(colorPink)
                    tvDayLabel.setTextColor(colorPink)

                    if (hasCheckedInToday) {
                        ivStar.setImageResource(R.drawable.ic_star_filled)
                    } else {
                        ivStar.setImageResource(R.drawable.ic_star_filled)
                    }
                }

                // --- Ngày tương lai ---
                else -> {
                    contentBox.setBackgroundResource(R.drawable.bg_checkin_item_future)
                    tvReward.setTextColor(colorGray)
                    tvDayLabel.setTextColor(colorGray)
                    ivStar.setImageResource(R.drawable.ic_star_outline)
                }
            }
        }
        if (hasCheckedInToday) {
            binding.btnCheckIn.text = "已簽到"
            binding.btnCheckIn.isEnabled = false
            binding.btnCheckIn.alpha = 0.5f
        } else {
            binding.btnCheckIn.text = "點擊簽到"
            binding.btnCheckIn.isEnabled = true
            binding.btnCheckIn.alpha = 1.0f
        }
    }

    private fun showCheckInSuccessDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogBinding = DialogPublicPopupBoxBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)
        dialog.window?.apply {
            setGravity(android.view.Gravity.BOTTOM)
            setLayout(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        val fullText = "你獲得 1 星幣！"
        val spannableString = SpannableString(fullText)
        val startIndex = fullText.indexOf("1")
        if (startIndex >= 0) {
            spannableString.setSpan(
                ForegroundColorSpan(Color.parseColor("#FFC0CB")),
                startIndex,
                startIndex + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        dialogBinding.tvDialogContent.text = spannableString

        dialogBinding.tvDialogConfirm.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showSortDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = DialogPublicPopupSortTypeBinding.inflate(LayoutInflater.from(requireContext()))

        dialog.setContentView(dialogBinding.root)

        dialog.window?.apply {
            val displayMetrics = resources.displayMetrics
            val screenHeight = displayMetrics.heightPixels
            val halfScreenHeight = (screenHeight * 0.5).toInt()
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                halfScreenHeight
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setGravity(Gravity.BOTTOM)
        }

        dialogBinding.ivCloseDialog.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.rgSort.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton = group.findViewById<RadioButton>(checkedId)
            if (selectedRadioButton != null) {
                val selectedText = selectedRadioButton.text.toString()
                binding.tvList.text = selectedText
            }


//                when (checkedId) {
//                    R.id.rb_sort_date_new_to_old -> viewModel.sortTaskListData(CountViewModel.SortMethod.SORT_DATE_NEW_TO_OLD)
//                    R.id.rb_sort_date_old_to_new -> viewModel.sortTaskListData(CountViewModel.SortMethod.SORT_DATE_OLD_TO_NEW)
//                    R.id.rb_sort_points_high_to_low -> viewModel.sortTaskListData(CountViewModel.SortMethod.SORT_POINTS_HIGH_TO_LOW)
//                    R.id.rb_sort_points_low_to_high -> viewModel.sortTaskListData(CountViewModel.SortMethod.SORT_POINTS_LOW_TO_HIGH)
//            }

            dialogBinding.root.postDelayed({
                    if (dialog.isShowing) dialog.dismiss()
            }, 500)
        }
        dialog.show()
    }

    private fun updateListDisplay() {
        val displayList = if (isExpanded) {
            binding.tvViewMore.visibility = View.GONE
            fullDataList
        } else {
            if (fullDataList.size > 3) {
                binding.tvViewMore.visibility = View.VISIBLE
                fullDataList.take(3)
            } else {
                binding.tvViewMore.visibility = View.GONE
                fullDataList
            }
        }
        // Cập nhật vào Adapter
        adapter.setList(displayList)
    }



    public fun setViewTop(view: View, top: Int){
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        layoutParams.topMargin = top
        view.setLayoutParams(layoutParams);
    }

}