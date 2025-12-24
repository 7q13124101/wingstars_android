package com.wingstars.user.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingstars.base.base.BaseActivity
import com.wingstars.user.R
import com.wingstars.user.adapter.MemberUI
import com.wingstars.user.adapter.MemberUIAdapter
import com.wingstars.user.databinding.ActivityChooseMemberBinding
import com.wingstars.user.dialog.ChooseMemberDialog
import com.wingstars.user.viewmodel.CheerLeaderViewModel
import kotlin.getValue

class ChooseMemberActivity : BaseActivity() {

    private lateinit var binding: ActivityChooseMemberBinding
    private val viewModel: CheerLeaderViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true
        initView()
        observeViewModel()
        viewModel.fetchCheerLeaderList()
    }
    private fun observeViewModel() {
        viewModel.memberListUI.observe(this) { members ->
            if (members.isNullOrEmpty()) {
                binding.rvMember.adapter = MemberUIAdapter(emptyList(),this)
                binding.rvMemberSecond.adapter = MemberUIAdapter(emptyList(), this)
                return@observe
            }

            val half = members.size / 2
            binding.rvMember.adapter =
                MemberUIAdapter(members.take(half), this)

            binding.rvMemberSecond.adapter =
                MemberUIAdapter(members.drop(half), this)
        }
        viewModel.errorMessage.observe(this) { msg ->
            msg?.takeIf { it.isNotBlank() }?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
        viewModel.isLoading.observe(this) {
        }
    }
    override fun initView() {
        binding.ivBack.setOnClickListener { finish() }
        binding.rvMember.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMemberSecond.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        var currentMemberList: List<MemberUI> = emptyList()
        viewModel.memberListUI.observe(this){
                members -> currentMemberList = members?: emptyList()
        }
        var selectedName: String? = null
        var selectedName1: String? = null
        var selectedName2: String? = null
        binding.rlTeamMember.setOnClickListener {
            if (isMemberAlreadySelected(selectedName, selectedName1, selectedName2)) {
                Toast.makeText(this, "請選擇其他成員。", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            ChooseMemberDialog(currentMemberList, { selected ->
                if (selected == selectedName1 || selected == selectedName2) {
                    Toast.makeText(this, "請選擇其他成員。", Toast.LENGTH_SHORT).show()
                    return@ChooseMemberDialog
                }
                val parts = selected.split("|")
                val number = parts.getOrNull(0) ?: ""
                val name = parts.getOrNull(1) ?: ""
                selectedName = selected
                binding.edtTeamMember.setText("$number $name")
                binding.edtTeamMember.setTextColor(getColor(R.color.color_4A5565))
                checkEnableSaveButton()
            }, selectedName).show(supportFragmentManager, "choose")
        }
        binding.ivArrowDown1.setOnClickListener {
            if (isMemberAlreadySelected(selectedName1, selectedName, selectedName2)) {
                Toast.makeText(this, "請選擇其他成員。", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            ChooseMemberDialog(currentMemberList, { selected ->
                if (selected == selectedName || selected == selectedName2) {
                    Toast.makeText(this, "請選擇其他成員。", Toast.LENGTH_SHORT).show()
                    return@ChooseMemberDialog
                }
                val parts = selected.split("|")
                val number = parts.getOrNull(0) ?: ""
                val name = parts.getOrNull(1) ?: ""
                selectedName1 = selected
                binding.edtTeamMember1.setText("$number $name")
                binding.edtTeamMember1.setTextColor(getColor(R.color.color_4A5565))
                checkEnableSaveButton()
            }, selectedName1).show(supportFragmentManager, "choose")
        }
        binding.ivArrowDown2.setOnClickListener {
            if (isMemberAlreadySelected(selectedName2, selectedName, selectedName1)) {
                Toast.makeText(this, "請選擇其他成員。", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            ChooseMemberDialog(currentMemberList, { selected ->
                if (selected == selectedName || selected == selectedName1) {
                    Toast.makeText(this, "請選擇其他成員。", Toast.LENGTH_SHORT).show()
                    return@ChooseMemberDialog
                }
                val parts = selected.split("|")
                val number = parts.getOrNull(0) ?: ""
                val name = parts.getOrNull(1) ?: ""
                selectedName2 = selected
                binding.edtTeamMember2.setText("$number $name")
                binding.edtTeamMember2.setTextColor(getColor(R.color.color_4A5565))
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
    private fun isMemberAlreadySelected(
        currentSelected: String?,
        vararg others: String?
    ): Boolean {
        return currentSelected != null && others.any { it == currentSelected }
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
            binding.btnSave.setTextColor(getColor(R.color.white))
            window.navigationBarColor = getColor(R.color.color_DE9DBA)
        } else {
            binding.btnSave.isEnabled = false
            binding.btnSave.setBackgroundColor(getColor(R.color.color_F3F4F6))
            binding.bottomLayout.setBackgroundColor(getColor(R.color.color_F3F4F6))
            window.navigationBarColor = getColor(R.color.color_F3F4F6)
        }
    }

}