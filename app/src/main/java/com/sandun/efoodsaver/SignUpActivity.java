package com.sandun.efoodsaver;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.sandun.efoodsaver.dto.User;
import com.sandun.efoodsaver.model.AlertButtonManager;
import com.sandun.efoodsaver.model.AlertPopUp;
import com.sandun.efoodsaver.model.EmailVerificationAPI;
import com.sandun.efoodsaver.model.IsLogIn;
import com.sandun.efoodsaver.model.MobileAuthAPI;
import com.sandun.efoodsaver.model.RequestClient;
import com.sandun.efoodsaver.service.Auth;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = SignUpActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        IsLogIn logCheck = new IsLogIn(this);
            if (logCheck.check()) {
                logCheck.toHome();
            }

        ConstraintLayout sideMenuLayout = findViewById(R.id.sideMenuLayout);
        ImageView imageView = findViewById(R.id.imageView2);

        Animation animation = AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.sign_in_t_ani);
        animation.setFillAfter(true);
        animation.setDuration(1000);
        sideMenuLayout.startAnimation(animation);
        Animation animation2 = AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.sign_in_t_ani);
        animation2.setFillAfter(true);
        animation2.setDuration(500);
        imageView.startAnimation(animation2);

        findViewById(R.id.sign_up_btn).setOnClickListener(v -> {
            EditText ec = findViewById(R.id.email_or_contact_text);
            EditText fName = findViewById(R.id.f_name);
            EditText lName = findViewById(R.id.l_name);
            EditText password = findViewById(R.id.password_text_sin);
            AlertPopUp alertPopUp = new AlertPopUp(SignUpActivity.this, AlertPopUp.NORMAL);
            String fNameText = fName.getText().toString();
            String lNameText = lName.getText().toString();
            String ecText = ec.getText().toString();
            String passwordText = password.getText().toString();
            if (fNameText.isEmpty() && lNameText.isEmpty() && ecText.isEmpty() && passwordText.isEmpty()) {
                alertPopUp.setUp("Please Fill All Details First!!", AlertPopUp.ERROR);
            } else {
                RequestClient<Auth> client = new RequestClient(Auth.class, "user/",getApplicationContext().getSharedPreferences("auth", MODE_PRIVATE));
                Auth auth = client.createService();
                Call<JsonObject> call = auth.insert(new User(fNameText, lNameText, ecText, passwordText, "false","User"));
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            JsonObject obj = response.body();
                            if (obj != null && obj.size() > 0) {
                                if (obj.get("response") != null && obj.get("response").getAsString().equals("Success")) {
                                    if (obj.get("type").getAsString().equals("Mobile")) {
                                        MobileAuthAPI mobileAPI = new MobileAuthAPI(SignUpActivity.this);
                                        mobileAPI.signInWithPhone(ecText);
                                    } else if (obj.get("type").getAsString().equals("Email")) {
                                        JsonObject responseUserObj = obj.get("data").getAsJsonObject();
                                        EmailVerificationAPI emailVerificationAPI = new EmailVerificationAPI(SignUpActivity.this, auth, responseUserObj.get("id").getAsString());
                                    }
                                } else {
                                    String error = "";
                                    for (String name : obj.keySet()) {
                                        error += name.toUpperCase() + ": " + obj.get(name).getAsString() + "\n";
                                    }
                                    alertPopUp.setUp(error, AlertPopUp.ERROR).show();
                                }
                            } else {
                                alertPopUp.setUp("Something Went Wrong!!", AlertPopUp.ERROR).show();
                            }
                        } else {
                            alertPopUp.setUp("Something Went Wrong!!", AlertPopUp.ERROR).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        alertPopUp.setUp("Something Went Wrong!!", AlertPopUp.ERROR).show();
                    }
                });
            }
        });
        findViewById(R.id.register_text).setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });
    }
}