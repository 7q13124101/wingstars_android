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
import android.view.Window
import com.wingstars.count.activity.ActivityExchangeActivity
import com.wingstars.count.activity.CountHistoryActivity
import com.wingstars.count.activity.ExchangeHistoryActivity
import com.wingstars.count.activity.GiftExchangeActivity
import com.wingstars.count.databinding.DialogPublicPopupBoxBinding

class CountFragment : BaseFragment(){
    private lateinit var viewModel : CountViewModel
    private lateinit var binding: FragmentCountBinding
    private var statusBarHeight = 0
    private lateinit var countTitleAdapter: CountTitleAdapter
    private var select =0
    private var eventType ="limited"
    private lateinit var adapter: CountSingleAdapter

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



    private fun initView() {
        viewModel = ViewModelProvider(this)[CountViewModel::class.java]

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

        binding.titleList.adapter = countTitleAdapter

        adapter = CountSingleAdapter(requireActivity(), ArrayList())
        binding.girlsList.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL, false
        )
        binding.girlsList.adapter = adapter
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
        val testData = mutableListOf(
            CountSingleItemViewModel("安之軒 10/03 生日留言","到官方FB專頁，在生日貼文留言祝福，... ","2025年10月3日", "10 點", R.drawable.ic_count_im, R.drawable.bg_count_deep),
            CountSingleItemViewModel("2025 WS LOGO卡冊 Get!","凡購買10月份指定商品即可獲得點數! ","2025年4月2日 ～ 2025年12月31日", "20 點", R.drawable.ic_count_im, R.drawable.bg_count_deep),
            CountSingleItemViewModel("2025 WS 女孩卡冊 Get!","凡購買11月份指定商品即可獲得點數! ","2025年4月2日 ～ 2025年12月31日", "20 點", R.drawable.ic_count_im, R.drawable.bg_count_deep),
            CountSingleItemViewModel("YouTube 星迷","訂閱官方 YouTube 頻道","2025年10月3日", "1 點", R.drawable.ic_count_im, R.drawable.bg_count_deep)
        )
        adapter.setList(testData)
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



    public fun setViewTop(view: View, top: Int){
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        layoutParams.topMargin = top
        view.setLayoutParams(layoutParams);
    }

}