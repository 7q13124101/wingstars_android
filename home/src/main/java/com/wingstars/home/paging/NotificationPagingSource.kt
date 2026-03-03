package com.wingstars.home.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.tencent.mmkv.MMKV
import com.wingstars.base.net.API
import com.wingstars.base.net.beans.CRMInAppMessageResponse
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class NotificationPagingSource(
    private val category: String // "" để lấy tất cả
) : PagingSource<Int, CRMInAppMessageResponse>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CRMInAppMessageResponse> {
        val page = params.key ?: 1
        val limit = params.loadSize
        val memberId = MMKV.defaultMMKV().decodeString("crm_member_id") ?: ""

        return try {
            // 1. Gọi API (Trả về Observable)
            val observable = API.shared?.api?.getInAppMessages(memberId, category, page, limit)

            // 2. Chuyển đổi Observable thành kết quả (Sử dụng hàm await tự viết bên dưới)
            val dataList = observable?.await() ?: emptyList()

            // 3. Trả về kết quả cho Paging
            LoadResult.Page(
                data = dataList,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (dataList.isEmpty() || dataList.size < limit) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CRMInAppMessageResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    // --- Hàm tiện ích chuyển đổi RxJava Observable -> Coroutine Suspend ---
    // Bạn có thể đưa hàm này ra file Utils nếu muốn dùng lại
    private suspend fun <T : Any> Observable<T>.await(): T {
        return suspendCancellableCoroutine { cont ->
            val disposable = this.subscribe(
                { value -> cont.resume(value) },
                { error -> cont.resumeWithException(error) }
            )
            cont.invokeOnCancellation { disposable.dispose() }
        }
    }
}