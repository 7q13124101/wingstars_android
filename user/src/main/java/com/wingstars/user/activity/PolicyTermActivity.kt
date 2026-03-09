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
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowInsetsControllerCompat
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
        
        // Config Noto Sans TC font for static titles
        val notoBold = ResourcesCompat.getFont(this, R.font.notosans_tc_bold)
        val notoRegular = ResourcesCompat.getFont(this, R.font.notosans_tc_regular)
        
        binding.txtTitle.typeface = notoBold
        binding.txtTitleTop.typeface = notoBold
        binding.txtContentTop.typeface = notoRegular
        
        // Increase line spacing for txtContentTop
        binding.txtContentTop.setLineSpacing(0f, 1.2f)

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
                            
                            // Apply font and color requirements
                            tvPolicyTitle?.typeface = notoBold
                            tvPolicyContent?.typeface = notoRegular

                            tvPolicyContent?.layoutParams?.let { lp ->
                                if (lp is LinearLayout.LayoutParams) {
                                    lp.rightMargin = dp2px(5F)?.toInt() ?: 0
                                    lp.topMargin = dp2px(0F)?.toInt() ?: 0 // Align text closely with title
                                    lp.bottomMargin = dp2px(5F)?.toInt() ?: 0
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
                            tvPolicyContent?.text = formatPolicyText(safeContent)
                            tvPolicyContent?.setTextColor(getColor(R.color.color_4A5565))
                            binding.llPolicyContent.addView(inflate)
                        }
                    }
                }
            }
        } else if (tag == "UserTerms") {
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
                            
                            // Apply font and color requirements
                            tvPolicyTitle?.typeface = notoBold
                            tvPolicyContent?.typeface = notoRegular

                            tvPolicyContent?.layoutParams?.let { lp ->
                                if (lp is LinearLayout.LayoutParams) {
                                    lp.rightMargin = dp2px(5F)?.toInt() ?: 0
                                    lp.topMargin = dp2px(0F)?.toInt() ?: 0 // Align text closely with title
                                    lp.bottomMargin = dp2px(5F)?.toInt() ?: 0
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
                            tvPolicyContent?.text = formatPolicyText(safeContent)
                            // Use #4A5565 for content
                            tvPolicyContent?.setTextColor(getColor(R.color.color_4A5565))
                            binding.llPolicyContent.addView(inflate)
                        }
                    }
                }
            }
        }
    }
    fun dp2px(dp: Float): Float {
        return resources.displayMetrics.density * dp + 0.5f
    }
    private fun formatPolicyText(raw: String?): CharSequence {
        if (raw.isNullOrEmpty()) return ""
        val ssb = SpannableStringBuilder(raw)
        val lines = raw.split("\n")
        var currentPos = 0
        for (line in lines) {
            val lineEnd = currentPos + line.length
            val numberMatch = Regex("^\\s*\\d+\\.\\s").find(line)
            val bulletMatch = Regex("^\\s*•\\s*").find(line)
            if (numberMatch != null) {
                ssb.setSpan(
                    LeadingMarginSpan.Standard(0, dp(24)),
                    currentPos,
                    lineEnd,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else if (bulletMatch != null) {
                ssb.setSpan(
                    LeadingMarginSpan.Standard(0, dp(14)),
                    currentPos,
                    lineEnd,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            currentPos = lineEnd + 1
            if (currentPos > ssb.length) break
        }
        return ssb
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()


}