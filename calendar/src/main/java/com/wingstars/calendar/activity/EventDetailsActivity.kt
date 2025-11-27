package com.wingstars.calendar.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.wingstars.base.base.BaseActivity
import com.wingstars.calendar.databinding.ActivityEventDetailsBinding
import com.wingstars.calendar.viewmodel.EventDetailsViewModel

class EventDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityEventDetailsBinding
    private lateinit var viewModel: EventDetailsViewModel

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
    }

}