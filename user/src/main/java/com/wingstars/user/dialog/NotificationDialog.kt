package com.wingstars.user.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.wingstars.user.R

class NotificationDialog(
    private var initialState: Boolean = false,
    private val onToggleChanged: ((Boolean) -> Unit)? = null
) : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.BottomDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_notification, container, false)
    }

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

    override fun onStart() {
        super.onStart()
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

    fun show(fm: FragmentManager) {
        show(fm, "NotificationDialog")
    }
}
