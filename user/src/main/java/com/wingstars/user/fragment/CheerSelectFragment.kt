package com.wingstars.user.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wingstars.base.net.beans.CheerData
import com.wingstars.base.net.beans.ColorData
import com.wingstars.base.net.beans.PhrasesBean
import com.wingstars.user.R
import com.wingstars.user.adapter.PhrasesAdapter
import com.wingstars.user.databinding.FragmentCheerSelectBinding
import com.wingstars.user.viewmodel.CheerModeViewModel

class CheerSelectFragment : DialogFragment() {

    private var _binding: FragmentCheerSelectBinding? = null
    private val binding get() = _binding!!
    private var cheerData: CheerData? = null
    private var listener: OnClickItemListener? = null
    private var dismissListener: OnDialogDismissListener? = null
    private var isSelectingFontColor = true
    private lateinit var viewModel: CheerModeViewModel
    private var memberList: List<PhrasesBean> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Áp dụng Style trượt sát đáy giống NotificationDialog
        setStyle(STYLE_NO_TITLE, R.style.BottomDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheerSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[CheerModeViewModel::class.java]
        initObserver()

        initView()
        initEvent()
    }

    override fun onStart() {
        super.onStart()
        // Ép Window sát đáy và hiển thị animation trượt
        dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setGravity(Gravity.BOTTOM)
            setDimAmount(0.1f)
        }
    }

    private fun initView() {
        cheerData?.let { data ->
            binding.edtHurraysPhrases.setText(data.phrases)
            binding.edtTeamMember.setText(data.memberName)
            viewModel.getTeamMembersData(data.memberName)
            setupFontSizeRecycler(data.fontSizeStr)
            setupSpeedRecycler(data.playSpeedStr)
            updateColorSelectionUI()
            updateColorIndicator(binding.viewFontColorIndicator, data.fontData.selectColor)
            updateColorIndicator(binding.viewBgColorIndicator, data.backgroundData.selectColor)
        }
    }

    private fun initObserver() {
        viewModel.allMembersData.observe(viewLifecycleOwner) { list ->
            memberList = list
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    @android.annotation.SuppressLint("ClickableViewAccessibility")
    private fun initEvent() {
        binding.ivClose.setOnClickListener {
            dismiss()
        }
        binding.llFontColor.setOnClickListener {
            isSelectingFontColor = true
            updateColorSelectionUI()
        }

        binding.llBgColor.setOnClickListener {
            isSelectingFontColor = false
            updateColorSelectionUI()
        }

        binding.edtTeamMember.setOnClickListener {
            showMemberSelectDialog()
        }

        binding.llFontColor.setOnClickListener {
            isSelectingFontColor = true
            updateColorSelectionUI()
            showColorDialog(isFontColor = true)
        }

        binding.llBgColor.setOnClickListener {
            isSelectingFontColor = false
            updateColorSelectionUI()
            showColorDialog(isFontColor = false)
        }


        binding.edtHurraysPhrases.setOnTouchListener { v, event ->
            val DRAWABLE_END = 2
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                val drawable = binding.edtHurraysPhrases.compoundDrawablesRelative[DRAWABLE_END]
                if (drawable != null) {
                    if (event.x >= (binding.edtHurraysPhrases.width - binding.edtHurraysPhrases.totalPaddingRight)) {
                        binding.edtHurraysPhrases.text?.clear()
                        notifyChangeToActivity()
                        v.performClick()
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }

    }

    private fun showMemberSelectDialog() {
        if (memberList.isEmpty()) {
            viewModel.getTeamMembersData(binding.edtTeamMember.text.toString())
            return
        }
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_member_select, null)
        dialog.setContentView(dialogView)

        val rvMembers = dialogView.findViewById<RecyclerView>(R.id.rv_members)
        val ivCloseDialog = dialogView.findViewById<View>(R.id.iv_close_dialog)

        rvMembers.layoutManager = LinearLayoutManager(requireContext())
        val adapter = PhrasesAdapter(memberList, PhrasesAdapter.MODE_MEMBER) { selectedItem ->
            binding.edtTeamMember.setText(selectedItem.title)
            cheerData?.memberName = selectedItem.title
            notifyChangeToActivity()
            dialog.dismiss()
        }
        rvMembers.adapter = adapter

        ivCloseDialog.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateColorSelectionUI() {
        if (isSelectingFontColor) {
            binding.llFontColor.alpha = 1.0f
            binding.llBgColor.alpha = 0.5f
        } else {
            binding.llFontColor.alpha = 0.5f
            binding.llBgColor.alpha = 1.0f
        }
    }

    private fun setupFontSizeRecycler(currentSize: String) {
        val list = listOf(
            PhrasesBean(
                getString(R.string.cheer_font_size_small),
                "",
                currentSize == getString(R.string.cheer_font_size_small)
            ),
            PhrasesBean(
                getString(R.string.cheer_font_size_medium),
                "",
                currentSize == getString(R.string.cheer_font_size_medium)
            ),
            PhrasesBean(
                getString(R.string.cheer_font_size_large),
                "",
                currentSize == getString(R.string.cheer_font_size_large)
            )
        )

        val adapter = PhrasesAdapter(list) { _ ->
            notifyChangeToActivity()
        }
        binding.rvFontSize.adapter = adapter
    }

    private fun setupSpeedRecycler(currentSpeed: String) {
        val list = listOf(
            PhrasesBean("0.8X", "", currentSpeed == "0.8X"),
            PhrasesBean("1X", "", currentSpeed == "1X"),
            PhrasesBean("1.5X", "", currentSpeed == "1.5X")
        )
        val adapter = PhrasesAdapter(list) { _ ->
            notifyChangeToActivity()
        }
        binding.rvPlaySpeed.adapter = adapter
    }

    private fun notifyChangeToActivity() {
        val currentText = binding.edtHurraysPhrases.text.toString()
        val speedAdapter = binding.rvPlaySpeed.adapter as? PhrasesAdapter
        val selectedSpeedItem = speedAdapter?.getSelectedItem()
        val speed = selectedSpeedItem?.title ?: "1X"
        val fontAdapter = binding.rvFontSize.adapter as? PhrasesAdapter
        val selectedFontItem = fontAdapter?.getSelectedItem()
        val currentMemberName = binding.edtTeamMember.text.toString()
        val memberBean =
            if (currentMemberName.isNotEmpty()) PhrasesBean(currentMemberName, "") else null
        if (selectedFontItem != null) {
            cheerData?.fontSizeStr = selectedFontItem.title
        }
        var displayString = currentText
        if (currentMemberName.isNotEmpty()) {
            displayString = "$currentText $currentMemberName"
        }

        listener?.onClickItem(
            cheerStr = displayString,
            phrase = currentText,
            memberInfo = memberBean,
            playSpeed = speed
        )

        if (cheerData != null) {
            listener?.onSelectFinalColorChange(
                cheerData!!.fontData,
                cheerData!!.backgroundData
            )
        }
    }

    private fun showColorDialog(isFontColor: Boolean) {
        val title =
            if (isFontColor) getString(R.string.select_font_color) else getString(R.string.select_bg_color)
        val currentColor = if (isFontColor) {
            cheerData?.fontData?.selectColor ?: "#000000"
        } else {
            cheerData?.backgroundData?.selectColor ?: "#FFFFFF"
        }
        val dialog = SelectDialogFragment.newInstance(title, currentColor)

        dialog.setOnColorSelectedListener { colorHex ->
            if (isFontColor) {
                cheerData?.fontData?.selectColor = colorHex
                updateColorIndicator(binding.viewFontColorIndicator, colorHex)
            } else {
                cheerData?.backgroundData?.selectColor = colorHex
                updateColorIndicator(binding.viewBgColorIndicator, colorHex)
            }
            notifyChangeToActivity()
        }

        dialog.show(parentFragmentManager, "SelectDialogFragment")
    }

    private fun updateColorIndicator(view: View, colorHex: String) {
        try {
            val color = android.graphics.Color.parseColor(colorHex)
            val background = view.background as? android.graphics.drawable.GradientDrawable
            background?.setColor(color) ?: view.setBackgroundColor(color)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    interface OnClickItemListener {
        fun onClickItem(
            cheerStr: String,
            phrase: String,
            memberInfo: PhrasesBean?,
            playSpeed: String
        )

        fun onSelectFinalColorChange(fontData: ColorData, backgroundData: ColorData)
    }

    interface OnDialogDismissListener {
        fun onDialogDismissed()
    }

    fun setListener(listener: OnClickItemListener) {
        this.listener = listener
    }

    fun setDismissListener(listener: OnDialogDismissListener) {
        this.dismissListener = listener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.onDialogDismissed()
        notifyChangeToActivity()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(cheerData: CheerData): CheerSelectFragment {
            val fragment = CheerSelectFragment()
            fragment.cheerData = cheerData
            return fragment
        }
    }
}