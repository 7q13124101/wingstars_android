package com.wingstars.user.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wingstars.user.net.BaseApplication
import com.wingstars.user.R
import com.wingstars.user.net.API
import com.wingstars.user.net.beans.FrequentlyQuestionsResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class FrequentlyAskedQuestionsViewModel : ViewModel() {
    var questionTaskData = MutableLiveData<MutableList<FrequentlyQuestionsResponse.Data.GroupDto>>()
    var questionRegisterData =
        MutableLiveData<MutableList<FrequentlyQuestionsResponse.Data.GroupDto>>()
    var questionDownloadData =
        MutableLiveData<MutableList<FrequentlyQuestionsResponse.Data.GroupDto>>()

    var isLoading = MutableLiveData<Boolean>()
    fun setIsLoading(isLoading: Boolean) {
        this.isLoading.postValue(isLoading)
    }
    private fun String.normalizeHawks(): String =
        this.replace("雄鷹", "啦啦隊").replace("雄鹰", "啦啦隊")

    fun getFrequentlyAskedQuestionsData() {
        val filterRules: Map<String, (String) -> Boolean> = mapOf(
            "點數任務" to { titleNum -> titleNum !in listOf("Q7", "Q8") },
            "登入帳號" to { topTitle -> topTitle !in listOf("Q4") }
        )
        API.Companion.shared?.api?.let {
            val observer = it.nsQuestions("${BaseApplication.Companion.HOST_HAWKS_CDN}/api/v1/app/questions")
            observer?.subscribeOn(Schedulers.io())
                ?.unsubscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ next ->
                    if (next.code == 2000 && next.data.isNotEmpty()) {
                        for (i in next.data) {
//                            Log.d("question",i.toString())
                            val groups = i.outData.map { g ->
                                val rule = filterRules[g.topTitle]
                                g.copy(
                                    topTitle = g.topTitle.normalizeHawks(),
                                    insideData = g.insideData
                                        .filter { item -> rule?.invoke(item.titleNum) ?: true }
                                        .mapIndexed {index, item ->
                                            item.copy(
                                                titleNum = "Q${index + 1}",
                                                title = item.title.normalizeHawks(),
                                                content = item.content.map { it.normalizeHawks()
                                                }
                                            )
                                        }
                                )
                            }.toMutableList()

                            when (i.partName) {
                                BaseApplication.Companion.shared()!!.getString(R.string.user_points_task) ->
                                    questionTaskData.postValue(groups)
                                BaseApplication.Companion.shared()!!.getString(R.string.user_register_login) ->
                                    questionRegisterData.postValue(groups)
                                BaseApplication.Companion.shared()!!.getString(R.string.user_download_and_install) ->
                                    questionDownloadData.postValue(groups)
                            }
                        }
                    }
                },
                    { error ->
                        //Toast.makeText(BaseApplication.shared()!!, error.message, Toast.LENGTH_LONG).show()
                    }
                )
        }
    }
}