package com.wingstars.register.registrationterms


import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.wingstars.login.R
import com.wingstars.login.databinding.ActivityRegistrationTermsBinding

class RegistrationTermsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationTermsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrationTermsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        binding.ivClose.setOnClickListener {
            finish()
        }

        val spannableString = SpannableString(binding.tvAppContent.text)
        val colorSpan = ForegroundColorSpan(resources.getColor(R.color.color_EE97BB))  // 设置点击文字颜色
        spannableString.setSpan(colorSpan, 82, 96, 0)
        spannableString.setSpan(StyleSpan(Typeface.BOLD),82, 96, 0)
        binding.tvAppContent.text = spannableString

        val spannableString1 = SpannableString(binding.tvAppContent.text)
        val colorSpan1 = ForegroundColorSpan(resources.getColor(R.color.color_EE97BB))  // 设置点击文字颜色
        spannableString1.setSpan(colorSpan1, 97, 101, 0)
        spannableString1.setSpan(StyleSpan(Typeface.BOLD), 97, 101, 0)
        binding.tvAppContent.text = spannableString1

        val spannableString2 = SpannableString(binding.tvPrivacyPolicy.text)
        val colorSpan2 = ForegroundColorSpan(resources.getColor(R.color.color_EE97BB))  // 设置点击文字颜色
        spannableString2.setSpan(colorSpan2, 6, 11, 0)
        spannableString2.setSpan(StyleSpan(Typeface.BOLD), 6, 11, 0)
        binding.tvPrivacyPolicy.text = spannableString2

        val spannableString3 = SpannableString(binding.txtcmd.text)
        val colorSpan3 = ForegroundColorSpan(resources.getColor(R.color.black))  // 设置点击文字颜色
        spannableString3.setSpan(colorSpan3, 0, 30, 0)
        spannableString3.setSpan(StyleSpan(Typeface.BOLD), 0, 29, 0)
        binding.txtcmd.text = spannableString3

        val spannableString4 = SpannableString(binding.txtcmd2.text)
        val colorSpan4 = ForegroundColorSpan(resources.getColor(R.color.black))  // 设置点击文字颜色
        spannableString4.setSpan(colorSpan4, 4, 11, 0)
        spannableString4.setSpan(StyleSpan(Typeface.BOLD), 4, 11, 0)
        binding.txtcmd2.text = spannableString4

        val spannableString5 = SpannableString(binding.txtcmd3.text)
        val colorSpan5 = ForegroundColorSpan(resources.getColor(R.color.black))  // 设置点击文字颜色
        spannableString5.setSpan(colorSpan5, 0, 13, 0)
        spannableString5.setSpan(StyleSpan(Typeface.BOLD), 0, 13, 0)
        binding.txtcmd3.text = spannableString5

        val spannableString6 = SpannableString(binding.txtcmd4.text)
        val colorSpan6 = ForegroundColorSpan(resources.getColor(R.color.black))  // 设置点击文字颜色
        spannableString6.setSpan(colorSpan6, 9, 13, 0)
        spannableString6.setSpan(StyleSpan(Typeface.BOLD), 9, 13, 0)
        binding.txtcmd4.text = spannableString6
        boldAll(binding.tvPhonePurposeLabel)
        boldAll(binding.tvPhonePrivacyLabel)
        boldAll(binding.tvEmailPurposeLabel)
        boldAll(binding.tvEmailPrivacyLabel)

        boldKeywords(binding.tvPhonePurpose1, "帳戶驗證機制","驗證碼")
        boldKeywords(binding.tvPhonePurpose2, "密碼找回")
        boldKeywords(binding.tvPhonePurpose3, "購票成功通知、交易提醒、會員權益資訊")

        boldKeywords(binding.tvEmailPurpose1,  "帳戶驗證與重要通知管道")
        boldKeywords(binding.tvEmailPurpose2,  "購票、購物、會員活動通知")
        boldKeywords(binding.tvEmailPrivacy1,  "您的電子信箱不會公開顯示，也不會提供給第三方使用。")
        boldKeywords(binding.tvEmailPrivacy2,  "信箱無法修改")

        boldAll(binding.tvBirthPurposeLabel)   // 「用途：」
        boldAll(binding.tvBirthPrivacyLabel)   // 「隱私保障：」

        boldKeywords(binding.tvBirthPurpose1, "APP 年齡限制規範")
        boldKeywords(binding.tvBirthPurpose2, "會員專屬生日禮", "特別活動邀請")
        boldKeywords(binding.tvBirthPrivacy2, "無法修改")




        // ===== 六、性別 =====
        boldAll(binding.tvGenderPurposeLabel)  // 「用途：」
        boldAll(binding.tvGenderPrivacyLabel)  // 「隱私保障：」





    }
    private fun boldAll(tv: TextView) {
        val s = tv.text.toString()
        val sp = SpannableString(s)
        sp.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(tv.context, R.color.black)),
            0, s.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        sp.setSpan(
            StyleSpan(Typeface.BOLD),
            0, s.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv.text = sp
    }

    private fun boldKeywords(tv: TextView, vararg keywords: String) {
        val sp = SpannableString(tv.text) // giữ các span đã có (nếu có)
        val color = ContextCompat.getColor(tv.context, R.color.black)
        val s = sp.toString()
        for (kw in keywords) {
            var start = s.indexOf(kw)
            while (start >= 0) {
                val end = start + kw.length
                sp.setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                sp.setSpan(StyleSpan(Typeface.BOLD),     start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                start = s.indexOf(kw, end) // tìm lần xuất hiện tiếp theo (nếu có)
            }
        }
        tv.text = sp
    }


}