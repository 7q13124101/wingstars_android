package com.wingstars.count.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import com.tencent.mmkv.MMKV
import com.wingstars.base.net.API
import com.wingstars.base.net.NetBase
import com.wingstars.base.net.beans.CRMJournalHistoryResponse
import java.io.IOException

class CountHistoryPagingSource(
    private val bObtained: Boolean,
    private val pageSize: Int
) : PagingSource<Int, CRMJournalHistoryResponse.Journal>() {

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, CRMJournalHistoryResponse.Journal> {
        return try {
            val id = MMKV.defaultMMKV().decodeString("crm_member_id") ?: ""

            if (id.isEmpty()) {
                return LoadResult.Error(Exception("Member ID not found"))
            }

            val points_sign = if(bObtained==true) "positive" else "negative"
            val currentPage = params.key ?: 1
            var response = API().pagingDataApi.crmJournalHistory("${NetBase.HOST_CRM}/api/v1/basic/member/${id}/journal-history",currentPage,pageSize,points_sign)
            var nextPage = if (currentPage < response?.data?.TotalPage ?: 0) {
                currentPage + 1
            } else {
                null
            }
            LoadResult.Page(data = response.data.Journals, prevKey = null, nextKey = nextPage)

        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CRMJournalHistoryResponse.Journal>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}