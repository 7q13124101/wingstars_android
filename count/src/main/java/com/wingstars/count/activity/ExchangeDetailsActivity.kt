package com.wingstars.count.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wingstars.count.R
import com.wingstars.count.databinding.ActivityExchangeDetailsBinding
import com.wingstars.count.databinding.DialogOtpCouponsBinding
import com.wingstars.count.databinding.DialogPublicPopupBoxBinding
import com.wingstars.count.viewmodel.CountListItemViewModel
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.listener.OnPageChangeListener
import kotlin.random.Random

class ExchangeDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExchangeDetailsBinding
    private var currentOtpCode = ""
    private var dataList: ArrayList<CountListItemViewModel> = arrayListOf()
    private var currentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityExchangeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadData()
        initView()
        initBanner()
    }

    // ===================== DATA =====================
    private fun loadData() {
        val receivedList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("EXTRA_LIST_DATA", CountListItemViewModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra("EXTRA_LIST_DATA")
        }
        currentPosition = intent.getIntExtra("EXTRA_CURRENT_POSITION", 0)
        if (receivedList != null) {
            dataList = receivedList
            val currentItem = dataList.getOrNull(currentPosition)

            currentItem?.let { it ->
                binding.couponName.text = it.title
                binding.pointCost.text = "${it.count} 點"
                binding.tvCouponTime.text = it.time
                binding.status.text = it.exitem
                binding.maxPerMember.text = it.limit
                binding.activityTime.text = it.total
                binding.finishTime.text = it.location
                binding.tvUsageRules.text = it.usageRules
                binding.tvInformation.text = it.information
                binding.tvPrecautions.text = it.description
            }
        }
    }

    // ===================== VIEW =====================
    private fun initView() {
        binding.imgBack.setOnClickListener { finish() }

        binding.rlRuleHeader.setOnClickListener {
            toggleSection(binding.tvUsageRules, binding.ivArrow)
        }

        binding.rlInformation.setOnClickListener {
            toggleSection(binding.tvInformation, binding.ivArrow1)
        }

        binding.rlPrecautions.setOnClickListener {
            toggleSection(binding.tvPrecautions, binding.ivArrow3)
        }


        val checkTypeButton = intent.getIntExtra("checkButton", 0)
        val qrCodeButtonState = intent.getIntExtra("qrCodeButton", 0)

        if (checkTypeButton == 2) {
            binding.button.visibility = View.GONE
        } else {
            binding.button.visibility = View.VISIBLE
            if (qrCodeButtonState == 1) {
                binding.btnExchange.text = "開啟條碼"
                binding.btnExchange.setOnClickListener {
                    if (dataList.isNotEmpty()) {
                        showBarcodeDialog(currentPosition, dataList)
                    }
                }
            }else {
                setupButtonUI(checkTypeButton)

                binding.btnExchange.setOnClickListener {
                    if (checkTypeButton == 0) {
                        showOtpDialog()
                    }
                }
            }
        }
    }

    // ===================== BANNER =====================
    private fun initBanner() {

        val imageList = listOf(
            R.drawable.bg_round_image,
            R.drawable.bg_round_image,
            R.drawable.bg_round_image
        )

        val bannerAdapter = object : BannerImageAdapter<Int>(imageList) {
            override fun onBindView(
                holder: BannerImageHolder,
                data: Int,
                position: Int,
                size: Int
            ) {
                // Load ảnh bằng Glide
                Glide.with(holder.itemView)
                    .load(data)
                    .into(holder.imageView)
            }
        }

        binding.bannerUserGuideImage.setAdapter(bannerAdapter)
        binding.bannerUserGuideImage.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                binding.tvIndicator.text = "${position + 1}/${imageList.size}"
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        binding.tvIndicator.text = "1/${imageList.size}"
        binding.bannerUserGuideImage.start()
    }

    // ===================== OTP =====================
    private fun showOtpDialog() {
        val otpBinding = DialogOtpCouponsBinding.inflate(LayoutInflater.from(this))
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(otpBinding.root)
        bottomSheetDialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout
            bottomSheet?.let {
                it.setBackgroundColor(Color.TRANSPARENT)
                val displayMetrics = resources.displayMetrics
                val height = displayMetrics.heightPixels
                val layoutParams = it.layoutParams
                layoutParams.height = (height * 0.7).toInt()
                it.layoutParams = layoutParams
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }

        otpBinding.etInput.visibility = View.VISIBLE
        otpBinding.title.visibility = View.VISIBLE
        otpBinding.content.visibility = View.VISIBLE
        otpBinding.title.text = "驗證碼確認"
        otpBinding.content.text = "請輸入驗證碼確認身分。兌換後不可取消，是否繼續？"
        fun refreshOtp() {
            val newCode = Random.nextInt(100000, 999999).toString()
            currentOtpCode = newCode
            otpBinding.tvOtpCode.text = newCode
        }

        refreshOtp()

        otpBinding.ivCloseDialog.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        otpBinding.etInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                if (input.length == 6) {
                    if (input == currentOtpCode) {
                        bottomSheetDialog.dismiss()
                        showSuccessDialog()
                    } else {
                        Toast.makeText(this@ExchangeDetailsActivity, "驗證碼錯誤！", Toast.LENGTH_SHORT).show()
                        otpBinding.etInput.text.clear()
                        refreshOtp()
                    }
                }
            }
        })
        bottomSheetDialog.show()
    }

    // ===================== SUCCESS =====================
    private fun showSuccessDialog() {
        val successBinding = DialogPublicPopupBoxBinding.inflate(LayoutInflater.from(this))
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(successBinding.root)

        bottomSheetDialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }

        successBinding.tvDialogTitle.text = "兌換成功"
        successBinding.tvDialogContent.text = "您的兌換券已放入「兌換歷程」，隨時可查看使用。"
        successBinding.ivDialogImage.setImageResource(R.drawable.ic_takamei)

        successBinding.tvDialogConfirm.setOnClickListener {
            bottomSheetDialog.dismiss()
            val intent = Intent(this, ExchangeHistoryActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.putExtra("EXTRA_TARGET_TAB", 0)
            startActivity(intent)
            finish()
        }

        bottomSheetDialog.show()
    }

    private fun showBarcodeDialog(startPosition: Int, dataList: List<CountListItemViewModel>) {
        if (dataList.isEmpty()) return
        val context = this
        val dialog = Dialog(context, com.google.android.material.R.style.Theme_MaterialComponents_Light_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_exchange_barcode)

        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            decorView.setPadding(0, 0, 0, 0)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)
        }

        val btnClose = dialog.findViewById<ImageView>(R.id.iv_close_dialog)
        val btnNext = dialog.findViewById<AppCompatButton>(R.id.btn_next)
        val btnPrev = dialog.findViewById<AppCompatButton>(R.id.btn_prev)
        val tvName = dialog.findViewById<TextView>(R.id.tv_exchange_name)
        val tvPeriod = dialog.findViewById<TextView>(R.id.tv_exchange_period1)
        val ivImage = dialog.findViewById<ImageView>(R.id.iv_goods_image)
        val ivQrCode = dialog.findViewById<ImageView>(R.id.iv_qr_code)
        val tvQrEnlarge = dialog.findViewById<TextView>(R.id.tv_qr_code)

        var currentDialogPosition = startPosition

        fun zoomQrCode() {
            val zoomDialog = Dialog(context)
            zoomDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            zoomDialog.setContentView(R.layout.dialog_zoom_qr)
            zoomDialog.window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                decorView.setPadding(0, 0, 0, 0)
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
            val ivZoomedQr = zoomDialog.findViewById<ImageView>(R.id.iv_zoomed_qr)
            val btnCloseZoom = zoomDialog.findViewById<ImageView>(R.id.iv_close_zoom)

            if (ivQrCode.drawable != null) {
                ivZoomedQr.setImageDrawable(ivQrCode.drawable.constantState?.newDrawable())
            }
            btnCloseZoom.setOnClickListener { zoomDialog.dismiss() }
            zoomDialog.show()
        }

        ivQrCode.setOnClickListener { zoomQrCode() }
        tvQrEnlarge.setOnClickListener { zoomQrCode() }

        fun updateDialogUI() {
            val item = dataList[currentDialogPosition]

            tvName.text = item.title
            tvPeriod.text = item.time
            Glide.with(context).load(item.leftImageRes).into(ivImage)
            if (currentDialogPosition == 0) {
                btnPrev.isEnabled = false
                btnPrev.setTextColor(ContextCompat.getColor(context, R.color.color_101828))
            } else {
                btnPrev.isEnabled = true
                btnPrev.setTextColor(ContextCompat.getColor(context, R.color.color_E2518D))
            }

            if (currentDialogPosition == dataList.size - 1) {
                btnNext.isEnabled = false
                btnNext.alpha = 0.5f
            } else {
                btnNext.isEnabled = true
                btnNext.alpha = 1.0f
            }
        }

        updateDialogUI()

        btnClose.setOnClickListener { dialog.dismiss() }

        btnNext.setOnClickListener {
            if (currentDialogPosition < dataList.size - 1) {
                currentDialogPosition++
                updateDialogUI()
            }
        }

        btnPrev.setOnClickListener {
            if (currentDialogPosition > 0) {
                currentDialogPosition--
                updateDialogUI()
            }
        }

        dialog.show()
    }

    private fun setupButtonUI(type: Int) {
        if (type == 0) {
            binding.btnExchange.text = "立即兌換"
            binding.btnExchange.isEnabled = true
        } else {
            binding.btnExchange.text = "已兌換贈品"
        }
    }

    // ===================== TOGGLE =====================
    private fun toggleSection(content: View, arrow: ImageView) {
        if (content.visibility == View.VISIBLE) {
            content.visibility = View.GONE
            arrow.animate().rotation(0f).setDuration(200).start()
        } else {
            content.visibility = View.VISIBLE
            arrow.animate().rotation(180f).setDuration(200).start()
        }
    }
}
