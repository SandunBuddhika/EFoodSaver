package com.sandun.efoodsaver.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.JsonObject;
import com.sandun.efoodsaver.HomeActivity;
import com.sandun.efoodsaver.R;
import com.sandun.efoodsaver.SignInActivity;
import com.sandun.efoodsaver.dao.UserDAO;
import com.sandun.efoodsaver.dto.User;
import com.sandun.efoodsaver.service.Auth;
import com.sandun.efoodsaver.util.AppDatabase;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleAuthAPI {
    private final static String TAG = GoogleAuthAPI.class.getName();
    private FirebaseAuth auth;
    private SignInClient signInClient;
    private AppCompatActivity context;

    public GoogleAuthAPI(AppCompatActivity context) {
        this.context = context;
        auth = FirebaseAuth.getInstance();
        signInClient = Identity.getSignInClient(context);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);
        Task<AuthResult> authResultTask = auth.signInWithCredential(authCredential);
        authResultTask.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                if (auth.getCurrentUser() != null) {
                    FirebaseUser user = auth.getCurrentUser();
                    Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:8080/EFoodSaverApi/api/user/").addConverterFactory(GsonConverterFactory.create()).build();
                    new Thread(() -> {
                        Auth authReq = retrofit.create(Auth.class);
                        String[] displayName = user.getDisplayName().split(" ", 2);
                        String fName = displayName[0];
                        String lName = displayName.length > 1 ? displayName[1] : "";
                        AlertPopUp alertPopUp = new AlertPopUp(context, AlertPopUp.NORMAL);
                        Call<JsonObject> req = authReq.auth(new User(fName, lName, user.getEmail(), user.getUid(), "true", user.getPhotoUrl().getPath(),"User"));
                        req.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                if (response.isSuccessful()) {
                                    JsonObject obj = response.body();
                                    if (obj != null && obj.size() > 0) {
                                        if (obj.get("response") != null && obj.get("response").getAsString().equals("Success")) {
                                            new Thread(() -> {
                                                AppDatabase database = new InternalDB<AppDatabase>(context.getApplicationContext(), AppDatabase.class).build();
                                                UserDAO userDAO = database.userDAO();
                                                com.sandun.efoodsaver.entities.User uObj = new com.sandun.efoodsaver.entities.User(obj.get("data").getAsJsonObject());
                                                try {
                                                    userDAO.delete(uObj);
                                                    userDAO.insert(uObj);
                                                    IsLogIn.user = uObj;
                                                    SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("auth", Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = preferences.edit();
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
                                                    dialog.dismiss();
                                                    Intent intent = new Intent(context, HomeActivity.class);
                                                    context.startActivity(intent);
                                                    context.finish();
                                                }
                                            });
                                            alertPopUp.show();
                                        } else {
                                            if (obj != null) {
                                                String error = "";
                                                for (String name : obj.keySet()) {
                                                    error += name.toUpperCase() + ": " + obj.get(name).getAsString() + "\n";
                                                }
                                                alertPopUp.setUp(error, AlertPopUp.ERROR).show();
                                            } else {
                                                alertPopUp.setUp("", AlertPopUp.ERROR).show();
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
                                Log.i(TAG, t.getMessage());
                                alertPopUp.setUp("Something Went Wrong!!", AlertPopUp.ERROR).show();
                            }
                        });
                    }).start();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Sign In Failed: " + e.getMessage());
            }
        });
    }

    public void handleSignInResult(Intent intent) {
        try {
            SignInCredential credential = signInClient.getSignInCredentialFromIntent(intent);
            String idToken = credential.getGoogleIdToken();
            firebaseAuthWithGoogle(idToken);
        } catch (ApiException e) {
            Log.e(TAG, "Sign In Failed: " + e.getMessage());
        }
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public void setAuth(FirebaseAuth auth) {
        this.auth = auth;
    }

    public SignInClient getSignInClient() {
        return signInClient;
    }

    public void setSignInClient(SignInClient signInClient) {
        this.signInClient = signInClient;
    }

    public AppCompatActivity getContext() {
        return context;
    }

    public void setContext(AppCompatActivity context) {
        this.context = context;
    }
}
