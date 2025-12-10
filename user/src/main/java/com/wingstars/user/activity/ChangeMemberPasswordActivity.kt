package com.wingstars.user.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.wingstars.base.base.BaseActivity
import com.wingstars.user.R
import com.wingstars.user.databinding.ActivityChangeMemberPasswordBinding

class ChangeMemberPasswordActivity: BaseActivity() {
    private lateinit var binding: ActivityChangeMemberPasswordBinding
    private  var countDownTimer: CountDownTimer? = null
    private var timeLeft = 60
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeMemberPasswordBinding.inflate(layoutInflater)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true  // icon đen
        setContentView(binding.root)
        binding.btnSendCode.isEnabled = true
        initView()

    }
    override fun initView(){
        binding.ivBack.setOnClickListener { finish() }
        binding.btnSendCode.setOnClickListener {
            startCountDown()
            // Call API send OTP
        }
        setupTextWatchers()
        checkEnableSaveButton()
        binding.btnSave.setOnClickListener {
            validateInputsAndSave()
        }

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
        binding.edtPhone.addTextChangedListener(watcher)
    }
    private fun startCountDown() {
        binding.btnSendCode.visibility = View.GONE
        binding.llCountdown.visibility = View.VISIBLE
        countDownTimer?.cancel()
        timeLeft = 60
        countDownTimer = object : CountDownTimer(60000,1000){
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = (millisUntilFinished / 1000).toInt()
                binding.tvSeconds.text = "${timeLeft}s"
            }

            override fun onFinish() {
                binding.btnSendCode.visibility = View.VISIBLE
            }
        }.start()
    }
    private fun checkEnableSaveButton() {
        val isOldCodeValid = binding.edtOldCode.text?.length in 8..20
        val isNewCodeValid = binding.edtNewCode.text?.length in 8..20
        val isNewConfirmValid = binding.edtNewConfirm.text?.length in 8..20
        val isPhoneValid = binding.edtPhone.text?.length == 8

        val allValid = isOldCodeValid && isNewCodeValid && isNewConfirmValid && isPhoneValid

        if (allValid) {
            binding.btnSave.isEnabled = true
            binding.btnSave.setBackgroundColor(getColor(R.color.color_DE9DBA))
            binding.bottomLayout.setBackgroundColor(getColor(R.color.color_DE9DBA))
            window.navigationBarColor = getColor(R.color.color_DE9DBA)
        } else {
            binding.btnSave.isEnabled = false
            binding.btnSave.setBackgroundColor(getColor(R.color.color_F3F4F6))
            binding.bottomLayout.setBackgroundColor(getColor(R.color.color_F3F4F6))
            window.navigationBarColor = getColor(R.color.color_F3F4F6)
        }
    }
    private fun validateInputsAndSave() {
        val oldCode = binding.edtOldCode.text.toString()
        val newCode = binding.edtNewCode.text.toString()
        val newConfirm = binding.edtNewConfirm.text.toString()
        val phone = binding.edtPhone.text.toString()

        when {
            oldCode.length !in 8..20 -> {
                showToast("舊密碼長度必須介於 8 到 20 個字元之間。")
                binding.edtOldCode.requestFocus()
            }
            newCode.length !in 8..20 -> {
                showToast("新密碼長度必須介於 8 到 20 個字元之間。")
                binding.edtNewCode.requestFocus()
            }
            newConfirm.length !in 8..20 -> {
                showToast("確認密碼長度必須介於 8 到 20 個字元之間。")
                binding.edtNewConfirm.requestFocus()
            }
            phone.length != 8 -> { // edtPhone chỉ được 8 kí tự
                showToast("驗證碼長度必須為 8 個字元。")
                binding.edtPhone.requestFocus()
            }
            newCode != newConfirm -> {
                showToast("新密碼和確認資訊不匹配")
                binding.edtNewConfirm.requestFocus()
            }
            else -> {
                showSuccessDialog(newConfirm)
            }
        }
    }
    @SuppressLint("MissingInflatedId")
    private fun showSuccessDialog(newPassword: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_password_change_success, null)

        val dialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialog)
        dialog.setContentView(dialogView)
        dialog.setCancelable(false)  // nếu muốn không đóng ngoài màn hình

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