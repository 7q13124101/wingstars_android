package com.wingstars.user.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wingstars.base.net.beans.PhrasesBean
import com.wingstars.user.R

class CheerModeViewModel : ViewModel() {

    var allPhrasesData = MutableLiveData<MutableList<PhrasesBean>>()
    var allMembersData = MutableLiveData<MutableList<PhrasesBean>>()
    var allFontSizesData = MutableLiveData<MutableList<PhrasesBean>>()
    var allPlaySpeedsData = MutableLiveData<MutableList<PhrasesBean>>()
    var isLoading = MutableLiveData<Boolean>()

    var fontSizeIndex = MutableLiveData<Int>()

    fun getTeamMembersData(selMember: String) {
        isLoading.postValue(true)

        val mockList = ArrayList<PhrasesBean>()
        mockList.add(PhrasesBean("安芝儇", "2"))
        mockList.add(PhrasesBean("Mingo", "90"))
        mockList.add(PhrasesBean("一粒", "22"))
        mockList.add(PhrasesBean("圈圈", "00"))
        mockList.add(PhrasesBean("恬魚", "5"))
        mockList.add(PhrasesBean("昆昆", "7"))
        mockList.add(PhrasesBean("李樂", "10"))
        mockList.add(PhrasesBean("JC", "16"))
        mockList.add(PhrasesBean("妡0", "19"))
        mockList.add(PhrasesBean("艾琳", "20"))
        mockList.add(PhrasesBean("瑈瑈", "23"))
        mockList.add(PhrasesBean("林浠", "33"))

        if (selMember.isNotEmpty()) {
            val find = mockList.find { it.title == selMember }
            find?.isSelected = true
        }

        allMembersData.postValue(mockList)
        isLoading.postValue(false)
    }

    fun getAllPhrasesData(context: Context, selPhrase: String) {
        val list = ArrayList<PhrasesBean>()
        list.add(PhrasesBean("WingStars！，閃耀每一場！", "", false))
//        list.add(PhrasesBean("I'm IN! I'm 鷹!", "", false))
//        list.add(PhrasesBean("全壘打 Home Run", "", false))
//        list.add(PhrasesBean("安打安打", "", false))

        val find = list.find { it.title == selPhrase }
        find?.isSelected = true

        allPhrasesData.postValue(list)
    }

    fun getAllFontSizesData(context: Context, selFontSize: String) {
        val list = ArrayList<PhrasesBean>()
        val smallText = context.getString(R.string.cheer_font_size_small)
        val mediumText = context.getString(R.string.cheer_font_size_medium)
        val largeText = context.getString(R.string.cheer_font_size_large)

        list.add(PhrasesBean(smallText, "", false))
        list.add(PhrasesBean(mediumText, "", false))
        list.add(PhrasesBean(largeText, "", false))
        var selectedIndex = 1
        val findIndex = list.indexOfFirst { it.title == selFontSize }
        if (findIndex != -1) {
            list[findIndex].isSelected = true
            selectedIndex = findIndex
        } else {
            if (list.size > 1) list[1].isSelected = true
        }
        allFontSizesData.postValue(list)
        fontSizeIndex.postValue(selectedIndex)
    }

    fun getAllPlaySpeedsData(selPlaySpeed: String) {
        val list = ArrayList<PhrasesBean>()

        list.add(PhrasesBean("0.8X", "", false))
        list.add(PhrasesBean("1X", "", false))
        list.add(PhrasesBean("1.5X", "", false))

        val find = list.find { it.title == selPlaySpeed }
        if (find == null && list.size > 1) {
            list[1].isSelected = true
        } else {
            find?.isSelected = true
        }

        allPlaySpeedsData.postValue(list)
    }
}