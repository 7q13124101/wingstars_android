package com.wingstars.home.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wingstars.home.R
import java.io.Serializable

//import com.wingstars.home.adapter.NewsData

data class NewsData(
    val title: String,
    val date: String,
    val imageUrl: Int // Tạm thời dùng Int (Resource ID) để test ảnh local
) : Serializable
class LatestNewsViewModel : ViewModel() {

    val newsList = MutableLiveData<List<NewsData>>()

    fun getLatestNews() {
        val list = mutableListOf<NewsData>()

        // Dữ liệu giả lập theo ảnh thiết kế
        list.add(NewsData("情感滿載！球迷美食應援大力支持台鋼雄鷹及Wing Stars", "2025.10.06", R.drawable.placeholder_image))
        list.add(NewsData("Stars House 出貨&店休公告", "2025.09.23", R.drawable.placeholder_image))
        list.add(NewsData("千千生日會-千堡們戰隊集合", "2025.09.22", R.drawable.placeholder_image))
        list.add(NewsData("JC生日會「Just Connect」維C能量補給站", "2025.09.22", R.drawable.placeholder_image))
        list.add(NewsData("Stars House 快閃預告", "2025.09.19", R.drawable.placeholder_image))
        list.add(NewsData("9/6 生日會&一日店長停車公告", "2025.09.04", R.drawable.placeholder_image))
        list.add(NewsData("台鋼 Wing Stars x 世界冠軍舞團 The Royal Family！", "2025.08.29", R.drawable.placeholder_image))
        list.add(NewsData("雄鷹實習應援團長出動!!!", "2025.08.02", R.drawable.placeholder_image))
        list.add(NewsData("Wing Stars 7月生日會圓滿落幕", "2025.08.01", R.drawable.placeholder_image))

        newsList.postValue(list)
    }
}