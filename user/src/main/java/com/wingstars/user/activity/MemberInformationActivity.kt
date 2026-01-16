package com.wingstars.user.activity

import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.WindowInsetsControllerCompat
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.net.API
import com.wingstars.base.net.ApiService
import com.wingstars.base.net.NetBase
import com.wingstars.base.net.beans.CRMBaseResponse
import com.wingstars.base.net.beans.CRMDeleteRespone
import com.wingstars.base.utils.MMKVManagement
import com.wingstars.base.view.UpLoadingDialog
import com.wingstars.login.LoginActivity
import com.wingstars.user.utils.KeyboardUtils
import com.wingstars.user.databinding.ActivityMemberInformationBinding
import com.wingstars.user.dialog.DeleteAccountDialog
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MemberInformationActivity : BaseActivity() {
    private lateinit var binding: ActivityMemberInformationBinding
    private val changePasswordLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val newPassword = result.data?.getStringExtra("new_password") ?: ""
                binding.edtPassword.setText(newPassword)
            }
        }

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
            if (view is EditText || view is AutoCompleteTextView) {
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
    override fun initView(){
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
            var intent = Intent(this, ChangeMemberPasswordActivity::class.java)
            changePasswordLauncher.launch(intent)
        }
        binding.edtDeleteAccount.setOnClickListener {
            DeleteAccountDialog(this) {
                // Khi người dùng nhấn "Xác nhận" trên Dialog
                deleteAccountApi()
            }.show()
        }
    }
    private val chooseMemberLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == RESULT_OK){
            val name1 = result.data?.getStringExtra("name1")?:""
            val name2 = result.data?.getStringExtra("name2")?:""
            val name3 = result.data?.getStringExtra("name3")?:""
            val displayText = listOf(name1, name2, name3)
                .filter { it.isNotEmpty() }
                .joinToString("、") { it.replace("|", " ") }
            binding.edtFavMember.setText(displayText)
        }
    }
    private val barcodeLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if(result.resultCode == RESULT_OK){
            val mobile = result.data?.getStringExtra("mobile_number") ?: ""
            binding.edtBarcodeCarrier.setText(mobile)
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
        binding.phoneMember.text = phone
        binding.idCardNumber.text = identity
        binding.birthday.text = birthday
        binding.edtPassword.setText(password)
        binding.userGender.text = when (gender) {
            "M", "Male", "男" -> "男"
            "F", "Female", "女" -> "女"
            else -> ""
        }
        binding.tvUserName.text = name
        binding.tvUserMail.text = mail
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
                            Toast.makeText(this@MemberInformationActivity, "帳號已成功刪除", Toast.LENGTH_SHORT).show()
                            performLogoutAfterDelete()
                        } else {
                            Toast.makeText(this@MemberInformationActivity, response.message, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onError(e: Throwable) {
                        loadingDialog.dismiss()
                        Toast.makeText(this@MemberInformationActivity, "網路錯誤: ${e.message}", Toast.LENGTH_SHORT).show()
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
    override fun onResume() {
        super.onResume()
        loadMemberInfo()
    }
}