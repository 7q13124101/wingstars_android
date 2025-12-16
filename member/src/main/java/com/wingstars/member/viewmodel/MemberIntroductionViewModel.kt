package com.wingstars.member.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wingstars.base.net.API
import com.wingstars.base.net.beans.WSMemberResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class MemberIntroductionViewModel : ViewModel(){
    var memberIntroductionListData = MutableLiveData<MutableList<WSMemberResponse>?>()

    var loading = MutableLiveData<Boolean>()
    fun getMemberIntroductionListData() {
        loading.postValue(true)
        API.shared?.api?.let {
            val observer = it.wsMembers(100, 1)
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    loading.postValue(false)
                    //Log.e("getWsMembersData", "[wsMembers] next.data.size:  ")
                    //val xx: MutableList<WSMemberResponse>? =MutableList(10){next[0]}
                    memberIntroductionListData.postValue(next)
                },
                { error ->
                    loading.postValue(false)
                    //Log.e("getWsMembersData", "error=${error.message}")
                    var msg = error.message.toString()
                    /*if (error is HttpException) {
                        try {
                            val gson = Gson()
                            val type = object : TypeToken<CRMBaseFailResponse>() {}.type
                            val failResponse = gson.fromJson<CRMBaseFailResponse>(
                                error.response()?.errorBody()?.string(), type
                            )
                            if (failResponse != null) {
                                failResponse.message?.let {
                                    msg = it
                                }
                            }
                        } catch (e: Exception) {

                        }
                    }*/

                    msg.let { it1 ->
                        //Toast.makeText(BaseApplication.shared()!!, "$it1", Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
    }
}