package com.sandun.efoodsaver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.sandun.efoodsaver.dto.ReqResponse;
import com.sandun.efoodsaver.dto.User;
import com.sandun.efoodsaver.service.Test;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        findViewById(R.id.button).setOnClickListener(v -> {
//            EditText text = findViewById(R.id.editTextText);
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl("http://10.0.2.2:8080/EFoodSaverApi/api/")
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//            new Thread(() -> {
//                Test test = retrofit.create(Test.class);
//                Call<JsonObject> req = test.test(new User("1","Sandun"));
//                req.enqueue(new Callback<JsonObject>() {
//                    @Override
//                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                        if (response.isSuccessful()) {
//                            Log.i(TAG,"Success");
//                        }
//                    }
//                    @Override
//                    public void onFailure(Call<JsonObject> call, Throwable t) {
//                        Log.e(TAG,"Error: "+t.getMessage());
//                    }
//                });
//            }).start();
//        });
    }
}