package com.wingstars.base.net;


import com.wingstars.base.net.beans.BeaconListResponse;
import com.wingstars.base.net.beans.BluetoothBeaconRequest;
import com.wingstars.base.net.beans.CRMBaseResponse;
import com.wingstars.base.net.beans.CRMCouponQRCodeRequest;
import com.wingstars.base.net.beans.CRMCouponQRCodeResponse;
import com.wingstars.base.net.beans.CRMCouponsAvailableResponse;
import com.wingstars.base.net.beans.CRMCouponsResponse;
import com.wingstars.base.net.beans.CRMDeleteRespone;
import com.wingstars.base.net.beans.CRMForgotPasswordRequest;
import com.wingstars.base.net.beans.CRMGenQRCodeRequest;
import com.wingstars.base.net.beans.CRMGenQRCodeResponse;
import com.wingstars.base.net.beans.CRMJournalHistoryResponse;
import com.wingstars.base.net.beans.CRMMemberContactResponse;
import com.wingstars.base.net.beans.CRMOTPCoupons;
import com.wingstars.base.net.beans.CRMRedeemCouponRequest;
import com.wingstars.base.net.beans.CRMRedeemCouponResponse;
import com.wingstars.base.net.beans.CRMRedeemStoresSearchResponse;
import com.wingstars.base.net.beans.CRMSMSRequest;
import com.wingstars.base.net.beans.CRMMemberDetailResponse;
import com.wingstars.base.net.beans.CRMResetPasswordRequest;
import com.wingstars.base.net.beans.CRMResetPasswordResponse;
import com.wingstars.base.net.beans.CRMSendOtpRequest;
import com.wingstars.base.net.beans.CRMSendOtpResponse;
import com.wingstars.base.net.beans.CRMSignInRequest;
import com.wingstars.base.net.beans.CRMSignInResponse;
import com.wingstars.base.net.beans.CRMSignUpRequest;
import com.wingstars.base.net.beans.CRMVerifyRequest;
import com.wingstars.base.net.beans.CRMVerifyResponse;
import com.wingstars.base.net.beans.EvtCheckinRequest;
import com.wingstars.base.net.beans.EvtCheckinResponse;
import com.wingstars.base.net.beans.EvtMemberBadgeResponse;
import com.wingstars.base.net.beans.EvtMemberTaskResponse;
import com.wingstars.base.net.beans.EvtTaskResponse;
import com.wingstars.base.net.beans.FrequentlyQuestionsResponse;
import com.wingstars.base.net.beans.NSBaseResponse;
import com.wingstars.base.net.beans.NSInfoRequest;
import com.wingstars.base.net.beans.NSInfoResponse;
import com.wingstars.base.net.beans.CRMBaseResponse;
import com.wingstars.base.net.beans.CRMGenQRCodeRequest;
import com.wingstars.base.net.beans.CRMGenQRCodeResponse;
import com.wingstars.base.net.beans.CRMMemberContactResponse;
import com.wingstars.base.net.beans.CRMSignInRequest;
import com.wingstars.base.net.beans.CRMSignInResponse;
import com.wingstars.base.net.beans.CRMVerifyRequest;
import com.wingstars.base.net.beans.CRMVerifyResponse;
import com.wingstars.base.net.beans.EvtMemberTaskResponse;
import com.wingstars.base.net.beans.NSInfoRequest;
import com.wingstars.base.net.beans.NSInfoResponse;
import com.wingstars.base.net.beans.NSParkingResponse;
import com.wingstars.base.net.beans.NSTokenNewResponse;
import com.wingstars.base.net.beans.NSTokenRefreshRequest;
import com.wingstars.base.net.beans.NSUserErrorInfoRequest;
import com.wingstars.base.net.beans.WSCalendarCategoryResponse;
import com.wingstars.base.net.beans.WSCalendarNResponse;
import com.wingstars.base.net.beans.WSCalendarResponse;
import com.wingstars.base.net.beans.WSCustomerResponse;
import com.wingstars.base.net.beans.WSFashionCategoryResponse;
import com.wingstars.base.net.beans.WSFashionDetailResponse;
import com.wingstars.base.net.beans.WSFashionResponse;
import com.wingstars.base.net.beans.WSMemberResponse;
import com.wingstars.base.net.beans.WSOrderResponse;
import com.wingstars.base.net.beans.WSPhotoFrameResponse;
import com.wingstars.base.net.beans.WSPostResponse;
import com.wingstars.base.net.beans.WSProductResponse;
import com.wingstars.base.net.beans.WSRankResponse;
import com.wingstars.base.net.beans.WSScheduleResponse;
import com.wingstars.base.net.beans.YoutubeListResponse;
import com.wingstars.base.net.beans.YoutubeSearchResponse;

