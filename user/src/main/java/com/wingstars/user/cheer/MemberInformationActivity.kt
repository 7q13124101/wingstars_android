package com.wingstars.user.cheer

import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.AutoCompleteTextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.wingstars.base.base.BaseActivity
import com.wingstars.user.KeyboardUtils
import com.wingstars.user.R
import com.wingstars.user.cheer.ChangeMemberPasswordActivity
import com.wingstars.user.databinding.ActivityChooseMemberBinding
import com.wingstars.user.databinding.ActivityMemberInformationBinding
import com.wingstars.user.dialog.DeleteAccountDialog

class MemberInformationActivity : BaseActivity() {
    private lateinit var binding: ActivityMemberInformationBinding

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemberInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setTitleFoot(
//            view1 = binding.root,
//            statusBarColor = R.color.color_F3F4F6,
//            navigationBarColor = R.color.color_F3F4F6
//        )
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true

        initView()

    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view is AutoCompleteTextView) {
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
        binding.fFavMember.setOnClickListener {
            chooseMemberLauncher.launch(Intent(this, ChooseMemberActivity::class.java))
        }
        binding.fBarcodeCarrier.setOnClickListener {
            barcodeLauncher.launch(Intent(this, MobileBarcodeCarrierActivity::class.java))

        }
        binding.ivIdCard.setOnClickListener {
            var intent = Intent(this, ChangeMemberPasswordActivity::class.java)
            startActivity(intent)
        }
        binding.edtDeleteAccount.setOnClickListener {
            DeleteAccountDialog(this) {
                // Xử lý khi người dùng xác nhận xóa
                // Ví dụ:
                // viewModel.deleteAccount()
            }.show()
        }

    }
    private val chooseMemberLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
        if(result.resultCode == RESULT_OK){
            val name1 = result.data?.getStringExtra("name1")?:""
            val name2 = result.data?.getStringExtra("name2")?:""
            val name3 = result.data?.getStringExtra("name3")?:""
//            val displayText = listOf(name1,name2,name3).filter { it.isNotEmpty() }.joinToString(separator = ",")
//            if(displayText.isNotEmpty()){
//                binding.edtFavMember.setText(displayText)
//            }
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

}

