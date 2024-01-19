package com.sandun.efoodsaver.service;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface User {
    @POST("getAllDetails")
    Call<JsonObject> getUserDetails(@Body JsonObject object);
    @POST("update")
    Call<JsonObject> updateUser(@Body JsonObject object);
    @POST("setLocation")
    Call<JsonObject> setLocation(@Body JsonObject object);

}