import okhttp3.ResponseBody;
import retrofit2.http.DELETE;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Url;


public interface ApiService {
    //Oauth > 客户端验证. ${BaseApplication.HOST_CRM}/api/v1/oauth/verify
    @POST()
    Observable<CRMBaseResponse<CRMVerifyResponse>> crmVerify(@Url String url, @Body CRMVerifyRequest verifyRequest);
    @POST()
    Call<CRMBaseResponse<CRMVerifyResponse>> crmVerifyCall(@Url String url, @Body CRMVerifyRequest verifyRequest);
    @POST()
    Call<CRMBaseResponse<CRMSignInResponse>> crmSignInCall(@Url String url, @Body CRMSignInRequest signInRequest);
    @GET(NetBase.HOST_CRM+"/api/v1/client/sign-in/check")
    Observable<CRMBaseResponse<Object>> crmSignInCheck(@Query("phone") String phone);
    @POST()
    Observable<CRMBaseResponse<CRMSignInResponse>> crmSignIn(@Url String url, @Body CRMSignInRequest signInRequest);

    ///v1/client/otp/sms 发送短信验证码
    @POST(NetBase.HOST_CRM+"/api/v1/client/otp/sms")
    Observable<CRMBaseResponse<Object>> crmSMS( @Body CRMSMSRequest signInRequest);

    @GET(NetBase.HOST_CRM+"/api/v1/client/sign-up/check")
    Observable<CRMBaseResponse<Object>> crmSignUpCheck( @Query("phone") String phone);

    @POST(NetBase.HOST_CRM+"/api/v1/client/sign-up")
    Observable<CRMBaseResponse<Object>> crmSignUp(@Body CRMSignUpRequest signUpRequest);
    @GET()
    Observable<CRMBaseResponse<CRMMemberContactResponse>> crmGetMemberContact(@Url String url);
    @GET()
    Observable<CRMBaseResponse<CRMMemberContactResponse>> crmGetMemberExpiredDate(@Url String url);
    @POST()
    Observable<CRMBaseResponse<CRMSendOtpResponse>> crmSendOtp(@Url String url, @Body CRMSendOtpRequest genSendOtp);
    @PUT()
    Observable<CRMBaseResponse<CRMResetPasswordResponse>> crmResetPassword(@Url String url, @Body CRMResetPasswordRequest genResetPassword);

    @POST(NetBase.HOST_CRM+"/api/v1/client/forgot-password")
    Observable<CRMBaseResponse<CRMResetPasswordResponse>> crmForgotPassword(@Body CRMForgotPasswordRequest genResetPassword);
    @DELETE()
    Observable<CRMBaseResponse<CRMDeleteRespone>> crmDeleteAccount(@Url String url, @Body CRMResetPasswordRequest genResetPassword);

    @POST()
    Observable<NSInfoResponse> nsInfo(@Url String url, @Body NSInfoRequest infoRequest);
    @POST()
    Observable<CRMBaseResponse<CRMGenQRCodeResponse>> crmGenQRCode(@Url String url, @Body CRMGenQRCodeRequest genQRCodeRequest);
    @GET()
    Observable<List<EvtMemberTaskResponse>> evtMemberTasks(@Url String url, @Query("encryptedIdentity") String encryptedIdentity);

