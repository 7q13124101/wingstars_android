package com.wingstars.count.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.wingstars.count.R
import com.wingstars.count.Repository.EventState
import com.wingstars.count.Repository.MessageEvent
import com.wingstars.count.adapter.CountNewDetailAdapter
import com.wingstars.count.databinding.ActivityGiftExchangeBinding
import com.wingstars.count.databinding.DialogPublicPopupSortTypeBinding
import com.wingstars.count.dialog.SortMethod
import com.wingstars.count.viewmodel.GiftExchangeViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class GiftExchangeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGiftExchangeBinding
    private lateinit var viewModel: GiftExchangeViewModel
    private lateinit var adapter: CountNewDetailAdapter
    private var currentSortMethod: SortMethod = SortMethod.SORT_DATE_NEW_TO_OLD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGiftExchangeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        EventBus.getDefault().register(this)
        viewModel = ViewModelProvider(this)[GiftExchangeViewModel::class.java]

        initView()
        initData()

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

//    override fun onResume() {
//        super.onResume()
//        viewModel.setProductCouponsInfo(-1, currentSortMethod)
//    }

    private fun initView() {
        adapter = CountNewDetailAdapter(this, mutableListOf()) { item ->
            val intent = Intent(this, GiftDetailsActivity::class.java)
            intent.putExtra("data", item)
            intent.putExtra("status", "GIFT_REDEEMED")
//            intent.putStringArrayListExtra("memberCards", viewModel.memberCards)
            intent.putExtra("count", binding.tvCountWin.text.toString())
            startActivity(intent)
        }

        binding.rvGoodsList.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 2)
        binding.rvGoodsList.adapter = adapter

        setupSearchInput()
    }

    private fun initData() {
        val countIntent = intent.getStringExtra("count")
        val count = countIntent?.toIntOrNull() ?: -1
        viewModel.setProductCouponsInfo(count, currentSortMethod)
        viewModel.searchActivityData.observe(this) { list ->
            adapter.setList(list)
            handleEmptyState(list.size)
        }

        viewModel.productCouponsData.observe(this) {
            adapter.setList(it)
        }

        viewModel.countWS.observe(this) { countVal ->
            binding.tvCountWin.text = "$countVal"
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.srlProductCoupons.autoRefresh()
            } else {
                binding.srlProductCoupons.finishRefresh()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent?) {
        if (event == null) return
        if (event.event == EventState.GLOBAL_REFRESH.name) {
            binding.tvCountWin.text = "${event.count}"
            val currentKeyword = binding.etSearch.text.toString()
            viewModel.searchData(currentKeyword, currentSortMethod)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
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

        when (currentSortMethod) {
            SortMethod.SORT_DATE_NEW_TO_OLD -> dialogBinding.rbSortDateNewToOld.isChecked = true
            SortMethod.SORT_DATE_OLD_TO_NEW -> dialogBinding.rbSortDateOldToNew.isChecked = true
            SortMethod.SORT_POINTS_HIGH_TO_LOW -> dialogBinding.rbSortPointsHighToLow.isChecked = true
            SortMethod.SORT_POINTS_LOW_TO_HIGH -> dialogBinding.rbSortPointsLowToHigh.isChecked = true
            else -> dialogBinding.rbSortDateNewToOld.isChecked = true
        }

        dialogBinding.rgSort.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton = group.findViewById<RadioButton>(checkedId)
            if (selectedRadioButton != null) {
                val selectedText = selectedRadioButton.text.toString()
                binding.tvList.text = selectedText
            }

            var newSortMethod = currentSortMethod
            when (checkedId) {
                R.id.rb_sort_date_new_to_old -> newSortMethod = SortMethod.SORT_DATE_NEW_TO_OLD
                R.id.rb_sort_date_old_to_new -> newSortMethod = SortMethod.SORT_DATE_OLD_TO_NEW
                R.id.rb_sort_points_high_to_low -> newSortMethod = SortMethod.SORT_POINTS_HIGH_TO_LOW
                R.id.rb_sort_points_low_to_high -> newSortMethod = SortMethod.SORT_POINTS_LOW_TO_HIGH
            }

            if (newSortMethod != currentSortMethod) {
                currentSortMethod = newSortMethod
                // Gọi ViewModel để sắp xếp lại list hiện tại
                val keyword = binding.etSearch.text.toString().trim()
                viewModel.searchData(keyword, currentSortMethod)
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
                viewModel.searchData(query, currentSortMethod)
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