package com.wingstars.user.cheer

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingstars.base.base.BaseActivity
import com.wingstars.user.R
import com.wingstars.user.databinding.ActivityChooseMemberBinding
import com.wingstars.user.dialog.ChooseMemberDialog

class ChooseMemberActivity : BaseActivity() {

    private lateinit var binding: ActivityChooseMemberBinding

    private val members = listOf(
        MemberUI(R.drawable.bg_image_member, R.drawable.ic_02),
        MemberUI(R.drawable.bg_image_member, R.drawable.ic_22),
        MemberUI(R.drawable.bg_image_member, R.drawable.ic_90),
        MemberUI(R.drawable.bg_image_member, R.drawable.ic_05)
    )
    private val members1 = listOf(
        MemberUI(R.drawable.bg_image_member, R.drawable.ic_08),
        MemberUI(R.drawable.bg_image_member, R.drawable.ic_39),
        MemberUI(R.drawable.bg_image_member, R.drawable.ic_57),
        MemberUI(R.drawable.bg_image_member, R.drawable.ic_99)
    )

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true  // icon đen
//        window.statusBarColor = getColor(R.color.color_DE9DBA)
//        window.navigationBarColor = getColor(R.color.color_DE9DBA)
//        setTitleFoot(
//            view1 = binding.root,
//            statusBarColor = R.color.white,
//            navigationBarColor = R.color.color_F3F4F6
//        )

        initView()

    }

    override fun initView() {
        binding.ivBack.setOnClickListener { finish() }

        val adapter = MemberUIAdapter(members)
        val adapter2 = MemberUIAdapter(members1)

        binding.rvMember.adapter = adapter
        binding.rvMember.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.rvMemberSecond.adapter = adapter2
        binding.rvMemberSecond.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        var selectedName: String? = null
        var selectedName1: String? = null
        var selectedName2: String? = null

        binding.rlTeamMember.setOnClickListener {
            ChooseMemberDialog({ selected ->
                val parts = selected.split("|")
                val number = parts.getOrNull(0)?:""
                val name = parts.getOrNull(1)?:""
                selectedName = selected
                binding.edtTeamMember.setText(name)
                checkEnableSaveButton()
            }, selectedName).show(supportFragmentManager, "choose")
        }

        binding.ivArrowDown1.setOnClickListener {
            ChooseMemberDialog({ selected ->
                val parts = selected.split("|")
                val number = parts.getOrNull(0) ?: ""
                val name = parts.getOrNull(1) ?: ""
                selectedName1 = selected
                binding.edtTeamMember1.setText(name)
                checkEnableSaveButton()
            }, selectedName1).show(supportFragmentManager, "choose")
        }

        binding.ivArrowDown2.setOnClickListener {
            ChooseMemberDialog({ selected ->
                val parts = selected.split("|")
                val number = parts.getOrNull(0) ?: ""
                val name = parts.getOrNull(1) ?: ""
                selectedName2 = selected
                binding.edtTeamMember2.setText(name)
                checkEnableSaveButton()
            }, selectedName2).show(supportFragmentManager, "choose")
        }
        binding.btnSave.setOnClickListener {
            val intent = Intent()
            intent.putExtra("name1", selectedName ?: "")
            intent.putExtra("name2", selectedName1 ?: "")
            intent.putExtra("name3", selectedName2 ?: "")
            setResult(RESULT_OK, intent)
            finish()
        }



    }
    private fun checkEnableSaveButton() {
        val has1 = !binding.edtTeamMember.text.isNullOrEmpty()
        val has2 = !binding.edtTeamMember1.text.isNullOrEmpty()
        val has3 = !binding.edtTeamMember2.text.isNullOrEmpty()

        val allSelected = has1 && has2 && has3

        if (allSelected) {
            binding.btnSave.isEnabled = true
            binding.btnSave.setBackgroundColor(getColor(R.color.color_DE9DBA))
            binding.bottomLayout.setBackgroundColor(getColor(R.color.color_DE9DBA))
            window.navigationBarColor = getColor(R.color.color_DE9DBA)

        } else {
            binding.btnSave.isEnabled = false
            binding.btnSave.setBackgroundColor(getColor(R.color.color_F3F4F6))
            binding.bottomLayout.setBackgroundColor(getColor(R.color.color_F3F4F6))
            window.navigationBarColor = getColor(R.color.color_F3F4F6)

        }
    }

}
