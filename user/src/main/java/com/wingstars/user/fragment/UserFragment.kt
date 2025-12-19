package com.wingstars.user.fragment

import android.content.ClipData
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.wingstars.base.base.BaseFragment
import com.wingstars.login.LoginActivity
import com.wingstars.user.R
import com.wingstars.user.activity.ContactCustomerActivity
import com.wingstars.user.activity.FrequentlyAskedQuestionsActivity
import com.wingstars.user.activity.MemBarCodeActivity
import com.wingstars.user.activity.MemberInformationActivity
import com.wingstars.user.activity.MemberLevelActivity
import com.wingstars.user.activity.MobileBarcodeCarrierActivity
import com.wingstars.user.activity.PolicyTermActivity
import com.wingstars.user.activity.StoreLocationActivity
import com.wingstars.user.databinding.FragmentUserBinding
import com.wingstars.user.dialog.LogoutDialog
import com.wingstars.user.dialog.NotificationDialog
import com.wingstars.user.net.BaseApplication
import com.wingstars.user.net.NetworkMonitorNew
import java.io.File
import java.io.FileOutputStream

class UserFragment : BaseFragment(){
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private val TAG = "UserFragment"
    private var isNotificationOn = false
    private var isBarcodeContentVisible = false
    private lateinit var originalConstraintSet: ConstraintSet
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        binding.srlUserRecord.setEnableNestedScroll(true)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        updateLoginUI()
        showMemberQRCode()
        originalConstraintSet = ConstraintSet()
        originalConstraintSet.clone(binding.barcodeMember)



    }
    private fun initView() {
        val packageManager: PackageManager =  requireActivity().packageManager
        val packageInfo: PackageInfo = packageManager.getPackageInfo(requireActivity().packageName, 0)

        binding.tvVersion.text = "版本 "+packageInfo.versionName
        binding.srlUserRecord.setOnRefreshListener {
            if (NetworkMonitorNew.getInstance(requireActivity()).currentNetworkState.isConnected) {
            } else {
                Toast.makeText(
                    BaseApplication.Companion.shared()!!,
                    BaseApplication.Companion.shared()!!.getString(R.string.user_error_network),
                    Toast.LENGTH_SHORT
                ).show()
            }
            binding.srlUserRecord.finishRefresh()
        }
       binding.layoutMain.containerForRectangleAndText.setOnClickListener {
           val intent = Intent(requireActivity(), LoginActivity::class.java)
           startActivity(intent)
       }
        binding.barcodeNull.setOnClickListener {
            val intent = Intent(requireActivity(), MobileBarcodeCarrierActivity::class.java)
            startActivity(intent)
        }
        binding.icArrowDown.setOnClickListener {
            isBarcodeContentVisible = !isBarcodeContentVisible
            val hasBarcode = hasBarcode()

            if (isBarcodeContentVisible) {
                // MỞ
                if (hasBarcode) {
                    binding.barcode.visibility = View.VISIBLE
                    binding.tvBarcodeDesc.visibility = View.VISIBLE
                    binding.barcodeNull.visibility = View.GONE
                } else {
                    binding.barcode.visibility = View.GONE
                    binding.tvBarcodeDesc.visibility = View.GONE
                    binding.barcodeNull.visibility = View.VISIBLE
                }

                val params = binding.barcodeMember.layoutParams
                params.height = resources.getDimensionPixelSize(R.dimen.dp_186)
                binding.barcodeMember.layoutParams = params

                originalConstraintSet.applyTo(binding.barcodeMember)

            } else {
                // ĐÓNG
                binding.barcode.visibility = View.GONE
                binding.tvBarcodeDesc.visibility = View.GONE
                binding.barcodeNull.visibility = View.GONE

                val params = binding.barcodeMember.layoutParams
                params.height = resources.getDimensionPixelSize(R.dimen.dp_56)
                binding.barcodeMember.layoutParams = params

                val set = ConstraintSet()
                set.clone(binding.barcodeMember)
                set.clear(binding.tvMemberBarcode.id, ConstraintSet.TOP)
                set.clear(binding.tvMemberBarcode.id, ConstraintSet.BOTTOM)
                set.clear(binding.icArrowDown.id, ConstraintSet.TOP)
                set.clear(binding.icArrowDown.id, ConstraintSet.BOTTOM)
                set.centerVertically(binding.tvMemberBarcode.id, ConstraintSet.PARENT_ID)
                set.centerVertically(binding.icArrowDown.id, ConstraintSet.PARENT_ID)
                set.applyTo(binding.barcodeMember)
            }
        }

        binding.llUserMemberInformation.setOnClickListener {
            val intent = Intent(requireActivity(), MemberInformationActivity::class.java)
            startActivity(intent)
        }
        binding.llUserNotificationSettings.setOnClickListener {
            val dialog = NotificationDialog(isNotificationOn) { isOn ->
                isNotificationOn = isOn
                binding.form4Status.text = if (isOn) "已開啟" else ""
            }
            dialog.show(parentFragmentManager)
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
            val imageUri = getImageUri(requireContext(), R.drawable.ic_logo_foreground)

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
        binding.llUserLogOut.setOnClickListener {
            LogoutDialog(requireContext()) {
                performLogout()
            }.show()
        }
        binding.llUserFacebook.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.facebook.com/tsgwingstars/?locale=zh_TW")
            startActivity(intent)
        }
        binding.llUserInstagram.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.instagram.com/wing_stars_official/")
            startActivity(intent)
        }
        binding.llUserYoutube.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.youtube.com/@WingStars-TSG")
            startActivity(intent)
        }
        binding.srlUserRecord.setOnRefreshListener { refreshLayout ->
            Toast.makeText(requireContext(),"loading", Toast.LENGTH_SHORT).show()
            refreshLayout.finishRefresh(1500/*ms*/)
        }
    }
    private fun getImageUri(requireContext: Context, logoShare: Int): Uri {
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

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }
    private fun performLogout() {
        val sharedPref = requireActivity().getSharedPreferences("user_prefs", 0)
        sharedPref.edit().clear().apply()
        updateLoginUI()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
    private fun updateLoginUI() {
        val sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("is_logged_in", false)
        val userName = sharedPref.getString("name", "")
        val effectiveDate = sharedPref.getString("effective_date", "")

        binding.cardGeneralMember.visibility = if (isLoggedIn) View.GONE else View.VISIBLE
        binding.cardFriendshipMember.visibility = if (isLoggedIn) View.VISIBLE else View.GONE
        binding.qrMember.visibility = if (isLoggedIn) View.VISIBLE else View.GONE
        binding.barcodeMember.visibility = if (isLoggedIn) View.VISIBLE else View.GONE
        binding.layoutMain.tvLogin.visibility = if (isLoggedIn) View.GONE else View.VISIBLE
        binding.layoutMain.tvLoginGenerally.visibility = if (isLoggedIn) View.VISIBLE else View.GONE
        binding.layoutMain.effectiveDate.visibility = if (isLoggedIn) View.VISIBLE else View.GONE
        if (isLoggedIn) {
            binding.layoutMain.tvUserName.text = userName ?: ""
            binding.layoutMain.effectiveDate.text = effectiveDate ?: ""
        }
    }
    private fun hasBarcode(): Boolean {
        val pref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return !pref.getString("barcode_number", "").isNullOrEmpty()
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

    private fun getMemberInfoString(phone: String?, code: String?, birthday: String?, gender: String?): String {
        return """
        {
            "phone": "$phone",
            "code": "$code",
            "birthday": "$birthday",
            "gender": "$gender"
        }
    """.trimIndent()
    }
    private fun showMemberQRCode() {
        val pref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = pref.getBoolean("is_logged_in", false)
        if (!isLoggedIn) return

        val phone = pref.getString("phone", "")
        val code = pref.getString("code", "")
        val birthday = pref.getString("birthday", "")
        val gender = pref.getString("gender", "")

        val qrData = getMemberInfoString(phone, code, birthday, gender)
        val bitmap = generateQRCode(qrData)
        bitmap?.let {
            binding.qrMember.visibility = View.VISIBLE
            binding.qr.setImageBitmap(it)
        }
    }
    private fun updateBarcodeUI() {
        val pref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val barcodeNumber = pref.getString("barcode_number", "")

        val hasBarcode = !barcodeNumber.isNullOrEmpty()

        if (hasBarcode) {
            val bitmap = generateBarcode(barcodeNumber!!)
            bitmap?.let {
                binding.barcode.setImageBitmap(it)
            }

            binding.barcode.visibility = View.VISIBLE
            binding.tvBarcodeDesc.visibility = View.VISIBLE
            binding.tvBarcodeDesc.text = barcodeNumber

            binding.barcodeNull.visibility = View.GONE
            binding.icArrowDown.visibility = View.VISIBLE

        } else {
            binding.barcode.visibility = View.GONE
            binding.tvBarcodeDesc.visibility = View.GONE

            binding.barcodeNull.visibility = View.VISIBLE
            binding.icArrowDown.visibility = View.GONE
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onResume() {
        super.onResume()
        updateBarcodeUI()
    }

}