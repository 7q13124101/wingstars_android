package com.wingstars.user.storelocation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.wingstars.user.R
import com.wingstars.user.databinding.ActivityStoreLocationsBinding
import java.util.*

class StoreLocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoreLocationsBinding
    private var calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true
//        window.statusBarColor = getColor(R.color.color_DE9DBA)

        binding = ActivityStoreLocationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        setDefaultDate()
        initClick()
        initRefresh()
    }

    private fun initView() {
        binding.ivBack.setOnClickListener { finish() }
    }

    private fun setDefaultDate() {
        updateDate()
    }

    private fun initClick() {
        binding.imgPre.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateDate()
        }

        binding.imgNext.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateDate()
        }
    }

    private fun initRefresh() {
        binding.srlUserRecord.setOnRefreshListener {
            resetToCurrentMonth()
            binding.srlUserRecord.finishRefresh()
        }
    }

    private fun resetToCurrentMonth() {
        calendar = Calendar.getInstance()
        updateDate()
    }

    private fun updateDate() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        binding.txtDate.text = String.format("%02d/%d", month, year)
    }
}
