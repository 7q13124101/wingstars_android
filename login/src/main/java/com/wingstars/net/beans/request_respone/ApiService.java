package com.wingstars.net.beans.request_respone;

import com.wingstars.base.net.NetBase;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST(NetBase.CRM_HOST + "/api/v1/oauth/verify")
    Observable<AccessTokenResponse> getAccessToken(@Body AccessTokenRequest request);
    @POST(NetBase.CRM_HOST + "/api/v1/client/sign-in")
    Observable<CRMLoginResponse> logIn(@Body LoginRequest request);
    @GET(NetBase.CRM_HOST + "/api/v1/basic/member/{id}")
    Observable<MemberInfoResponse> getMemberInfo(
            @Header("Authorization") String token,
            @Path("id") String userId
    );
    @POST(NetBase.CRM_HOST + "/api/v1/client/otp/sms")
    Observable<OtpSmsResponse> requestOtpSms(@Body OtpSmsRequest request);

}
