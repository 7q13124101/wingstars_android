package com.wingstars.base.view


import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.wingstars.base.R
import com.wingstars.base.databinding.CustomTitleBarBinding



class TitleView(context: Context,  attrs: AttributeSet) : RelativeLayout(context,attrs) {
    private lateinit var binding: CustomTitleBarBinding
    init {
        initView(context,attrs);
    }

    @SuppressLint("CustomViewStyleable", "Recycle")
    private fun initView(context: Context, attrs: AttributeSet) {
        binding = CustomTitleBarBinding.inflate(LayoutInflater.from(context),this,true)
        val attr = context.obtainStyledAttributes(attrs, R.styleable.DoraTitleBar)
        val title = attr.getString(R.styleable.DoraTitleBar_title)
        val show_back = attr.getBoolean(R.styleable.DoraTitleBar_show_back,true)
        val icon_right = attr.getResourceId(R.styleable.DoraTitleBar_icon_right,0)
        val text_right = attr.getString(R.styleable.DoraTitleBar_text_right)
        if (icon_right!=0){
            binding.rightIcon.visibility = VISIBLE
            binding.rightIconImage.setImageResource(icon_right)
        }else{
            binding.rightIcon.visibility = GONE
        }
        if (!text_right.isNullOrEmpty()){
           binding.rightText.text = text_right
        }else{
            binding.rightText.visibility = GONE
        }
        if (!show_back){
            binding.imageBack.visibility = GONE
        }
        if (!title.isNullOrEmpty()){
            binding.text.text = "$title"
        }

    }

    fun setTitle(title: String){
        binding.text.text = "$title"
    }
    /**
     * 返回键的点击监听
     */
    fun setBackClickListener(listener: OnClickListener){
        binding.backs.setOnClickListener(listener)
    }
    fun setRightIconClickListener(listener: OnClickListener){
        binding.rightIcon.setOnClickListener(listener)
    }
}