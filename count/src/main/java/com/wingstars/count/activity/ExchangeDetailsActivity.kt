package com.wingstars.count.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.wingstars.base.net.beans.CRMCouponsAvailableResponse
import com.wingstars.count.R
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

    private var currentCoupon: CRMCouponsAvailableResponse? = null
    // Mặc định là FOR_EXCHANGE nếu không truyền status
    private var couponStatus: CouponStatus = CouponStatus.FOR_EXCHANGE

    enum class CouponStatus {
        FOR_EXCHANGE, UNUSED, USED
    }

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
        val jsonStr = intent.getStringExtra("EXTRA_COUPON_DATA")
        if (jsonStr != null) {
            currentCoupon = Gson().fromJson(jsonStr, CRMCouponsAvailableResponse::class.java)
        }
        val statusSerializable = intent.getSerializableExtra("EXTRA_COUPON_STATUS")
        if (statusSerializable != null) {
            couponStatus = statusSerializable as CouponStatus
        }

        if (currentCoupon == null) {
            Toast.makeText(this, "錯誤：找不到兌換券資料。", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        displayCouponDetails(currentCoupon!!)
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
        viewModel.isLoading.observe(this) {
            binding.srlProductCoupons.isVisible = it
        }

        viewModel.messages.observe(this) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.otpData.observe(this) { otpData ->
            val couponId = currentCoupon?.id
            if (otpData != null && !couponId.isNullOrEmpty()) {
                val otpCode = otpData.otp?.code ?: "" // Null safety
                showOtpDialog(couponId, otpCode)
            }
        }

        viewModel.redeemSuccessfully.observe(this) { successMessage ->
            if (!successMessage.isNullOrEmpty()) {
                showSuccessDialog()
            }
        }

        viewModel.couponQRCode.observe(this) { qrCode ->
            if (!qrCode.isNullOrEmpty()) {
                showBarcodeDialog(qrCode)
            }
        }
    }

    private fun displayCouponDetails(item: CRMCouponsAvailableResponse) {
        binding.couponName.text = item.couponName
        binding.pointCost.text = "${item.pointCost ?: 0} 點"
        val start = formatDate(item.redeemStartAt)
        val end = formatDate(item.redeemEndAt)
        binding.tvCouponTime.text = "$start ~ $end"
        binding.tvUsageRules.text = item.usageRules
        binding.tvInformation.text = item.description

        val images = item.galleryImages ?: listOf(item.coverImage)
        initBanner(images.filterNotNull())
    }

    private fun formatDate(dateStr: String?): String {
        if (dateStr.isNullOrEmpty()) return ""
        return try {
            val input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val output = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val date = input.parse(dateStr)
            output.format(date!!)
        } catch (e: Exception) {
            dateStr // Trả về gốc nếu lỗi parse
        }
    }

    private fun handleExchangeButtonClick() {
        when (couponStatus) {
            CouponStatus.FOR_EXCHANGE -> {
                // [SỬA 4] Logic kiểm tra OTP.
                // Nếu model chưa có field otpRequired, bạn có thể hardcode logic kiểm tra hoặc thêm vào model.
                // Ví dụ: kiểm tra verificationType == "otp"
                /* if (currentCoupon?.verificationType == "otp") {
                     currentCoupon?.id?.let { viewModel.getOTPCoupons(it) }
                } else {
                     showConfirmExchangeDialog()
                }
                */

                // Tạm thời gọi showConfirmExchangeDialog() cho luồng cơ bản
                showConfirmExchangeDialog()
            }
            CouponStatus.UNUSED -> {
                val userCouponCode = currentCoupon?.id ?: ""
                if (userCouponCode.isNotEmpty()) {
                    viewModel.crmCouponQRCode(userCouponCode)
                }
            }
            CouponStatus.USED -> { }
        }
    }

    private fun updateButtonState(userPoints: Int) {
        val coupon = currentCoupon ?: return

        when (couponStatus) {
            CouponStatus.FOR_EXCHANGE -> {
                val (canExchange, reason) = checkExchangeConditions(coupon, userPoints)
                binding.btnExchange.isEnabled = canExchange
                binding.btnExchange.text = if (canExchange) "立即兌換" else reason

//                if (!canExchange) {
//                    binding.btnExchange.setBackgroundResource(R.drawable.bg_button_disable)
//                }
            }
            CouponStatus.UNUSED -> {
                binding.btnExchange.isEnabled = true
                binding.btnExchange.text = "開啟條碼"
            }
            CouponStatus.USED -> {
                binding.btnExchange.isEnabled = false
                binding.btnExchange.text = "已使用"
            }
        }
    }

    private fun checkExchangeConditions(coupon: CRMCouponsAvailableResponse, userPoints: Int): Pair<Boolean, String> {
        val pointCost = coupon.pointCost ?: Int.MAX_VALUE
        if (userPoints < pointCost) return Pair(false, "點數不足")
        return Pair(true, "立即兌換")
    }

    private fun showConfirmExchangeDialog() {
        val dialogBinding = DialogPublicPopupBoxBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }
        dialogBinding.tvDialogTitle.text = "確認兌換"
        dialogBinding.tvDialogContent.text = "您確定要使用 ${currentCoupon?.pointCost} 點兌換此商品嗎？此操作無法復原。"
        dialogBinding.ivDialogImage.visibility = View.GONE

        dialogBinding.tvDialogConfirm.text = "確認"
        dialogBinding.tvDialogConfirm.setOnClickListener {
            dialog.dismiss()
            currentCoupon?.id?.let { viewModel.crmRedeemCoupon(it, "") }
        }
        dialog.show()
    }

    private fun showOtpDialog(couponId: String, otpCode: String) {
        val otpBinding = DialogOtpCouponsBinding.inflate(LayoutInflater.from(this))
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(otpBinding.root)
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }

        otpBinding.title.text = "驗證碼確認"
        otpBinding.content.text = "請輸入驗證碼以完成兌換。"
        otpBinding.tvOtpCode.text = otpCode
        otpBinding.ivCloseDialog.setOnClickListener { dialog.dismiss() }

        otpBinding.etInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 6) {
                    if (s.toString() == otpCode) {
                        viewModel.crmRedeemCoupon(couponId, s.toString())
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this@ExchangeDetailsActivity, "驗證碼錯誤！", Toast.LENGTH_SHORT).show()
                        s?.clear()
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        dialog.show()
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
            // Chuyển hướng
            val intent = Intent(this, ExchangeHistoryActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
        dialog.show()
    }

    // [SỬA 2] Thực hiện hàm hiển thị QR Code
    // Cần import com.google.zxing.* và com.journeyapps.barcodescanner.BarcodeEncoder
    private fun showBarcodeDialog(qrContent: String) {
        val qrBinding = DialogOtpCouponsBinding.inflate(LayoutInflater.from(this))
        val dialog = Dialog(this)
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setContentView(qrBinding.root)

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        qrBinding.ivCloseDialog.setOnClickListener { dialog.dismiss() }
        qrBinding.title.text = "兌換條碼"
        qrBinding.tvOtpCode.text = qrContent

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
        binding.bannerUserGuideImage.setLoopTime(3000) // Auto scroll

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
}