package com.wingstars.calendar.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.wingstars.base.base.BaseActivity
import com.wingstars.calendar.R
import com.wingstars.calendar.databinding.ActivityEventDetailsBinding
import com.wingstars.calendar.viewmodel.EventDetailsViewModel

class EventDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityEventDetailsBinding
    private lateinit var viewModel: EventDetailsViewModel

    private var isContentVisible = true
    private var isPrecautionsVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEventDetailsBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[EventDetailsViewModel::class.java]

        setTitleFoot(binding.root)

        initData()
        initView()
    }

    private fun initData() {

    }

    override fun initView() {
        binding.title.setBackClickListener { finish() }

        binding.llEventInformation.setOnClickListener {
            isContentVisible = !isContentVisible

            if (isContentVisible) {
                binding.tvEventInformationContent.visibility=View.VISIBLE
                binding.ivEventInformation.setImageResource(R.drawable.calendar_ic_arrow_up)
            } else {
                binding.tvEventInformationContent.visibility=View.GONE
                binding.ivEventInformation.setImageResource(R.drawable.calendar_ic_arrow_down)
            }
        }

        binding.llPrecautions.setOnClickListener {
            isPrecautionsVisible = !isPrecautionsVisible

            if (isPrecautionsVisible) {
                binding.tvPrecautionsContent.visibility=View.VISIBLE
                binding.ivPrecautions.setImageResource(R.drawable.calendar_ic_arrow_up)
            } else {
                binding.tvPrecautionsContent.visibility=View.GONE
                binding.ivPrecautions.setImageResource(R.drawable.calendar_ic_arrow_down)
            }
        }

    }

}