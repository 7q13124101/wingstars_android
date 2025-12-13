package com.wingstars.user.activity

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.LeadingMarginSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.wingstars.user.net.BaseApplication
import com.wingstars.user.R
import com.wingstars.user.databinding.ActivityUserTermsBinding
import com.wingstars.user.viewmodel.PolicyTermModel

class PolicyTermActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserTermsBinding
    private val viewModel: PolicyTermModel by viewModels()
    private var tag = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true
        binding = ActivityUserTermsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tag = intent?.getStringExtra("tag").toString()
        initView()
    }

    private fun initView() {
        binding.ivBack.setOnClickListener { finish() }
        if (tag == "PrivacyPolicy") {
            viewModel.getPrivacyPolicyJson(this)
            binding.txtTitle.text = getString(R.string.user_privacy_policy)
            binding.txtTitle.setTextColor(getColor(R.color.color_101828))
            binding.txtTitleTop.visibility = View.VISIBLE
            binding.txtContentTop.visibility = View.VISIBLE
            viewModel.privacyPolicyData.observe(this) { privacyPolicyResponse ->
                if (privacyPolicyResponse != null) {
                    binding.txtTitleTop.text = privacyPolicyResponse.top_title ?: ""
                    binding.txtTitleTop.setTextColor(getColor(R.color.color_101828))
                    binding.txtContentTop.text = privacyPolicyResponse.top_title_content ?: ""
                    binding.txtContentTop.setTextColor(getColor(R.color.color_4A5565))
                    val list = privacyPolicyResponse.policy_data
                    if (!list.isNullOrEmpty()) {
                        binding.llPolicyContent.removeAllViews()
                        for (dataDTO in list) {
                            val inflate = LayoutInflater.from(this)
                                .inflate(R.layout.item_policy, binding.llPolicyContent, false)
                            val tvPolicyTitle: TextView? = inflate.findViewById(R.id.tv_policy_title)
                            val tvPolicyContent: TextView? = inflate.findViewById(R.id.tv_policy_content)
                            tvPolicyContent?.layoutParams?.let { lp ->
                                if (lp is LinearLayout.LayoutParams) {
                                    lp.rightMargin = BaseApplication.Companion.shared()?.dp2px(5F)?.toInt() ?: 0
                                    lp.topMargin = BaseApplication.Companion.shared()?.dp2px(5F)?.toInt() ?: 0
                                    lp.bottomMargin = BaseApplication.Companion.shared()?.dp2px(5F)?.toInt() ?: 0
                                    tvPolicyContent.layoutParams = lp
                                }
                            }
                            if (dataDTO.title.isNullOrEmpty()) {
                                tvPolicyTitle?.visibility = View.GONE
                            } else {
                                tvPolicyTitle?.visibility = View.VISIBLE
                                tvPolicyTitle?.text = dataDTO.title
                                tvPolicyTitle?.setTextColor(getColor(R.color.color_101828))
                            }
                            val safeContent = dataDTO.content ?: ""
                            tvPolicyContent?.text = formatNumberedText(safeContent)
                            tvPolicyContent?.setTextColor(getColor(R.color.color_4A5565))
                            binding.llPolicyContent.addView(inflate)
                        }
                    }
                }
            }
        }else if (tag == "UserTerms") {
            viewModel.getUserTermsJson(this)
            binding.txtTitle.text = getString(R.string.user_terms_of_use)
            binding.txtTitle.setTextColor(getColor(R.color.color_101828))
            binding.txtTitleTop.visibility = View.VISIBLE
            binding.txtContentTop.visibility = View.VISIBLE
            viewModel.userTermsData.observe(this) { userTermsResponse ->
                if (userTermsResponse != null) {
                    binding.txtTitleTop.text = userTermsResponse.top_title ?: ""
                    binding.txtTitleTop.setTextColor(getColor(R.color.color_101828))
                    binding.txtContentTop.text = userTermsResponse.top_title_content ?: ""
                    binding.txtContentTop.setTextColor(getColor(R.color.color_4A5565))
                    val list = userTermsResponse.policy_data
                    if (!list.isNullOrEmpty()) {
                        binding.llPolicyContent.removeAllViews()
                        for (dataDTO in list) {
                            val inflate = LayoutInflater.from(this)
                                .inflate(R.layout.item_policy, binding.llPolicyContent, false)
                            val tvPolicyTitle: TextView? = inflate.findViewById(R.id.tv_policy_title)
                            val tvPolicyContent: TextView? = inflate.findViewById(R.id.tv_policy_content)
                            tvPolicyContent?.layoutParams?.let { lp ->
                                if (lp is LinearLayout.LayoutParams) {
                                    lp.rightMargin = BaseApplication.Companion.shared()?.dp2px(5F)?.toInt() ?: 0
                                    lp.topMargin = BaseApplication.Companion.shared()?.dp2px(5F)?.toInt() ?: 0
                                    lp.bottomMargin = BaseApplication.Companion.shared()?.dp2px(5F)?.toInt() ?: 0
                                    tvPolicyContent.layoutParams = lp
                                }
                            }
                            if (dataDTO.title.isNullOrEmpty()) {
                                tvPolicyTitle?.visibility = View.GONE
                            } else {
                                tvPolicyTitle?.visibility = View.VISIBLE
                                tvPolicyTitle?.text = dataDTO.title
                            }
                            val safeContent = dataDTO.content ?: ""
                            tvPolicyContent?.text = formatNumberedText(safeContent)
                            tvPolicyContent?.setTextColor(getColor(R.color.color_101828))
                            binding.llPolicyContent.addView(inflate)
                        }
                    }
                }
            }
        }
    }
    private fun formatNumberedText(raw: String?): CharSequence {
        if (raw.isNullOrEmpty()) return ""

        val ssb = SpannableStringBuilder(raw)
        val regex = Regex("(?m)^\\d+\\.\\s")
        regex.findAll(raw).forEach { m ->
            ssb.setSpan(
                LeadingMarginSpan.Standard(0, dp(24)),
                m.range.first,
                raw.indexOf('\n', m.range.first).takeIf { it >= 0 } ?: raw.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return ssb
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()


}