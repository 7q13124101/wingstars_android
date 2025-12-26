package com.wingstars.count.Repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.wingstars.base.net.beans.CRMJournalHistoryResponse
import com.wingstars.count.repository.CountHistoryPagingSource
import kotlinx.coroutines.flow.Flow

object CountHistoryRepository{
    private const val PAGE_SIZE = 100

    fun getCountHistoryData(bObtained:Boolean): Flow<PagingData<CRMJournalHistoryResponse.Journal>> {
        val pager = Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = PAGE_SIZE/2,
                initialLoadSize = PAGE_SIZE/2, enablePlaceholders = true),
            pagingSourceFactory = { CountHistoryPagingSource(bObtained,PAGE_SIZE) }
        )
        return pager.flow
    }
}