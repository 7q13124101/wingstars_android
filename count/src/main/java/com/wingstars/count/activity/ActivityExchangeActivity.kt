package com.wingstars.count.activity

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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingstars.count.R
import com.wingstars.count.adapter.CountListAdapter
import com.wingstars.count.databinding.ActivityExchangeBinding
import com.wingstars.count.databinding.DialogPublicPopupSortTypeBinding
import com.wingstars.count.viewmodel.CountListItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActivityExchangeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExchangeBinding
    private lateinit var adapter: CountListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityExchangeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        loadData()

        binding.imgBack.setOnClickListener {
            finish()
        }
//        binding.llEmpty.visibility = View.VISIBLE
        binding.tvList.setOnClickListener {
            showSortDialog()
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initView() {

        adapter = CountListAdapter(this, null)
        binding.rvGoodsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvGoodsList.adapter = adapter
    }

    private fun loadData() {
        lifecycleScope.launch {
            val fetchedList = withContext(Dispatchers.IO) {
                fetchListFromRepository()
            }
            adapter.setList(fetchedList.toMutableList())

            handleEmptyState(fetchedList.size)
        }
    }

    private fun showSortDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = DialogPublicPopupSortTypeBinding.inflate(LayoutInflater.from(this))

        dialog.setContentView(dialogBinding.root)

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
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
            }, 200)
        }
        dialog.show()
    }

    private fun handleEmptyState(dataSize: Int) {
        if (dataSize == 0) {
            binding.llEmpty.visibility = View.VISIBLE
            binding.rvGoodsList.visibility = View.GONE
        } else {
            binding.llEmpty.visibility = View.GONE
            binding.rvGoodsList.visibility = View.VISIBLE
        }
    }

    private suspend fun fetchListFromRepository(): List<CountListItemViewModel> {
        return listOf(
            CountListItemViewModel("有鷹來同樂 TSG Party -  Wing Stars 簽名會（第三梯次）", "2025/11/09 (日)", "100", R.drawable.bg_round_image),
            CountListItemViewModel("有鷹來同樂 TSG Party -  Wing Stars 簽名會（第三梯次）", "2025/11/09 (日)", "100", R.drawable.bg_round_image),
            CountListItemViewModel("有鷹來同樂 TSG Party -  Wing Stars 簽名會（第三梯次）", "2025/11/09 (日)", "100", R.drawable.bg_round_image),
            CountListItemViewModel("有鷹來同樂 TSG Party -  Wing Stars 簽名會（第三梯次）", "2025/11/09 (日)", "100", R.drawable.bg_round_image)
        )
    }
}