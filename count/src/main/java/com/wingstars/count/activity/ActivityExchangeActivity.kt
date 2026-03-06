package com.wingstars.count.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.Window
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingstars.count.R
import com.wingstars.count.Repository.ActivityStatusEnum
import com.wingstars.count.adapter.ActivityExchangeAdapter
import com.wingstars.count.databinding.ActivityExchangeBinding
import com.wingstars.count.databinding.DialogPublicPopupSortTypeBinding
import com.wingstars.count.dialog.SortMethod
import com.wingstars.count.viewmodel.ActivityExchangeViewModel

class ActivityExchangeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExchangeBinding
    private lateinit var adapter: ActivityExchangeAdapter
    private val viewModel: ActivityExchangeViewModel by viewModels()
    private var currentSortMethod = SortMethod.SORT_DATE_NEW_TO_OLD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExchangeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        setupObservers()
        initListeners()

        viewModel.activityListData(currentSortMethod)
        viewModel.getMemberPointFromDetailsData()
    }

    private fun initView() {
        adapter = ActivityExchangeAdapter { item ->
            val currentList = viewModel.searchActivityData.value ?: arrayListOf()
            val index = currentList.indexOf(item)

            val intent = Intent(this, ExchangeDetailsActivity::class.java).apply {
                putExtra("data", item)
                putExtra("status", ActivityStatusEnum.GIFT_REDEEMED.name)
                putExtra("count",binding.tvCountWin.text)
                val listToSend = ArrayList(currentList)
                putExtra("EXTRA_LIST_DATA", listToSend)
                putExtra("EXTRA_CURRENT_INDEX", index)
            }
            startActivity(intent)
        }

        binding.rvGoodsList.layoutManager = LinearLayoutManager(this)
        binding.rvGoodsList.adapter = adapter

        setupSearchInput()
    }

    private fun initListeners() {
        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.tvList.setOnClickListener {
            showSortDialog()
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                 binding.srlProductCoupons.autoRefresh()
            } else {
                binding.srlProductCoupons.finishRefresh()
            }
        }

        viewModel.searchActivityData.observe(this) { activities ->
            adapter.submitList(activities)
            handleEmptyState(activities.isNullOrEmpty())
        }

        viewModel.points.observe(this) { points ->
            binding.tvCountWin.text = points
        }

        viewModel.errorMessage.observe(this) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
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

        when (currentSortMethod) {
            SortMethod.SORT_DATE_NEW_TO_OLD -> dialogBinding.rbSortDateNewToOld.isChecked = true
            SortMethod.SORT_DATE_OLD_TO_NEW -> dialogBinding.rbSortDateOldToNew.isChecked = true
            SortMethod.SORT_POINTS_HIGH_TO_LOW -> dialogBinding.rbSortPointsHighToLow.isChecked = true
            SortMethod.SORT_POINTS_LOW_TO_HIGH -> dialogBinding.rbSortPointsLowToHigh.isChecked = true
            else -> dialogBinding.rbSortDateNewToOld.isChecked = true
        }

        dialogBinding.ivCloseDialog.setOnClickListener { dialog.dismiss() }
        dialogBinding.rgSort.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton = group.findViewById<RadioButton>(checkedId)
            binding.tvList.text = selectedRadioButton?.text ?: getString(R.string.dialog_sort_type)

            currentSortMethod = when (checkedId) {
                R.id.rb_sort_date_old_to_new -> SortMethod.SORT_DATE_OLD_TO_NEW
                R.id.rb_sort_points_high_to_low -> SortMethod.SORT_POINTS_HIGH_TO_LOW
                R.id.rb_sort_points_low_to_high -> SortMethod.SORT_POINTS_LOW_TO_HIGH
                else -> SortMethod.SORT_DATE_NEW_TO_OLD
            }
            val keyword = binding.etSearch.text.toString()
            viewModel.searchData(keyword, currentSortMethod)

            dialog.dismiss()
        }
        dialog.show()
    }

    private fun handleEmptyState(isEmpty: Boolean) {
        binding.llEmpty.isVisible = isEmpty
        binding.rvGoodsList.isVisible = !isEmpty
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSearchInput() {
        val etSearch = binding.etSearch
        val clearDrawable = ContextCompat.getDrawable(this, R.drawable.ic_cancel_search)

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                viewModel.searchData(query, currentSortMethod)
                val drawable = if (query.isNotEmpty()) clearDrawable else null
                etSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null)
            }
        })

        etSearch.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = etSearch.compoundDrawablesRelative[2]
                if (drawableEnd != null) {
                    if (event.rawX >= (etSearch.right - etSearch.paddingRight - drawableEnd.bounds.width())) {
                        etSearch.text.clear()
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
    }
}