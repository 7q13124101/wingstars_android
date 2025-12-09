package com.wingstars.count.activity

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wingstars.count.databinding.ActivityCountItemBinding
import com.wingstars.count.databinding.DialogPublicPopupBoxBinding
import com.wingstars.count.viewmodel.CountSingleItemViewModel

class Count_Item_Activity : AppCompatActivity() {

    private lateinit var binding: ActivityCountItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCountItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
        loadData()
    }

    private fun initView() {
        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.btnConfirm.setOnClickListener {
            showSuccessDialog()
        }
    }

    private fun loadData() {
        val item: CountSingleItemViewModel? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("EXTRA_ITEM_DATA", CountSingleItemViewModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("EXTRA_ITEM_DATA")
        }
        item?.let { data ->
            binding.tvTitle.text = data.title
            binding.tvCount.text = data.count
            binding.tvTimeItem.text = data.time

            if (data.detailContent.isNotEmpty()) {
                binding.tvDetail.text = data.detailContent
            }

            if (data.rules.isNotEmpty()) {
                binding.tvDescription.text = data.rules
            }
        }
    }

    private fun showSuccessDialog() {
        val dialogBinding = DialogPublicPopupBoxBinding.inflate(LayoutInflater.from(this))
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(dialogBinding.root)

        bottomSheetDialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }

        dialogBinding.tvDialogTitle.text = "獲得點數"
        dialogBinding.tvDialogContent.text = "恭喜！你獲得 1 點！"

        dialogBinding.tvDialogConfirm.setOnClickListener {
            bottomSheetDialog.dismiss()
            updateButtonToCompleted()
        }

        bottomSheetDialog.show()
    }

    private fun updateButtonToCompleted() {
        binding.btnConfirm.apply {

            text = "已完成"
            isEnabled = false
            backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E5E7EB"))

        }
    }

}