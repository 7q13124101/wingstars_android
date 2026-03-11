package com.wingstars.count.activity

import android.app.Activity
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
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wingstars.base.net.beans.CRMCouponsAvailableResponse
import com.wingstars.base.net.beans.CRMCouponsResponse
import com.wingstars.count.R
import com.wingstars.count.Repository.ActivityStatusEnum
import com.wingstars.count.databinding.ActivityGiftDetailsBinding
import com.wingstars.count.databinding.DialogOtpCouponsBinding
import com.wingstars.count.databinding.DialogPublicPopupBoxBinding
import com.wingstars.count.viewmodel.ActivityDetailsExchangeViewModel
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GiftDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGiftDetailsBinding
    private lateinit var viewModel: ActivityDetailsExchangeViewModel
    private lateinit var data: CRMCouponsAvailableResponse
    private var memberCards: ArrayList<String>? = null
    private var currentOtpCode = ""
    private var status: String? = null
    private var claimedCount = 0
    private var couponCode: String? = null
    private var fullDataList: ArrayList<CRMCouponsAvailableResponse> = arrayListOf()
    private var currentIndex: Int = 0
    private var qrCodeDialog: android.app.Dialog? = null
    private val handler = Handler(Looper.getMainLooper())
    private var checkStatusRunnable: Runnable? = null
    private var currentItemInDialog: CRMCouponsAvailableResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityGiftDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        EventBus.getDefault().register(this)

        viewModel = ViewModelProvider(this)[ActivityDetailsExchangeViewModel::class.java]

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadData()
        initObservers()
        initView()
    }

    fun String?.formatDate(): String {
        if (this.isNullOrEmpty()) return ""
        return try {
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
            val outputFormat = java.text.SimpleDateFormat("yyyy/MM/dd HH:mm", java.util.Locale.getDefault())

            val date = inputFormat.parse(this)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            this
        }
    }

    private fun loadData() {
        status = intent.getStringExtra("status")
        couponCode = intent.getStringExtra("couponCode")
        memberCards = intent.getStringArrayListExtra("memberCards")

        val couponDataExtra = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("coupon_data", CRMCouponsResponse.Coupon::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("coupon_data") as? CRMCouponsResponse.Coupon
        }
        val serializableData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("data", CRMCouponsAvailableResponse::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("data") as? CRMCouponsAvailableResponse
        }

        if (couponDataExtra != null) {
            val toJson = com.google.gson.Gson().toJson(couponDataExtra)
            val type = object : com.google.gson.reflect.TypeToken<CRMCouponsAvailableResponse>() {}.type
            data = com.google.gson.Gson().fromJson(toJson, type)
        } else if (serializableData != null) {
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
            if (::data.isInitialized) {
                fullDataList.add(data)
                currentIndex = 0
            }
        }

        val pageTitle = intent.getStringExtra("title") ?: getString(R.string.gift_details)
        binding.title.text = pageTitle
        status = intent.getStringExtra("status")
        if (status.isNullOrEmpty()) {
            status = ActivityStatusEnum.GIFT_REDEEMED.name
        }
        val countStr = intent.getStringExtra("count") ?: "0"
        memberCards = intent.getStringArrayListExtra("memberCards")
        couponCode = intent.getStringExtra("couponCode")

        binding.couponName.text = data.couponName ?: ""
        binding.pointCost.text = "${data.pointCost ?: 0} 點"
        binding.tvCouponTime.text = "${data.couponStartDate.formatDate()} ~ ${data.couponEndDate.formatDate()}"


        val eligibleMembersStr = data.eligibleMembersStr
        if (!eligibleMembersStr.isNullOrEmpty() && eligibleMembersStr != getString(R.string.all_members)) {
            binding.status.text = eligibleMembersStr
        } else {
            binding.status.text = getString(R.string.all_members)
        }

        binding.maxPerMember.text = if (data.maxPerMember == -1) getString(R.string.NoLimit) else "${data.maxPerMember} 次"
        binding.activityTime.text = "${data.totalQuantity ?: 0}"
//        binding.exchangeLocation.text = data.redeemStore?.joinToString(", ") ?: ""
        binding.exchangeLocation.text = data.redeemStore?.joinToString(", ")?.ifEmpty { null } ?: "不限定兌換地點"

        binding.tvUsageRules.text = data.description ?: ""
        binding.tvPrecautions.text = data.usageRules ?: ""

        val imageUrl = if (!data.galleryImages.isNullOrEmpty()) data.galleryImages[0] else data.coverImage
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.gift_details_image_background)
                .into(binding.merchandise)
        }

        when (status) {
            ActivityStatusEnum.USED_REDEMPTION.name -> {
                binding.btnExchange.visibility = View.GONE
                binding.status.text = getString(R.string.count_have_used)
            }
            ActivityStatusEnum.UNUSED_REDEMPTION.name -> {
                binding.btnExchange.visibility = View.VISIBLE
                val claimStartAt = data.claimStartAt
                if (claimStartAt != null) {
                    val claimDate = parseDate(claimStartAt)
                    if (claimDate != null && claimDate.after(Date())) {
                        binding.btnExchange.text = getString(R.string.not_yet_open)
                        disableButton()
                    } else {
                        binding.btnExchange.text = getString(R.string.count_activate_barcode)
                        setButtonRestore()
                    }
                } else {
                    binding.btnExchange.text = getString(R.string.count_activate_barcode)
                    setButtonRestore()
                }
                binding.status.text = getString(R.string.count_not_used)
            }
            ActivityStatusEnum.GIFT_REDEEMED.name -> {
                binding.btnExchange.visibility = View.VISIBLE
                val currentPoints = countStr.toIntOrNull() ?: 0
                claimedCount = data.claimedCount?.toInt() ?: 0
                setButtonBackground(data.pointCost ?: 0, currentPoints, claimedCount, data.maxPerMember)
            }
        }
    }

    private fun setButtonBackground(pointCost: Int, point: Int, claimedCount: Int, maxPerMember: Int) {
        if (point < pointCost) {
            binding.btnExchange.text = getString(R.string.insufficient_points)
            disableButton()
            return
        }
//        binding.button.visibility = View.VISIBLE
//        binding.btnExchange.isEnabled = true
//        val redeemStartAt = data.redeemStartAt
//        if (redeemStartAt != null) {
//            val startDate = parseDate(redeemStartAt)
//            if (startDate != null && startDate.after(Date())) {
//                binding.btnExchange.text = getString(R.string.not_yet_open)
//                disableButton()
//                return

        binding.btnExchange.text = getString(R.string.count_activate_barcode)

        val couponStartDate = data.couponStartDate
        if (couponStartDate != null) {
            val startDate = parseDate(couponStartDate)
            if (startDate != null && startDate.after(Date())) {
                binding.btnExchange.text = getString(R.string.not_yet_open)
                disableButton()
                return
            }
        }

//        val redeemEndAt = data.redeemEndAt
//        if (redeemEndAt != null) {
//            val endDate = parseDate(redeemEndAt)
//            if (endDate != null && Date().after(endDate)) {
//                binding.btnExchange.text = getString(R.string.finished)
//                disableButton()
//                return

        val couponEndDate = data.couponEndDate
        if (couponEndDate != null) {
            val endDate = parseDate(couponEndDate)
            if (endDate != null && Date().after(endDate)) {
                binding.btnExchange.text = getString(R.string.finished)
                disableButton()
                return
            }
        }

        try {
            val eligibleMembers = data.eligibleMembers ?: emptyList<String>()
            val criteria = data.eligibilityCriteria

            if (!criteria.isNullOrEmpty() && eligibleMembers.isNotEmpty()) {
                if (memberCards.isNullOrEmpty()) {
                    setLimitedButtonText(eligibleMembers, data.eligibleMembersStr.orEmpty())
                    disableButton()
                    return
                }
                val hasOverlap = eligibleMembers.any { it in memberCards!! }
                if (!hasOverlap) {
                    setLimitedButtonText(eligibleMembers, data.eligibleMembersStr.orEmpty())
                    disableButton()
                    return
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (maxPerMember != -1) {
            if (claimedCount >= maxPerMember) {
                binding.btnExchange.text = getString(R.string.has_completed)
                disableButton()
                return
            }
        }

        val totalIssued = data.totalIssued ?: 0
        val totalQuantity = data.totalQuantity?: -1
        if (totalQuantity != -1 && totalIssued >= totalQuantity) {
            binding.btnExchange.text = getString(R.string.has_completed)
            disableButton()
            return
        }
        setButtonRestore()
    }

    private fun setButtonRestore(){
        if (status == ActivityStatusEnum.UNUSED_REDEMPTION.name) {
            binding.btnExchange.text = getString(R.string.count_activate_barcode)
        } else {
            binding.btnExchange.text = getString(R.string.redeem_immediately)
        }
        enableButton()
    }

    private fun setLimitedButtonText(eligibleMembers: List<String>, eligibleMembersStr: String) {
        if (eligibleMembers.size == 1) {
            val onlyName = if (eligibleMembersStr.isNotEmpty()) eligibleMembersStr else "指定會員"
            binding.btnExchange.text = "限${onlyName}會員兌換"
        } else {
            binding.btnExchange.text = getString(R.string.limit)
        }
    }

    private fun disableButton() {
        binding.btnExchange.setTextColor(getColor(R.color.color_101828))
        binding.btnExchange.setBackgroundResource(R.drawable.activity_error_button_background)
        binding.btnExchange.isEnabled = false
    }

    private fun enableButton() {
        binding.btnExchange.setTextColor(getColor(R.color.white))
        binding.btnExchange.setBackgroundResource(R.drawable.bg_login_btn_able)
        binding.btnExchange.isEnabled = true
    }

    private fun parseDate(dateStr: String): Date? {
        return try {
            val format = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
            format.parse(dateStr)
        } catch (e: Exception) {
            null
        }
    }

    private fun initObservers() {
        viewModel.isLoading.observe(this) {
        }
        viewModel.otpData.observe(this) { otp ->
            if (otp != null && !otp.otp.code.isNullOrEmpty()) {
                showOtpDialog(otp.otp.code!!)
            } else {
                Toast.makeText(this, "無法取得驗證碼", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.redeemSuccessfully.observe(this) { message ->
            setResult(Activity.RESULT_OK)
            showSuccessDialog()
        }
        viewModel.messages.observe(this) { msg ->
            if (!msg.isNullOrEmpty()) Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
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

    private fun initView() {
        binding.imgBack.setOnClickListener { finish() }
        binding.rlRuleHeader.setOnClickListener { toggleSection(binding.tvUsageRules, binding.ivArrow) }
        binding.rlPrecautions.setOnClickListener { toggleSection(binding.tvPrecautions, binding.ivArrow1) }
        binding.btnExchange.setOnClickListener { handleExchangeClick() }
    }

    private fun handleExchangeClick() {
//        android.util.Log.d("GiftDetails", "Current Status: $status")
        when (status) {
            ActivityStatusEnum.GIFT_REDEEMED.name -> {
                if (data.otpRequired) {
                    viewModel.getOTPCoupons(data.id)
                } else {
                    viewModel.crmRedeemCoupon(data.id, "")
//                    val randomOtp = (100000..999999).random().toString()
//                    showOtpDialog(randomOtp)
//                    showConfirmRedeemDialog()
                }
            }
            ActivityStatusEnum.UNUSED_REDEMPTION.name -> {
                if (couponCode.isNullOrEmpty()) {
                    Toast.makeText(this, getString(R.string.data_failed), Toast.LENGTH_SHORT).show()
                    return
                }
                viewModel.crmCouponQRCode(couponCode!!)
            }
        }
    }

//    private fun showConfirmRedeemDialog() {
//        val confirmDialog = DialogPublicPopupBoxBinding.inflate(LayoutInflater.from(this))
//        val confirmSheet = BottomSheetDialog(this)
//        confirmSheet.setContentView(confirmDialog.root)
//
//        confirmSheet.setOnShowListener { dialog ->
//            (dialog as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.setBackgroundColor(Color.TRANSPARENT)
//        }
//
//        confirmDialog.tvDialogTitle.text = "兌換確認"
//        confirmDialog.tvDialogContent.text = getString(R.string.exchange_content)
//        confirmDialog.tvDialogConfirm.text = getString(R.string.confirm)
//
//        confirmDialog.tvDialogConfirm.setOnClickListener {
//            confirmSheet.dismiss()
//            viewModel.crmRedeemCoupon(data.id, "")
//        }
//        confirmSheet.show()
//    }

    private fun showOtpDialog(serverOtpCode: String) {
        val otpBinding = DialogOtpCouponsBinding.inflate(LayoutInflater.from(this))
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(otpBinding.root)

        bottomSheetDialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout
            bottomSheet?.let {
                it.setBackgroundColor(Color.TRANSPARENT)
                val displayMetrics = resources.displayMetrics
                val layoutParams = it.layoutParams
                layoutParams.height = (displayMetrics.heightPixels * 0.7).toInt()
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
        otpBinding.tvOtpCode.text = serverOtpCode
        currentOtpCode = serverOtpCode

        otpBinding.ivCloseDialog.setOnClickListener { bottomSheetDialog.dismiss() }

        otpBinding.etInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val inputCode = s.toString().trim()
                val targetCode = currentOtpCode.trim()
                if (inputCode.length >= targetCode.length) {
                    if (inputCode == targetCode) {
                        bottomSheetDialog.dismiss()
                        viewModel.crmRedeemCoupon(data.id, targetCode)
                    } else {
                        Toast.makeText(this@GiftDetailsActivity, "驗證碼錯誤！", Toast.LENGTH_SHORT).show()
                        otpBinding.etInput.text.clear()
                    }
                }
            }
        })
        bottomSheetDialog.show()
    }

    private fun showComplexBarcodeDialog(initialQrUrl: String?) {
        val dialog = android.app.Dialog(this, com.google.android.material.R.style.Theme_MaterialComponents_Light_Dialog)
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

            val zoomDialog = android.app.Dialog(this)
            zoomDialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
            zoomDialog.setContentView(R.layout.dialog_zoom_qr)
            zoomDialog.window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
            val ivZoomedQr = zoomDialog.findViewById<ImageView>(R.id.iv_zoomed_qr)
            val ivCloseZoom = zoomDialog.findViewById<ImageView>(R.id.iv_close_zoom)

            if (!currentUrl.isNullOrEmpty()) {
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

        fun updateContent(index: Int, qrUrlToLoad: String?) {
            if (index < 0 || index >= fullDataList.size) return
            val item = fullDataList[index]
            tvName.text = item.couponName
            tvPeriod1.text = "兌換期間：${item.couponStartDate.formatDate() ?: ""} ~ ${item.couponEndDate.formatDate() ?: ""}"
            val imgUrl = if (!item.galleryImages.isNullOrEmpty()) item.galleryImages[0] else item.coverImage
            Glide.with(this).load(imgUrl).placeholder(R.drawable.bg_round_image).into(ivImage)

            val eligibleMembersStr = item.eligibleMembersStr
            if (!eligibleMembersStr.isNullOrEmpty() && eligibleMembersStr != getString(R.string.all_members)) {
                labelContainer?.visibility = View.VISIBLE
                labelTv?.text = eligibleMembersStr
            } else {
                labelContainer?.visibility = View.GONE
            }

            // Nút Next/Prev
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

//                if (codeString.isNotEmpty()) {
//                    val bitmap = createQRCodeBitmap(codeString, 600, 600)
//                    if (bitmap != null) {
//                        ivQrCode.setImageBitmap(bitmap)
//                    } else {
//                        ivQrCode.setImageResource(R.drawable.ic_qr_code_placeholder)
//                    }
//                } else {
//                    ivQrCode.setImageResource(R.drawable.ic_qr_code_placeholder)
//                }
                ivQrCode.tag = null
            }

            updateActivityBackgroundData(item)
        }

        updateContent(currentIndex, initialQrUrl)

        btnNext.setOnClickListener {
            if (currentIndex < fullDataList.size - 1) {
                currentIndex++
                updateContent(currentIndex, null)
                val code = fullDataList[currentIndex].couponCode ?: fullDataList[currentIndex].id
                viewModel.crmCouponQRCode(code)
            }
        }

        // Xử lý Prev
        btnPrev.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                updateContent(currentIndex, null)

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
        binding.title.setText(
            if (newData.couponType == 1)
                R.string.gift_details
            else
                R.string.exchange_details
        )
        binding.couponName.text = newData.couponName
        binding.pointCost.text = "${newData.pointCost ?: 0} 點"
        binding.activityTime.text = "${newData.totalQuantity ?: 0}"
        binding.exchangeLocation.text = newData.redeemStore?.joinToString(", ") ?: ""
        binding.tvUsageRules.text = newData.description ?: ""
        binding.tvPrecautions.text = newData.usageRules ?: ""

        Glide.with(this)
            .load(newData.galleryImages?.firstOrNull() ?: newData.coverImage)
            .into(binding.merchandise)
    }

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
            finish()
        }
        bottomSheetDialog.show()
    }

    private fun dpToPx(dp: Float): Int {
        return android.util.TypedValue.applyDimension(
            android.util.TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        ).toInt()
    }

    private fun loopCheckCouponStatus() {
        if (checkStatusRunnable != null) {
            handler.removeCallbacks(checkStatusRunnable!!)
        }

        // 2. Tạo lệnh check mới
        checkStatusRunnable = Runnable {
            if (viewModel.getLoop()) {
                viewModel.findHaveUsedCouponsData(data.id)
            }
        }
        handler.postDelayed(checkStatusRunnable!!, 3000)
    }

    private fun toggleSection(contentView: View, arrowView: ImageView) {
        if (contentView.visibility == View.VISIBLE) {
            contentView.visibility = View.GONE
            arrowView.animate().rotation(0f).setDuration(200).start()
        } else {
            contentView.visibility = View.VISIBLE
            arrowView.animate().rotation(180f).setDuration(200).start()
        }
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