package com.wingstars.count.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.wingstars.base.net.beans.EvtTaskResponse
import com.wingstars.count.R
import com.wingstars.count.databinding.ActivityCountItemBinding
import com.wingstars.count.databinding.DialogPublicPopupBoxBinding

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

        initView()
        loadData()
    }

    private fun initView() {
        binding.imgBack.setOnClickListener {
            finish()
        }

        // Nút xác nhận/thực hiện nhiệm vụ
        binding.btnConfirm.setOnClickListener {
            currentItemData?.let { data ->
                if (currentPointType == GetPointType.IMMEDIATELY) {
                    handleImmediatelyAction(data)
                } else {
                    showSuccessDialog()
                }
            }
        }
    }

    private fun loadData() {
        val jsonString = intent.getStringExtra("EXTRA_ITEM_JSON")

        if (jsonString != null) {
            val data = Gson().fromJson(jsonString, EvtTaskResponse::class.java)
            currentItemData = data
            binding.tvTitle.text = data.topic
            binding.tvCount.text = "${data.point} 點"
            determinePointType(data)
            setupTypeView()

            binding.tvTimeItem.text = "${data.startDate} ~ ${data.endDate}"
            binding.tvDetail.text = data.content
            binding.tvDescription.text = data.pointProcess
            updateButtonState(data)
        }
    }

    private fun determinePointType(itemData: EvtTaskResponse) {
        currentPointType = GetPointType.AIRDROP

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
                } ?: run {
                    currentPointType = GetPointType.AIRDROP
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
                } ?: run {
                    currentPointType = GetPointType.AIRDROP
                }
            }
        }
    }

    private fun setupTypeView() {

        // Ví dụ binding:
        /*
        when (currentPointType) {
            GetPointType.BEACON -> {
                binding.ivType.setImageResource(R.drawable.ic_beacon) // Đảm bảo có ảnh này
                binding.tvType.text = "Beacon"
            }
            GetPointType.AIRDROP -> {
                binding.ivType.setImageResource(R.drawable.ic_airdrop)
                binding.tvType.text = "AirDrop"
            }
            GetPointType.IMMEDIATELY -> {
                binding.ivType.setImageResource(R.drawable.ic_immediately)
                binding.tvType.text = "Immediate"
            }
            GetPointType.SCANCODE -> {
                binding.ivType.setImageResource(R.drawable.ic_scan_code_green)
                binding.tvType.text = "Scan Code"
            }
        }
        */
    }

    private fun updateButtonState(itemData: EvtTaskResponse) {
        if (currentPointType == GetPointType.IMMEDIATELY) {
            if (itemData.triggerTag.equals("survey", ignoreCase = true) ||
                (!itemData.isSendAPI && itemData.statusInfo.equals("pending", ignoreCase = true))
            ) {
                binding.btnConfirm.visibility = View.VISIBLE
                binding.btnConfirm.isEnabled = true
                binding.btnConfirm.text = "前往連結"
            }
        } else {
            binding.btnConfirm.visibility = View.VISIBLE
            when (itemData.statusInfo?: "") {
                "completed", "reward" -> updateButtonToCompleted()
                "pending" -> {
                    binding.btnConfirm.isEnabled = true
                    binding.btnConfirm.text = "領取點數"
                }
                else -> {
                    binding.btnConfirm.text = "前往"
                }
            }
        }
    }

    private fun handleImmediatelyAction(data: EvtTaskResponse) {
        var url = ""
        when (data.triggerTag) {
            "fb" -> url = "https://www.facebook.com/tsgwingstars/"
            "instagram" -> url = "https://www.instagram.com/wing_stars_official/"
            "yt" -> url = "https://www.youtube.com/@WingStars-TSG/"
            "survey" -> url = ""
        }

        if (url.isNotEmpty()) {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }
    }

    private fun showSuccessDialog() {
        val dialogBinding = DialogPublicPopupBoxBinding.inflate(LayoutInflater.from(this))
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(dialogBinding.root)

        bottomSheetDialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }

        dialogBinding.tvDialogTitle.text = "獲得點數"
        dialogBinding.tvDialogContent.text = "恭喜！你獲得 1 點！"

        dialogBinding.tvDialogConfirm.setOnClickListener {
            bottomSheetDialog.dismiss()
            updateButtonToCompleted()
        }

        bottomSheetDialog.show()
    }

    private fun updateButtonToCompleted() {
        binding.btnConfirm.apply {
            text = "已完成"
            isEnabled = false
            backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E5E7EB"))
            setTextColor(Color.parseColor("#737373"))
        }
    }
}