package com.sandun.efoodsaver.model;

import android.content.SharedPreferences;

import com.sandun.efoodsaver.interceptor.RequestInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestClient<T> {
    private Class<T> apiService;
    private Retrofit retrofit;
    private String token;
    private String baseUrl = "http://10.0.2.2:8080/EFoodSaverApi/api/";

    public RequestClient(Class<T> apiService, String baseUrl, SharedPreferences preferences) {
        this.apiService = apiService;
        this.baseUrl += baseUrl;
        token = preferences.getString("token","");
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new RequestInterceptor(token)).build();
        retrofit = new Retrofit.Builder().client(client).baseUrl(this.baseUrl).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public T createService(){
        return retrofit.create(apiService);
    }

}