    //首頁 > Youtube
    @GET(NetBase.HOST_GOOGLE + "/youtube/v3/search")
    Observable<YoutubeSearchResponse> getYoutubeVideos(
            @Query("part") String part,
            @Query("channelId") String channelId,
            @Query("maxResults") int maxResults,
            @Query("order") String order,
            @Query("type") String type,
            @Query("key") String apiKey
    );

    //Member > 查询会员详细资料. ${BaseApplication.HOST_CRM}/api/v1/basic/member/{id}
    @GET(NetBase.HOST_CRM + "/api/v1/basic/member/{id}")
    Observable<CRMBaseResponse<CRMMemberDetailResponse>> crmMemberDetail(@Path("id") String id);
    //今日行程
    @GET(NetBase.HOST_BASE + "/wp-json/wp/v2/calendar?_fields=id,title.rendered,acf,content.rendered,yoast_head_json.og_image,calendar_category")
    Observable<List<WSCalendarResponse>> wsSchedule(@Query("per_page") int per_page, @Query("page") int page);

    //热销商品
    @GET(NetBase.HOST_BASE + "/wp-json/wc/v3/products?per_page=4&order=desc&status=publish")
    Observable<List<WSProductResponse>> wsProducts();

    //最新消息
    @GET(NetBase.HOST_BASE + "/wp-json/wp/v2/posts?per_page=4&order=desc")
    Observable<List<WSPostResponse>> wsPosts();

    //氛围时尚
    @GET(NetBase.HOST_BASE + "/wp-json/wp/v2/fashion?_fields=id,title,yoast_head_json.og_image,fashion_category&orderby=date&order=desc")
    Observable<List<WSFashionResponse>> wsFashions(@QueryMap HashMap<String, Integer> param, @Query("per_page") int per_page, @Query("page") int page);

    //人气排行-名次
    @GET(NetBase.HOST_BASE + "/wp-json/wp/v2/rank?_fields=id,title.rendered,acf,content.rendered")
    Observable<List<WSRankResponse>> wsRank();

    //成员 > 成员介绍
    @GET(NetBase.HOST_BASE + "/wp-json/wp/v2/member_list?_fields=id,title,yoast_head_json.og_image,acf")
    Observable<List<WSMemberResponse>> wsMembers(@Query("per_page") int per_page, @Query("page") int page);

    //人气排行-名次对应成员头贴图片
    @GET(NetBase.HOST_BASE + "/wp-json/wp/v2/member_list?_fields=id,title,yoast_head_json.og_image,acf")
    Observable<List<WSMemberResponse>> wsPhotos(@Query("per_page") int per_page,@Query("page") int page);

    //成员 > 拍照图框
    @GET(NetBase.HOST_BASE + "/wp-json/wp/v2/member_list?_fields=id,title,acf.number,acf.photoFrame,acf.photoFrame_image_urls")
    Observable<List<WSPhotoFrameResponse>> wsPhotoFrames();

    //成员 > 氛围时尚-分类
    @GET(NetBase.HOST_BASE + "/wp-json/wp/v2/fashion_category?_fields=id,name")
    Observable<List<WSFashionCategoryResponse>> wsFashionCategorys();

    //成员 > 氛围时尚-内页
    @GET(NetBase.HOST_BASE + "/wp-json/wp/v2/fashion/{fashion_id}?_fields=id,title,content,yoast_head_json.og_image,acf.gallery,acf")
    Observable<WSFashionDetailResponse> wsFashion(@Path("fashion_id") int fashion_id);

    //日历（与今日行程相同）
    @GET(NetBase.HOST_BASE + "/wp-json/wp/v2/calendar?_fields=id,title.rendered,acf,content.rendered,yoast_head_json.og_image,calendar_category")
    Observable<List<WSCalendarResponse>> wsCalendar(@Query("per_page") int per_page, @Query("page") int page);

    //日历2（新版）
    @GET(NetBase.HOST_BASE + "/wp-json/tsg-schedule/v1/calendar")
    Observable<List<WSCalendarNResponse>> wsCalendarN(@QueryMap HashMap<String, String> param);


