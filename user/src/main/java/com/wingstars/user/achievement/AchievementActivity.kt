package com.wingstars.user.achievement

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.wingstars.user.databinding.ActivityAchievementBinding

class AchievementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAchievementBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAchievementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        showEmptyState(true)

        binding.imgBack.setOnClickListener {
            finish()
        }
    }

    private fun showEmptyState(isShow: Boolean) {
        if (isShow) {
            binding.llEmpty.visibility = View.VISIBLE
        } else {
            binding.llEmpty.visibility = View.GONE
        }
    }
}