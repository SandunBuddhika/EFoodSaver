package com.sandun.efoodsaver.interceptor;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RequestInterceptor implements Interceptor {
    private static final String TAG = RequestInterceptor.class.getName();
    private String token;

    public RequestInterceptor(String token) {
        this.token = token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Log.i(TAG,"Calling Interceptor...");
        Request request = chain.request().newBuilder().header("Authorization","Bearer "+token).build();
        return chain.proceed(request);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
