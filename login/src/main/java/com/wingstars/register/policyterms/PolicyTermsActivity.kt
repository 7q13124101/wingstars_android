package com.wingstars.register.policyterms

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.LeadingMarginSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.wingstars.login.databinding.ActivityPolicyTermsBinding
//import androidx.activity.viewModels
import com.wingstars.login.R
import com.wingstars.login.databinding.ActivityRegisterBinding
import com.wingstars.login.databinding.ActivityRegistrationTermsBinding
import com.wingstars.net.beans.PrivacyPolicyResponse

class PolicyTermsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationTermsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrationTermsBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}