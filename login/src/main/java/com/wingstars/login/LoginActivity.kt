package com.wingstars.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.net.NetBase
import com.wingstars.login.databinding.ActivityLoginBinding
import com.wingstars.net.beans.request_respone.RetrofitClient
import com.wingstars.viewmodel.LoginViewModel
import com.wingstars.viewmodel.LoginViewModelFactory

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun initView() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTitleFoot(
            view1 = binding.root,
            navigationBarColor = R.color.color_F9DCE8,
            statusBarColor = R.color.color_F9DCE8
        )

        RetrofitClient.init(this)
        val factory = LoginViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        // Status/Navi bar
        window.statusBarColor = ContextCompat.getColor(this, R.color.color_F9DCE8)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        initListeners()
        setupLiveValidation()
        observeLogin()
    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.ivClose.setOnClickListener {
            val intent = Intent("com.company.wingstars.OPEN_MAIN")
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            if (intent.resolveActivity(packageManager) != null) startActivity(intent)
            finish()
        }

        binding.cbPsdVisible.setOnCheckedChangeListener { _, isChecked ->
            binding.edtPsd.inputType = if (isChecked)
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            else
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.edtPsd.text?.let { binding.edtPsd.setSelection(it.length) }
        }

        binding.btnLogin.setOnClickListener { onLoginClick() }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, com.wingstars.register.RegisterActivity::class.java))
        }

        binding.tvForgetPsd.setOnClickListener {
            startActivity(Intent(this, com.wingstars.resetpsd.ResetPsdActivity::class.java))
        }
    }

    private fun observeLogin() {
        viewModel.loginSuccess.observe(this) { respone ->
            val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            sharedPref.edit().apply {
                putBoolean("is_logged_in", true)
                putString("phone", binding.edtPhone.text.toString().trim())
                putString("password", binding.edtPsd.text.toString())
                apply()
            }
            navigateToMain()
        }
        viewModel.loginError.observe(this) { msg ->
            showDialog(getString(R.string.login), msg)
        }
    }

    private fun navigateToMain() {
        val intent = Intent("com.company.wingstars.OPEN_MAIN").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun onLoginClick() {
        val phone = binding.edtPhone.text?.toString()?.trim().orEmpty()
        val password = binding.edtPsd.text?.toString().orEmpty()

        if (phone.isEmpty() || password.isEmpty()) {
            showDialog(getString(R.string.login), getString(R.string.error_empty))
            return
        }
        if (!isTaiwanPhone(phone)) {
            showDialog(getString(R.string.account), getString(R.string.error_phone_format))
            return
        }
        if (!isPasswordStrong(password)) {
            showDialog(getString(R.string.password), getString(R.string.note_register_psd))
            return
        }

        viewModel.loginWithToken(
            apiKey = NetBase.API_KEY,
            username = phone,
            password = password
        )
    }

    private fun setupLiveValidation() {
        binding.edtPhone.addTextChangedListener(object : SimpleTW() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val phone = s?.toString()?.trim().orEmpty()
                when {
                    phone.isEmpty() -> showPhoneError(getString(R.string.hint_phone))
                    !isTaiwanPhone(phone) -> showPhoneError(getString(R.string.error_phone_format))
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
            }
        })
    }

    // ---------------- UI error helpers ----------------
    private fun showPhoneError(msg: String) {
        binding.tvPhoneInputError.text = msg
        binding.tvPhoneInputError.visibility = View.VISIBLE
        binding.alertCircle.visibility = View.VISIBLE
    }

    private fun showPhoneNormal() {
        binding.tvPhoneInputError.visibility = View.INVISIBLE
        binding.alertCircle.visibility = View.INVISIBLE
    }

    private fun showPsdError(msg: String) {
        binding.tvPsdInputError.text = msg
        binding.tvPsdInputError.visibility = View.VISIBLE
    }

    private fun showPsdNormal() {
        binding.tvPsdInputError.visibility = View.INVISIBLE
        binding.cbPsdVisible.visibility = View.VISIBLE
    }

    // ---------------- Validators ----------------
    private fun isTaiwanPhone(phone: String): Boolean = Regex("^09\\d{8}$").matches(phone)

    private fun isPasswordStrong(pwd: String): Boolean {
        if (pwd.length < 8) return false
        val hasLetter = pwd.any { it.isLetter() }
        val hasDigit = pwd.any { it.isDigit() }
        return hasLetter && hasDigit
    }

    // ---------------- Simple TextWatcher ----------------
    private open class SimpleTW : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {}
    }

    private fun showDialog(title: String, message: String) {
        if (isFinishing) return
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.confirm)) { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
