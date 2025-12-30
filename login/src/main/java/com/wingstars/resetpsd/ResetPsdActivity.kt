package com.wingstars.resetpsd

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.net.beans.CRMForgotPasswordRequest
import com.wingstars.login.R
import com.wingstars.login.databinding.ActivityRegistersBinding
import com.wingstars.login.databinding.ActivityResetPsdBinding
class ResetPsdActivity : BaseActivity(), ResetPsdNavigator {
    private lateinit var binding: ActivityResetPsdBinding
    private val viewModel: ResetPsdViewModel by viewModels()

    private var timer: CountDownTimer? = null
    private val phoneRegex = Regex("^09\\d{8}$")
    private val otpLength = 6
    private var isCodeSent = false

    private var currentStep = 1
    private var confirmedPhone = ""
    private var confirmedOtp = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPsdBinding.inflate(layoutInflater)
        setTitleFoot(
            view1 = binding.root,
            navigationBarColor = R.color.gray_200,
            statusBarColor = R.color.white
        )
        // 1. Cài đặt ViewModel & Navigator
        viewModel.setNavigator(this)

        initView()
        updateSendButtonState()
        updateConfirmButtonState()
    }

    override fun initView() {
        // --- Nút Back ---
        binding.ivClose.setOnClickListener { handleBackPress() }

        // --- Setup giao diện nhập SĐT ---
        binding.edtPhone.setOnFocusChangeListener { _, hasFocus ->
            binding.rlPhone.isActivated = hasFocus
            updateSendButtonUI()
        }

        binding.edtPhone.addTextChangedListener(object : SimpleTW() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val ok = phoneRegex.matches(s?.toString().orEmpty())
                binding.tvPhoneInputError.visibility =
                    if (s.isNullOrEmpty() || ok) View.INVISIBLE else View.VISIBLE
                if (isCodeSent) {
                    isCodeSent = false
                    showSendButtonUI()
                }
                updateSendButtonState()
                updateConfirmButtonState()
            }
        })

        // --- Setup nút Gửi mã ---
        binding.btnSendCode.setOnClickListener {
            val phone = binding.edtPhone.text?.toString().orEmpty()
            if (!phoneRegex.matches(phone)) {
                binding.tvPhoneInputError.visibility = View.VISIBLE
                return@setOnClickListener
            }
            // Gọi API gửi mã
            viewModel.getResetPsdPhoneCode(phone)
        }

        // --- Setup giao diện nhập OTP ---
        binding.edtPhoneCode.addTextChangedListener(object : SimpleTW() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateConfirmButtonState()
            }
        })

        // --- Nút "Gửi lại" khi hết giờ ---
        binding.tvResend?.setOnClickListener {
            val phone = binding.edtPhone.text?.toString().orEmpty()
            if (phoneRegex.matches(phone)) {
                viewModel.getResetPsdPhoneCode(phone)
            }
        }

        // --- Setup giao diện nhập Mật khẩu (Bước 2) ---
        binding.cbPsdVisible.setOnCheckedChangeListener { _, isChecked ->
            binding.edtPsd.inputType = if (isChecked) InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.edtPsd.setSelection(binding.edtPsd.length())
        }

        binding.cbPsdConfirmVisible.setOnCheckedChangeListener { _, isChecked ->
            binding.edtPsdConfirm.inputType = if (isChecked) InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.edtPsdConfirm.setSelection(binding.edtPsdConfirm.length())
        }

        // Validate mật khẩu
        binding.edtPsd.addTextChangedListener(object : SimpleTW() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val pwd = s?.toString().orEmpty()
                when {
                    pwd.isEmpty() -> showPsdError(getString(R.string.error_psd_empty))
                    !isPasswordStrong(pwd) -> showPsdError(getString(R.string.note_register_psd))
                    else -> showPsdNormal()
                }
                validatePasswordConfirm()
                updateConfirmButtonState()
            }
        })

        binding.edtPsdConfirm.addTextChangedListener(object : SimpleTW() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePasswordConfirm()
                updateConfirmButtonState()
            }
        })

        // --- Nút Xác nhận (Dùng chung cho cả 2 bước) ---
        binding.btnConfirm.setOnClickListener {
            if (currentStep == 1) {
                confirmedPhone = binding.edtPhone.text.toString()
                confirmedOtp = binding.edtPhoneCode.text.toString()
                switchToStep2()
            } else {
                val password = binding.edtPsd.text.toString()
                val request = CRMForgotPasswordRequest(confirmedOtp, password, confirmedPhone)
                viewModel.resetPsd(request)
            }
        }
    }

    // --- Xử lý Logic ---

    private fun switchToStep2() {
        currentStep = 2
        binding.llInputCode.visibility = View.GONE
        binding.llInputPsd.visibility = View.VISIBLE
        binding.tvTitle.text = "設定新密碼"
        updateConfirmButtonState()
    }

    private fun switchToStep1() {
        currentStep = 1
        binding.llInputCode.visibility = View.VISIBLE
        binding.llInputPsd.visibility = View.GONE
        binding.tvTitle.text = getString(R.string.forget_psd)
        updateConfirmButtonState()
    }

    private fun handleBackPress() {
        if (currentStep == 2) {
            switchToStep1()
        } else {
            finish()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        handleBackPress()
    }

    override fun getPhoneCodeSuccess() {
        isCodeSent = true
        showTimerUI()
        startCountDown()
        Toast.makeText(this, "驗證碼已發送。", Toast.LENGTH_SHORT).show()
    }

    override fun resetPsdSuccess() {
        showSuccessDialog()
    }

    private fun showSuccessDialog() {
        if (isFinishing) return
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_reset_success, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val btnConfirm = dialogView.findViewById<android.view.View>(R.id.btnConfirm)
        btnConfirm.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        dialog.show()
    }

    private fun startCountDown(totalMs: Long = 300_000) {
        timer?.cancel()
        binding.tvCodeTimer.visibility = View.VISIBLE
        binding.tvResend?.visibility = View.GONE

        timer = object : CountDownTimer(totalMs, 1000) {
            override fun onTick(ms: Long) {
                val min = (ms / 1000) / 60
                val sec = (ms / 1000) % 60
                binding.tvCodeTimer.text = String.format("重新發送 %02d:%02d", min, sec)
            }
            override fun onFinish() {
                if (binding.tvResend != null) {
                    binding.tvCodeTimer.visibility = View.GONE
                    binding.tvResend.visibility = View.VISIBLE
                } else {
                    showSendButtonUI()
                }
            }
        }.start()
    }

    private fun updateSendButtonState() {
        val ok = phoneRegex.matches(binding.edtPhone.text?.toString().orEmpty())
        binding.btnSendCode.isEnabled = ok
        updateSendButtonUI()
    }

    private fun updateSendButtonUI() {
        val ok = phoneRegex.matches(binding.edtPhone.text?.toString().orEmpty())
        binding.btnSendCode.isEnabled = ok
        val hasFocus = binding.edtPhone.hasFocus()
        val colorRes = if (hasFocus && ok) R.color.white else R.color.text_tittle
        val colorBg = if (hasFocus && ok) R.drawable.bg_send_code_able else R.drawable.bg_sends_code
        binding.btnSendCode.setTextColor(ContextCompat.getColor(this, colorRes))
        binding.btnSendCode.background = ContextCompat.getDrawable(this, colorBg)
    }

    private fun showTimerUI() {
        binding.btnSendCode.visibility = View.GONE
        binding.rlCodeTimer.visibility = View.VISIBLE
        setEditTextRightAnchor(R.id.rl_code_timer)
    }

    private fun showSendButtonUI() {
        binding.btnSendCode.visibility = View.VISIBLE
        binding.rlCodeTimer.visibility = View.GONE
        setEditTextRightAnchor(R.id.btn_send_code)
        updateSendButtonState()
    }

    private fun setEditTextRightAnchor(targetId: Int) {
        val lp = binding.edtPhone.layoutParams as RelativeLayout.LayoutParams
        lp.addRule(RelativeLayout.START_OF, targetId)
        binding.edtPhone.layoutParams = lp
    }

    // --- Validation Logic ---

    private fun validatePasswordConfirm() {
        val pwd = binding.edtPsd.text?.toString().orEmpty()
        val confirm = binding.edtPsdConfirm.text?.toString().orEmpty()
        if (confirm.isEmpty()) {
            showPsdConfirmNormal()
            return
        }
        if (pwd != confirm) {
            showPsdConfirmError(getString(R.string.error_psd_not_match))
        } else {
            showPsdConfirmNormal()
        }
    }

    private fun showPsdConfirmError(msg: String) {
        binding.tvPsdReinputError.text = msg
        binding.tvPsdReinputError.visibility = View.VISIBLE
    }

    private fun showPsdConfirmNormal() {
        binding.tvPsdReinputError.text = ""
        binding.tvPsdReinputError.visibility = View.INVISIBLE
    }

    private fun showPsdNormal() {
        binding.tvPsdInputError.setTextColor(ContextCompat.getColor(this, R.color.gray_400))
        binding.tvPsdInputError.visibility = View.VISIBLE
    }

    private fun showPsdError(msg: String) {
        binding.tvPsdInputError.text = msg
        binding.tvPsdInputError.setTextColor(ContextCompat.getColor(this, R.color.color_FB2C36))
        binding.tvPsdInputError.visibility = View.VISIBLE
    }

    private fun isPasswordStrong(pwd: String): Boolean {
        if (pwd.length < 8) return false
        val hasLetter = pwd.any { it.isLetter() }
        val hasDigit = pwd.any { it.isDigit() }
        return hasLetter && hasDigit
    }

    private fun updateConfirmButtonState() {
        val enabled = if (currentStep == 1) {
            val phone = binding.edtPhone.text?.toString().orEmpty()
            val code = binding.edtPhoneCode.text?.toString().orEmpty()
            phoneRegex.matches(phone) && code.length == otpLength && isCodeSent        } else {
            val pwd = binding.edtPsd.text?.toString().orEmpty()
            val confirm = binding.edtPsdConfirm.text?.toString().orEmpty()
            isPasswordStrong(pwd) && pwd == confirm
        }

        binding.btnConfirm.isEnabled = enabled
        if (enabled) {
            binding.btnConfirm.background = ContextCompat.getDrawable(this, R.drawable.bg_button_login_able)
            binding.btnConfirm.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            binding.btnConfirm.background = ContextCompat.getDrawable(this, R.drawable.bg_button_login_disable)
            binding.btnConfirm.setTextColor(ContextCompat.getColor(this, R.color.gray_500))

        }
    }

    private open class SimpleTW : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {}
    }
    override fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }
}