package com.alaske.demo.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Author: zhaocheng
 * Date: 2016-04-12
 * Time: 17:44
 * Name:ApiManager
 */
public interface WebApiService {

    @GET
    Call<ResponseBody> getUrl(@Url String url);
}
