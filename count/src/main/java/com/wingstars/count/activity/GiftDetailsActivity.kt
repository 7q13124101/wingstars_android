package com.wingstars.count.activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.wingstars.count.R
import com.wingstars.count.databinding.ActivityGiftDetailsBinding
import com.wingstars.count.viewmodel.CountNewDetailViewModel

class GiftDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGiftDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityGiftDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadData()
        setupEvents()
    }

    private fun loadData() {

        val item: CountNewDetailViewModel? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("EXTRA_GIFT_ITEM", CountNewDetailViewModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("EXTRA_GIFT_ITEM")
        }

        item?.let {

            binding.couponName.text = it.title
            binding.pointCost.text = "${it.count} 點"
            binding.tvCouponTime.text = it.expiryDate
            binding.status.text = it.exitem
            binding.maxPerMember.text = it.limit
            binding.activityTime.text = it.total
            binding.finishTime.text = it.location
            binding.tvUsageRules.text = it.usageRules
            binding.tvPrecautions.text = it.description


            Glide.with(this)
                .load(it.image)
                .placeholder(R.drawable.gift_details_image_background)
                .into(binding.merchandise)
        }
    }

    private fun setupEvents() {
        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.rlRuleHeader.setOnClickListener {
            toggleSection(binding.tvUsageRules, binding.ivArrow)
        }
        binding.rlPrecautions.setOnClickListener {
            toggleSection(binding.tvPrecautions, binding.ivArrow)
        }

        binding.btnExchange.setOnClickListener {
        }
    }

    private fun toggleSection(contentView: View, arrowView: ImageView) {
        if (contentView.visibility == View.VISIBLE) {
            contentView.visibility = View.GONE
            arrowView.animate().rotation(0f).setDuration(200).start()
        } else {
            contentView.visibility = View.VISIBLE
            arrowView.animate().rotation(180f).setDuration(200).start()
        }
    }
}