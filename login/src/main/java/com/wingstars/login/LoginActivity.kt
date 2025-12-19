package com.wingstars.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tencent.mmkv.MMKV
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.net.NetBase
import com.wingstars.base.net.beans.CRMSignInRequest
import com.wingstars.login.databinding.ActivityLoginBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class LoginActivity : BaseActivity(), LoginNavigator {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private var tag = ""
    private var isFromSplash = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setTitleFoot(
            view1 = binding.root,
            navigationBarColor = R.color.white,
            statusBarColor = R.color.color_F9DCE8
        )
        window.statusBarColor = ContextCompat.getColor(this, R.color.color_F9DCE8)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        tag = intent?.getStringExtra("tag").toString()
        isFromSplash = intent?.getBooleanExtra("isFromSplash", false) == true

        viewModel.setNavigator(this)
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    override fun initView() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }

        // --- MMKV: Ghi nhớ tài khoản ---
        if (MMKV.defaultMMKV().decodeBool("isRememberAccount")) {
            val account = MMKV.defaultMMKV().decodeString("member_account")
            val psd = MMKV.defaultMMKV().decodeString("member_psd")
            if (!account.isNullOrEmpty() && !psd.isNullOrEmpty()) {
                binding.edtPhone.setText(account)
                binding.edtPsd.setText(psd)
            }
            binding.cbPsd.isChecked = true
        } else {
            binding.cbPsd.isChecked = false
        }

        binding.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.ivClose.setOnClickListener {
            if (isFromSplash) {
                navigateToMain()
            }
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

        binding.cbPsdVisible.setOnCheckedChangeListener { _, isChecked ->
            binding.edtPsd.inputType = if (isChecked)
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            else
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.edtPsd.text?.let { binding.edtPsd.setSelection(it.length) }
        }

        // --- Nút Login ---
        binding.btnLogin.setOnClickListener {
            val phoneStr = binding.edtPhone.text.toString().trim()
            val psdStr = binding.edtPsd.text.toString().trim()

            if (phoneStr.isEmpty()) {
                showPhoneError(getString(R.string.hint_phone))
                return@setOnClickListener
            }
            if (!isTaiwanPhone(phoneStr)) {
                showDialog(getString(R.string.account), getString(R.string.error_phone_format))
                return@setOnClickListener
            }
            if (psdStr.isEmpty()) {
                showPsdError(getString(R.string.error_psd_empty))
                return@setOnClickListener
            }
            // Check mật khẩu mạnh (từ code bạn của bạn)
            if (!isPasswordStrong(psdStr)) {
                showPsdError(getString(R.string.note_register_psd))
                return@setOnClickListener
            }

            viewModel.userCheck(CRMSignInRequest(phoneStr, psdStr), binding.cbPsd.isChecked)
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, com.wingstars.register.RegisterActivity::class.java))
        }

        binding.tvForgetPsd.setOnClickListener {
            startActivity(Intent(this, com.wingstars.resetpsd.ResetPsdActivity::class.java))
        }
    }

    private fun navigateToMain() {
        val intent1 = Intent("com.company.wingstars.OPEN_MAIN")
        intent?.getStringExtra("fcmTag")?.let {
            intent1.putExtra("fcmTag", it)
        }
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        if (intent1.resolveActivity(packageManager) != null) {
            startActivity(intent1)
        }
    }

    // --- Validation Logic ---
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
    private fun showPsdNormal() {
        binding.tvPsdInputError.text = ""
        binding.tvPsdInputError.visibility = View.INVISIBLE
        binding.cbPsdVisible.visibility = View.VISIBLE
    }

    private fun showDialog(title: String, message: String) {
        if (isFinishing) return
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.confirm)) { d, _ -> d.dismiss() }
            .show()
    }

    private fun isTaiwanPhone(phone: String): Boolean = Regex("^09\\d{8}$").matches(phone)

    private fun isPasswordStrong(pwd: String): Boolean {
        if (pwd.length < 8) return false
        val hasLetter = pwd.any { it.isLetter() }
        val hasDigit = pwd.any { it.isDigit() }
        return hasLetter && hasDigit
    }

    private open class SimpleTW : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {}
    }

    private fun setUserName(userName: String){
        MMKV.defaultMMKV().encode("user_name", userName)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent?) {
    }

    // --- QUAN TRỌNG: LOGIN SUCCESS ---
    override fun loginSuccess() {
        NetBase.refreshEvtTasks(true)
        val phoneStr = binding.edtPhone.text.toString().trim()
        val psdStr = binding.edtPsd.text.toString().trim()

        setUserName(phoneStr)

        // >>> BỔ SUNG: Lưu SharedPreferences cho UserFragment dùng <<<
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().apply {
            putBoolean("is_logged_in", true)
            putString("phone", phoneStr)
            putString("password", psdStr)
            apply()
        }
        // >>> KẾT THÚC BỔ SUNG <<<

        sendBroadcast(Intent(NetBase.BROADCAST_USER_LOGIN))
        if (tag.isNotEmpty()) {
            val intent = Intent(NetBase.BROADCAST_LOGIN_SUCCESS_INTENT)
            intent.putExtra("intentTag", tag)
            sendBroadcast(intent)
        }
        EventBus.getDefault().post(MessageEvent(EventState.LOG_IN.name, ""))

        if (isFromSplash) {
            navigateToMain()
        } else {
            finish()
        }
    }

    override fun showNotRegisteredDialog() {
        showCustomWarningDialog()
    }

    private fun showCustomWarningDialog() {
        if (isFinishing) return
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_warning_custom, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val btnConfirm = dialogView.findViewById<android.view.View>(R.id.btnConfirm)
        btnConfirm.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, com.wingstars.register.RegisterActivity::class.java))
        }
        dialog.show()
    }
}