    //日历-分类
    @GET(NetBase.HOST_BASE + "/wp-json/wp/v2/calendar_category?_fields=id,name")
    Observable<List<WSCalendarCategoryResponse>> wsCalendarCategory(@Query("per_page") int per_page, @Query("page") int page);

    //班表
    @GET(NetBase.HOST_BASE + "/wp-json/tsg-schedule/v1/schedules")
    Observable<List<WSScheduleResponse>> wsSchedules(@QueryMap HashMap<String, String> param);


    //商城---
    //查询指定客户 customer_id
    @GET(NetBase.HOST_BASE + "/wp-json/wc/v3/customers")
    Observable<List<WSCustomerResponse>> wsCustomer(@Query("consumer_key") String consumer_key, @Query("consumer_secret") String consumer_secret, @Query("email") String email);

    //查询指定客户订单
    @GET(NetBase.HOST_BASE + "/wp-json/wc/v3/orders?status=completed")
    Observable<List<WSOrderResponse>> wsOrders(@Query("consumer_key") String consumer_key, @Query("consumer_secret") String consumer_secret, @Query("customer") int customer, @Query("per_page") int per_page, @Query("page") int page);


    //CRM Event Service======
    //Event > 取得活跃任务列表.   ${BaseApplication.HOST_EVENT}/api/v1/public/events/tasks
    @GET(NetBase.HOST_EVENT + "api/v1/public/events/tasks")
    Observable<List<EvtTaskResponse>> evtTasks();

    //Event > 获得点数.   ${BaseApplication.HOST_EVENT}/api/v1/public/events/reward
    @POST(NetBase.HOST_EVENT + "/api/v1/public/events/reward")
    Observable<EvtCheckinResponse> evtReward(@Body EvtCheckinRequest evtCheckinRequest);

    //Event > 会员任务状态列表.   ${BaseApplication.HOST_EVENT}/api/v1/public/members/tasks?encryptedIdentity=...
    @GET(NetBase.HOST_EVENT + "/api/v1/public/members/tasks")
    Observable<List<EvtMemberTaskResponse>> evtMemberTasks( @Query("encryptedIdentity") String encryptedIdentity);

    //Event > 查询会员勋章列表.   ${BaseApplication.HOST_EVENT}/api/v1/public/member/badges?encryptedIdentity=...
    @GET(NetBase.HOST_EVENT + "/api/v1/public/member/badges")
    Observable<List<EvtMemberBadgeResponse>> evtMemberBadges(@Query("encryptedIdentity") String encryptedIdentity);

    //Event > 取得任务详情.   ${BaseApplication.HOST_EVENT}/api/v1/public/event-tasks/{taskid}
    @GET(NetBase.HOST_EVENT + "/api/v1/public/event-tasks/{taskid}")
    Observable<EvtTaskResponse> evtTaskInfo();

    //Event > 取得任务详情.   ${BaseApplication.HOST_EVENT}/api/v1/public/event-tasks/{taskid}?encryptedIdentity=...
    @GET(NetBase.HOST_EVENT + "/api/v1/public/event-tasks/{taskid}")
    Observable<EvtMemberTaskResponse> evtTaskInfo( @Query("encryptedIdentity") String encryptedIdentity);

    @GET(NetBase.HOST_CRM + "/api/v1/basic/member/{id}/coupons/available")
    Observable<CRMBaseResponse<List<CRMCouponsAvailableResponse>>> crmCouponsAvailable(
            @Path("id") String id,
            @Query("coupon_type") Integer couponType,
            @Query("page") Integer page,
            @Query("size") Integer size
    );

    @GET()
    Observable<CRMBaseResponse<List<CRMCouponsResponse>>> crmCoupons(@Url String url);

    @GET()
    Observable<CRMBaseResponse<CRMOTPCoupons>> crmOTPCoupons(@Url String url);

    //Coupon > Coupon核销QR Code.
    @POST()
    Observable<CRMBaseResponse<CRMCouponQRCodeResponse>> crmCouponQRCode(@Url String url, @Body CRMCouponQRCodeRequest couponQRCodeRequest);

