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
import com.wingstars.base.net.API
import com.wingstars.base.net.NetBase
import com.wingstars.base.net.beans.CRMExtraData
import com.wingstars.base.net.beans.CRMUpdateContactRequest
import com.wingstars.base.utils.MMKVManagement
import com.wingstars.base.view.UpLoadingDialog
import com.wingstars.user.R
import com.wingstars.user.adapter.MemberUI
import com.wingstars.user.adapter.MemberUIAdapter
import com.wingstars.user.databinding.ActivityChooseMemberBinding
import com.wingstars.user.dialog.ChooseMemberDialog
import com.wingstars.user.utils.MemberStorage
import com.wingstars.user.viewmodel.CheerLeaderViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
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
        selectedNames[0] = names[0].takeIf { it.isNotBlank() }
        selectedNames[1] = names[1].takeIf { it.isNotBlank() }
        selectedNames[2] = names[2].takeIf { it.isNotBlank() }
        updateAllInputFields()
        checkEnableSaveButton()
    }

    private fun updateAllInputFields() {
        val inputs = arrayOf(
            binding.edtTeamMember,
            binding.edtTeamMember1,
            binding.edtTeamMember2
        )
        for (i in 0..2) {
            val value = selectedNames[i]
            if (value.isNullOrBlank()) {
                inputs[i].setText("")
                resetInputStyle(i)
            } else {
                val parts = value.split("|")
                inputs[i].setText("${parts.getOrNull(0) ?: ""} ${parts.getOrNull(1) ?: ""}")
                updateInputStyle(i)
            }
        }
    }

    private fun buildSelectedMap(): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        selectedNames.forEachIndexed { index, value ->
            val memberId = value?.split("|")?.getOrNull(0)
            if (!memberId.isNullOrBlank()) {
                map[memberId] = index + 1
            }
        }
        return map
    }

    private fun refreshAdapters() {
        if (currentMemberList.isEmpty()) return
        val selectedMap = buildSelectedMap()
        val half = currentMemberList.size / 2
        binding.rvMember.adapter = MemberUIAdapter(
            currentMemberList.take(half),
            selectedMap,
            ::onMemberImageClicked
        )
        binding.rvMemberSecond.adapter = MemberUIAdapter(
            currentMemberList.drop(half),
            selectedMap,
            ::onMemberImageClicked
        )
    }

    private fun onMemberImageClicked(member: MemberUI) {
        val key = "${member.memberId}|${member.memberName}"
        val existingIndex = selectedNames.indexOfFirst { it == key }
        if (existingIndex != -1) {
            // Deselect and compact: shift remaining entries left
            selectedNames[existingIndex] = null
            val compacted: MutableList<String?> = selectedNames.filterNotNull().toMutableList()
            repeat(3 - compacted.size) { compacted.add(null) }
            selectedNames[0] = compacted[0]
            selectedNames[1] = compacted[1]
            selectedNames[2] = compacted[2]
        } else {
            val emptyIndex = selectedNames.indexOfFirst { it.isNullOrEmpty() }
            if (emptyIndex == -1) {
                Toast.makeText(this, "已選擇3位成員，請先移除一位", Toast.LENGTH_SHORT).show()
                return
            }
            selectedNames[emptyIndex] = key
        }
        updateAllInputFields()
        checkEnableSaveButton()
        refreshAdapters()
    }

    private fun observeViewModel() {
        viewModel.memberListUI.observe(this) { members ->
            currentMemberList = members ?: emptyList()
            if (members.isNullOrEmpty()) {
                binding.rvMember.adapter = MemberUIAdapter(emptyList())
                binding.rvMemberSecond.adapter = MemberUIAdapter(emptyList())
                return@observe
            }
            refreshAdapters()
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
            updateFavoriteMembers()
        }
    }
    private fun updateFavoriteMembers() {
        val memberId = MMKVManagement.getCrmMemberId()
        if (memberId.isEmpty() || memberId == "0") {
            Toast.makeText(this, "無法取得會員ID", Toast.LENGTH_SHORT).show()
            return
        }

        val favoriteIds = selectedNames
            .mapNotNull { it?.takeIf { name -> name.isNotBlank() } }

        val loadingDialog = UpLoadingDialog.Builder(this).createDialog(this)
        loadingDialog.show()

        val request = CRMUpdateContactRequest(
            extraData = CRMExtraData(favorite_players = favoriteIds)
        )
        val url = "${NetBase.HOST_CRM}/api/v1/basic/member/$memberId/contact"

        API.shared?.api?.let { apiService ->
            apiService.crmUpdateMemberContact(url, request)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        loadingDialog.dismiss()
                        if (response.success) {
                            val member1 = selectedNames[0] ?: ""
                            val member2 = selectedNames[1] ?: ""
                            val member3 = selectedNames[2] ?: ""
                            MemberStorage.saveSelectedMembers(member1, member2, member3)
                            val intent = Intent().apply {
                                putExtra("name1", member1)
                                putExtra("name2", member2)
                                putExtra("name3", member3)
                            }
                            setResult(RESULT_OK, intent)
                            finish()
                        } else {
                            Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                        }
                    },
                    { error ->
                        loadingDialog.dismiss()
                        Toast.makeText(this, "網路錯誤: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                )
        } ?: run {
            loadingDialog.dismiss()
            Toast.makeText(this, "API 未初始化", Toast.LENGTH_SHORT).show()
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
            refreshAdapters()
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

    private fun resetInputStyle(index: Int) {
        val color = getColor(R.color.color_99A1AF)
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