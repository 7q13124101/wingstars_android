package com.wingstars.user.net;

import com.wingstars.base.net.NetBase;
import com.wingstars.user.net.beans.CRMMemberRespone;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

public interface ApiService {
    @GET(NetBase.HOST_BASE + "/wp-json/wp/v2/member_list?_fields=id,title,yoast_head_json.og_image,acf.number")
    Observable<List<CRMMemberRespone>> getMemberList();
}
