package com.wingstars.register

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
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
    private val phoneRegex = Regex("^09\\d{8}$")     // Taiwan mobile (ví dụ)
    private val viewModel: RegisterViewModel by viewModels()
    private var gender = "M"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistersBinding.inflate(layoutInflater)
        setTitleFoot(
            view1 = binding.root,
            navigationBarColor = R.color.gray_200,
            setFoot = false,
            initialization = this
        )

    }

    private fun startCountDown(totalMs: Long = 60_000) {
        timer?.cancel()
        binding.tvCodeTimer.visibility = View.VISIBLE
        binding.tvResend?.visibility = View.GONE

        timer = object : CountDownTimer(totalMs, 1000) {
            override fun onTick(ms: Long) {
                val sec = (ms / 1000).toInt()
                binding.tvCodeTimer.text = "${sec}s"
            }

            override fun onFinish() {
                binding.tvCodeTimer.text = "0s"
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
        // Nếu bạn đang đổi màu chữ theo focus, đừng dùng màu "enabled" khi nút đang disabled
        val hasFocus = binding.edtPhone.hasFocus()
        val colorRes = if (hasFocus && ok) R.color.white else R.color.text_tittle
        val colorBg = if (hasFocus && ok) R.drawable.bg_send_code_able
        else R.drawable.bg_sends_code
        binding.btnSendCode.setTextColor(ContextCompat.getColor(this, colorRes))
        Log.e("edtPhone", "updateSendButtonState")
        binding.btnSendCode.background = ContextCompat.getDrawable(this, colorBg)

    }

    private fun showTimerUI() {
        // Ẩn nút gửi mã, hiện khung timer
        binding.btnSendCode.visibility = View.GONE
        binding.rlCodeTimer.visibility = View.VISIBLE
        // Chuyển neo phải của EditText sang timer
        setEditTextRightAnchor(R.id.rl_code_timer)
        updateConfirmButtonState()
    }

    private fun showSendButtonUI() {
        binding.btnSendCode.visibility = View.VISIBLE
        binding.rlCodeTimer.visibility = View.GONE
        setEditTextRightAnchor(R.id.btn_send_code)
        updateSendButtonState()
        updateConfirmButtonState()
    }

    private fun setEditTextRightAnchor(targetId: Int) {
        val lp = binding.edtPhone.layoutParams as RelativeLayout.LayoutParams
        lp.addRule(RelativeLayout.START_OF, targetId)
        binding.edtPhone.layoutParams = lp
    }

    private fun showPhoneError(msg: String) {
        binding.tvPhoneInputError.text = msg
        binding.tvPhoneInputError.visibility = View.VISIBLE
//        binding.alertCircle.visibility = View.VISIBLE
    }

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

    private fun showPhoneNormal() {
        binding.tvPhoneInputError.text = ""
        binding.tvPhoneInputError.visibility = View.INVISIBLE
//        binding.alertCircle.visibility = View.INVISIBLE
    }

    private fun showPsdError(msg: String) {
        binding.tvPsdInputError.text = msg
        binding.tvPsdInputError.setTextColor(
            ContextCompat.getColor(this, R.color.color_FB2C36) // @ColorInt
        )
        binding.tvPsdInputError.visibility = View.VISIBLE

//        binding.alertCircle.visibility = View.VISIBLE
    }

    private fun showPsdNormal() {
        binding.tvPsdInputError.setTextColor(
            ContextCompat.getColor(this, R.color.gray_400) // @ColorInt
        )
        binding.tvPsdInputError.visibility = View.VISIBLE
//        binding.alertCircle.visibility = View.VISIBLE
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
        val name = binding.edtName?.text?.toString()?.trim().orEmpty()
        val phone = binding.edtPhone.text?.toString()?.trim().orEmpty()
        val code = binding.edtPhoneCode?.text?.toString()?.trim().orEmpty() // nếu có ô nhập OTP
        val pwd = binding.edtPsd.text?.toString().orEmpty()
        val confirm = binding.edtPsdConfirm.text?.toString().orEmpty()
        val email = binding.edtEmail?.text?.toString()?.trim().orEmpty()
        val agreed =
            binding.rbPrivacyPolicy?.isChecked == true && binding.rbUserTerms?.isChecked == true

        val phoneOk = isTaiwanPhone(phone)
        val codeOk = code.isNotEmpty()
        val pwdOk = isPasswordStrong(pwd)
        val matchOk = confirm.isNotEmpty() && pwd == confirm
        val nameOk = name.isNotEmpty()
        val emailOk = isEmailValid(email) // nếu email là optional thì cho phép trống

        return nameOk && phoneOk && codeOk && pwdOk && matchOk && emailOk && agreed
    }

    private fun updateConfirmButtonState() {
        val enabled = isAllValid()
        binding.btnConfirm.isEnabled = enabled
        if (enabled) {
            binding.bottomView.setBackgroundColor(getColor(R.color.color_EE97BB))
            binding.btnConfirm.background =
                ContextCompat.getDrawable(this, R.drawable.bg_button_login_able)
            binding.btnConfirm.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            binding.bottomView.setBackgroundColor(getColor(R.color.gray_200))
            binding.btnConfirm.background =
                ContextCompat.getDrawable(this, R.drawable.bg_button_login_disable)
            binding.btnConfirm.setTextColor(ContextCompat.getColor(this, R.color.gray_500))
        }
//        debugValidity(enabled)
    }

    //    private fun debugValidity(enabled: Boolean) {
//        if (enabled) return
//        val name     = binding.edtName?.text?.toString()?.trim().orEmpty()
//        val phone    = binding.edtPhone.text?.toString()?.trim().orEmpty()
//        val code     = binding.edtPhoneCode?.text?.toString()?.trim().orEmpty()
//        val pwd      = binding.edtPsd.text?.toString().orEmpty()
//        val confirm  = binding.edtPsdConfirm.text?.toString().orEmpty()
//        val email    = binding.edtEmail?.text?.toString()?.trim().orEmpty()
//        val agreed   = binding.rbPrivacyPolicy?.isChecked == true && binding.rbUserTerms?.isChecked == true
//        val phoneOk  = isTaiwanPhone(phone)
//        val codeOk   = code.isNotEmpty() || binding.rlCodeTimer?.visibility == View.VISIBLE
//        val pwdOk    = isPasswordStrong(pwd)
//        val matchOk  = confirm.isNotEmpty() && pwd == confirm
//        val nameOk   = name.isNotEmpty()
//        val emailOk  = email.isEmpty() || isEmailValid(email)
//
//        android.util.Log.d("RegisterValid",
//            "nameOk=$nameOk, phoneOk=$phoneOk, codeOk=$codeOk, pwdOk=$pwdOk, matchOk=$matchOk, emailOk=$emailOk, agreed=$agreed " +
//                    "(name='$name', phone='$phone', code='${code.length}', email='$email', rlCodeTimer=${binding.rlCodeTimer?.visibility})"
//        )
//    }
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
//        updateConfirmButtonState()
//        updateSendButtonState()
        binding.tvResend.setOnClickListener(this)
        binding.ivClose.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.tvPhoneInputError.visibility = View.INVISIBLE
        binding.tvPsdInputError.visibility = View.INVISIBLE
        binding.privacy.setOnClickListener(this)
        binding.agreement.setOnClickListener(this)
        setupLiveValidation()
        binding.edtPhone.setOnFocusChangeListener { _, hasFocus ->
            binding.rlPhone.isActivated = hasFocus
            val colorRes = if (hasFocus && binding.btnSendCode.isEnabled) R.color.white
            else R.color.text_tittle
            val colorBg =
                if (hasFocus && binding.btnSendCode.isEnabled) R.drawable.bg_send_code_able
                else R.drawable.bg_sends_code
            binding.btnSendCode.setTextColor(ContextCompat.getColor(this, colorRes))
            Log.e("edtPhone", "edtPhone")
            binding.btnSendCode.background = ContextCompat.getDrawable(this, colorBg)
        }
        binding.edtPhone.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val ok = phoneRegex.matches(s?.toString().orEmpty())
                binding.tvPhoneInputError.visibility =
                    if (s.isNullOrEmpty() || ok) View.INVISIBLE else View.VISIBLE
                updateSendButtonState()
                updateConfirmButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
        viewModel.isLoading.observe(this) {
            showLoadingUI(it, this)
        }
        viewModel.message.observe(this) {
            showToast("$it")
        }
        binding.btnSendCode.setOnClickListener {
            val phone = binding.edtPhone.text?.toString().orEmpty()
            if (!phoneRegex.matches(phone)) {
                binding.tvPhoneInputError.visibility = View.VISIBLE
                return@setOnClickListener
            }
            viewModel.getRegisterPhoneCode(phone)

        }
        binding.cbPsdConfirmVisible.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.edtPsdConfirm.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding.edtPsdConfirm.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.edtPsdConfirm.text?.let { binding.edtPsdConfirm.setSelection(it.length) }
        }
        binding.cbPsdVisible.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.edtPsd.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding.edtPsd.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
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
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                updateConfirmButtonState()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.rbPrivacyPolicy?.setOnCheckedChangeListener { _, _ -> updateConfirmButtonState() }
        binding.rbUserTerms?.setOnCheckedChangeListener { _, _ -> updateConfirmButtonState() }
    }

    private fun setupLiveValidation() {
        binding.edtPhone.addTextChangedListener(object : SimpleTW() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val phone = s?.toString()?.trim().orEmpty()
                when {
                    phone.isEmpty() -> showPhoneError(getString(R.string.hint_phone))   // yêu cầu nhập
                    !isTaiwanPhone(phone) -> showPhoneError(getString(R.string.error_phone_format)) // sai định dạng
                    else -> showPhoneNormal()
                }
            }
        })

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
        binding.btnConfirm.setOnClickListener {
//            if (!isAllValid()) return@setOnClickListener
            // showRegisterSuccessDialog()
            val password = binding.edtPsd.text.toString()

            val name = binding.edtName.text.toString()
            val phone = binding.edtPhone.text.toString()
            val otp = binding.edtPhoneCode.text.toString()
            val email = binding.edtEmail.text.toString()

            if (binding.rbSexMale.isChecked) {
                gender = "M"
            } else if (binding.rbSexFemale.isChecked) {
                gender = "F"
            } else {
                gender = "S"
            }



            viewModel.checkPhone(
                CRMSignUpRequest(
                    name,
                    phone,
                    otp,
                    password,
                    email,
                    "",
                    gender,
                    ""
                )
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
        binding.btnSendCode.isEnabled = false
        val navigationBarHeight = getNavigationBarHeight()
        if (navigationBarHeight != 0) {
            var height = navigationBarHeight
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                height
            )
            binding.bottomView?.layoutParams = layoutParams
        }
        initView()
    }

    override fun getPhoneCodeSuccess() {
        showTimerUI()      // ẩn nút, hiện đồng hồ
        startCountDown()   // 60 giây
    }

    override fun registerSuccess() {
        showRegisterSuccessDialog()
    }

    // TextWatcher rút gọn
    private open class SimpleTW : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {}
    }

}
