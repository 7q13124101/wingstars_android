package com.wingstars.user.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.utils.MMKVManagement
import com.wingstars.user.R
import com.wingstars.user.databinding.ActivityChangeMemberPasswordBinding
import com.wingstars.login.LoginViewModel

class ChangeMemberPasswordActivity : BaseActivity() {

    private lateinit var binding: ActivityChangeMemberPasswordBinding
    private var countDownTimer: CountDownTimer? = null
    private var timeLeft = 60
    private val viewModel: LoginViewModel by viewModels()
    private val loginPassword: String
        get() = MMKVManagement.getMemberPassword()
    private val memberId: String
        get() = MMKVManagement.getCrmMemberId()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeMemberPasswordBinding.inflate(layoutInflater)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true
        setContentView(binding.root)
        if (loginPassword.isBlank()) {
            showToast("登入資訊遺失，請重新登入")
            finish()
            return
        }
        viewModel.resetPasswordResult.observe(this) { success ->
            if (success) {
                showSuccessDialog(binding.edtNewCode.text.toString())
            }
        }

        viewModel.resetPasswordError.observe(this) { msg ->
            showToast(msg)
        }


        initView()
    }
    override fun initView() {
        binding.ivBack.setOnClickListener { finish() }
        binding.btnSendCode.setOnClickListener {
            sendOtp()
        }
        setupTextWatchers()
        checkEnableSaveButton()
        binding.btnSave.setOnClickListener {
            validateInputsAndSave()
        }
    }
    private fun sendOtp() {
        val phone = MMKVManagement.getMemberPhone()
        if (phone.isBlank()) {
            showToast("手機號碼不存在")
            return
        }
        binding.btnSendCode.isEnabled = false
        viewModel.sendOtp()
        showToast("驗證碼已發送")
        startCountDown()
    }

    private fun setupTextWatchers() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkEnableSaveButton()
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        binding.edtOldCode.addTextChangedListener(watcher)
        binding.edtNewCode.addTextChangedListener(watcher)
        binding.edtNewConfirm.addTextChangedListener(watcher)
        binding.edtOtp.addTextChangedListener(watcher)
    }
    private fun startCountDown() {
        binding.btnSendCode.visibility = View.GONE
        binding.llCountdown.visibility = View.VISIBLE
        countDownTimer?.cancel()
        timeLeft = 60
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = (millisUntilFinished / 1000).toInt()
                binding.tvSeconds.text = "${timeLeft}s"
            }
            override fun onFinish() {
                binding.btnSendCode.visibility = View.VISIBLE
                binding.llCountdown.visibility = View.GONE
                binding.btnSendCode.isEnabled = true
            }
        }.start()
    }
    private fun checkEnableSaveButton() {
        val isOldCodeValid = binding.edtOldCode.text?.length in 8..20
        val isNewCodeValid = binding.edtNewCode.text?.length in 8..20
        val isNewConfirmValid = binding.edtNewConfirm.text?.length in 8..20
        val isOtpValid = binding.edtOtp.text?.length == 6
        val allValid = isOldCodeValid && isNewCodeValid && isNewConfirmValid && isOtpValid
        binding.btnSave.isEnabled = allValid
        val color = if (allValid) R.color.color_DE9DBA else R.color.color_F3F4F6
        binding.btnSave.setBackgroundColor(getColor(color))
        binding.bottomLayout.setBackgroundColor(getColor(color))
        window.navigationBarColor = getColor(color)
    }
    private fun validateInputsAndSave() {
        val oldCode = binding.edtOldCode.text.toString()
        val newCode = binding.edtNewCode.text.toString()
        val newConfirm = binding.edtNewConfirm.text.toString()
        val otp = binding.edtOtp.text.toString()
        fun isPasswordStrong(pwd: String) = pwd.length >= 8 && pwd.any { it.isLetter() } && pwd.any { it.isDigit() }
        when {
            oldCode != loginPassword -> {
                showToast("舊密碼錯誤")
                binding.edtOldCode.requestFocus()
                return
            }
            newCode.length !in 8..20 -> {
                showToast("新密碼長度必須介於 8 到 20 個字元之間")
                binding.edtNewCode.requestFocus()
                return
            }
            !isPasswordStrong(newCode) -> {
                showToast("新密碼必須包含字母和數字")
                binding.edtNewCode.requestFocus()
                return
            }
            newConfirm.length !in 8..20 -> {
                showToast("確認密碼長度必須介於 8 到 20 個字元之間")
                binding.edtNewConfirm.requestFocus()
                return
            }
            otp.length != 6 -> {
                showToast("驗證碼長度必須為 6 個字元")
                binding.edtOtp.requestFocus()
                return
            }
            newCode != newConfirm -> {
                showToast("新密碼和確認資訊不匹配")
                binding.edtNewConfirm.requestFocus()
                return
            }
            else -> {
                resetPassword(newCode, otp)
            }
        }
    }

    private fun resetPassword(newPassword: String, otp: String) {
        if (memberId.isBlank()) {
            showToast("會員ID不存在")
            return
        }
        viewModel.resetPassword(otp, newPassword)
    }


    @SuppressLint("MissingInflatedId")
    private fun showSuccessDialog(newPassword: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_password_change_success, null)
        val dialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialog)
        dialog.setContentView(dialogView)
        dialog.setCancelable(false)
        val btnOk = dialogView.findViewById<MaterialButton>(R.id.btn_ok)
        btnOk.setOnClickListener {
            dialog.dismiss()
            val intent = Intent()
            intent.putExtra("new_password", newPassword)
            setResult(RESULT_OK, intent)
            finish()
        }
        dialog.show()
    }
    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
