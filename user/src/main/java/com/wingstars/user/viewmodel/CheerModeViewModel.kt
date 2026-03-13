package com.wingstars.user.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wingstars.base.net.API
import com.wingstars.base.net.beans.PhrasesBean
import com.wingstars.base.net.beans.WSMemberResponse
import com.wingstars.user.R
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException

class CheerModeViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    var allPhrasesData = MutableLiveData<MutableList<PhrasesBean>>()
    var allMembersData = MutableLiveData<MutableList<PhrasesBean>>()
    var allFontSizesData = MutableLiveData<MutableList<PhrasesBean>>()
    var allPlaySpeedsData = MutableLiveData<MutableList<PhrasesBean>>()
    var isLoading = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String?>()

    var fontSizeIndex = MutableLiveData<Int>()

    fun getTeamMembersData(selMember: String, perPage: Int = 50, page: Int = 1) {
        isLoading.postValue(true)
        errorMessage.postValue(null)

        val api = API.shared?.api
        if (api == null) {
            isLoading.postValue(false)
            errorMessage.postValue("API尚未初始化.")
            allMembersData.postValue(mutableListOf())
            return
        }

        val disposable = api.wsMembers(perPage, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    isLoading.value = false
                    allMembersData.value = mapWsResponseToPhrases(response, selMember)
                },
                { error ->
                    isLoading.value = false
                    errorMessage.value = parseError(error)
                    allMembersData.value = mutableListOf()
                }
            )
        compositeDisposable.add(disposable)
    }

    @Suppress("UNUSED_PARAMETER")
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

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    private fun mapWsResponseToPhrases(
        list: List<WSMemberResponse>,
        selMember: String
    ): MutableList<PhrasesBean> {
        return list.mapNotNull { item ->
            val number = item.acf?.number ?: return@mapNotNull null
            val name = item.title?.rendered ?: return@mapNotNull null
            val iconUrl = item.yoast_head_json
                ?.og_image
                ?.firstOrNull()
                ?.url
            if (iconUrl.isNullOrEmpty()) {
                return@mapNotNull null
            }
            PhrasesBean(
                title = name,
                UniformNo = number,
                isSelected = name == selMember
            )
        }.toMutableList()
    }

    private fun parseError(error: Throwable): String {
        return if (error is HttpException) {
            "HTTP ${error.code()} – server error 伺服器錯誤"
        } else {
            error.message ?: "未知錯誤"
        }
    }
}