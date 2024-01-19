package com.sandun.efoodsaver.model;

import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.sandun.efoodsaver.HomeActivity;
import com.sandun.efoodsaver.SignInActivity;
import com.sandun.efoodsaver.service.Auth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailVerificationAPI {
    private static final String TAG = EmailVerificationAPI.class.getName();
    private String uId;
    public static int tryCount;
    private AlertPopUp alertPopUp;
    private Auth auth;
    private AppCompatActivity context;

    public EmailVerificationAPI(AppCompatActivity context, Auth auth, String uId) {
        this.context = context;
        this.auth = auth;
        this.uId = uId;
        createAlert();
        alertPopUp.show();
    }

    public void createAlert() {
        alertPopUp = new AlertPopUp(context, AlertPopUp.OTP);
        alertPopUp.customResendButtonSetUp(new AlertButtonManager() {
            @Override
            public void process(AlertDialog dialog, AlertPopUp alertPopUp) {
                resendTheVerificationCode();
            }
        });
        checkTheVerification();
    }

    public void checkTheVerification() {
        JsonObject vf = new JsonObject();
        vf.addProperty("id", uId);
        alertPopUp.customButtonSetUp(new AlertButtonManager() {
            @Override
            public void process(AlertDialog dialog, AlertPopUp alertPopUp) {
                Call<JsonObject> call = auth.verification(vf);
                vf.addProperty("code", alertPopUp.getCode().getText().toString());
                EmailVerificationAPI.this.alertPopUp.dismiss();
                AlertPopUp errorAlert = new AlertPopUp(context, AlertPopUp.NORMAL);
                errorAlert.customButtonSetUp(new AlertButtonManager() {
                    @Override
                    public void process(AlertDialog dialog, AlertPopUp alertPopUp) {
                        errorAlert.dismiss();
                        createAlert();
                        EmailVerificationAPI.this.alertPopUp.show();
                    }
                });
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Log.i(TAG, response.message());
                        if (response.isSuccessful()) {
                            JsonObject obj = response.body();
                            if (obj != null && obj.size() > 0) {
                                if (obj.get("response") != null && obj.get("response").getAsString().equals("Success")) {
                                    errorAlert.customButtonSetUp(new AlertButtonManager() {
                                        @Override
                                        public void process(AlertDialog dialog, AlertPopUp alertPopUp) {
                                            errorAlert.dismiss();
                                            Intent intent = new Intent(context, SignInActivity.class);
                                            context.startActivity(intent);
                                            context.finish();
                                        }
                                    });
                                    errorAlert.show();
                                } else {
                                    String error = "";
                                    for (String name : obj.keySet()) {
                                        error += name.toUpperCase() + ": " + obj.get(name).getAsString() + "\n";
                                    }
                                    errorAlert.setUp(error, AlertPopUp.ERROR).show();
                                }
                            } else {
                                errorAlert.setUp("Something Went Wrong!!", AlertPopUp.ERROR).show();
                            }
                        } else {
                            errorAlert.setUp("Something Went Wrong!!", AlertPopUp.ERROR).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.i(TAG, t.getMessage());
                        errorAlert.setUp("Something Went Wrong!!", AlertPopUp.ERROR).show();
                    }
                });
            }
        });
        tryCount++;
    }

    public void resendTheVerificationCode() {
        if (tryCount < 5) {
            AlertPopUp errorAlert = new AlertPopUp(context, AlertPopUp.NORMAL);
            alertPopUp.changeResendButtonState(AlertPopUp.DISABLE_STATE);
            JsonObject vf = new JsonObject();
            vf.addProperty("id", uId);
            Call<JsonObject> call = auth.resendVerificationCode(vf);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    alertPopUp.changeResendButtonState(AlertPopUp.ENABLE_STATE);
                    alertPopUp.getText().setText("New OTP Sended To Your Email");
                    Log.i(TAG, "OTP SEND...");
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    alertPopUp.changeResendButtonState(AlertPopUp.ENABLE_STATE);
                    alertPopUp.dismiss();
                    errorAlert.setUp("Something Went Wrong While Sending the OTP", AlertPopUp.ERROR);
                    errorAlert.show();
                }
            });
            tryCount++;
        } else {
            if (alertPopUp != null) {
                alertPopUp.dismiss();
            }
            AlertPopUp errorAlert = new AlertPopUp(context, AlertPopUp.NORMAL).setUp("You has reached the limit of the otp requests, Please Try again in 5 minutes!", AlertPopUp.ERROR);
            errorAlert.show();
            resetCount();
        }
    }

    private void resetCount() {
        new Thread(() -> {
            try {
                Thread.sleep((1000 * 60 * 5));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                tryCount = 0;
            }
        }).start();
    }

    public AlertPopUp getAlertPopUp() {
        return alertPopUp;
    }

    public void setAlertPopUp(AlertPopUp alertPopUp) {
        this.alertPopUp = alertPopUp;
    }
}
