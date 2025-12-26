package com.wingstars.base.net


import com.wingstars.base.net.beans.CRMBaseResponse
import com.wingstars.base.net.beans.CRMJournalHistoryResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface DataApi {
    @GET
    suspend fun crmJournalHistory(
        @Url url: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("points_sign") points_sign: String
    ): CRMBaseResponse<CRMJournalHistoryResponse>
//    @GET("wp-json/wp/v2/roster_list")
//    fun getRoster(
//        @Query("year") year: String,
//        @Query("teamNo") teamNo: String,
//        @Query("type") type: String
//    ): Single<List<CpblGetTeamItem>>



//    @GET
//    fun wpRosterList(
//        @Url url: String
//    ): Single<List<RosterItem>>
//
//    @GET
//    suspend fun crmInAppMessages(
//        @Url url: String,
//        @Query("category") category: String,
//        @Query("page") page: Int,
//        @Query("size") size: Int,
//    ): CRMBaseResponse<List<CRMInAppMessageResponse>>
}