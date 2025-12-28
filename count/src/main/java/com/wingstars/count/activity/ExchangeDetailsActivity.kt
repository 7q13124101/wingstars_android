package com.wingstars.count.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wingstars.base.net.beans.CRMCouponsAvailableResponse
import com.wingstars.count.R
import com.wingstars.count.Repository.ActivityStatusEnum
import com.wingstars.count.databinding.ActivityExchangeDetailsBinding
import com.wingstars.count.databinding.DialogOtpCouponsBinding
import com.wingstars.count.databinding.DialogPublicPopupBoxBinding
import com.wingstars.count.viewmodel.ActivityDetailsExchangeViewModel
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.listener.OnPageChangeListener
import java.text.SimpleDateFormat
import java.util.Locale

class ExchangeDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExchangeDetailsBinding
    private val viewModel: ActivityDetailsExchangeViewModel by viewModels()

    private lateinit var data: CRMCouponsAvailableResponse
    private var status: String? = null
    private var couponCode: String? = null
    private var fullDataList: ArrayList<CRMCouponsAvailableResponse> = arrayListOf()
    private var currentIndex: Int = 0
    private var qrCodeDialog: android.app.Dialog? = null
    private val handler = Handler(Looper.getMainLooper())
    private var checkStatusRunnable: Runnable? = null
    private var currentBannerImages: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExchangeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindow()
        loadDataFromIntent()
        initListeners()
        setupObservers()

        viewModel.getMemberPointFromDetailsData()
    }

    private fun setupWindow() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadDataFromIntent() {
        val serializableData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("data", CRMCouponsAvailableResponse::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("data") as? CRMCouponsAvailableResponse
        }

        if (serializableData != null) {
            data = serializableData
        } else {
            Toast.makeText(this, "Data error", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val listExtra = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("EXTRA_LIST_DATA", ArrayList::class.java) as? ArrayList<CRMCouponsAvailableResponse>
        } else {
            intent.getSerializableExtra("EXTRA_LIST_DATA") as? ArrayList<CRMCouponsAvailableResponse>
        }

        if (listExtra != null && listExtra.isNotEmpty()) {
            fullDataList = listExtra
            currentIndex = intent.getIntExtra("EXTRA_CURRENT_INDEX", 0)
        } else {
            // Fallback: Tạo list giả nếu không có
            if (::data.isInitialized) {
                fullDataList.add(data)
                currentIndex = 0
            }
        }

        status = intent.getStringExtra("status")
        couponCode = intent.getStringExtra("couponCode")

        displayCouponDetails(data)
        updateButtonState()
    }

    private fun initListeners() {
        binding.imgBack.setOnClickListener { finish() }

        // Animation toggle logic
        binding.rlRuleHeader.setOnClickListener { toggleSection(binding.tvUsageRules, binding.ivArrow) }
        binding.rlInformation.setOnClickListener { toggleSection(binding.tvInformation, binding.ivArrow1) }
        binding.rlPrecautions.setOnClickListener { toggleSection(binding.tvPrecautions, binding.ivArrow3) }

        binding.btnExchange.setOnClickListener {
            handleExchangeButtonClick()
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
        }

        viewModel.messages.observe(this) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.otpData.observe(this) { otpData ->
            val couponId = data.id
            if (otpData != null && couponId.isNotEmpty()) {
                val otpCode = otpData.otp?.code ?: ""
                showOtpDialog(couponId, otpCode)
            }
        }

        viewModel.redeemSuccessfully.observe(this) { successMessage ->
            showSuccessDialog()
        }
        viewModel.couponQRCode.observe(this) { qrData ->
            if (!qrData.isNullOrEmpty()) {
                if (qrCodeDialog != null && qrCodeDialog!!.isShowing) {
                    val ivQrCode = qrCodeDialog!!.findViewById<ImageView>(R.id.iv_qr_code)
                    if (ivQrCode != null) {
                        if (qrData.startsWith("http")) {
                            Glide.with(this)
                                .load(qrData)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .dontAnimate()
                                .into(ivQrCode)
                            ivQrCode.tag = qrData
                        } else {
                            val bitmap = createQRCodeBitmap(qrData, 1000, 1000)
                            if (bitmap != null) {
                                ivQrCode.setImageBitmap(bitmap)
                            }
                            ivQrCode.tag = null
                        }
                    }
                } else {
                    showComplexBarcodeDialog(qrData)
                }
                viewModel.setLoop(true)
                loopCheckCouponStatus()
            }
        }

        viewModel.haveUsedCoupon.observe(this) { isUsed ->
            if (isUsed) {
                if (qrCodeDialog != null && qrCodeDialog!!.isShowing) {
                    qrCodeDialog!!.dismiss()
                }
                if (checkStatusRunnable != null) handler.removeCallbacks(checkStatusRunnable!!)
                viewModel.setLoop(false)

                Toast.makeText(this, "使用成功！", Toast.LENGTH_LONG).show()
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                if (viewModel.getLoop()) {
                    loopCheckCouponStatus()
                }
            }
        }
    }

    private fun displayCouponDetails(item: CRMCouponsAvailableResponse) {
        binding.couponName.text = item.couponName
        binding.pointCost.text = "${item.pointCost ?: 0} 點"
        binding.tvCouponTime.text = "${item.redeemStartAt ?: ""} ~ ${item.redeemEndAt ?: ""}"
        val eligibleMembersStr = data.eligibleMembersStr
        if (!eligibleMembersStr.isNullOrEmpty() && eligibleMembersStr != getString(R.string.all_members)) {
            binding.status.text = eligibleMembersStr
        } else {
            binding.status.text = getString(R.string.all_members)
        }
        binding.maxPerMember.text = if (data.maxPerMember == -1) getString(R.string.NoLimit) else "${data.maxPerMember} 次"
        binding.activityTime.text = "${data.totalQuantity ?: 0}"
        binding.exchangeLocation.text = data.redeemStore?.joinToString(", ") ?: ""
        binding.tvUsageRules.text = item.usageRules
        binding.tvInformation.text = item.description

        val newImages = item.galleryImages ?: listOfNotNull(item.coverImage)
        if (currentBannerImages != newImages) {
            currentBannerImages = newImages
            initBanner(newImages)
        }
    }

    private fun handleExchangeButtonClick() {
        if (status == ActivityStatusEnum.UNUSED_REDEMPTION.name) {
            val codeToUse = if (!couponCode.isNullOrEmpty()) couponCode!! else data.id

            if (codeToUse.isEmpty()) {
                Toast.makeText(this, "Coupon Code Error", Toast.LENGTH_SHORT).show()
                return
            }
            viewModel.crmCouponQRCode(codeToUse)
        } else {
            if (data.otpRequired) {
                viewModel.getOTPCoupons(data.id)
            } else {
                val randomOtp = (100000..999999).random().toString()
                showOtpDialog(data.id, randomOtp)
            }
        }
    }

    private fun updateButtonState() {
        if (status == ActivityStatusEnum.UNUSED_REDEMPTION.name) {
            binding.btnExchange.text = "開啟條碼"
            binding.btnExchange.isEnabled = true
        } else {
            binding.btnExchange.text = "立即兌換"
            binding.btnExchange.isEnabled = true
        }
    }

    private fun showComplexBarcodeDialog(initialQrUrl: String?) {
        val dialog = Dialog(this, com.google.android.material.R.style.Theme_MaterialComponents_Light_Dialog)
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_exchange_barcode)

        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)
        }

        val btnNext = dialog.findViewById<View>(R.id.btn_next)
        val btnPrev = dialog.findViewById<View>(R.id.btn_prev)
        val tvName = dialog.findViewById<android.widget.TextView>(R.id.tv_exchange_name)
        val tvPeriod1 = dialog.findViewById<android.widget.TextView>(R.id.tv_exchange_period1)
        val ivImage = dialog.findViewById<ImageView>(R.id.iv_goods_image)
        val ivQrCode = dialog.findViewById<ImageView>(R.id.iv_qr_code)
        val tvQrEnlarge = dialog.findViewById<android.widget.TextView>(R.id.tv_qr_code)
        val btnClose = dialog.findViewById<ImageView>(R.id.iv_close_dialog)
        val labelContainer = dialog.findViewById<View>(R.id.label)
        val labelTv = dialog.findViewById<TextView>(R.id.label_tv)

        fun zoomQrCode() {
            val currentUrl = ivQrCode.tag as? String
            val zoomDialog = Dialog(this)
            zoomDialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
            zoomDialog.setContentView(R.layout.dialog_zoom_qr)
            zoomDialog.window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
            val ivZoomedQr = zoomDialog.findViewById<ImageView>(R.id.iv_zoomed_qr)
            val ivCloseZoom = zoomDialog.findViewById<ImageView>(R.id.iv_close_zoom)

            if (!currentUrl.isNullOrEmpty() && currentUrl.startsWith("http")) {
                Glide.with(this).load(currentUrl).into(ivZoomedQr)
            } else {
                if (ivQrCode.drawable != null) {
                    ivZoomedQr.setImageDrawable(ivQrCode.drawable.constantState?.newDrawable())
                }
            }
            ivCloseZoom.setOnClickListener { zoomDialog.dismiss() }
            zoomDialog.show()
        }
        ivQrCode.setOnClickListener { zoomQrCode() }
        tvQrEnlarge.setOnClickListener { zoomQrCode() }

        fun updateContent(index: Int, qrUrlToLoad: String?, updateBackground: Boolean) {
            if (index < 0 || index >= fullDataList.size) return
            val item = fullDataList[index]

            // 1. Cập nhật Dialog UI
            tvName.text = item.couponName
            tvPeriod1.text = "兌換期間：${item.redeemStartAt ?: ""} ~ ${item.redeemEndAt ?: ""}"
            val imgUrl = if (!item.galleryImages.isNullOrEmpty()) item.galleryImages[0] else item.coverImage
            Glide.with(this).load(imgUrl).placeholder(R.drawable.bg_round_image).into(ivImage)

            val eligibleMembersStr = item.eligibleMembersStr
            if (!eligibleMembersStr.isNullOrEmpty() && eligibleMembersStr != getString(R.string.all_members)) {
                labelContainer?.visibility = View.VISIBLE
                labelTv?.text = eligibleMembersStr
            } else {
                labelContainer?.visibility = View.GONE
            }

            btnPrev.isEnabled = index > 0
            btnPrev.alpha = if (index > 0) 1.0f else 0.5f
            btnNext.isEnabled = index < fullDataList.size - 1
            btnNext.alpha = if (index < fullDataList.size - 1) 1.0f else 0.5f

            if (qrUrlToLoad != null) {
                if (qrUrlToLoad.startsWith("http")) {
                    Glide.with(this).load(qrUrlToLoad).dontAnimate().into(ivQrCode)
                    ivQrCode.tag = qrUrlToLoad
                } else {
                    val bitmap = createQRCodeBitmap(qrUrlToLoad, 1000, 1000)
                    if (bitmap != null) ivQrCode.setImageBitmap(bitmap)
                    ivQrCode.tag = null
                }
            } else {
                val codeString = item.couponCode ?: item.id
                if (codeString.isNotEmpty()) {
                    val bitmap = createQRCodeBitmap(codeString, 1000, 1000)
                    if (bitmap != null) {
                        ivQrCode.setImageBitmap(bitmap)
                    } else {
                        ivQrCode.setImageResource(R.drawable.ic_qr_code_placeholder)
                    }
                }
                ivQrCode.tag = null
            }
            updateActivityBackgroundData(item)
        }
        updateContent(currentIndex, initialQrUrl, false)

        btnNext.setOnClickListener {
            if (currentIndex < fullDataList.size - 1) {
                currentIndex++
                updateContent(currentIndex, null, true)
                val code = fullDataList[currentIndex].couponCode ?: fullDataList[currentIndex].id
                viewModel.crmCouponQRCode(code)
            }
        }

        btnPrev.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                updateContent(currentIndex, null, true)
                val code = fullDataList[currentIndex].couponCode ?: fullDataList[currentIndex].id
                viewModel.crmCouponQRCode(code)
            }
        }

        btnClose.setOnClickListener { dialog.dismiss() }
        dialog.setOnDismissListener { viewModel.setLoop(false) }

        qrCodeDialog = dialog
        dialog.show()
    }

    private fun createQRCodeBitmap(content: String, width: Int, height: Int): android.graphics.Bitmap? {
        return try {
            val bitMatrix = com.google.zxing.MultiFormatWriter().encode(
                content,
                com.google.zxing.BarcodeFormat.QR_CODE,
                width,
                height
            )
            val w = bitMatrix.width
            val h = bitMatrix.height
            val pixels = IntArray(w * h)
            for (y in 0 until h) {
                for (x in 0 until w) {
                    pixels[y * w + x] = if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                }
            }
            android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888).apply {
                setPixels(pixels, 0, w, 0, 0, w, h)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun updateActivityBackgroundData(newData: CRMCouponsAvailableResponse) {
        data = newData
        couponCode = newData.couponCode
        displayCouponDetails(newData)
    }
    private fun loopCheckCouponStatus() {
        if (checkStatusRunnable != null) {
            handler.removeCallbacks(checkStatusRunnable!!)
        }
        checkStatusRunnable = Runnable {
            if (viewModel.getLoop()) {
                viewModel.findHaveUsedCouponsData(data.id)
            }
        }
        handler.postDelayed(checkStatusRunnable!!, 3000)
    }

    private fun showOtpDialog(couponId: String, otpCode: String) {
        val otpBinding = DialogOtpCouponsBinding.inflate(LayoutInflater.from(this))
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(otpBinding.root)
        bottomSheetDialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? android.widget.FrameLayout
            bottomSheet?.let {
                it.setBackgroundColor(Color.TRANSPARENT)
                val displayMetrics = resources.displayMetrics
                val layoutParams = it.layoutParams
                layoutParams.height = (displayMetrics.heightPixels * 0.7).toInt()
                it.layoutParams = layoutParams
                val behavior = com.google.android.material.bottomsheet.BottomSheetBehavior.from(it)
                behavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }

        otpBinding.etInput.visibility = View.VISIBLE
        otpBinding.title.visibility = View.VISIBLE
        otpBinding.content.visibility = View.VISIBLE

        otpBinding.title.text = "驗證碼確認"
        otpBinding.content.text = "請輸入驗證碼確認身分。兌換後不可取消，是否繼續？"
        otpBinding.tvOtpCode.text = otpCode
        otpBinding.ivCloseDialog.setOnClickListener { bottomSheetDialog.dismiss() }
        otpBinding.etInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val inputCode = s.toString().trim()
                val targetCode = otpCode.trim()

                if (inputCode.length >= targetCode.length) {
                    if (inputCode == targetCode) {
                        bottomSheetDialog.dismiss()
                        viewModel.crmRedeemCoupon(couponId, targetCode)
                    } else {
                        Toast.makeText(this@ExchangeDetailsActivity, "驗證碼錯誤！", Toast.LENGTH_SHORT).show()
                        otpBinding.etInput.text.clear()
                    }
                }
            }
        })
        bottomSheetDialog.show()
    }

    private fun showSuccessDialog() {
        val successBinding = DialogPublicPopupBoxBinding.inflate(LayoutInflater.from(this))
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(successBinding.root)
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }

        successBinding.tvDialogTitle.text = "兌換成功！"
        successBinding.tvDialogContent.text = "您的兌換券已放入「兌換歷程」，隨時可查看使用。"
        successBinding.ivDialogImage.setImageResource(R.drawable.ic_takamei)

        successBinding.tvDialogConfirm.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, ExchangeHistoryActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
        dialog.show()
    }

    private fun initBanner(images: List<String>) {
        if (images.isEmpty()) {
            binding.bannerUserGuideImage.visibility = View.GONE
            return
        }
        binding.bannerUserGuideImage.visibility = View.VISIBLE

        val bannerAdapter = object : BannerImageAdapter<String>(images) {
            override fun onBindView(holder: BannerImageHolder, data: String, position: Int, size: Int) {
                Glide.with(holder.itemView)
                    .load(data)
                    .centerCrop()
                    .into(holder.imageView)
            }
        }
        binding.bannerUserGuideImage.setAdapter(bannerAdapter)
        binding.bannerUserGuideImage.setLoopTime(3000)

        binding.bannerUserGuideImage.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                binding.tvIndicator.text = "${position + 1}/${images.size}"
            }
            override fun onPageScrolled(p: Int, p1: Float, p2: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
        })
        binding.tvIndicator.text = "1/${images.size}"
    }

    private fun toggleSection(content: View, arrow: ImageView) {
        content.isVisible = !content.isVisible
        arrow.animate().rotation(if (content.isVisible) 180f else 0f).setDuration(200).start()
    }
    override fun onDestroy() {
        super.onDestroy()
        viewModel.setLoop(false)
        if (checkStatusRunnable != null) {
            handler.removeCallbacks(checkStatusRunnable!!)
        }
        if (qrCodeDialog != null && qrCodeDialog!!.isShowing) {
            qrCodeDialog!!.dismiss()
        }
    }
}