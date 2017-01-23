package com.bridgeconn.autographago.utils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiInterface {

    @Streaming
    @GET("/resource/example.zip")
    Call<ResponseBody> downloadFileWithFixedUrl();

    @Streaming
    @GET
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);

}
