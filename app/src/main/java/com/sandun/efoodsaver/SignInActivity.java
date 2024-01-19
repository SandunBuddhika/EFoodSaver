package com.sandun.efoodsaver;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;
import com.sandun.efoodsaver.dao.UserDAO;
import com.sandun.efoodsaver.dto.User;
import com.sandun.efoodsaver.model.AlertButtonManager;
import com.sandun.efoodsaver.model.AlertPopUp;
import com.sandun.efoodsaver.model.EmailVerificationAPI;
import com.sandun.efoodsaver.model.GoogleAuthAPI;
import com.sandun.efoodsaver.model.InternalDB;
import com.sandun.efoodsaver.model.IsLogIn;
import com.sandun.efoodsaver.model.RequestClient;
import com.sandun.efoodsaver.service.Auth;
import com.sandun.efoodsaver.util.AppDatabase;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = SignInActivity.class.getName();
    private GoogleAuthAPI googleAuthAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        IsLogIn logCheck = new IsLogIn(this);
        if (logCheck.check()) {
            logCheck.toHome();
        }
        ConstraintLayout sideMenuLayout = findViewById(R.id.sideMenuLayout);
        ImageView imageView = findViewById(R.id.imageView2);

        Animation animation = AnimationUtils.loadAnimation(SignInActivity.this, R.anim.sign_in_t_ani);
        animation.setFillAfter(true);
        animation.setDuration(1000);
        sideMenuLayout.startAnimation(animation);
        Animation animation2 = AnimationUtils.loadAnimation(SignInActivity.this, R.anim.sign_in_t_ani);
        animation2.setFillAfter(true);
        animation2.setDuration(500);
        imageView.startAnimation(animation2);

        findViewById(R.id.google_sign_In).setOnClickListener(v -> {
            googleAuthAPI = new GoogleAuthAPI(SignInActivity.this);
            signIn();
        });
        findViewById(R.id.fb_sign_In).setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, TestActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.sign_in_btn).setOnClickListener(v -> {
            EditText ec = findViewById(R.id.email_or_contact_text);
            EditText password = findViewById(R.id.password_text_sin);
            AlertPopUp alertPopUp = new AlertPopUp(SignInActivity.this, AlertPopUp.NORMAL);
            if (!ec.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                Auth apiClient = new RequestClient<>(Auth.class, "user/", getApplicationContext().getSharedPreferences("auth", MODE_PRIVATE)).createService();
                Call<JsonObject> call = apiClient.auth(new User(ec.getText().toString(), password.getText().toString(), "User"));
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            JsonObject obj = response.body();
                            if (obj != null && obj.size() > 0) {
                                if (obj.get("response") != null && obj.get("response").getAsString().equals("Success")) {
                                    new Thread(() -> {
                                        AppDatabase database = new InternalDB<AppDatabase>(getApplicationContext(), AppDatabase.class).build();
                                        UserDAO userDAO = database.userDAO();
                                        com.sandun.efoodsaver.entities.User uObj = new com.sandun.efoodsaver.entities.User(obj.get("data").getAsJsonObject());
                                        try {
                                            userDAO.delete(uObj);
                                            userDAO.insert(uObj);
                                            SharedPreferences preferences = getApplicationContext().getSharedPreferences("auth", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = preferences.edit();
                                            IsLogIn.user = uObj;
                                            if (obj.get("token") != null) {
                                                editor.putString("token", obj.get("token").getAsString());
                                                editor.apply();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }).start();
                                    alertPopUp.customButtonSetUp(new AlertButtonManager() {
                                        @Override
                                        public void process(AlertDialog dialog, AlertPopUp alertPopUp) {
                                            Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                            alertPopUp.dismiss();
                                        }
                                    });
                                    alertPopUp.show();
                                } else {
                                    if (obj.get("response") != null && obj.get("response").getAsString().equals("Please Verify Your Account")) {
                                        JsonObject uObj = obj.get("data").getAsJsonObject();
                                        alertPopUp.customButtonSetUp(new AlertButtonManager() {
                                            @Override
                                            public void process(AlertDialog dialog, AlertPopUp alertPopUp) {
                                                EmailVerificationAPI emailVerificationAPI = new EmailVerificationAPI(SignInActivity.this, apiClient, uObj.get("id").getAsString());
                                                emailVerificationAPI.resendTheVerificationCode();
                                                alertPopUp.dismiss();
                                            }
                                        });
                                        alertPopUp.setUp("Please Verify Your Account", AlertPopUp.ERROR).show();
                                    } else {
                                        String error = "";
                                        for (String name : obj.keySet()) {
                                            error += name.toUpperCase() + ": " + obj.get(name).getAsString() + "\n";
                                        }
                                        alertPopUp.setUp(error, AlertPopUp.ERROR).show();
                                    }
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

            } else {
                alertPopUp.setUp("Please Fill Enter Your Details!!", AlertPopUp.ERROR).show();
            }
        });
        findViewById(R.id.register_text).setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
//            SignInActivity.this.finish();
        });
    }

    public void signIn() {
        GetSignInIntentRequest request = GetSignInIntentRequest.builder().setServerClientId(getString(R.string.web_client_id)).build();
        Task<PendingIntent> signInIntent = googleAuthAPI.getSignInClient().getSignInIntent(request);
        signInIntent.addOnSuccessListener(new OnSuccessListener<PendingIntent>() {
            @Override
            public void onSuccess(PendingIntent pendingIntent) {
                IntentSenderRequest senderRequest = new IntentSenderRequest.Builder(pendingIntent).build();
                signInLauncher.launch(senderRequest);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignInActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public final ActivityResultLauncher<IntentSenderRequest> signInLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            googleAuthAPI.handleSignInResult(result.getData());
        }
    });

}