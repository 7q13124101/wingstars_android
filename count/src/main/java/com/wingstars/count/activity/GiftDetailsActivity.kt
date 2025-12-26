package com.wingstars.count.activity

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wingstars.base.net.beans.CRMCouponsAvailableResponse
import com.wingstars.count.R
import com.wingstars.count.databinding.ActivityGiftDetailsBinding
import com.wingstars.count.databinding.DialogOtpCouponsBinding
import com.wingstars.count.databinding.DialogPublicPopupBoxBinding
import com.wingstars.count.viewmodel.ActivityDetailsExchangeViewModel
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

    enum class ActivityStatusEnum {
        USED_REDEMPTION,
        UNUSED_REDEMPTION,
        GIFT_REDEEMED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityGiftDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    private fun loadData() {

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

        status = intent.getStringExtra("status")
        val countStr = intent.getStringExtra("count") ?: "0"
        memberCards = intent.getStringArrayListExtra("memberCards")


        binding.couponName.text = data.couponName ?: ""
        binding.pointCost.text = "${data.pointCost ?: 0} 點"
//        binding.tvCouponTime.text = data
//        binding.status.text = data
        binding.maxPerMember.text = if (data.maxPerMember == -1) getString(R.string.NoLimit) else "${data.maxPerMember} 次"
        binding.activityTime.text = "${data.totalQuantity ?: 0}"
        binding.finishTime.text = data.redeemStore?.joinToString(", ") ?: ""
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
                binding.status.text = getString(R.string.count_have_used) // "已使用"
            }
            ActivityStatusEnum.UNUSED_REDEMPTION.name -> {
                binding.btnExchange.visibility = View.VISIBLE
                val claimStartAt = data.claimStartAt
                if (claimStartAt != null) {
                    val claimDate = parseDate(claimStartAt)
                    if (claimDate != null && claimDate.after(Date())) {
                        binding.btnExchange.text = getString(R.string.not_yet_open) // "尚未開放"
                        disableButton()
                    } else {
                        binding.btnExchange.text = getString(R.string.count_activate_barcode) // "開啟兌換碼"
                        enableButton()
                    }
                } else {
                    binding.btnExchange.text = getString(R.string.count_activate_barcode)
                    enableButton()
                }
                binding.status.text = getString(R.string.count_not_used) // "未使用"
            }
            ActivityStatusEnum.GIFT_REDEEMED.name -> {
                val currentPoints = countStr.toIntOrNull() ?: 0
                claimedCount = data.claimedCount?.toInt() ?: 0
                setButtonBackground(data.pointCost ?: 0, currentPoints, claimedCount, data.maxPerMember)
            }
        }
    }

    private fun setButtonBackground(pointCost: Int, point: Int, claimedCount: Int, maxPerMember: Int) {
        val redeemStartAt = data.redeemStartAt
        if (redeemStartAt != null) {
            val startDate = parseDate(redeemStartAt)
            if (startDate != null && startDate.after(Date())) {
                binding.btnExchange.text = getString(R.string.not_yet_open)
                disableButton()
                return
            }
        }


        val redeemEndAt = data.redeemEndAt
        if (redeemEndAt != null) {
            val endDate = parseDate(redeemEndAt)
            if (endDate != null && Date().after(endDate)) {
                binding.btnExchange.text = getString(R.string.finished) // "已結束"
                disableButton()
                return
            }
        }

        if (maxPerMember != -1) {
            if (maxPerMember <= claimedCount) {
                binding.btnExchange.text = getString(R.string.redeemed) // "已兌換"
                disableButton()
                return
            }
        }

        if (point < pointCost) {
            binding.btnExchange.text = getString(R.string.insufficient_points) // "點數不足"
            disableButton()
            return
        }

        val totalIssued = data.totalIssued ?: 0
        val totalQuantity = data.totalQuantity
        if (totalQuantity != -1 && totalIssued >= totalQuantity) {
            binding.btnExchange.text = getString(R.string.has_completed) // "已兌完"
            disableButton()
            return
        }

        binding.btnExchange.text = getString(R.string.redeem_immediately) // "立即兌換"
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
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            format.parse(dateStr)
        } catch (e: Exception) {
            null
        }
    }


    private fun initObservers() {
        viewModel.isLoading.observe(this) { }
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
    }

    private fun initView() {
        binding.imgBack.setOnClickListener { finish() }
        binding.rlRuleHeader.setOnClickListener { toggleSection(binding.tvUsageRules, binding.ivArrow) }
        binding.rlPrecautions.setOnClickListener { toggleSection(binding.tvPrecautions, binding.ivArrow1) }
        binding.btnExchange.setOnClickListener { handleExchangeClick() }
    }

    private fun handleExchangeClick() {
        when (status) {
            ActivityStatusEnum.GIFT_REDEEMED.name -> {
                if (data.otpRequired) {
                    viewModel.getOTPCoupons(data.id)
                } else {
                    viewModel.crmRedeemCoupon(data.id, "")
                }
            }
            ActivityStatusEnum.UNUSED_REDEMPTION.name -> {
            }
        }
    }

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
                if (s.toString().length == 6) {
                    if (s.toString() == currentOtpCode) {
                        bottomSheetDialog.dismiss()
                        viewModel.crmRedeemCoupon(data.id, currentOtpCode)
                    } else {
                        Toast.makeText(this@GiftDetailsActivity, "驗證碼錯誤！", Toast.LENGTH_SHORT).show()
                        otpBinding.etInput.text.clear()
                    }
                }
            }
        })
        bottomSheetDialog.show()
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

    private fun toggleSection(contentView: View, arrowView: ImageView) {
        if (contentView.visibility == View.VISIBLE) {
            contentView.visibility = View.GONE
            arrowView.animate().rotation(0f).setDuration(200).start()
        } else {
            contentView.visibility = View.VISIBLE
            arrowView.animate().rotation(180f).setDuration(200).start()
        }
    }
}