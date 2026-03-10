package com.wingstars.user.activity

import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.WindowInsetsControllerCompat
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.net.API
import com.wingstars.base.net.NetBase
import com.wingstars.base.net.beans.CRMBaseResponse
import com.wingstars.base.net.beans.CRMDeleteRespone
import com.wingstars.base.net.beans.CRMMemberContactResponse
import com.wingstars.base.utils.MMKVManagement
import com.wingstars.base.view.UpLoadingDialog
import com.wingstars.login.LoginActivity
import com.wingstars.user.utils.KeyboardUtils
import com.wingstars.user.databinding.ActivityMemberInformationBinding
import com.wingstars.user.dialog.DeleteAccountDialog
import com.wingstars.user.utils.MemberStorage
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MemberInformationActivity : BaseActivity() {
    private lateinit var binding: ActivityMemberInformationBinding
    private var name1: String = ""
    private var name2: String = ""
    private var name3: String = ""

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemberInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true
        initView()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view is EditText) {
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    KeyboardUtils.hideKeyboard(view)
                    view.clearFocus()
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun loadSelectedMembers() {
        val names = MemberStorage.getSelectedMembers()
        name1 = names[0]
        name2 = names[1]
        name3 = names[2]

        val displayText = listOf(name1, name2, name3)
            .filter { it.isNotBlank() }
            .joinToString("、")
        binding.edtFavMember.setText(displayText)
    }

    override fun initView() {
        binding.ivBack.setOnClickListener { finish() }
        binding.edtFavMember.setOnClickListener {
            chooseMemberLauncher.launch(Intent(this, ChooseMemberActivity::class.java))
        }
        binding.icArrow.setOnClickListener {
            chooseMemberLauncher.launch(Intent(this, ChooseMemberActivity::class.java))
        }
        binding.edtBarcodeCarrier.setOnClickListener {
            barcodeLauncher.launch(Intent(this, MobileBarcodeCarrierActivity::class.java))
        }
        binding.ivArrowRight.setOnClickListener {
            barcodeLauncher.launch(Intent(this, MobileBarcodeCarrierActivity::class.java))
        }

        binding.ivIdCard.setOnClickListener {
            val intent = Intent(this, ChangeMemberPasswordActivity::class.java)
            changePasswordLauncher.launch(intent)
        }
        binding.edtPassword.setOnClickListener {
            val intent = Intent(this, ChangeMemberPasswordActivity::class.java)
            changePasswordLauncher.launch(intent)
        }
        binding.edtDeleteAccount.setOnClickListener {
            DeleteAccountDialog(this) {
                deleteAccountApi()
            }.show()
        }
    }

    private val chooseMemberLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val names = listOf(
                    result.data?.getStringExtra("name1") ?: "",
                    result.data?.getStringExtra("name2") ?: "",
                    result.data?.getStringExtra("name3") ?: ""
                ).filter { it.isNotBlank() }

                binding.edtFavMember.setText(names.joinToString("、"))

                MMKVManagement.setMemberFavMember(names)
            }
        }
    private val barcodeLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val mobile = result.data?.getStringExtra("mobile_number") ?: ""
            binding.edtBarcodeCarrier.setText(mobile)
        }
    }

    private val changePasswordLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val newPassword = result.data?.getStringExtra("new_password") ?: ""
                binding.edtPassword.setText(newPassword)
            }
        }

    private fun loadMemberInfo() {
        if (!MMKVManagement.isLogin()) return
        val password = MMKVManagement.getMemberPassword()
        val phone = MMKVManagement.getMemberPhone()
        val identity = MMKVManagement.getMemberIdentity()
        val birthday = MMKVManagement.getMemberBirthday()
        val gender = MMKVManagement.getMemberGender()
        val name = MMKVManagement.getMemberName()
        val mail = MMKVManagement.getMemberMail()
        val invoiceNumber = MMKVManagement.getCrmMemberInvoiceNumber()
        val favMembers = MMKVManagement.getMemberFavMember()
        binding.phoneMember.text = phone
        binding.idCardNumber.text = identity
        binding.birthday.text = formatBirthday(birthday)
        binding.birthday.text = formatBirthday(birthday)
        binding.edtFavMember.setText(favMembers.joinToString("、"))
        binding.edtPassword.setText(password)
        binding.userGender.text = when (gender) {
            "M", "Male", "男" -> "男姓"
            "F", "Female", "女" -> "女姓"
            else -> ""
        }
        binding.tvUserName.text = name
        binding.tvUserMail.text = mail
        binding.edtBarcodeCarrier.setText(invoiceNumber)
    }

    fun formatBirthday(birthdayRaw: String?): String {
        if (birthdayRaw.isNullOrBlank()) return ""

        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            val date: Date = inputFormat.parse(birthdayRaw)!!
            val outputFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            outputFormat.format(date)
        } catch (_: Exception) {
            ""
        }
    }

    private fun deleteAccountApi() {
        val userId = MMKVManagement.getCrmMemberCode()
        if (userId.isEmpty()) {
            Toast.makeText(this, "無法取得會員ID", Toast.LENGTH_SHORT).show()
            return
        }

        val loadingDialog = UpLoadingDialog.Builder(this).createDialog(this)
        loadingDialog.show()

        API.shared?.api?.let { apiService ->
            val url = "${NetBase.HOST_CRM}/api/v1/basic/member/$userId"

            apiService.crmMemberDelete(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<CRMBaseResponse<CRMDeleteRespone>> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onNext(response: CRMBaseResponse<CRMDeleteRespone>) {
                        loadingDialog.dismiss()
                        if (response.success) {
                            Toast.makeText(
                                this@MemberInformationActivity,
                                "帳號已成功刪除",
                                Toast.LENGTH_SHORT
                            ).show()
                            performLogoutAfterDelete()
                        } else {
                            Toast.makeText(
                                this@MemberInformationActivity,
                                response.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onError(e: Throwable) {
                        loadingDialog.dismiss()
                        Toast.makeText(
                            this@MemberInformationActivity,
                            "網路錯誤: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onComplete() {
                        loadingDialog.dismiss()
                    }
                })
        }
    }

    private fun performLogoutAfterDelete() {
        val mmkv = com.tencent.mmkv.MMKV.defaultMMKV()
        mmkv.clearAll()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun loadMemberInfoFromApi() {
        if (!MMKVManagement.isLogin()) return
        val memberId = MMKVManagement.getCrmMemberId()
        API.shared?.api?.let { apiService ->
            val url = "${NetBase.HOST_CRM}/api/v1/basic/member/$memberId/contact"
            apiService.crmGetMemberContact(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<CRMBaseResponse<CRMMemberContactResponse>> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onNext(response: CRMBaseResponse<CRMMemberContactResponse>) {
                        if (response.success) {
                            bindContactDataToView(response.data)
                            MMKVManagement.setMemberName(response.data.Name?: "")
                            MMKVManagement.setMemberPhone(response.data.Phone?: "")
                            MMKVManagement.setCrmMemberCode(response.data.Code?: "")
                            MMKVManagement.setMemberBirthday(response.data.Birthday?: "")
                            MMKVManagement.setMemberGender(response.data.Gender?: "")
                            MMKVManagement.setMemberIdentity(response.data.Identity?: "")
                            MMKVManagement.setMemberMail(response.data.Email?: "")
                            MMKVManagement.setCrmMemberInvoiceNumber(response.data.ExtraData.invoice_number?: "")
                            MMKVManagement.setMemberFavMember(response.data.ExtraData.favorite_players ?: emptyList())
                        } else {
                            loadMemberInfo()
                        }
                    }
                    override fun onError(e: Throwable) {
                        loadMemberInfo()
                    }
                    override fun onComplete() {}
                })
        } ?: loadMemberInfo()
    }

    private fun bindContactDataToView(data: CRMMemberContactResponse) {
        binding.phoneMember.text = data.Phone
        binding.idCardNumber.text = data.Identity
        binding.birthday.text = formatBirthday(data.Birthday)
        val favMembers = data.ExtraData.favorite_players ?: emptyList()
        binding.edtFavMember.setText(favMembers.joinToString("、"))
        binding.edtPassword.setText(MMKVManagement.getMemberPassword())
        binding.userGender.text = when (data.Gender) {
            "M", "Male", "男" -> "男姓"
            "F", "Female", "女" -> "女姓"
            else -> ""
        }
        binding.tvUserName.text = data.Name
        binding.tvUserMail.text = data.Email
        binding.edtBarcodeCarrier.setText(data.ExtraData.invoice_number)
    }

    override fun onResume() {
        super.onResume()
        loadMemberInfoFromApi()
    }
}