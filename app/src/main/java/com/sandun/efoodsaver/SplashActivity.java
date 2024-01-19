package com.sandun.efoodsaver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.gson.JsonObject;
import com.sandun.efoodsaver.entities.User;
import com.sandun.efoodsaver.model.IsLogIn;
import com.sandun.efoodsaver.model.RequestClient;
import com.sandun.efoodsaver.service.Auth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        IsLogIn isLogIn = new IsLogIn(this);
        isLogIn.check();
        if (IsLogIn.user != null) {
            Auth service = new RequestClient<>(Auth.class, "user/", getApplicationContext().getSharedPreferences("auth", MODE_PRIVATE)).createService();
            JsonObject object = new JsonObject();
            object.addProperty("id", IsLogIn.user.getId());
            object.addProperty("em_or_cno", IsLogIn.user.getEmailOrCno());
            Call<JsonObject> call = service.check(object);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        isLogIn.toHome();
                    } else if (response.code() == 401) {
                        isLogIn.deleteUser(isLogIn::toHome);
                    }
                }
                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    isLogIn.deleteUser(isLogIn::toHome);
                }
            });
        }else{
            isLogIn.toHome();
        }
    }

}