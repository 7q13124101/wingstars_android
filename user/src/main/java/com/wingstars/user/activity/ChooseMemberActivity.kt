package com.wingstars.user.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
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
import com.wingstars.user.utils.MemberStorage
import com.wingstars.user.viewmodel.CheerLeaderViewModel
import kotlin.getValue

class ChooseMemberActivity : BaseActivity() {

    private lateinit var binding: ActivityChooseMemberBinding
    private val viewModel: CheerLeaderViewModel by viewModels()
    private var selectedNames = arrayOf<String?>(null, null, null)
    private var currentMemberList: List<MemberUI> = emptyList()
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true
        initView()
        restoreSelectedMember()
        observeViewModel()
        viewModel.fetchCheerLeaderList()
    }
    private fun restoreSelectedMember() {
        val names = MemberStorage.getSelectedMembers()
        selectedNames[0] = names[0]
        selectedNames[1] = names[1]
        selectedNames[2] = names[2]

        binding.edtTeamMember.setText(selectedNames[0] ?: "")
        binding.edtTeamMember1.setText(selectedNames[1] ?: "")
        binding.edtTeamMember2.setText(selectedNames[2] ?: "")

        selectedNames.forEachIndexed { index, name ->
            if (!name.isNullOrBlank()) {
                updateInputStyle(index)
            }
        }
        checkEnableSaveButton()
    }

    private fun observeViewModel() {
        viewModel.memberListUI.observe(this) { members ->
            currentMemberList = members ?: emptyList()
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
        val inputViews = arrayOf(
            binding.edtTeamMember to binding.ivArrowDown,
            binding.edtTeamMember1 to binding.ivArrowDown1,
            binding.edtTeamMember2 to binding.ivArrowDown2
        )

        inputViews.forEachIndexed { index, pair ->
            val (editText, arrow) = pair
            val clickListener = View.OnClickListener { openMemberDialog(index) }
            editText.setOnClickListener(clickListener)
            arrow.setOnClickListener(clickListener)
        }
        binding.btnSave.setOnClickListener {
            val member1 = selectedNames[0] ?: ""
            val member2 = selectedNames[1] ?: ""
            val member3 = selectedNames[2] ?: ""

            MemberStorage.saveSelectedMembers(member1, member2, member3)

            val intent = Intent().apply {
                putExtra("name1", selectedNames[0] ?: "")
                putExtra("name2", selectedNames[1] ?: "")
                putExtra("name3", selectedNames[2] ?: "")
            }
            setResult(RESULT_OK, intent)
            finish()
        }
    }
    private fun openMemberDialog(index: Int) {
        ChooseMemberDialog(currentMemberList, { selected ->
            val isAlreadySelectedElsewhere = selectedNames.indices
                .any { i -> i != index && selectedNames[i] == selected }

            if (isAlreadySelectedElsewhere) {
                Toast.makeText(this, "請選擇其他成員。", Toast.LENGTH_SHORT).show()
                return@ChooseMemberDialog
            }
            selectedNames[index] = selected
            val parts = selected.split("|")
            val displayText = "${parts.getOrNull(0) ?: ""} ${parts.getOrNull(1) ?: ""}"

            when(index) {
                0 -> binding.edtTeamMember.setText(displayText)
                1 -> binding.edtTeamMember1.setText(displayText)
                2 -> binding.edtTeamMember2.setText(displayText)
            }

            updateInputStyle(index)
            checkEnableSaveButton()
        }, selectedNames[index]).show(supportFragmentManager, "choose")
    }
    private fun updateInputStyle(index: Int) {
        val color = getColor(R.color.color_4A5565)
        when(index) {
            0 -> binding.edtTeamMember.setTextColor(color)
            1 -> binding.edtTeamMember1.setTextColor(color)
            2 -> binding.edtTeamMember2.setTextColor(color)
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