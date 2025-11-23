package com.wingstars.resetpsd

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.wingstars.login.R
import com.wingstars.login.databinding.ActivityResetPsdBinding
import com.wingstars.register.RegisterActivity.SimpleTW

class ResetPsdActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResetPsdBinding
    private var timer: CountDownTimer? = null

    private val phoneRegex = Regex("^09\\d{8}$")     // Taiwan mobile (ví dụ)
    private val otpLength = 6                        // giả định OTP 6 số

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPsdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        updateSendButtonState()

        // Back
        binding.ivClose.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Focus hiệu ứng khung + đổi màu chữ nút
        binding.edtPhone.setOnFocusChangeListener { _, hasFocus ->
            binding.rlPhone.isActivated = hasFocus
            val colorRes = if (hasFocus && binding.btnSendCode.isEnabled) R.color.white
            else R.color.text_tittle
            val colorBg = if (hasFocus && binding.btnSendCode.isEnabled) R.drawable.bg_send_code_able
            else R.drawable.bg_send_code
            binding.btnSendCode.setTextColor(ContextCompat.getColor(this, colorRes))
            binding.btnSendCode.background = ContextCompat.getDrawable(this, colorBg)
        }


        // Theo dõi nhập SĐT: bật/ẩn lỗi + bật nút Gửi mã
        binding.edtPhone.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val ok = phoneRegex.matches(s?.toString().orEmpty())
                binding.tvPhoneInputError.visibility =
                    if (s.isNullOrEmpty() || ok) View.INVISIBLE else View.VISIBLE
                updateSendButtonState()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })


        // Bấm gửi mã (giả lập)
        binding.btnSendCode.setOnClickListener {
            val phone = binding.edtPhone.text?.toString().orEmpty()
            if (!phoneRegex.matches(phone)) {
                binding.tvPhoneInputError.visibility = View.VISIBLE
                return@setOnClickListener
            }
            showTimerUI()      // ẩn nút, hiện đồng hồ
            startCountDown()   // 60 giây
        }

        // Nhập OTP: enable nút Confirm khi đủ độ dài
        binding.edtPhoneCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val enabled = s?.length == otpLength
                binding.btnConfirm.isEnabled = enabled
                if (enabled) {
                    binding.btnConfirm.background = getDrawable(R.drawable.bg_button_login_able)
                    binding.btnConfirm.setTextColor(getColor(R.color.white))


                } else {
                    binding.btnConfirm.background = getDrawable(R.drawable.bg_button_login_disable)
                    binding.btnConfirm.setTextColor(getColor(R.color.gray_500))


                }
            }
        })

        // “重新傳送”
        binding.tvResend?.setOnClickListener {
            startCountDown()
        }
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
            // Xử lý khi người dùng nhấn nút Confirm
            binding.llInputCode.visibility = View.GONE
            binding.llInputPsd.visibility = View.VISIBLE
            binding.btnConfirm.background = getDrawable(R.drawable.bg_button_login_disable)
            binding.btnConfirm.setTextColor(getColor(R.color.gray_500))
            binding.tvTitle.setText("設定新密碼")
        }
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
        else R.drawable.bg_send_code
        binding.btnSendCode.setTextColor(ContextCompat.getColor(this, colorRes))
        binding.btnSendCode.background = ContextCompat.getDrawable(this, colorBg)

    }
    private fun showTimerUI() {
        // Ẩn nút gửi mã, hiện khung timer
        binding.btnSendCode.visibility = View.GONE
        binding.rlCodeTimer.visibility = View.VISIBLE
        // Chuyển neo phải của EditText sang timer
        setEditTextRightAnchor(R.id.rl_code_timer)
    }

    private fun showSendButtonUI() {
        binding.btnSendCode.visibility = View.VISIBLE
        binding.rlCodeTimer.visibility = View.GONE
        setEditTextRightAnchor(R.id.btn_send_code)
        updateSendButtonState()
    }


    /** Đổi rule: edt_phone START_OF targetId (RelativeLayout rules) */
    private fun setEditTextRightAnchor(targetId: Int) {
        val lp = binding.edtPhone.layoutParams as RelativeLayout.LayoutParams
        lp.addRule(RelativeLayout.START_OF, targetId)
        binding.edtPhone.layoutParams = lp
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
    private fun showPsdConfirmError(msg: String) {

        binding.tvPsdReinputError.text = msg
        binding.tvPsdReinputError.visibility = View.VISIBLE
    }
    private fun showPsdConfirmNormal() {
        binding.tvPsdReinputError.text = ""
        binding.tvPsdReinputError.visibility = View.INVISIBLE
    }
    private fun showPsdNormal() {
        binding.tvPsdInputError.setTextColor(
            ContextCompat.getColor(this, R.color.gray_400) // @ColorInt
        )
        binding.tvPsdInputError.visibility = View.VISIBLE
//        binding.alertCircle.visibility = View.VISIBLE
    }
    private fun showPsdError(msg: String) {
        binding.tvPsdInputError.text = msg
        binding.tvPsdInputError.setTextColor(
            ContextCompat.getColor(this, R.color.color_FB2C36) // @ColorInt
        )
        binding.tvPsdInputError.visibility = View.VISIBLE

//        binding.alertCircle.visibility = View.VISIBLE
    }
    private fun isPasswordStrong(pwd: String): Boolean {
        if (pwd.length < 8) return false
        val hasLetter = pwd.any { it.isLetter() }
        val hasDigit  = pwd.any { it.isDigit() }
        return hasLetter && hasDigit
    }
    private fun isAllValid(): Boolean {

        val phone    = binding.edtPhone.text?.toString()?.trim().orEmpty()
        val code     = binding.edtPhoneCode?.text?.toString()?.trim().orEmpty() // nếu có ô nhập OTP
        val pwd      = binding.edtPsd.text?.toString().orEmpty()
        val confirm  = binding.edtPsdConfirm.text?.toString().orEmpty()
        return phone.isNotEmpty() && code.isNotEmpty() && pwd.isNotEmpty() && confirm.isNotEmpty()
    }
    private fun updateConfirmButtonState() {
        val enabled = isAllValid()
        binding.btnConfirm.isEnabled = enabled
        if (enabled) {
            binding.btnConfirm.background = ContextCompat.getDrawable(this, R.drawable.bg_button_login_able)
            binding.btnConfirm.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            binding.btnConfirm.background = ContextCompat.getDrawable(this, R.drawable.bg_button_login_disable)
            binding.btnConfirm.setTextColor(ContextCompat.getColor(this, R.color.gray_500))
        }
//        debugValidity(enabled)
    }

    private open class SimpleTW : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {}
    }
    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }
}
