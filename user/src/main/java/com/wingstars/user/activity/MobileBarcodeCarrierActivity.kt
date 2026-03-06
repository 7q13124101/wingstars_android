package com.wingstars.user.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.addTextChangedListener
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.net.API
import com.wingstars.base.net.NetBase
import com.wingstars.base.net.beans.CRMBaseResponse
import com.wingstars.base.net.beans.CRMExtraData
import com.wingstars.base.net.beans.CRMMemberContactResponse
import com.wingstars.base.net.beans.CRMUpdateContactRequest
import com.wingstars.base.utils.MMKVManagement
import com.wingstars.base.view.UpLoadingDialog
import com.wingstars.user.utils.KeyboardUtils
import com.wingstars.user.R
import com.wingstars.user.databinding.ActivityMobileBarcodeCarrierBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class MobileBarcodeCarrierActivity: BaseActivity() {
    private lateinit var binding: ActivityMobileBarcodeCarrierBinding
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        val view = ActivityMobileBarcodeCarrierBinding.inflate(layoutInflater)
        binding = view
        setContentView(view.root)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true
        initView()
    }
    @SuppressLint("SuspiciousIndentation")
    override fun initView() {
        val barcodeNumber = MMKVManagement.getCrmMemberBarcode()
        if(barcodeNumber.isNotEmpty())
            binding.edtMobile.setText(barcodeNumber)

        binding.ivBack.setOnClickListener { finish() }
        binding.rlMobile.setOnClickListener {
            binding.edtMobile.requestFocus()
            binding.edtMobile.hint = ""
            KeyboardUtils.showKeyboard(binding.edtMobile)
        }
        binding.edtMobile.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.edtMobile.hint = ""
            } else {
                if (binding.edtMobile.text.isNullOrEmpty()) {
                    binding.edtMobile.hint = getString(R.string.user_enter_your_mobile)
                }
            }
        }
        binding.edtMobile.addTextChangedListener {
            val input = it.toString()
            if(input.length >= 8){
                binding.btnSave.isEnabled = true
                binding.btnSave.setBackgroundColor(getColor(R.color.color_DE9DBA))
                binding.btnSave.setTextColor(getColor(R.color.white))

            }else{
                binding.btnSave.isEnabled = false
                binding.btnSave.setBackgroundColor(getColor(R.color.color_F3F4F6))
                binding.btnSave.setTextColor(getColor(R.color.black))
            }
        }
        binding.btnSave.setOnClickListener {
            val mobile = binding.edtMobile.text.toString().trim()
            updateMemberContact(mobile)
        }
    }

    private fun updateMemberContact(invoiceNumber: String) {
        val memberId = MMKVManagement.getCrmMemberId()
        if (memberId.isEmpty() || memberId == "0") {
            Toast.makeText(this, "無法取得會員ID", Toast.LENGTH_SHORT).show()
            return
        }

        val loadingDialog = UpLoadingDialog.Builder(this).createDialog(this)
        loadingDialog.show()
        val crmExtraData = CRMExtraData(invoice_number= invoiceNumber)
        val request = CRMUpdateContactRequest(
            extraData= crmExtraData
        )

        val url = "${NetBase.HOST_CRM}/api/v1/basic/member/$memberId/contact"

        API.shared?.api?.let { apiService ->
            val observer: Observable<CRMBaseResponse<CRMMemberContactResponse>>? =
                apiService.crmUpdateMemberContact(url, request)
            observer?.subscribeOn(Schedulers.io())
                ?.unsubscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { response: CRMBaseResponse<CRMMemberContactResponse> ->
                        loadingDialog.dismiss()
                        if (response.success) {
                            MMKVManagement.setCrmMemberBarcode(invoiceNumber)
                            val intent = Intent()
                            intent.putExtra("mobile_number", invoiceNumber)
                            setResult(RESULT_OK, intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@MobileBarcodeCarrierActivity,
                                response.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    { error ->
                        loadingDialog.dismiss()
                        Toast.makeText(
                            this@MobileBarcodeCarrierActivity,
                            "網路錯誤: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
        } ?: run {
            loadingDialog.dismiss()
            Toast.makeText(this, "API 未初始化", Toast.LENGTH_SHORT).show()
        }
    }

}