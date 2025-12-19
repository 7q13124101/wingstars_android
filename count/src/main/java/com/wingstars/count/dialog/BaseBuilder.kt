package com.wingstars.count.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.wingstars.count.R

open class BaseBuilder  {

    public fun  setNavigationBar(viewGroup: ViewGroup,navigationBarHeight:Int,context: Context,
                                 color:Int = R.color.color_F5F5F5){
        if (navigationBarHeight!=0){
            var  view = View(context)
            view.setBackgroundColor(context.getColor(color))
            var params1 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,navigationBarHeight)
            view.layoutParams = params1
            viewGroup.addView(view)

        }
    }
}
