package com.wingstars.count.activity

import ExchangeHistoryAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.wingstars.count.R
import com.wingstars.count.databinding.ActivityExchangeHistoryBinding

class ExchangeHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExchangeHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExchangeHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupEvents()
    }

    private fun setupViewPager() {
        val adapter = ExchangeHistoryAdapter(this)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.count_not_used)
                1 -> tab.text = getString(R.string.count_have_used)
            }
        }.attach()
    }

    private fun setupEvents() {
        binding.imgBack.setOnClickListener {
            finish()
        }
    }
}