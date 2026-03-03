package com.wingstars.count.activity

import ExchangeHistoryAdapter
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.wingstars.count.R
import com.wingstars.count.databinding.ActivityExchangeHistoryBinding

class ExchangeHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExchangeHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityExchangeHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupEvents()
        handleTabSelection()
    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleTabSelection()
    }

    private fun handleTabSelection() {
        val targetTab = intent.getIntExtra("EXTRA_TARGET_TAB", -1)

        if (targetTab != -1) {
            binding.viewPager.currentItem = targetTab
            intent.removeExtra("EXTRA_TARGET_TAB")
        }
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