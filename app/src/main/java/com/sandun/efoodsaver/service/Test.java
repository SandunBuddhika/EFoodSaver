package com.sandun.efoodsaver.service;

import com.google.gson.JsonObject;
import com.sandun.efoodsaver.dto.ReqResponse;
import com.sandun.efoodsaver.dto.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Test {
    @POST("test")
    @Headers("Content-Type: application/json")
    Call<JsonObject> test(@Body User req);
}
