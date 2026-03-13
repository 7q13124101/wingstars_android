package com.wingstars.register

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputType
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.net.beans.CRMSignUpRequest
import com.wingstars.login.R
import com.wingstars.login.databinding.ActivityRegistersBinding
import com.wingstars.register.registrationterms.RegistrationTermsActivity
import kotlin.getValue

class RegisterActivity : BaseActivity(), View.OnClickListener, BaseActivity.OnInitialization,
    RegisterNavigator {
    private lateinit var binding: ActivityRegistersBinding
    private var timer: CountDownTimer? = null
    private val phoneRegex = Regex("^09\\d{8}$")
    private val viewModel: RegisterViewModel by viewModels()
    private var gender = "M"

    private var isOtpSent = false

    private var lastSentPhone = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistersBinding.inflate(layoutInflater)
        setTitleFoot(
            view1 = binding.root,
            navigationBarColor = R.color.gray_200,
            setFoot = false,
            setHeadAndFoot = false,
            initialization = this
        )

    }

    private fun resetOtpState() {
        timer?.cancel()
        isOtpSent = false
        lastSentPhone = ""

        binding.btnSendCode.visibility = View.VISIBLE
        binding.rlCodeTimer.visibility = View.GONE
        binding.tvResend?.visibility = View.GONE
        binding.edtPhone.isEnabled = true
        updateSendButtonState()
    }

    private fun showTimerUI() {
        binding.btnSendCode.visibility = View.GONE
        binding.rlCodeTimer.visibility = View.VISIBLE
        updateConfirmButtonState()
    }

    private fun showSendButtonUI() {
        binding.btnSendCode.visibility = View.VISIBLE
        binding.rlCodeTimer.visibility = View.GONE
        binding.tvResend?.visibility = View.GONE
        binding.edtPhone.isEnabled = true
        updateSendButtonState()
    }
    private fun startCountDown(totalMs: Long = 60_000) {
        timer?.cancel()
        binding.btnSendCode.visibility = View.GONE
        binding.rlCodeTimer.visibility = View.VISIBLE
        binding.tvCodeTimer.visibility = View.VISIBLE
        binding.tvResend?.visibility = View.GONE
        binding.edtPhone.isEnabled = false

        timer = object : CountDownTimer(totalMs, 1000) {
            override fun onTick(ms: Long) {
                val sec = (ms / 1000) % 60
                val pinkText = "${sec}s "
                val grayText = "重新發送"
                val fullText = pinkText + grayText
                val spannable = SpannableString(fullText)

                spannable.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this@RegisterActivity, R.color.color_E2518D)),
                    0, pinkText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                spannable.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this@RegisterActivity, R.color.text_subtitle)),
                    pinkText.length, fullText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                binding.tvCodeTimer.text = spannable
            }

            override fun onFinish() {
                isOtpSent = false
                showSendButtonUI() // DÙNG HÀM NÀY THAY VÌ tự set visibility
            }
        }.start()
    }

    private fun updateSendButtonState() {
        val phone = binding.edtPhone.text?.toString().orEmpty()
        val isPhoneValid = phoneRegex.matches(phone)

        binding.btnSendCode.isEnabled = isPhoneValid

        val colorRes = if (isPhoneValid) R.color.white else R.color.text_tittle
        val bgRes = if (isPhoneValid) R.drawable.bg_send_code_able else R.drawable.bg_sends_code

        binding.btnSendCode.setTextColor(ContextCompat.getColor(this, colorRes))
        binding.btnSendCode.background = ContextCompat.getDrawable(this, bgRes)
    }


    private fun setEditTextRightAnchor(targetId: Int) {
        val lp = binding.edtPhone.layoutParams as RelativeLayout.LayoutParams
        lp.addRule(RelativeLayout.START_OF, targetId)
        binding.edtPhone.layoutParams = lp
    }

    private fun showPhoneError(msg: String) {
        binding.tvPhoneInputError.text = msg
        binding.tvPhoneInputError.visibility = View.VISIBLE

    }

    private fun validatePasswordConfirm() {
        showPsdConfirmNormal()
    }

    private fun showPhoneNormal() {
        binding.tvPhoneInputError.text = ""
        binding.tvPhoneInputError.visibility = View.INVISIBLE
    }

    private fun showPsdNormal() {
        binding.tvPsdInputError.setTextColor(
            ContextCompat.getColor(this, R.color.gray_400) // Hoặc R.color.color_E2518D nếu muốn nó màu hồng khi đúng
        )
        binding.tvPsdInputError.visibility = View.VISIBLE
    }

    private fun showPsdError(msg: String) {
        binding.tvPsdInputError.text = msg
        binding.tvPsdInputError.setTextColor(
            ContextCompat.getColor(this, R.color.color_FB2C36)
        )
        binding.tvPsdInputError.visibility = View.VISIBLE
    }

    private fun showPsdConfirmError(msg: String) {

        binding.tvPsdReinputError.text = msg
        binding.tvPsdReinputError.visibility = View.VISIBLE
    }

    private fun showPsdConfirmNormal() {
        binding.tvPsdReinputError.text = ""
        binding.tvPsdReinputError.visibility = View.INVISIBLE
    }

    // ---------------- Validators ----------------
    private fun isTaiwanPhone(phone: String): Boolean = Regex("^09\\d{8}$").matches(phone)

    private fun isPasswordStrong(pwd: String): Boolean {
        if (pwd.length < 8) return false
        val hasLetter = pwd.any { it.isLetter() }
        val hasDigit = pwd.any { it.isDigit() }
        return hasLetter && hasDigit
    }

    private fun isEmailValid(s: String) =
        android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()

    private fun isAllValid(): Boolean {
        val phone = binding.edtPhone.text.toString().trim()
        val name = binding.edtName?.text?.toString()?.trim().orEmpty()
        val code = binding.edtPhoneCode.text.toString().trim()
        val pwd = binding.edtPsd.text?.toString().orEmpty()
        val confirm = binding.edtPsdConfirm.text?.toString().orEmpty()
        val email = binding.edtEmail?.text?.toString()?.trim().orEmpty()
        val agreed = binding.rbPrivacyPolicy?.isChecked == true && binding.rbUserTerms?.isChecked == true

        val phoneOk = isTaiwanPhone(phone)
        val codeOk = isOtpSent && phone == lastSentPhone && code.isNotEmpty()
        val pwdOk = isPasswordStrong(pwd)
        val nameOk = name.isNotEmpty()
        val emailOk = isEmailValid(email)

        return nameOk && phoneOk && codeOk && pwdOk && emailOk && agreed
    }

    private fun updateConfirmButtonState() {
        val enabled = isAllValid()
        binding.btnConfirm.isEnabled = enabled
        if (enabled) {
            binding.btnConfirm.background =
                ContextCompat.getDrawable(this, R.drawable.bg_button_login_able)
            binding.btnConfirm.setTextColor(ContextCompat.getColor(this, R.color.white))
            updateNavigationBarColor(R.color.color_EE97BB, isLightIcon = true)
        } else {
            binding.btnConfirm.background =
                ContextCompat.getDrawable(this, R.drawable.bg_button_login_disable)
            binding.btnConfirm.setTextColor(ContextCompat.getColor(this, R.color.gray_500))
            updateNavigationBarColor(R.color.gray_200, isLightIcon = false)
        }
    }

    private fun showRegisterSuccessDialog() {
        val dialog = BottomSheetDialog(this, R.style.MyBottomSheetDialogTheme)

        val view = layoutInflater.inflate(R.layout.dialog_register_success, null)
        dialog.setContentView(view)
        dialog.setCancelable(false)
        dialog.setOnDismissListener {

        }
        val btnGoLogin = view.findViewById<TextView>(R.id.btnGoLogin)
        dialog.window?.apply {
            // 关键代码：允许内容延伸到导航栏下方
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            // setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
            // 全屏模式 + 沉浸式处理
            decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    )

            val params = attributes
            params.flags =
                params.flags or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
            attributes = params

            setGravity(Gravity.BOTTOM)
        }
        btnGoLogin.setOnClickListener {
            if (dialog!=null){
                dialog.dismiss()
            }
            finish()
        }
        val view1 = view.findViewById<View>(R.id.view)
        val navigationBarHeight = getNavigationBarHeight()
        if (navigationBarHeight != 0) {
            var height = navigationBarHeight
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                height
            )
            view1?.layoutParams = layoutParams
        }
        dialog.show()
    }


    override fun initView() {
        binding.btnConfirm.isEnabled = false
        viewModel.setNavigator(this)

        val autoPhone = intent.getStringExtra("PHONE_NUMBER")
        if (!autoPhone.isNullOrEmpty()) {
            binding.edtPhone.setText(autoPhone)
            binding.edtPhone.setSelection(autoPhone.length)
            updateSendButtonState()
        }

        updateSendButtonState()

        binding.tvResend.setOnClickListener(this)
        binding.ivClose.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.tvPhoneInputError.visibility = View.INVISIBLE
        binding.tvPsdInputError.visibility = View.VISIBLE
        binding.tvPsdInputError.text = getString(R.string.note_register_psd)
        binding.tvPsdInputError.setTextColor(
            ContextCompat.getColor(this, R.color.gray_400)
        )
        binding.privacy.setOnClickListener(this)
        binding.agreement.setOnClickListener(this)
        setupLiveValidation()


        binding.edtPhone.setOnFocusChangeListener { _, hasFocus ->
            binding.rlPhone.isActivated = hasFocus
        }

        binding.edtPhone.addTextChangedListener(object : SimpleTW() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val phone = s?.toString().orEmpty()
                val ok = phoneRegex.matches(phone)

                if (phone.isEmpty()) {
                    showPhoneError(getString(R.string.hint_phone))
                } else if (!ok) {
                    showPhoneError(getString(R.string.error_phone_format))
                } else {
                    showPhoneNormal()
                }

                if (phone != lastSentPhone && isOtpSent) {
                    resetOtpState()
                }

                updateSendButtonState()
                updateConfirmButtonState()
            }
        })

        viewModel.isLoading.observe(this) { showLoadingUI(it, this) }
        viewModel.message.observe(this) { showToast("$it") }

        binding.btnSendCode.setOnClickListener {
            val phone = binding.edtPhone.text?.toString().orEmpty()
            if (!phoneRegex.matches(phone)) {
                binding.tvPhoneInputError.visibility = View.VISIBLE
                return@setOnClickListener
            }
            viewModel.getRegisterPhoneCode(phone)
        }

        binding.cbPsdConfirmVisible.setOnCheckedChangeListener { _, isChecked ->
            val typeface = binding.edtPsdConfirm.typeface
            binding.edtPsdConfirm.inputType = if (isChecked) {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.edtPsdConfirm.typeface = typeface
            binding.edtPsdConfirm.text?.let { binding.edtPsdConfirm.setSelection(it.length) }
        }

        binding.cbPsdVisible.setOnCheckedChangeListener { _, isChecked ->
            val typeface = binding.edtPsd.typeface
            binding.edtPsd.inputType = if (isChecked) {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.edtPsd.typeface = typeface
            binding.edtPsd.text?.let { binding.edtPsd.setSelection(it.length) }
        }

        binding.ivSexCircle.setOnClickListener {
            val intent = Intent(this, RegistrationTermsActivity::class.java)
            startActivity(intent)
        }

        binding.edtName?.addTextChangedListener(object : SimpleTW() {
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {
                updateConfirmButtonState()
            }
        })

        binding.edtPhoneCode?.addTextChangedListener(object : SimpleTW() {
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {
                updateConfirmButtonState()
            }
        })

        binding.edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                updateConfirmButtonState()
            }
            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.rbPrivacyPolicy?.setOnCheckedChangeListener { _, _ -> updateConfirmButtonState() }
        binding.rbUserTerms?.setOnCheckedChangeListener { _, _ -> updateConfirmButtonState() }
    }


    private fun setupLiveValidation() {
        binding.edtPsd.addTextChangedListener(object : SimpleTW() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val pwd = s?.toString().orEmpty()

                if (pwd.isEmpty()) {
                    showPsdNormal()
                    binding.tvPsdInputError.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.gray_400))
                } else if (!isPasswordStrong(pwd)) {
                    showPsdError(getString(R.string.note_register_psd))
                } else {
                    showPsdNormal()
                }
                updateConfirmButtonState()
            }
        })


        binding.edtPsdConfirm.addTextChangedListener(object : SimpleTW() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val pwd = binding.edtPsd.text.toString()
                val confirmPwd = s?.toString().orEmpty()

                if (confirmPwd.isNotEmpty() && pwd != confirmPwd) {
//                    showPsdConfirmError(getString(R.string.error_password_not_match))
                } else {
                    showPsdConfirmNormal()
                }
                updateConfirmButtonState()
            }
        })


        binding.btnConfirm.setOnClickListener {
            val password = binding.edtPsd.text.toString()
            val name = binding.edtName.text.toString()
            val phone = binding.edtPhone.text.toString()
            val otp = binding.edtPhoneCode.text.toString()
            val email = binding.edtEmail.text.toString()

            gender = when {
                binding.rbSexMale.isChecked -> "M"
                binding.rbSexFemale.isChecked -> "F"
                else -> "S"
            }

            viewModel.checkPhone(
                CRMSignUpRequest(name, phone, otp, password, email, "", gender, "")
            )
        }
    }


    override fun onClick(v: View?) {
        var id = v?.id
        when (id) {
            binding.privacy.id -> {
                val intent = Intent()
                intent.action = "policy_term"
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.putExtra("tag", "PrivacyPolicy")
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }

            binding.agreement.id -> {
                val intent = Intent()
                intent.action = "policy_term"
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.putExtra("tag", "UserTerms")
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }
            //重新发送
            binding.tvResend.id -> {
                val phone = binding.edtPhone.text?.toString().orEmpty()
                if (!phoneRegex.matches(phone)) {
                    binding.tvPhoneInputError.visibility = View.VISIBLE
                    return
                }
                viewModel.getRegisterPhoneCode(phone)
            }
        }
    }

    override fun onInitializationSuccessful() {

        val navigationBarHeight = getNavigationBarHeight()
        if (navigationBarHeight != 0) {
            var height = navigationBarHeight
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                height
            )
        }

        initView()
    }

    override fun getPhoneCodeSuccess() {
        isOtpSent = true
        lastSentPhone = binding.edtPhone.text.toString()

        showTimerUI()
        startCountDown()

        Toast.makeText(this, "驗證碼已發送", Toast.LENGTH_SHORT).show()
    }

    override fun registerSuccess() {
        showRegisterSuccessDialog()
    }


    private open class SimpleTW : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {}
    }

}
