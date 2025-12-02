package com.wingstars.base.net;


import com.wingstars.base.net.beans.WSCalendarCategoryResponse;
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

    //今日行程
    @GET(NetBase.HOST_BASE + "/wp-json/wp/v2/calendar?_fields=id,title.rendered,acf,content.rendered,yoast_head_json.og_image,calendar_category")
    Observable<List<WSCalendarResponse>> wsSchedule(@Query("per_page") int per_page, @Query("page") int page);

    //热销商品
    @GET(NetBase.HOST_BASE + "/wp-json/wc/v3/products?per_page=4&order=desc")
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
    Observable<List<WSMemberResponse>> wsPhotos(@Query("per_page") int per_page, @Query("page") int page);

    //成员 > 拍照图框
    @GET(NetBase.HOST_BASE + "/wp-json/wp/v2/member_list?_fields=id,title,acf.number,acf.photoFrame,acf.photoFrame_image_urls")
    Observable<List<WSPhotoFrameResponse>> wsPhotoFrames(@Query("per_page") int per_page, @Query("page") int page);

    //成员 > 氛围时尚-分类
    @GET(NetBase.HOST_BASE + "/wp-json/wp/v2/fashion_category?_fields=id,name")
    Observable<List<WSFashionCategoryResponse>> wsFashionCategorys();

    //成员 > 氛围时尚-内页
    @GET(NetBase.HOST_BASE + "/wp-json/wp/v2/fashion/{fashion_id}?_fields=id,title,content,yoast_head_json.og_image,acf.gallery,acf")
    Observable<WSFashionDetailResponse> wsFashion(@Path("fashion_id") int fashion_id);

    //日历（与今日行程相同）
    @GET(NetBase.HOST_BASE + "/wp-json/wp/v2/calendar?_fields=id,title.rendered,acf,content.rendered,yoast_head_json.og_image,calendar_category")
    Observable<List<WSCalendarResponse>> wsCalendar(@Query("per_page") int per_page, @Query("page") int page);

    //日历-分类
    @GET(NetBase.HOST_BASE + "/wp-json/wp/v2/calendar_category?_fields=id,name")
    Observable<List<WSCalendarCategoryResponse>> wsCalendarCategory(@Query("per_page") int per_page, @Query("page") int page);

    //商城---
    //查询指定客户 customer_id
    @GET(NetBase.HOST_BASE + "/wp-json/wc/v3/customers")
    Observable<List<WSCustomerResponse>> wsCustomer(@Query("consumer_key") String consumer_key, @Query("consumer_secret") String consumer_secret, @Query("email") String email);

    //查询指定客户订单
    @GET(NetBase.HOST_BASE + "/wp-json/wc/v3/orders")
    Observable<List<WSOrderResponse>> wsOrders(@Query("consumer_key") String consumer_key, @Query("consumer_secret") String consumer_secret, @Query("customer") int customer, @Query("status") String status, @Query("per_page") int per_page, @Query("page") int page);


}



























