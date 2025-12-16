package com.wingstars.member.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wingstars.base.net.beans.WSMemberResponse
import com.wingstars.member.R
import com.wingstars.member.adapter.BasicIntroductionFunBean


class BasicInformationViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    var introductionList = MutableLiveData<MutableList<BasicIntroductionFunBean>>()
    var hobbyLists = MutableLiveData<MutableList<String>>()

    fun getBasicIntroductionList(acfInfo: WSMemberResponse.Acf) {
        val itemList: MutableList<BasicIntroductionFunBean> = mutableListOf()
        var bean: BasicIntroductionFunBean
        if (acfInfo.height.isNotEmpty()) {
            bean = BasicIntroductionFunBean(
                "身高",
                R.drawable.ic_introduce_heigh,
                "${acfInfo.height} cm"
            )
            itemList.add(bean)
        }

        if (acfInfo.weight.isNotEmpty()) {
            bean = BasicIntroductionFunBean(
                "體重",
                R.drawable.ic_introduce_weight,
                "${acfInfo.weight} kg"
            )
            itemList.add(bean)
        }
        if (acfInfo.birthdate.isNotEmpty()) {
            bean = BasicIntroductionFunBean(
                "生日",
                R.drawable.ic_introduce_birthday,
                acfInfo.birthdate
            )
            itemList.add(bean)
        }
        if (acfInfo.sign.isNotEmpty()) {
            bean = BasicIntroductionFunBean(
                "星座",
                R.drawable.ic_introduce_constellation,
                acfInfo.sign
            )
            itemList.add(bean)
        }
        if (acfInfo.blood_type.isNotEmpty()) {
            bean = BasicIntroductionFunBean(
                "血型",
                R.drawable.ic_introduce_blood_type,
                acfInfo.blood_type
            )
            itemList.add(bean)
        }

        introductionList.postValue(itemList)
    }

    fun getHobbyLists(interest: String) {
        val itemList = interest.split(Regex("\\r\\n+")).toMutableList()
        /*val itemList = mutableListOf<String>(
            "黑色",
            "白色",
            "收藏",
            "睡覺",
            "運動",
            "吃東西",
            "吃",
            "好吃",
            "擅长吃喝",
            "尽情的吃喝",
            "享受的人生",
            "喝",
            "人生得意须尽欢",
            "玩",
            "独处是一道最美的风景线",
            "乐"
        )*/
        hobbyLists.postValue(itemList)
    }
}