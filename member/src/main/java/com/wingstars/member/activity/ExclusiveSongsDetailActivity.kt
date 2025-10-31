package com.wingstars.member.activity

import android.os.Bundle
import com.wingstars.base.base.BaseActivity
import com.wingstars.member.databinding.ActivityExclusiveSongsDetailBinding


class ExclusiveSongsDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityExclusiveSongsDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExclusiveSongsDetailBinding.inflate(layoutInflater)
        setTitleFoot(binding.root)
        initView()
    }

    override fun initView() {
        binding.title.setBackClickListener { finish() }
    }
}