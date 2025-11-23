package com.wingstars.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wingstars.login.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Status/Navi bar
        window.statusBarColor = ContextCompat.getColor(this, R.color.color_F9DCE8)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        binding.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.ivClose.setOnClickListener {
            val intent = Intent("com.company.wingstars.OPEN_MAIN")
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            if (intent.resolveActivity(packageManager) != null) startActivity(intent)
            finish()
        }

        binding.tvPhoneInputError.visibility = View.INVISIBLE
        binding.tvPsdInputError.visibility = View.INVISIBLE

        setupLiveValidation()
        binding.edtPhone.setOnFocusChangeListener { _, hasFocus ->
            binding.rlPhone.isActivated = hasFocus
        }
        binding.edtPsd.setOnFocusChangeListener { _, hasFocus ->
            binding.rlPsd.isActivated = hasFocus
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
        // Đăng nhập (demo)
        binding.btnLogin.setOnClickListener {
            Toast.makeText(this, "Login clicked", Toast.LENGTH_SHORT).show()
            onLoginClick() }

        binding.tvRegister.apply {
            isClickable = true
            isFocusable = true
            setOnClickListener {
                startActivity(
                    Intent(this@LoginActivity, com.wingstars.register.RegisterActivity::class.java)
                )
            }
        }
        binding.tvForgetPsd.apply {
            isClickable = true
            isFocusable = true
            setOnClickListener {
                startActivity(
                    Intent(this@LoginActivity, com.wingstars.resetpsd.ResetPsdActivity::class.java)
                )
            }
        }
    }


    // ---------------- Live validation ----------------
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
            }
        })
    }

    // ---------------- Click login (demo) ----------------
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


        // Thành công (demo) → về Main
        val toMain = Intent("com.company.wingstars.OPEN_MAIN")
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        if (toMain.resolveActivity(packageManager) != null) startActivity(toMain)
        finish()
    }


    private fun showDialog(
        title: String,
        message: String,
        positiveText: String = getString(R.string.confirm),
        negativeText: String? = null,
        neutralText: String? = null,
        onPositive: (() -> Unit)? = null,
        onNegative: (() -> Unit)? = null,
        onNeutral:  (() -> Unit)? = null
    ) {
        if (isFinishing) return
        val builder = MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveText) { d, _ ->
                d.dismiss()
                onPositive?.invoke()
            }

        if (!negativeText.isNullOrEmpty()) {
            builder.setNegativeButton(negativeText) { d, _ ->
                d.dismiss()
                onNegative?.invoke()
            }
        }
        if (!neutralText.isNullOrEmpty()) {
            builder.setNeutralButton(neutralText) { d, _ ->
                d.dismiss()
                onNeutral?.invoke()
            }
        }

        builder.show()
    }

    // ---------------- UI error helpers ----------------
    private fun showPhoneError(msg: String) {
        binding.tvPhoneInputError.text = msg
        binding.tvPhoneInputError.visibility = View.VISIBLE
        binding.alertCircle.visibility = View.VISIBLE
    }

    private fun showPhoneNormal() {
        binding.tvPhoneInputError.text = ""
        binding.tvPhoneInputError.visibility = View.INVISIBLE
        binding.alertCircle.visibility = View.INVISIBLE
    }

    private fun showPsdError(msg: String) {
        binding.tvPsdInputError.text = msg
        binding.tvPsdInputError.visibility = View.VISIBLE

    }

    private fun showPsdNormal() {0
        binding.tvPsdInputError.text = ""
        binding.tvPsdInputError.visibility = View.INVISIBLE
        binding.cbPsdVisible.visibility = View.VISIBLE

    }

    // ---------------- Validators ----------------
    private fun isTaiwanPhone(phone: String): Boolean = Regex("^09\\d{8}$").matches(phone)

    private fun isPasswordStrong(pwd: String): Boolean {
        if (pwd.length < 8) return false
        val hasLetter = pwd.any { it.isLetter() }
        val hasDigit  = pwd.any { it.isDigit() }
        return hasLetter && hasDigit
    }

    // TextWatcher rút gọn
    private open class SimpleTW : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {}
    }
}