    //Coupon > 查询兑换通路.
    @GET()
    Observable<CRMBaseResponse<List<CRMRedeemStoresSearchResponse>>> crmRedeemStoresSearch(@Url String url);


    //Coupon > 扣除红利兑换Coupon. ${BaseApplication.HOST_CRM}/api/v1/basic/member/{id}/points/redeem-coupon
    @POST()
    Observable<CRMBaseResponse<CRMRedeemCouponResponse>> crmRedeemCoupon(@Url String url, @Body CRMRedeemCouponRequest redeemCouponRequest);


    //中继
    //获取token
    @GET(NetBase.HOST_NEWSOFT + "/api/v1/com/token/new/" + NetBase.NEWSOFT_APP_ID)
    Observable<NSBaseResponse<NSTokenNewResponse>> nsTokenNew();

    //中继
    //获取token
    @GET(NetBase.HOST_NEWSOFT + "/api/v1/com/token/new/" + NetBase.NEWSOFT_APP_ID)
    Call<NSBaseResponse<NSTokenNewResponse>> nsTokenNewCall();

    //刷新token
    @POST(NetBase.HOST_NEWSOFT + "/api/v1/com/token/refresh/" + NetBase.NEWSOFT_APP_ID)
    Call<NSBaseResponse<NSTokenNewResponse>> nsTokenRefresh(@Body NSTokenRefreshRequest tokenRefreshRequest);

    //记录手机设备信息、CRM会员信息
    @POST(NetBase.HOST_NEWSOFT + "/api/v1/app/mobile_crm/info")
    Observable<NSInfoResponse> nsInfo(@Body NSInfoRequest infoRequest);

    //post error
    @POST()
    Observable<NSInfoResponse> nsUserErrorInfo(@Url String url, @Body NSUserErrorInfoRequest infoRequest);

    //中继
    //获取server管理员维护的信标设备列表
    @GET(NetBase.HOST_NEWSOFT + "/api/v1/app/beacon/list")
    Observable<BeaconListResponse> getBeaconList(@Query("pageNum") int pageNum, @Query("pageSize") int pageSize);

    //中继
    //APP侦测到设备列表后请求
    @POST(NetBase.HOST_NEWSOFT + "/api/v1/app/device-bluetooth/interaction")
    Observable<NSInfoResponse> bluetoothToBeancon(@Body BluetoothBeaconRequest request);

    //中继
    //获取Youtube视频
    @GET(NetBase.HOST_NEWSOFT + "/api/v1/app/youtube/vlist")
    Observable<YoutubeListResponse> nsYtbList();

    //获取Youtube List
    //eventType=completed：僅包含已結束的廣播。
    //eventType=live：只包含進行中的廣播訊息。
    //eventType=upcoming：只包含即將播送的直播內容。
    //eventType=shorts： 短片
    //eventType=vlog：Vlog
    @GET(NetBase.HOST_NEWSOFT + "/api/v1/app/youtube/vlist")
    Observable<YoutubeListResponse> nsYtbList(@Query("eventType") String eventType);

    //jsonfile=wingstarsschedule：鹰援班表
    //jsonfile=catering：餐饮
    //返回json文件数据
    @GET(NetBase.HOST_NEWSOFT + "/api/v1/app/jsondata/${jsonfile}")
    Observable<Object> nsJsonData(@Path("jsonfile") String jsonfile);

    //zipfile=wingstarsschedule：鹰援班表
    @GET(NetBase.HOST_NEWSOFT + "/api/v1/app/bidata/${zipfile}")
    Observable<ResponseBody> nsBiData(@Path("zipfile") String zipfile);

    //取动态停车位
    @GET(NetBase.HOST_NEWSOFT + "/api/v1/app/remotedata/parking")
    Observable<NSParkingResponse> nsParking();

    //常见问题
    @GET(NetBase.HOST_NEWSOFT + "/api/v1/app/questions")
    Observable<FrequentlyQuestionsResponse> nsQuestions();

}



























