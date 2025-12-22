package com.wingstars.user.activity
import android.content.res.Resources
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.tencent.mmkv.MMKV
import com.wingstars.base.net.beans.CheerData
import com.wingstars.base.net.beans.ColorData
import com.wingstars.base.net.beans.PhrasesBean
import com.wingstars.user.R
import com.wingstars.user.databinding.ActivityCheerModeBinding
import com.wingstars.user.fragment.CheerSelectFragment
import com.wingstars.user.viewmodel.CheerModeViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import me.jessyan.autosize.AutoSizeCompat
import me.jessyan.autosize.utils.ScreenUtils


class CheerModeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheerModeBinding
    private lateinit var viewModel: CheerModeViewModel
    private var animation: TranslateAnimation? = null
    private var cheerSelectFragment: CheerSelectFragment? = null
    private var speedMillis: Double = 2.00

    // Default Data
    private var cheerData: CheerData = CheerData(
        "",
        "WingStars！，閃耀每一場！",
        "",
        "中",
        "1X",
        ColorData("#222222",0f,"#222222"),
        ColorData("#F3B9D1",0f,"#F3B9D1")
    )

    private var cheerPhraseString = ""
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        loadCheerInfo()
        try {
            window.navigationBarColor = cheerData.backgroundData.selectColor.toColorInt()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding = ActivityCheerModeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideSystemUI()
        initInitialUI()
        viewModel = ViewModelProvider(this)[CheerModeViewModel::class.java]

        initData()
        initView()
    }

    private fun hideSystemUI() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        ViewCompat.setOnApplyWindowInsetsListener(binding.rlTop) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }

    private fun initInitialUI() {
        try {
            binding.tvContent.setTextColor(cheerData.fontData.selectColor.toColorInt())
            binding.flRoot.setBackgroundColor(cheerData.backgroundData.selectColor.toColorInt())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        updateFontSize(cheerData.fontSizeStr)
        updateSpeed(cheerData.playSpeedStr)
    }

    private fun updateFontSize(sizeStr: String) {
        var fontSize: Float = 180f
        when (sizeStr) {
            getString(R.string.cheer_font_size_small), "Small", "小" -> fontSize = 144f
            getString(R.string.cheer_font_size_medium), "Medium", "中" -> fontSize = 180f
            getString(R.string.cheer_font_size_large), "Large", "大" -> fontSize = 270f
        }
        binding.tvContent.textSize = fontSize
        cheerData.fontSizeStr = sizeStr
    }

    private fun updateSpeed(speedStr: String) {
        when (speedStr) {
            "0.8X" -> speedMillis = 2.50
            "1X" -> speedMillis = 2.00
            "1.5X" -> speedMillis = 1.33
        }
        cheerData.playSpeedStr = speedStr
    }

    private fun initData() {
        viewModel.getAllPhrasesData(this, cheerData.phrases)
        viewModel.getAllFontSizesData(this, cheerData.fontSizeStr)
        viewModel.getAllPlaySpeedsData(cheerData.playSpeedStr)
        viewModel.getTeamMembersData(cheerData.memberName)

    }

    private fun initView() {
        viewModel.allMembersData.observe(this) {
            startMarqueeAnimation(true)
        }
        viewModel.fontSizeIndex.observe(this) { index ->

        }

        binding.rlTop.visibility = View.VISIBLE

        binding.tvContent.setOnClickListener {
            toggleTopBar()
        }

        binding.flRoot.setOnClickListener {
            toggleTopBar()
        }

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.ivCheerFragment.setOnClickListener {
            showSelectCheerFragment()
        }

        viewModel.isLoading.observe(this) { isLoading ->
        }
    }

    private fun toggleTopBar() {
        if (binding.rlTop.visibility == View.VISIBLE) {
            binding.rlTop.visibility = View.GONE
            WindowCompat.getInsetsController(window, window.decorView).hide(WindowInsetsCompat.Type.systemBars())
        } else {
            binding.rlTop.visibility = View.VISIBLE
        }
    }

    private fun setImageBannerView(v: View?, width: Int, height: Int) {
        val scSize = ScreenUtils.getScreenSize(this@CheerModeActivity)
        val params = v?.layoutParams
        params?.width = if (width < scSize[1]) { scSize[1] } else width
        params?.height = scSize[0]
        v?.layoutParams = params
    }

    private fun startMarqueeAnimation(isFirstLoad: Boolean = false) {
        var displayText = cheerData.cheerStr
        if (displayText.isEmpty()) {
            displayText = cheerData.phrases
            if (cheerData.memberName.isNotEmpty()) {
                displayText += " ${cheerData.memberName}"
            }
        }

        if (displayText.trim().isEmpty()) {
            binding.tvContent.text = ""
            binding.tvContent.clearAnimation()
            binding.tvContent.visibility = View.GONE
            binding.rlTop.visibility = View.VISIBLE
            return
        }

        binding.tvContent.text = displayText
        binding.tvContent.post {
            binding.rotatedScrollView.rotation = 90f
            setImageBannerView(
                binding.rotatedScrollView,
                binding.tvContent.width,
                binding.tvContent.height
            )

            binding.tvContent.visibility = View.VISIBLE
            binding.tvContent.post {
                binding.tvContent.clearAnimation()

                val textWidth = binding.tvContent.paint.measureText(binding.tvContent.text.toString())

                animation = TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.6f,
                    Animation.RELATIVE_TO_PARENT, -0.6f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f
                ).apply {
                    duration = (textWidth * speedMillis).toLong()
                    interpolator = LinearInterpolator()
                    repeatCount = Animation.INFINITE
                    repeatMode = Animation.RESTART
                }

                binding.tvContent.startAnimation(animation)
                if (!isFirstLoad) {
                    binding.rlTop.visibility = View.GONE
                }
            }
        }
    }

    private fun showSelectCheerFragment() {
        cheerSelectFragment = CheerSelectFragment.newInstance(cheerData)
        cheerSelectFragment!!.setListener(object : CheerSelectFragment.OnClickItemListener {
            override fun onClickItem(cheerStr: String, phrase: String, memberInfo: PhrasesBean?, playSpeed: String) {
                cheerData.cheerStr = cheerStr
                cheerData.phrases = phrase
                memberInfo?.let { cheerData.memberName = it.title }

                if (playSpeed.isNotEmpty() && cheerData.playSpeedStr != playSpeed) {
                    updateSpeed(playSpeed)
                }

                updateFontSize(cheerData.fontSizeStr)

                // Restart Animation
                startMarqueeAnimation()
            }

            override fun onSelectFinalColorChange(fontData: ColorData, backgroundData: ColorData) {
                cheerData.fontData = fontData
                cheerData.backgroundData = backgroundData

                try {
                    binding.tvContent.setTextColor(cheerData.fontData.selectColor.toColorInt())
                    binding.flRoot.setBackgroundColor(cheerData.backgroundData.selectColor.toColorInt())
                    window.navigationBarColor = cheerData.backgroundData.selectColor.toColorInt()
                } catch (e: Exception) {}
            }
        })

        cheerSelectFragment!!.setDismissListener(object : CheerSelectFragment.OnDialogDismissListener {
            override fun onDialogDismissed() {
                startMarqueeAnimation()
            }
        })
        cheerSelectFragment!!.show(supportFragmentManager, "CheerSelectFragment")
    }

    private fun loadCheerInfo() {
        val kv = MMKV.defaultMMKV()
        val userId = kv.decodeString("crm_member_id") ?: ""

        if (userId.isNotEmpty()) {
            cheerData.cheerStr = kv.decodeString("cheer_string_$userId", "")!!
            cheerData.phrases = kv.decodeString("cheer_phrases_$userId", "WingStars！，閃耀每一場！")!!
            cheerData.memberName = kv.decodeString("cheer_member_name_$userId", "")!!
            cheerData.fontSizeStr = kv.decodeString("cheer_font_size_$userId", "中")!!
            cheerData.playSpeedStr = kv.decodeString("cheer_play_speed_$userId", "1X")!!

            // Font Color
            cheerData.fontData.selectColor = kv.decodeString("cheer_font_color_$userId", "#FF000000")!!
            cheerData.fontData.progress = kv.decodeFloat("cheer_font_progress_$userId", 0f)
            cheerData.fontData.gradientStartColor = kv.decodeString("cheer_font_start_color_$userId", "#FF000000")!!

            // Background Color
            cheerData.backgroundData.selectColor = kv.decodeString("cheer_background_color_$userId", "#FFF4ED54")!!
            cheerData.backgroundData.progress = kv.decodeFloat("cheer_background_progress_$userId", 0f)
            cheerData.backgroundData.gradientStartColor = kv.decodeString("cheer_background_start_color_$userId", "#FFF4ED54")!!
        }
    }

    private fun saveCheerInfo() {
        val kv = MMKV.defaultMMKV()
        val userId = kv.decodeString("crm_member_id") ?: ""

        if (userId.isNotEmpty()) {
            kv.encode("cheer_string_$userId", cheerData.cheerStr)
            kv.encode("cheer_phrases_$userId", cheerData.phrases)
            kv.encode("cheer_member_name_$userId", cheerData.memberName)
            kv.encode("cheer_font_size_$userId", cheerData.fontSizeStr)
            kv.encode("cheer_play_speed_$userId", cheerData.playSpeedStr)

            // Font Color
            kv.encode("cheer_font_color_$userId", cheerData.fontData.selectColor)
            kv.encode("cheer_font_progress_$userId", cheerData.fontData.progress)
            kv.encode("cheer_font_start_color_$userId", cheerData.fontData.gradientStartColor)

            // Background Color
            kv.encode("cheer_background_color_$userId", cheerData.backgroundData.selectColor)
            kv.encode("cheer_background_progress_$userId", cheerData.backgroundData.progress)
            kv.encode("cheer_background_start_color_$userId", cheerData.backgroundData.gradientStartColor)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
        saveCheerInfo()
        binding.tvContent.clearAnimation()
    }

    override fun getResources(): Resources {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            AutoSizeCompat.autoConvertDensityOfGlobal(super.getResources())
        }
        return super.getResources()
    }
}