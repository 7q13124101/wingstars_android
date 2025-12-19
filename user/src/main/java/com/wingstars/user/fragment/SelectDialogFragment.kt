package com.wingstars.user.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wingstars.base.net.beans.PhrasesBean
import com.wingstars.user.R
import com.wingstars.user.adapter.PhrasesAdapter
import com.wingstars.user.databinding.DialogColorSelectBinding

class SelectDialogFragment : BottomSheetDialogFragment() {

    private var _binding: DialogColorSelectBinding? = null
    private val binding get() = _binding!!
    private var title: String = "字體顏色"
    private var initialColor: String = "#000000"
    private var onColorSelected: ((String) -> Unit)? = null
    private var adapterPalette: PhrasesAdapter? = null
    private var adapterBasic: PhrasesAdapter? = null
    private var paletteList = ArrayList<PhrasesBean>()
    private var basicList = ArrayList<PhrasesBean>()

    companion object {
        fun newInstance(title: String, initialColor: String): SelectDialogFragment {
            val fragment = SelectDialogFragment()
            fragment.title = title
            fragment.initialColor = initialColor
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        dialog?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogColorSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTitle.text = title
        binding.ivClose.setOnClickListener { dismiss() }
        binding.ivBack.setOnClickListener { dismiss() }
        initColorData()

        binding.rvPalette.layoutManager = GridLayoutManager(context, 12)
        adapterPalette = PhrasesAdapter(paletteList, PhrasesAdapter.MODE_COLOR_SQUARE) { item ->
            handleColorSelection(item.title)
        }
        binding.rvPalette.adapter = adapterPalette
        binding.rvBasicColors.layoutManager = GridLayoutManager(context, 6)
        adapterBasic = PhrasesAdapter(basicList, PhrasesAdapter.MODE_COLOR_CIRCLE) { item ->
            handleColorSelection(item.title)
        }
        binding.rvBasicColors.adapter = adapterBasic
        updatePreview(initialColor)
    }

    private fun handleColorSelection(selectedHex: String) {
        updatePreview(selectedHex)
        paletteList.forEach { it.isSelected = (it.title.equals(selectedHex, ignoreCase = true)) }
        basicList.forEach { it.isSelected = (it.title.equals(selectedHex, ignoreCase = true)) }
        adapterPalette?.notifyDataSetChanged()
        adapterBasic?.notifyDataSetChanged()

        onColorSelected?.invoke(selectedHex)
        dismiss()
    }

    private fun updatePreview(colorHex: String) {
        try {
            binding.viewPreviewColor.setBackgroundColor(Color.parseColor(colorHex))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initColorData() {
        val basics = listOf(
            "#000000", "#0060FD", "#31C759", "#FFCC01", "#FF3B2F", "#F4A4C1",
            "#FFFFFF", "#69FEFF", "#7FFF6F", "#D29D01", "#9629D4", "#E53B7A"
        )
        basicList.clear()
        basics.forEach { hex ->
            basicList.add(PhrasesBean(hex, "", hex.equals(initialColor, true)))
        }

        paletteList.clear()

        val hues = floatArrayOf(0f, 33f, 66f, 99f, 132f, 165f, 198f, 231f, 264f, 297f, 330f)

        for (row in 0 until 10) {
            val value = 1.0f - (row * 0.1f)
            for (col in 0 until 12) {
                val hsv: FloatArray
                if (col == 0) {
                    hsv = floatArrayOf(0f, 0f, value)
                } else {
                    val currentHue = hues[col - 1]
                    hsv = floatArrayOf(currentHue, 1.0f, value)
                }

                val colorInt = Color.HSVToColor(hsv)
                val hex = String.format("#%06X", (0xFFFFFF and colorInt))

                paletteList.add(PhrasesBean(hex, "", hex.equals(initialColor, true)))
            }
        }
    }

    fun setOnColorSelectedListener(listener: (String) -> Unit) {
        this.onColorSelected = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}