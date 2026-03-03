package com.wingstars.count.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tencent.mmkv.MMKV
import com.wingstars.base.net.API
import com.wingstars.base.net.beans.CRMJournalHistoryResponse
import com.wingstars.count.Repository.CountHistoryRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow

class CountHistoryViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    var isLoading = MutableLiveData<Boolean>()
    var countWinStar = MutableLiveData<String>()
    fun setIsLoading(isLoading: Boolean) {
        this.isLoading.postValue(isLoading)
    }

    fun getCountHistoryList(bObtained: Boolean): Flow<PagingData<CRMJournalHistoryResponse.Journal>> {
        return CountHistoryRepository.getCountHistoryData(bObtained)
            .cachedIn(viewModelScope)
    }

    fun setWinStarCount(count: String?) {
        if (count == "-1" || count == null) {
            getMemberPointFromDetailsData()
        } else {
            countWinStar.postValue(count!!)
        }
    }

    fun getMemberPointFromDetailsData(showLoading: Boolean = true) {
        if (MMKV.defaultMMKV().decodeBool("isLogin")) {
            if (showLoading) {
                setIsLoading(true)
            }

            API.shared?.api?.let { api ->
                val id = MMKV.defaultMMKV().decodeString("crm_member_id")
                if (id.isNullOrEmpty()) {
                    if (showLoading) setIsLoading(false)
                    countWinStar.postValue("0")
                    return@let
                }

                val observer = api.crmMemberDetail(id)

                val disposable = observer?.subscribeOn(Schedulers.io())
                    ?.unsubscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(
                        { next ->
                            if (showLoading) setIsLoading(false)

                            if (next.success && next.data != null) {
                                val p = next.data.Points?.toString() ?: "0"
                                countWinStar.postValue(p)
                            } else {
                                // countHawks.postValue("0")
                            }
                        },
                        { error ->
                            if (showLoading) setIsLoading(false)
                            //Log.e("CountHistoryViewModel", "Error: ${error.message}")
                            // countHawks.postValue("0")
                        }
                    )

                if (disposable != null) {
                    compositeDisposable.add(disposable)
                }
            }
        } else {
            countWinStar.postValue("0")
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}