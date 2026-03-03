package com.wingstars.count.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.wingstars.base.net.beans.EvtTaskResponse
import com.wingstars.count.databinding.ActivityCountItemBinding
import com.wingstars.count.databinding.DialogPublicPopupBoxBinding
import com.wingstars.count.viewmodel.CountItemViewModel

enum class GetPointType {
    BEACON,
    AIRDROP,
    IMMEDIATELY,
    SCANCODE
}

class Count_Item_Activity : AppCompatActivity() {

    private lateinit var binding: ActivityCountItemBinding
    private var currentItemData: EvtTaskResponse? = null
    private var currentPointType: GetPointType = GetPointType.AIRDROP
    private lateinit var viewModel: CountItemViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCountItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this)[CountItemViewModel::class.java]

        loadData()
        initView()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.claimSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                showSuccessDialog()
            }
        }

        viewModel.errorMessage.observe(this) { msg ->
            if (!msg.isNullOrEmpty()) {
                currentItemData?.isSendApiF = false
                showFailureDialog(msg)
            }
        }

        // 3. Xử lý trạng thái Loading
        viewModel.isLoading.observe(this) { isLoading ->
            binding.btnConfirm.isEnabled = !isLoading
            binding.btnConfirm.text = if (isLoading) "處理中..." else getButtonTextForType(currentPointType)
        }
    }

    private fun initView() {
        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.btnConfirm.setOnClickListener {
            currentItemData?.let { data ->
                when (currentPointType) {
                    GetPointType.IMMEDIATELY -> {
                        handleImmediatelyAction(data)
                    }
                    GetPointType.SCANCODE -> {
                        Toast.makeText(this, "請掃描 QR Code", Toast.LENGTH_SHORT).show()
                    }
                    GetPointType.AIRDROP, GetPointType.BEACON -> {
                        requestClaimPoint(data)
                    }
                }
            }
        }
    }

    private fun requestClaimPoint(data: EvtTaskResponse) {
        if (!data.isSendApiF) {
            data.isSendApiF = true
            viewModel.claimPoint(data)
        }
    }

    private fun handleImmediatelyAction(data: EvtTaskResponse) {
        if ((data.statusInfo == "pending" || data.statusInfo == null) && !data.isSendApiF) {
            requestClaimPoint(data)
        }

        var url = ""
        when (data.triggerTag) {
            "fb" -> url = "https://www.facebook.com/tsgwingstars/"
            "instagram" -> url = "https://www.instagram.com/wing_stars_official/"
            "yt" -> url = "https://www.youtube.com/@WingStars-TSG/"
//            "survey" -> url = "https://www.surveycake.com/s/LnnMR"
        }

        if (url.isNotEmpty()) {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "無法開啟連結", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getButtonTextForType(type: GetPointType): String {
        return when (type) {
            GetPointType.IMMEDIATELY -> "前往連結"
            GetPointType.SCANCODE -> "掃描 QR Code"
            else -> "領取點數"
        }
    }

    private fun loadData() {
        val jsonString = intent.getStringExtra("EXTRA_ITEM_JSON")

        if (jsonString != null) {
            try {
                val data = Gson().fromJson(jsonString, EvtTaskResponse::class.java)
                currentItemData = data
                binding.tvTitle.text = data.topic
                binding.tvCount.text = "${data.point} 點"
                binding.tvTimeItem.text = "${data.startDate.formatDate()} ~ ${data.endDate.formatDate()}"
                binding.tvDetail.text = data.content
                binding.tvDescription.text = data.pointProcess
                determinePointType(data)
                updateButtonState(data)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Data Error", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun determinePointType(itemData: EvtTaskResponse) {
        currentPointType = GetPointType.AIRDROP // Default

        when (itemData.eventType) {
            "limited" -> {
                itemData.triggerTag?.let { tag ->
                    if (tag.equals("fb", ignoreCase = true) ||
                        tag.equals("instagram", ignoreCase = true) ||
                        tag.equals("yt", ignoreCase = true) ||
                        tag.equals("survey", ignoreCase = true)
                    ) {
                        currentPointType = GetPointType.IMMEDIATELY
                    } else if (tag.equals("thanks", ignoreCase = true)) {
                        currentPointType = GetPointType.AIRDROP
                    } else if (tag.equals("ytMember", ignoreCase = true) ||
                        tag.equals("item", ignoreCase = true)
                    ) {
                        currentPointType = GetPointType.SCANCODE
                    }
                }
            }
            "exclusive" -> {
                itemData.triggerTag?.let { tag ->
                    if (tag.equals("card", ignoreCase = true) ||
                        tag.equals("attendance", ignoreCase = true)
                    ) {
                        currentPointType = GetPointType.AIRDROP
                    } else if (tag.equals("mvp", ignoreCase = true) ||
                        tag.equals("takao", ignoreCase = true)
                    ) {
                        currentPointType = GetPointType.SCANCODE
                    }
                }
            }
            "daily" -> {
                itemData.triggerTag?.let { tag ->
                    if (tag.equals("checkin", ignoreCase = true)) {
                        currentPointType = GetPointType.BEACON
                    } else if (tag.equals("threshold", ignoreCase = true) ||
                        tag.equals("item", ignoreCase = true)
                    ) {
                        currentPointType = GetPointType.SCANCODE
                    }
                }
            }
        }
    }

    private fun updateButtonState(itemData: EvtTaskResponse) {
        if (itemData.statusInfo == "completed" || itemData.statusInfo == "reward") {
            updateButtonToCompleted()
            return
        }
        when (currentPointType) {
            GetPointType.SCANCODE,
            GetPointType.AIRDROP,
            GetPointType.BEACON -> {
                binding.btnConfirm.visibility = View.GONE
            }

            else -> {
                binding.btnConfirm.visibility = View.VISIBLE
                binding.btnConfirm.isEnabled = true
                binding.btnConfirm.text = getButtonTextForType(currentPointType)
            }
        }
    }

    private fun showSuccessDialog() {
        if (isFinishing) return

        val dialogBinding = DialogPublicPopupBoxBinding.inflate(LayoutInflater.from(this))
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(dialogBinding.root)

        // Set background trong suốt
        bottomSheetDialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }

        dialogBinding.tvDialogTitle.text = "獲得點數"
        val points = currentItemData?.point ?: "0"
        dialogBinding.tvDialogContent.text = "恭喜！你獲得 $points 點！"

        dialogBinding.tvDialogConfirm.setOnClickListener {
            bottomSheetDialog.dismiss()
            updateButtonToCompleted()
            setResult(RESULT_OK)
            finish()
        }

        bottomSheetDialog.show()
    }

    // Dialog thất bại
    private fun showFailureDialog(errorMessage: String) {
        if (isFinishing) return
        val dialogBinding = DialogPublicPopupBoxBinding.inflate(LayoutInflater.from(this))
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(dialogBinding.root)
        bottomSheetDialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }

        dialogBinding.tvDialogTitle.text = "領取失敗"
        dialogBinding.tvDialogTitle.setTextColor(Color.RED)
        dialogBinding.tvDialogContent.text = errorMessage
        dialogBinding.tvDialogConfirm.text = "關閉"
        dialogBinding.tvDialogConfirm.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun updateButtonToCompleted() {
        binding.btnConfirm.visibility = View.VISIBLE
        binding.btnConfirm.text = "已完成"
        binding.btnConfirm.isEnabled = false
        binding.btnConfirm.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
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
}