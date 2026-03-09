package com.wingstars.user.dialog

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.user.R
import com.wingstars.user.adapter.MemberAdapter
import com.wingstars.user.adapter.MemberInfo
import com.wingstars.user.adapter.MemberUI
class ChooseMemberDialog(
    private val memberListFromApi: List<MemberUI>,
    private val onMemberSelected: (String) -> Unit,
    private val selectedIds: Set<String> = emptySet(),
) : BaseBottomDialog(R.layout.dialog_choose_member) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ImageView>(R.id.btn_close).setOnClickListener { dismiss() }
        val rv = view.findViewById<RecyclerView>(R.id.rv_member)
        rv.layoutManager = LinearLayoutManager(context)
        val members = memberListFromApi.map{memberUI ->
            MemberInfo(
                number = memberUI.memberId.trim(),
                name = memberUI.memberName
            )
        }
        rv.adapter = MemberAdapter(
            members,
            selectedIds,
        ) { selected ->
            onMemberSelected("${selected.number}|${selected.name}")
            dismiss()
        }
    }
}
