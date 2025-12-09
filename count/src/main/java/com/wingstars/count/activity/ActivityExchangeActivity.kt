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
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

        adapter = CountListAdapter(this, null) { item ->
            android.util.Log.d("DEBUG_CLICK", "Đã click vào item: ${item.title}")
            val intent = Intent(this, ExchangeDetailsActivity::class.java)
            intent.putExtra("EXTRA_GIFT_ITEM", item)

            startActivity(intent)
        }

        binding.rvGoodsList.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 1)
        binding.rvGoodsList.adapter = adapter

        setupSearchInput()
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
            CountListItemViewModel(1,"有鷹來同樂 TSG Party -  Wing Stars 簽名會（第三梯次）", "2025/11/09 (日)", "100", R.drawable.bg_round_image,"所有會員皆適用","1次","80","澄清湖棒球場（高雄市烏松區大埤路113號）","部分商品數量有限，換完為止。 \n" +
                    "兌換成功後無法轉讓、取消或更換其他商品，請確認兌換內容無誤後再行操作。 \n" +
                    "本券僅限於指定兌換地點使用，請於現場出示 APP 票券QRCODE條碼。 \n" +
                    "兌換券使用期限至2026年02月02日止，活動內容如有異動，將依球團公告為準。 \n" +
                    "期限內若無完成兌換，本券視同失效，點數不予退回。 ","aa",""),
            CountListItemViewModel(2,"有鷹來同樂 TSG Party -  Wing Stars 簽名會（第三梯次）", "2025/11/09 (日)", "100", R.drawable.bg_round_image,"所有會員皆適用","1次","80","澄清湖棒球場（高雄市烏松區大埤路113號）","部分商品數量有限，換完為止。 \n" +
                    "兌換成功後無法轉讓、取消或更換其他商品，請確認兌換內容無誤後再行操作。 \n" +
                    "本券僅限於指定兌換地點使用，請於現場出示 APP 票券QRCODE條碼。 \n" +
                    "兌換券使用期限至2026年02月02日止，活動內容如有異動，將依球團公告為準。 \n" +
                    "期限內若無完成兌換，本券視同失效，點數不予退回。 ","aa",""),
            CountListItemViewModel(3,"有鷹來同樂 TSG Party -  Wing Stars 簽名會（第三梯次）", "2025/11/09 (日)", "100", R.drawable.bg_round_image,"所有會員皆適用","1次","80","澄清湖棒球場（高雄市烏松區大埤路113號）","部分商品數量有限，換完為止。 \n" +
                    "兌換成功後無法轉讓、取消或更換其他商品，請確認兌換內容無誤後再行操作。 \n" +
                    "本券僅限於指定兌換地點使用，請於現場出示 APP 票券QRCODE條碼。 \n" +
                    "兌換券使用期限至2026年02月02日止，活動內容如有異動，將依球團公告為準。 \n" +
                    "期限內若無完成兌換，本券視同失效，點數不予退回。 ","aa",""),
            CountListItemViewModel(4,"安芝儇 x Mingo 一日店長，專屬福利送不停", "2025/09/20 (六)", "150", R.drawable.bg_round_image,"所有會員皆適用","1次","80","澄清湖棒球場（高雄市烏松區大埤路113號）","部分商品數量有限，換完為止。 \n" +
                    "兌換成功後無法轉讓、取消或更換其他商品，請確認兌換內容無誤後再行操作。 \n" +
                    "本券僅限於指定兌換地點使用，請於現場出示 APP 票券QRCODE條碼。 \n" +
                    "兌換券使用期限至2026年02月02日止，活動內容如有異動，將依球團公告為準。 \n" +
                    "期限內若無完成兌換，本券視同失效，點數不予退回。 ","aa","")
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