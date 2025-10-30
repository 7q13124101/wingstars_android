package com.wingstars.base.net;


import com.wingstars.base.net.beans.NSPlayerBean;

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

//    //中继
//    //套餐管理 > 查询分类下的套餐    ${NetBase.HOST_BASE}/api/api/burgerking/app/combination/tree-with-combinations/{categoryType}
//    //categoryType: 分类类型（0:普通商品分类, 1:优惠券分类, 2:人气排行榜, 3:超值加点）
//    @GET(NetBase.HOST_BASE + "/api/api/burgerking/app/combination/tree-with-combinations/{categoryType}")
//    Observable<NSBaseResponse<List<NSCategoryResponse>>> nsCategorys(@Path("categoryType") int categoryType);
//
//    //用户认证 > 用户登录   ${NetBase.HOST_BASE}/api/api/burgerking/app/auth/login
//    @POST(NetBase.HOST_BASE + "/api/api/burgerking/app/auth/login")
//    Observable<NSBaseResponse<NSLoginResponse>> nsLogin(@Body NSLoginRequest nsLoginRequest);
//
//    //门店管理 > 获取附近门店    ${NetBase.HOST_BASE}/api/api/burgerking/app/store/nearby?latitude=24.165161&longitude=120.64618721&radius=5
//    @GET(NetBase.HOST_BASE + "/api/api/burgerking/app/store/nearby")
//    Observable<NSBaseResponse<List<NSStoreBean>>> nsStoreNearby(@Query("latitude") Double latitude, @Query("longitude") Double longitude, @Query("radius") Double radius);

//    @GET(NetBase.HOST_BASE + "/league/player/list/o/plg/teamId/3c3a601a-358a-11ed-ab92-711c34ed1298")
//    Observable<List<NSPlayerBean>> nsPlayers();

}



























