package com.wingstars.user

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.wingstars.base.base.BaseFragment
import com.wingstars.user.databinding.FragmentUserBinding
import com.wingstars.user.cheer.MemberInformationActivity
import com.wingstars.user.code.MemBarCodeActivity
import com.wingstars.user.dialog.NotificationDialog
import com.wingstars.user.frequentlyaskedquestion.FrequentlyAskedQuestionsActivity
import com.wingstars.user.membercontact.ContactCustomerActivity
import com.wingstars.user.memberlevel.MemberLevelActivity
import com.wingstars.user.storelocation.StoreLocationActivity
import com.wingstars.user.policyterm.PolicyTermActivity
import java.io.File
import java.io.FileOutputStream

class UserFragment : BaseFragment(){
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private val TAG = "UserFragment"
    private var isNotificationOn = false


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
    }
    private fun initView() {
        val packageManager: PackageManager =  requireActivity().packageManager
        val packageInfo: PackageInfo = packageManager.getPackageInfo(requireActivity().packageName, 0)

        binding.tvVersion.text = "版本 "+packageInfo.versionName
        binding.srlUserRecord.setOnRefreshListener {
            if (NetworkMonitorNew.getInstance(requireActivity()).currentNetworkState.isConnected) {
//                refreshUI()
            } else {
                Toast.makeText(
                    BaseApplication.shared()!!,
                    BaseApplication.shared()!!.getString(R.string.user_error_network),
                    Toast.LENGTH_SHORT
                ).show()
            }
            binding.srlUserRecord.finishRefresh()
        }

        binding.rlVisitorsQrCode.setOnClickListener {
            val intent = Intent(requireActivity(), MemBarCodeActivity::class.java)
            startActivity(intent)
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
            val imageUri = getImageUri(requireContext(),R.drawable.ic_logo_foreground)

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(Intent.EXTRA_TEXT, getString(R.string.txt_share_app))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                clipData = ClipData.newUri(requireContext().contentResolver, "Logo", imageUri)
            }
            startActivity(Intent.createChooser(intent, "Chia sẻ ứng dụng"))

        }
        binding.llUserCCustomerService.setOnClickListener {
            val intent = Intent(requireActivity(), ContactCustomerActivity::class.java)
            startActivity(intent)
        }
        binding.llUserFacebook.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.facebook.com/Tainan.TSG.GhostHawks/?locale=zh_TW")
            startActivity(intent)
        }
        binding.llUserInstagram.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.instagram.com/tainan_tsg_ghosthawks/")
            startActivity(intent)
        }
        binding.llUserYoutube.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.youtube.com/@tainantsgghosthawks662")
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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}