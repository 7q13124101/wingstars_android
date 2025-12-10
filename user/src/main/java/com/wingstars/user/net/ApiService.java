package com.wingstars.user.net;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

import com.wingstars.user.net.beans.FrequentlyQuestionsResponse;
import com.wingstars.user.net.beans.CRMBaseResponse;
import com.wingstars.user.net.beans.CRMMemberContactResponse;
import com.wingstars.user.net.beans.CRMMemberDetailResponse;
import com.wingstars.user.net.beans.CRMSignInRequest;
import com.wingstars.user.net.beans.CRMSignInResponse;
import com.wingstars.user.net.beans.CRMTotalSpentResponse;
import com.wingstars.user.net.beans.CRMVerifyRequest;
import com.wingstars.user.net.beans.CRMVerifyResponse;
import com.wingstars.user.net.beans.HksCustomersResponse;
import com.wingstars.user.net.beans.HksOrdersResponse;
import com.wingstars.user.net.beans.NSBaseResponse;
import com.wingstars.user.net.beans.NSInfoRequest;
import com.wingstars.user.net.beans.NSInfoResponse;
import com.wingstars.user.net.beans.NSTokenNewResponse;

import java.util.List;

public interface ApiService {
    @POST()
    Call<CRMBaseResponse<CRMVerifyResponse>> crmVerifyCall(@Url String url, @Body CRMVerifyRequest verifyRequest);

    @POST()
    Call<CRMBaseResponse<CRMSignInResponse>> crmSignInCall(@Url String url, @Body CRMSignInRequest signInRequest);

    @GET()
    Call<NSBaseResponse<NSTokenNewResponse>> nsTokenNewCall(@Url String url);
    @GET()
    Observable<CRMBaseResponse<CRMMemberDetailResponse>> crmMemberDetail(@Url String url);
    @GET()
    Observable<CRMBaseResponse<CRMTotalSpentResponse>> crmTotalSpent(@Url String url);
    @GET()
    Observable<CRMBaseResponse<CRMMemberContactResponse>> crmGetMemberContact(@Url String url);
    @GET()
    Observable<List<HksCustomersResponse>> hksCustomers(@Url String url, @Query("email") String email, @Query("consumer_key") String consumer_key, @Query("consumer_secret") String consumer_secret);
    @GET()
    Observable<List<HksOrdersResponse>> hksOrders(@Url String url, @Query("customer") int customerId, @Query("page") int curPage, @Query("per_page") int perPage, @Query("status") String status);
    @POST()
    Observable<NSInfoResponse> nsInfo(@Url String url, @Body NSInfoRequest infoRequest);
    //常见问题      ${BaseApplication.HOST_HAWKS_CDN}/api/v1/app/questions
    @GET()
    Observable<FrequentlyQuestionsResponse> nsQuestions(@Url String url);


}
