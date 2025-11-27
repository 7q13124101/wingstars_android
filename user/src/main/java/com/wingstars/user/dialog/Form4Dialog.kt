package com.wingstars.user.dialog

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import com.wingstars.user.R

class Form4Dialog(
    private var initialState: Boolean = false,
    private val onToggleChanged: ((Boolean) -> Unit)? = null
) : BaseBottomDialog(R.layout.dialog_form4) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ImageView>(R.id.btn_close)?.setOnClickListener {
            dismiss()
        }
        val toggleOff = view.findViewById<ImageView>(R.id.toggles_off)
        val toggleOn = view.findViewById<ImageView>(R.id.toggles_on)
        if (initialState) {
            toggleOff.visibility = View.GONE
            toggleOn.visibility = View.VISIBLE
        } else {
            toggleOff.visibility = View.VISIBLE
            toggleOn.visibility = View.GONE
        }
        toggleOff?.setOnClickListener {
            toggleOff.visibility = View.GONE
            toggleOn.visibility = View.VISIBLE
            onToggleChanged?.invoke(true)
        }

        toggleOn?.setOnClickListener {
            toggleOn.visibility = View.GONE
            toggleOff.visibility = View.VISIBLE
            onToggleChanged?.invoke(false)
        }
    }
    fun show(fm: FragmentManager) {
        show(fm, "Form4Dialog")
    }
}