package com.wingstars.user.fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.widget.NestedScrollView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.tencent.mmkv.MMKV
import com.wingstars.base.base.BaseFragment
import com.wingstars.base.net.NetBase
import com.wingstars.base.utils.MMKVManagement
import com.wingstars.login.LoginActivity
import com.wingstars.user.R
import com.wingstars.user.activity.AchievementActivity
import com.wingstars.user.activity.CheerModeActivity
import com.wingstars.user.activity.ContactCustomerActivity
import com.wingstars.user.activity.CumulativeAmountActivity
import com.wingstars.user.activity.FrequentlyAskedQuestionsActivity
import com.wingstars.user.activity.MemberInformationActivity
import com.wingstars.user.activity.MemberLevelActivity
import com.wingstars.user.activity.PolicyTermActivity
import com.wingstars.user.activity.StoreLocationActivity
import com.wingstars.user.databinding.FragmentUserBinding
import com.wingstars.user.dialog.LogoutDialog
import com.wingstars.user.dialog.NotificationDialog
import com.wingstars.user.viewmodel.UserNotificationViewModel
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri

class UserFragment : BaseFragment() {
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private var isNotificationOn = false
    private var isBarcodeContentVisible = true
    private lateinit var originalConstraintSet: ConstraintSet
    
    private val notificationViewModel: UserNotificationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        binding.srlUserRecord.setEnableNestedScroll(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        originalConstraintSet = ConstraintSet()
        originalConstraintSet.clone(binding.barcodeMember)

        val gradientDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.bg_review_gradient)!!
        val whiteOverlay = ColorDrawable(Color.WHITE).also { it.alpha = 0 }
        binding.layoutMain.rlTop.background = LayerDrawable(arrayOf(gradientDrawable, whiteOverlay))

        binding.content.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
                val headerHeight = binding.bgHeader1.height.toFloat()
                val ratio = (scrollY / headerHeight).coerceIn(0f, 1f)
                binding.bgHeader1.apply {
                    alpha = 1f - ratio
                    scaleX = 1f + ratio * 0.1f
                    scaleY = 1f + ratio * 0.1f
                }
                (binding.layoutMain.rlTop.background as? LayerDrawable)
                    ?.getDrawable(1)?.alpha = (ratio * 255).toInt()
            }
        )
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        val packageManager: PackageManager = requireActivity().packageManager
        val packageInfo: PackageInfo =
            packageManager.getPackageInfo(requireActivity().packageName, 0)
        binding.tvVersion.text = "版本 " + packageInfo.versionName
        binding.srlUserRecord.setOnRefreshListener {
            if (!NetBase.checkNetworkOrToast(requireContext())) return@setOnRefreshListener
            binding.srlUserRecord.finishRefresh()
        }
        binding.layoutMain.containerForRectangleAndText.setOnClickListener {
            if (!MMKVManagement.isLogin()) {
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
            }
        }
        binding.barcodeNull.setOnClickListener {
            val intent = Intent(requireActivity(), MemberInformationActivity::class.java)
            startActivity(intent)
        }
        binding.icArrowDown.setOnClickListener {
            isBarcodeContentVisible = !isBarcodeContentVisible

            if (isBarcodeContentVisible) {
                val params = binding.barcodeMember.layoutParams
                params.height = resources.getDimensionPixelSize(R.dimen.dp_186)
                binding.barcodeMember.layoutParams = params
                originalConstraintSet.applyTo(binding.barcodeMember)
                updateBarcodeUI()
            } else {
                val params = binding.barcodeMember.layoutParams
                params.height = resources.getDimensionPixelSize(R.dimen.dp_56)
                binding.barcodeMember.layoutParams = params
                binding.barcode.visibility = View.GONE
                binding.tvBarcodeDesc.visibility = View.GONE
                binding.barcodeNull.visibility = View.GONE
                val set = ConstraintSet()
                set.clone(binding.barcodeMember)
                set.centerVertically(binding.tvMemberBarcode.id, ConstraintSet.PARENT_ID)
                set.centerVertically(binding.icArrowDown.id, ConstraintSet.PARENT_ID)
                set.applyTo(binding.barcodeMember)
            }
        }
        binding.llUserMemberInformation.setOnClickListener {
            checkLoginOrGoLogin {
                val intent = Intent(requireActivity(), MemberInformationActivity::class.java)
                startActivity(intent)
            }
        }
        binding.llUserNotificationSettings.setOnClickListener {
            checkLoginOrGoLogin {
                val dialog = NotificationDialog(MMKVManagement.isNotificationOn()) { isOn ->
                    isNotificationOn = isOn
                    MMKVManagement.setIsNotificationOn(isOn)
                    binding.form4Status.text = if (isOn) "已開啟" else ""
                    
                    // 1. Sync setting with Server
                    notificationViewModel.syncNotificationSetting(isOn)
                    
                    // 2. If turned ON, fetch and push unread messages locally
                    if (isOn) {
                        notificationViewModel.pushUnreadMessagesLocally(requireContext())
                    }
                }
                dialog.show(parentFragmentManager)
            }
        }
        binding.llUserFaq.setOnClickListener {
            val intent = Intent(requireActivity(), FrequentlyAskedQuestionsActivity::class.java)
            startActivity(intent)
        }
        binding.llUserStoreLocations.setOnClickListener {
            val intent = Intent(requireActivity(), StoreLocationActivity::class.java)
            startActivity(intent)
        }
        binding.llUserPrivacyPolicy.setOnClickListener {
            val intent = Intent(requireActivity(), PolicyTermActivity::class.java)
            intent.putExtra("tag", "PrivacyPolicy")
            startActivity(intent)
        }
        binding.llUserTermsOfUse.setOnClickListener {
            val intent = Intent(requireActivity(), PolicyTermActivity::class.java)
            intent.putExtra("tag", "UserTerms")
            startActivity(intent)
        }
        binding.llUserMembershipLevels.setOnClickListener {
            val intent = Intent(requireActivity(), MemberLevelActivity::class.java)
            startActivity(intent)
        }
        binding.llUserShareApp.setOnClickListener {
            val imageUri = getImageUri()

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(Intent.EXTRA_TEXT, getString(R.string.txt_share_app))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                clipData = ClipData.newUri(requireContext().contentResolver, "Logo", imageUri)
            }
            startActivity(Intent.createChooser(intent, "Share APP"))

        }
        binding.llUserCCustomerService.setOnClickListener {
            val intent = Intent(requireActivity(), ContactCustomerActivity::class.java)
            startActivity(intent)
        }
