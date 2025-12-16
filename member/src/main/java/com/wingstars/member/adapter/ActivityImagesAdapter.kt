package com.wingstars.member.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.youth.banner.adapter.BannerAdapter

class ActivityImagesAdapter(images: MutableList<String>, var contexts: Context) :
    BannerAdapter<String, ActivityImagesAdapter.ImageHolder>(images) {


    class ImageHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view as ImageView
    }

    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): ImageHolder {
        val imageView = ImageView(parent!!.context)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        imageView.layoutParams = params
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        return ImageHolder(imageView)
    }

    override fun onBindView(holder: ImageHolder?, data: String?, position: Int, size: Int) {
        if (data != null) {
          //  holder?.imageView?.setImageResource("$data")
            Glide.with(contexts).load("${data}").into(holder?.imageView!!)
        }
    }
}