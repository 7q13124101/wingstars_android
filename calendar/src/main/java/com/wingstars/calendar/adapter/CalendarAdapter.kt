package com.wingstars.calendar.adapter

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.wingstars.base.net.beans.WSCalendarResponse
import com.wingstars.calendar.R
import com.wingstars.calendar.databinding.ItemActivityCardBinding
import com.wingstars.calendar.databinding.ItemSportsCardBinding
import com.wingstars.calendar.utils.CalendarDateUtils.Companion.formatCalendarDate
import com.wingstars.calendar.viewmodel.CalendarViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random

class CalendarAdapter(
    private val context: Context,
    private var dataList: MutableList<WSCalendarResponse>?,
    private val onItemListener: onItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    // 缓存每个位置的随机图片资源ID
    private val randomDrawableCache = mutableMapOf<Int, Int>()

    init {
        if (dataList == null) {
            dataList = mutableListOf()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CalendarViewModel.CalendarCategory.GENERAL_ACTIVITY,
            CalendarViewModel.CalendarCategory.BIRTHDAY -> {
                val binding = ItemActivityCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                NormalActivityCardItemViewHolder(binding)
            }
            CalendarViewModel.CalendarCategory.SKY_EAGLE,
            CalendarViewModel.CalendarCategory.HUNT_EAGLE,
            CalendarViewModel.CalendarCategory.MALE_EAGLE -> {
                val binding = ItemSportsCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                NormalSportsCardItemViewHolder(binding)
            }
            else -> {
                val binding = ItemActivityCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                NormalActivityCardItemViewHolder(binding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = dataList?.getOrNull(position)
            ?: return CalendarViewModel.CalendarCategory.GENERAL_ACTIVITY
        return item.calendar_categoryF
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun updateData(newData: List<WSCalendarResponse>) {
        dataList?.clear() ?: mutableListOf<WSCalendarResponse>()
        dataList?.addAll(newData)

        // 清除旧的缓存
        randomDrawableCache.clear()

        // 为每个雄鹰项生成随机图片
        newData.forEachIndexed { index, item ->
            if (item.calendar_categoryF == CalendarViewModel.CalendarCategory.MALE_EAGLE) {
                randomDrawableCache[index] = getRandomTakooDrawableId(index)
            }
        }

        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = dataList?.getOrNull(position) ?: return

        when (holder) {
            is NormalActivityCardItemViewHolder -> holder.binding(data, position, onItemListener)
            is NormalSportsCardItemViewHolder -> holder.binding(data, position, onItemListener)
        }
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    fun getPureTextFromHtml(htmlStr: String?): String {
        if (TextUtils.isEmpty(htmlStr)) return ""
        val text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(htmlStr, Html.FROM_HTML_MODE_LEGACY).toString()
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(htmlStr).toString()
        }

        return text.trim()
            .replace("\\n+".toRegex(), "")
    }

    inner class NormalActivityCardItemViewHolder(private val binding: ItemActivityCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun binding(data: WSCalendarResponse, position: Int, listener: onItemClickListener) {
            binding.tvGeneralName.text = getPureTextFromHtml(data.titleF)
            binding.tvGeneralMap.text = getPureTextFromHtml(data.mapF)

            val stDate = data.st_dateF
            val edDate = data.ed_dateF
            binding.tvGeneralActivityTime.text = formatCalendarDate(stDate, edDate)

            if (data.calendar_categoryF.equals(CalendarViewModel.CalendarCategory.GENERAL_ACTIVITY)) {
                binding.ivGeneralType.setImageResource(R.drawable.calendar_ic_star)

                Glide.with(binding.ivGeneralPhoto.context).clear(binding.ivGeneralPhoto)
                if (data.urlF.isNotEmpty()) {
                    Glide.with(context)
                        .load(data.urlF)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(false)
                        .dontAnimate()
                        .into(binding.ivGeneralPhoto)
                } else {
                    Glide.with(context)
                        .load(R.drawable.calendar_ic_photo)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(false)
                        .dontAnimate()
                        .into(binding.ivGeneralPhoto)
                }
            } else if (data.calendar_categoryF.equals(CalendarViewModel.CalendarCategory.BIRTHDAY)) {
                binding.ivGeneralType.setImageResource(R.drawable.calendar_ic_grey_birthday)

                Glide.with(binding.ivGeneralPhoto).clear(binding.ivGeneralPhoto)
                if (data.urlF.isNotEmpty()) {
                    Glide.with(context)
                        .load(data.urlF)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(false)
                        .dontAnimate()
                        .into(binding.ivGeneralPhoto)
                } else {
                    Glide.with(context)
                        .load(R.drawable.calendar_ic_jc_birthday_01)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(false)
                        .dontAnimate()
                        .into(binding.ivGeneralPhoto)
                }
            }

            binding.clEventDetails.setOnClickListener {
                listener.onItemClick(data, position)
            }
            binding.executePendingBindings()
        }
    }

    fun extractTimeFromStr(timeStr: String): String {
        val originalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val targetFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        return try {
            val date = originalFormat.parse(timeStr)
            date?.let { targetFormat.format(it) } ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    inner class NormalSportsCardItemViewHolder(private val binding: ItemSportsCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun binding(data: WSCalendarResponse, position: Int, listener: onItemClickListener) {
            binding.tvTitle.text = getPureTextFromHtml(data.titleF)
            binding.tvContentRendered.text = getPureTextFromHtml(data.contentF)
            binding.tvMap.text = getPureTextFromHtml(data.mapF)

            val hourMinute = extractTimeFromStr(data.st_dateF)
            binding.tvAcfActivityTime.text = hourMinute

            if (data.calendar_categoryF == CalendarViewModel.CalendarCategory.MALE_EAGLE) {
                binding.ivBaseball.setImageResource(R.drawable.calendar_ic_baseball)
                binding.ivSportsType.setImageResource(R.drawable.calendar_ic_tg_xy)
                binding.tvSportsTypeName.setText(R.string.calendar_hawks)

                // 从缓存中获取随机图片资源ID
                val randomResId = randomDrawableCache[position]
                if (randomResId != null) {
                    binding.ivTakooType.setImageResource(randomResId)
                } else {
                    // 如果缓存中没有，生成一个新的（理论上不会发生）
                    val newResId = getRandomTakooDrawableId(position)
                    binding.ivTakooType.setImageResource(newResId)
                }

            } else if (data.calendar_categoryF == CalendarViewModel.CalendarCategory.SKY_EAGLE) {
                binding.ivBaseball.setImageResource(R.drawable.calendar_ic_volleyball)
                binding.ivSportsType.setImageResource(R.drawable.calendar_ic_tg_ty)
                binding.tvSportsTypeName.setText(R.string.calendar_sky_hawks)
                binding.ivTakooType.setImageResource(R.drawable.calendar_ic_sky)
            } else if (data.calendar_categoryF == CalendarViewModel.CalendarCategory.HUNT_EAGLE) {
                binding.ivBaseball.setImageResource(R.drawable.calendar_ic_basketball)
                binding.ivSportsType.setImageResource(R.drawable.calendar_ic_tg_ly)
                binding.tvSportsTypeName.setText(R.string.calendar_ghost_hawks)
                binding.ivTakooType.setImageResource(R.drawable.calendar_ic_da_yuan)
            }

            binding.executePendingBindings()
        }
    }

    private fun getRandomTakooDrawableId(position: Int): Int {
        val img1 = R.drawable.calendar_ic_takoo
        val img2 = R.drawable.calendar_ic_takamel

        // 可以基于位置生成不同的随机数，确保同一个位置总是返回相同的图片
        val random = Random(position * 31L + System.currentTimeMillis() / 1000)
        return if (random.nextBoolean()) img1 else img2
    }

    interface onItemClickListener {
        fun onItemClick(data: WSCalendarResponse, position: Int)
    }
}