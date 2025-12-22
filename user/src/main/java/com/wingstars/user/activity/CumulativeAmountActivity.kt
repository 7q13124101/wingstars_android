package com.wingstars.user.activity

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.wingstars.user.R
import com.wingstars.user.databinding.ActivityCumulativeAmountBinding
import com.wingstars.user.databinding.DialogUserPopupSortTypeBinding

class CumulativeAmountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCumulativeAmountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCumulativeAmountBinding.inflate(layoutInflater)
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

        binding.tvList.setOnClickListener {
            showSortDialog()
        }
    }

    private fun showEmptyState(isShow: Boolean) {
        if (isShow) {
            binding.llEmpty.visibility = View.VISIBLE
        } else {
            binding.llEmpty.visibility = View.GONE
        }
    }

    private fun showSortDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = DialogUserPopupSortTypeBinding.inflate(LayoutInflater.from(this))

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

            when (checkedId) {
                R.id.rb_sort_date_new_to_old -> { }
                R.id.rb_sort_date_old_to_new -> { }
                R.id.rb_sort_points_high_to_low -> { }
                R.id.rb_sort_points_low_to_high -> { }
            }

            dialogBinding.root.postDelayed({
                if (!isFinishing && !isDestroyed && dialog.isShowing) {
                    dialog.dismiss()
                }
            }, 500)
        }
        dialog.show()
    }
}