package com.wingstars.user.dialog

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.wingstars.user.R
import com.wingstars.user.adapter.MemberAdapter
import com.wingstars.user.adapter.MemberInfo
import com.wingstars.user.adapter.MemberUI
class ChooseMemberDialog(
    private val memberListFromApi: List<MemberUI>,
    private val onMemberSelected: (String) -> Unit,
    private val selectedName: String? = null,
) : BaseBottomDialog(R.layout.dialog_choose_member) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ImageView>(R.id.btn_close).setOnClickListener { dismiss() }
        val rv = view.findViewById<RecyclerView>(R.id.rv_member)
        rv.layoutManager = LinearLayoutManager(context)
        val members = memberListFromApi.map{memberUI ->
            MemberInfo(
                number = memberUI.memberId,
                name = memberUI.memberName
            )
        }
        rv.adapter = MemberAdapter(
            members,
            selectedName?.split("|")?.getOrNull(0),
        ) { selected ->
            onMemberSelected("${selected.number}|${selected.name}")
            dismiss()
        }
        view.post {
            val bottomSheet = dialog?.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            ) ?: return@post
            val behavior = BottomSheetBehavior.from(bottomSheet)
            val headerView = requireActivity().findViewById<View>(R.id.rl_top)
            val location = IntArray(2)
            headerView.getLocationOnScreen(location)
            val headerBottom = location[1] + headerView.height
            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
            val desiredHeight = screenHeight - headerBottom
            bottomSheet.layoutParams.height = desiredHeight
            bottomSheet.requestLayout()
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            behavior.isDraggable = true
        }


    }
}
