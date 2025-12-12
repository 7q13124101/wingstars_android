package com.wingstars.calendar.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.wingstars.base.net.beans.WSCalendarResponse
import com.wingstars.base.net.beans.WSMemberResponse
import com.wingstars.calendar.R
import com.wingstars.calendar.databinding.ItemActivityCardBinding
import com.wingstars.calendar.databinding.ItemBirthdayCardBinding
import com.wingstars.calendar.databinding.ItemSportsCardBinding
import com.wingstars.calendar.utils.CalendarDateUtils.Companion.formatCalendarDate
import com.wingstars.calendar.viewmodel.CalendarViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.max
import kotlin.random.Random

class CalendarMemberAdapter(
    private val context: Context,
    private var dataList: MutableList<WSMemberResponse>?
) : RecyclerView.Adapter<CalendarMemberAdapter.NormalItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalItemViewHolder {
        val binding =
            ItemBirthdayCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.lifecycleOwner = parent.context as LifecycleOwner
        return NormalItemViewHolder(binding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // -------------------------------------------
    override fun onBindViewHolder(holder: NormalItemViewHolder, position: Int) {
        holder.binding(position)
    }

    // -------------------------------------------
    override fun getItemCount(): Int {
        return if (dataList != null) dataList!!.size else 0
    }

    fun setList(list: List<WSMemberResponse>?) {
        dataList = if (dataList == null) {
            ArrayList()
        } else {
            dataList == null
            ArrayList()
        }
        dataList!!.addAll(list!!)
        notifyDataSetChanged()
    }

    fun updateData(newData: List<WSMemberResponse>) {
        dataList!!.clear()
        dataList!!.addAll(newData)
        notifyDataSetChanged()
    }

    fun getData(): MutableList<WSMemberResponse>? {
        if (dataList == null) {
            return null
        }
        return dataList
    }

    inner class NormalItemViewHolder(private val binding: ItemBirthdayCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun binding(position: Int) {
            val data = dataList!![position]
            binding.tvTitleRendered.text = data.titleF
            binding.tvNumber.text =data.acf.number

            Glide.with(binding.ivMemberType).clear(binding.ivMemberType)
            if (data.urlF.isNotEmpty()) {
                Glide.with(context)
                    .load(data.urlF)
//                    .error(R.drawable.ic_team_members_null)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .dontAnimate()
                    .into(binding.ivMemberType)
            } else {
                Glide.with(context)
                    .load(R.drawable.calendar_ic_jc_16)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .dontAnimate()
                    .into(binding.ivMemberType)
            }
        }
        fun onBind(position: Int) {
        }
    }
}