package com.wingstars.member.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.wingstars.base.net.API
import com.wingstars.member.bean.WSRankBean
import com.wingstars.member.bean.WSRankBean.ACFBean
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlin.collections.mutableListOf

class PopularityRankingViewModel: ViewModel() {
    var rankinglist = MutableLiveData<MutableList<Int>>()
    var loading = MutableLiveData<Boolean>()
    var tip = MutableLiveData<String>()

    var wsRankData = MutableLiveData<MutableList<WSRankBean>>()



    public fun getRenderedList(){
        loading.postValue(true)
        API.shared?.api?.let {
            val observer = it.wsRank()
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    if (!next.isNullOrEmpty()){
                        var data = mutableListOf<WSRankBean>()
                        next.forEach {
                            var title = it.title
                            val content = it.content
                            val acf1 = it.acf
                            var acf = mutableListOf<ACFBean>()
                            for (i in 1..10){
                                val rankBean = acf1.rankBean(i)
                                if (rankBean!=null){
                                    acf.add(ACFBean(name = rankBean.name, volume = rankBean.volume))
                                }
                            }
                            var wsRankBean = WSRankBean(title=if (title!=null)title.rendered else "",
                                content=if (content!=null) content.rendered else "",acf = acf)
                            data.add(wsRankBean)
                        }
                        wsPhotos(data)


                    }
                },
                { error ->
                    loading.postValue(false)
                    tip.postValue(error.message)
                    Log.e("getRenderedList","error=${error.message}")
                }
            )
        }
    }
    private fun  wsPhotos(data: MutableList<WSRankBean>){
        API.shared?.api?.let {
            val observer = it.wsPhotos(100,1)
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    loading.postValue(false)
                    if (!next.isNullOrEmpty()){
                        data.forEach {
                            val acf = it.acf
                            if (!acf.isNullOrEmpty()){
                                acf.forEach { list->
                                    val imageList = next.filter { it.title.rendered.trim()== list.name}
                                    if (!imageList.isNullOrEmpty()){
                                        var yoast_head_json = imageList[0].yoast_head_json
                                        if (yoast_head_json!=null){
                                            val ogImage = yoast_head_json.og_image
                                            if (!ogImage.isNullOrEmpty()){
                                                list.image = ogImage[0].url
                                            }
                                        }

                                    }
                                }
                            }
                        }
                        wsRankData.postValue(data)

                    }
                },
                { error ->
                    loading.postValue(false)
                    tip.postValue(error.message)
                }
            )
        }
    }
}