//        binding.llUserLogOut.setOnClickListener {
//            LogoutDialog(requireContext()) {
//                performLogout()
//            }.show()
//        }
        binding.llUserFacebook.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = "https://www.facebook.com/tsgwingstars/?locale=zh_TW".toUri()
            startActivity(intent)
        }
        binding.llUserInstagram.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = "https://www.instagram.com/wing_stars_official/".toUri()
            startActivity(intent)
        }
        binding.llUserYoutube.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = "https://www.youtube.com/@WingStars-TSG".toUri()
            startActivity(intent)
        }
        binding.srlUserRecord.setOnRefreshListener { refreshLayout ->
            Toast.makeText(requireContext(), "loading", Toast.LENGTH_SHORT).show()
            refreshLayout.finishRefresh(1500/*ms*/)
        }

        binding.achievement.setOnClickListener {
            checkLoginOrGoLogin {
                val intent = Intent(requireActivity(), AchievementActivity::class.java)
                startActivity(intent)
            }
        }

//        binding.qrMember.setOnClickListener {
//            val intent = Intent(requireActivity(), CumulativeAmountActivity::class.java)
//            startActivity(intent)
//        }
        binding.llUserCheeringMode.setOnClickListener {
            checkLoginOrGoLogin {
                val intent = Intent(requireActivity(), CheerModeActivity::class.java)
                startActivity(intent)
            }
        }
        binding.cardGeneralMember.setOnClickListener {
            checkLoginOrGoLogin { }
        }

    }

    private fun getImageUri(): Uri {
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.logo_share)!!
        val bitmap = drawableToBitmap(drawable)
        val file = File(requireContext().cacheDir, "share_logo.png")
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()
        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: 300
            val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: 300
            val bitmap = createBitmap(width, height)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    private fun performLogout() {
        val sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().apply {
            remove("is_logged_in")
            remove("phone")
            remove("password")
            remove("name")
            remove("code")
            remove("birthday")
            remove("gender")
            remove("barcode_number")
            remove("effective_date")
            apply()
        }
        val mmkv = MMKV.defaultMMKV()
        mmkv.encode("isLogin", false)
        mmkv.removeValueForKey("crm_member_access_token")
        mmkv.removeValueForKey("member_name")
        mmkv.removeValueForKey("crm_member_id")
        updateLoginUI()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.putExtra("isFromSplash", true)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun updateLoginUI() {
        val isLoggedIn = MMKVManagement.isLogin()
        val name = MMKVManagement.getMemberName()

        binding.userLogOut.text =
            if (isLoggedIn) "登出帳號" else "登入帳號"

        binding.llUserLogOut.setOnClickListener {
            if (MMKVManagement.isLogin()) {
                LogoutDialog(requireContext()) {
                    performLogout()
                }.show()
            } else {
                startActivity(Intent(requireActivity(), LoginActivity::class.java))
            }
        }

        binding.containerLeft.isEnabled = false
        binding.qrMember.visibility = if (isLoggedIn) View.VISIBLE else View.GONE
        binding.barcodeMember.visibility = if (isLoggedIn) View.VISIBLE else View.GONE
        binding.layoutMain.tvLogin.visibility = if (isLoggedIn) View.GONE else View.VISIBLE
        binding.layoutMain.tvLoginGenerally.visibility = if (isLoggedIn) View.VISIBLE else View.GONE
        binding.layoutMain.effectiveDateGeneral.visibility =
            if (isLoggedIn) View.GONE else View.VISIBLE

        if (isLoggedIn) {
            isNotificationOn = MMKVManagement.isNotificationOn()
            binding.form4Status.text = if (isNotificationOn) "已開啟" else ""
            binding.layoutMain.tvUserName.text = name
            val expiredDate = MMKVManagement.getMemberExpiredDate()

            binding.layoutMain.effectiveDate.text =
                if (expiredDate.isNotEmpty()) {
                    "會員到期 : ${expiredDate.replace("-", "/")}"
                } else {
                    MMKVManagement.getMemberBirthday()
                }
        }
    }

    private fun generateQRCode(data: String): Bitmap? {
        return try {
            val bitMatrix = MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 400, 400)
            BarcodeEncoder().createBitmap(bitMatrix)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun showMemberQRCode() {
        if (!MMKVManagement.isLogin()) return
        val phone = MMKVManagement.getMemberPhone()
        val code = MMKVManagement.getCrmMemberCode()
        val birthday = MMKVManagement.getMemberBirthday()
        val gender = MMKVManagement.getMemberGender()
        val qrData = """
        {
            "phone": "$phone",
            "code": "$code",
            "birthday": "$birthday",
            "gender": "$gender"
        }
    """.trimIndent()
        generateQRCode(qrData)?.let {
            binding.qrMember.visibility = View.VISIBLE
            binding.qr.setImageBitmap(it)
        }
    }

    private fun updateBarcodeUI() {
        val invoiceNumber = MMKVManagement.getCrmMemberInvoiceNumber()
        val hasBarcode = invoiceNumber.isNotEmpty()
        if (hasBarcode) {
            val bitmap = generateBarcode(invoiceNumber)
            bitmap?.let { binding.barcode.setImageBitmap(it) }
            binding.tvBarcodeDesc.text = invoiceNumber
            binding.icArrowDown.visibility = View.VISIBLE
            if (isBarcodeContentVisible) {
                val params = binding.barcodeMember.layoutParams
                params.height = resources.getDimensionPixelSize(R.dimen.dp_186)
                binding.barcodeMember.layoutParams = params
                originalConstraintSet.applyTo(binding.barcodeMember)
                binding.barcode.visibility = View.VISIBLE
                binding.tvBarcodeDesc.visibility = View.VISIBLE
                binding.barcodeNull.visibility = View.GONE
            } else {
                val params = binding.barcodeMember.layoutParams
                params.height = resources.getDimensionPixelSize(R.dimen.dp_56)
                binding.barcodeMember.layoutParams = params
                val set = ConstraintSet()
                set.clone(binding.barcodeMember)
                set.centerVertically(binding.tvMemberBarcode.id, ConstraintSet.PARENT_ID)
                set.centerVertically(binding.icArrowDown.id, ConstraintSet.PARENT_ID)
                set.applyTo(binding.barcodeMember)
                binding.barcode.visibility = View.GONE
                binding.tvBarcodeDesc.visibility = View.GONE
                binding.barcodeNull.visibility = View.GONE
            }
        } else {
            binding.barcode.visibility = View.GONE
            binding.tvBarcodeDesc.visibility = View.GONE
            binding.barcodeNull.visibility = View.VISIBLE
            binding.icArrowDown.visibility = View.VISIBLE
        }
    }

    private fun generateBarcode(data: String): Bitmap? {
        return try {
            val bitMatrix = MultiFormatWriter()
                .encode(data, BarcodeFormat.CODE_128, 600, 200)
            BarcodeEncoder().createBitmap(bitMatrix)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun checkLoginOrGoLogin(action: () -> Unit) {
        if (MMKVManagement.isLogin()) {
            action()
        } else {
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        updateBarcodeUI()
        updateLoginUI()
        showMemberQRCode()
    }

}