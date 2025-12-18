package com.wingstars.calendar.activity

import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.net.beans.WSCalendarNResponse
import com.wingstars.calendar.R
import com.wingstars.calendar.databinding.ActivityEventDetailsBinding
import com.wingstars.calendar.utils.CalendarDateUtils.Companion.formatCalendarDate
import com.wingstars.calendar.viewmodel.CalendarViewModel
import com.wingstars.calendar.viewmodel.EventDetailsViewModel

class EventDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityEventDetailsBinding
    private lateinit var viewModel: EventDetailsViewModel
    private var selectedWSCalendar: WSCalendarNResponse? = null
    private var isContentVisible = true
    private var isPrecautionsVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEventDetailsBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[EventDetailsViewModel::class.java]
        setTitleFoot(binding.root)

        selectedWSCalendar =
            intent.extras?.getSerializable("WSCalendar_Details") as WSCalendarNResponse

        initData()
        initView()
    }

    private fun initData() {
        binding.tvTitleName.text = Html.fromHtml(selectedWSCalendar?.titleF ?: "")
        val stDate = selectedWSCalendar?.st_dateF ?: ""
        val edDate = selectedWSCalendar?.ed_dateF ?: ""
        binding.tvDate.text = formatCalendarDate(stDate, edDate)

        binding.tvMap.text = Html.fromHtml(selectedWSCalendar?.locationF ?: "")
        binding.tvEventInformationContent.text = Html.fromHtml(selectedWSCalendar?.contentF ?: "")
        binding.tvPrecautionsContent.text = Html.fromHtml(selectedWSCalendar?.precautionsF ?: "")

        if (selectedWSCalendar?.contentF != null) {
            if (selectedWSCalendar!!.contentF.equals(CalendarViewModel.CalendarCategory.GENERAL_ACTIVITY)) {
                binding.ivImageType.setImageResource(R.drawable.calendar_ic_star)
            } else if (selectedWSCalendar!!.contentF.equals(CalendarViewModel.CalendarCategory.BIRTHDAY)) {
                binding.ivImageType.setImageResource(R.drawable.calendar_ic_grey_birthday)
            }
        }

        Glide.with(binding.ivDialogImage).clear(binding.ivDialogImage)
        if (selectedWSCalendar!!.urlF.isNotEmpty()) {
            Glide.with(binding.ivDialogImage)
                .load(selectedWSCalendar!!.urlF)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .dontAnimate()
                .into(binding.ivDialogImage)
        } else {
            Glide.with(this)
                .load(R.drawable.calendar_ic_photo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .dontAnimate()
                .into(binding.ivDialogImage)
        }
    }

    override fun initView() {
        binding.title.setBackClickListener { finish() }

        binding.llEventInformation.setOnClickListener {
            isContentVisible = !isContentVisible

            if (isContentVisible) {
                binding.tvEventInformationContent.visibility = View.VISIBLE
                binding.ivEventInformation.setImageResource(R.drawable.calendar_ic_arrow_up)
            } else {
                binding.tvEventInformationContent.visibility = View.GONE
                binding.ivEventInformation.setImageResource(R.drawable.calendar_ic_arrow_down)
            }
        }

        binding.llPrecautions.setOnClickListener {
            isPrecautionsVisible = !isPrecautionsVisible

            if (isPrecautionsVisible) {
                binding.tvPrecautionsContent.visibility = View.VISIBLE
                binding.ivPrecautions.setImageResource(R.drawable.calendar_ic_arrow_up)
            } else {
                binding.tvPrecautionsContent.visibility = View.GONE
                binding.ivPrecautions.setImageResource(R.drawable.calendar_ic_arrow_down)
            }
        }

    }

}