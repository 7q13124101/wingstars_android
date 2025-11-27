package com.wingstars.count.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.wingstars.count.R
import com.wingstars.count.adapter.CountNewDetailAdapter
import com.wingstars.count.databinding.ActivityGiftExchangeBinding
import com.wingstars.count.databinding.DialogPublicPopupSortTypeBinding
import com.wingstars.count.viewmodel.CountNewDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GiftExchangeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGiftExchangeBinding
    private lateinit var adapter: CountNewDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGiftExchangeBinding.inflate(layoutInflater)
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

        adapter = CountNewDetailAdapter(this, null)
        binding.rvGoodsList.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 2)
        binding.rvGoodsList.adapter = adapter

        setupSearchInput()
    }

    private fun loadData() {
        lifecycleScope.launch {
            val fetchedList = withContext(Dispatchers.IO) {
                fetchDetailFromRepository()
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

    private suspend fun fetchDetailFromRepository(): List<CountNewDetailViewModel> {
        return listOf(
            CountNewDetailViewModel(R.drawable.ic_count_gift_4,"2025 WS單曲寫真壓克力鑰匙圈","2499"),
            CountNewDetailViewModel(R.drawable.ic_count_gift_3,"2025 WS單曲紀念專輯吊飾","2099"),
            CountNewDetailViewModel(R.drawable.ic_count_gift_2,"2025 WS單曲寫真女孩貼紙包","1699"),
            CountNewDetailViewModel(R.drawable.ic_count_gift_1,"2025 WS LOGO杯墊","799"),
            CountNewDetailViewModel(R.drawable.ic_count_gift_5,"2025 WS LOGO燈箱吊飾","499"),
            CountNewDetailViewModel(R.drawable.ic_count_gift_6,"2024 WS立牌拼圖｜WS款","199")
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSearchInput() {
        val etSearch = binding.etSearch
        val clearDrawable = ContextCompat.getDrawable(this, R.drawable.ic_cancel_search)

        fun updateClearButton(text: String) {
            if (text.isNotEmpty()) {
                etSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, clearDrawable, null)
            } else {
                etSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
            }
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()

                updateClearButton(query)

                adapter.filter(query)

                handleEmptyState(adapter.itemCount)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        etSearch.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = etSearch.compoundDrawablesRelative[2]
                if (drawableEnd != null) {
                    if (event.rawX >= (etSearch.right - drawableEnd.bounds.width() - etSearch.paddingEnd)) {
                        etSearch.text.clear()
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
    }
}