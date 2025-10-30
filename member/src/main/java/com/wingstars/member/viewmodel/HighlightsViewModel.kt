package com.wingstars.member.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wingstars.member.adapter.HighlightsData

enum class HighlightsType {
    HT_WONDERFUL_VIDEOS,      //精彩影片
    HT_FLASH_SHORT_FILM,      //快閃短片
    HT_DAILY_VLOG             //日常Vlog
}

class HighlightsViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    var highlightsList = MutableLiveData<MutableList<HighlightsData>>()

    fun getHighlightsList(highlightsType: HighlightsType) {
        when (highlightsType) {
            HighlightsType.HT_WONDERFUL_VIDEOS -> {//精彩影片
                val arrayList = mutableListOf<HighlightsData>()
                var node = HighlightsData("Wing Stars - 恬魚生日會 活動花絮","2025.09.11",false)
                arrayList.add(node)
                node = HighlightsData("Wing Stars - \uD83C\uDF1F 夢幻聯動\uD83C\uDF1F 女孩們與街舞天團的國際交流","2025.09.08",false)
                arrayList.add(node)
                node = HighlightsData("Wing Stars - 小安 X Mingo Stars House 一日店長華麗登場!!!","2025.09.03",false)
                arrayList.add(node)
                node = HighlightsData("Wing Stars - 玩客瘋探班女孩拍攝!!","2025.08.20",false)
                arrayList.add(node)
                node = HighlightsData("Wing Stars - 一日鷹援軍！！","2025.08.08",false)
                arrayList.add(node)
                node = HighlightsData("\uD835\uDDEA\uD835\uDDF6\uD835\uDDFB\uD835\uDDF4 \uD835\uDDE6\uD835\uDE01\uD835\uDDEE\uD835\uDDFF\uD835\uDE00 - \uD83C\uDFAC 明星賽幕後全紀錄 ✨","2025.07.28",false)
                arrayList.add(node)
                node = HighlightsData("Wing Stars - ET生日會 活動花絮","2025.07.21",false)
                arrayList.add(node)
                node = HighlightsData("Ｗing Stars - 魷戲玩到底","2025.07.16",false)
                arrayList.add(node)
                node = HighlightsData("Wing Stars出任務- 天鷹球員加盟記者會主持","2025.07.08",false)
                arrayList.add(node)
                node = HighlightsData("Wing Stars - 雄鷹上半季親子主題日＆感謝花絮","2025.07.03",false)
                arrayList.add(node)
                node = HighlightsData("Wing Stars出任務 - 尼莫畢業典禮驚喜篇","2025.06.25",false)
                arrayList.add(node)
                highlightsList.postValue(arrayList)
            }

            HighlightsType.HT_FLASH_SHORT_FILM -> {//快閃短片
                val arrayList = mutableListOf<HighlightsData>()
                var node = HighlightsData("Ｗing Stars - 魷戲玩到底","2025.07.16",false)
                arrayList.add(node)
                node = HighlightsData("Wing Stars出任務- 天鷹球員加盟記者會主持","2025.07.08",false)
                arrayList.add(node)
                node = HighlightsData("Wing Stars - 雄鷹上半季親子主題日＆感謝花絮","2025.07.03",false)
                arrayList.add(node)
                node = HighlightsData("Wing Stars出任務 - 尼莫畢業典禮驚喜篇","2025.06.25",false)
                arrayList.add(node)
                highlightsList.postValue(arrayList)
            }

            HighlightsType.HT_DAILY_VLOG -> {//日常Vlog
                val arrayList = mutableListOf<HighlightsData>()
                var node = HighlightsData("Wing Stars - 玩客瘋探班女孩拍攝!!","2025.08.20",false)
                arrayList.add(node)
                node = HighlightsData("Wing Stars - 一日鷹援軍！！","2025.08.08",false)
                arrayList.add(node)
                node = HighlightsData("\uD835\uDDEA\uD835\uDDF6\uD835\uDDFB\uD835\uDDF4 \uD835\uDDE6\uD835\uDE01\uD835\uDDEE\uD835\uDDFF\uD835\uDE00 - \uD83C\uDFAC 明星賽幕後全紀錄 ✨","2025.07.28",false)
                arrayList.add(node)
                highlightsList.postValue(arrayList)
            }
        }
    }
}