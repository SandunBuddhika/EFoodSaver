package com.sandun.efoodsaver.model;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.sandun.efoodsaver.HomeActivity;
import com.sandun.efoodsaver.SignInActivity;

import java.util.concurrent.TimeUnit;

public class MobileAuthAPI {
    private static final String TAG = MobileAuthAPI.class.getName();
    private FirebaseAuth auth;
    private String verificationCode;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private AppCompatActivity context;
    AlertPopUp alertPopUp;
    public static int tryCount;

    public MobileAuthAPI(AppCompatActivity context) {
        auth = FirebaseAuth.getInstance();
        this.context = context;
        this.callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.i(TAG, "OnVerificationCompleted: " + phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.e(TAG, "OnVerificationFailed: " + e.getMessage());
            }

            @Override
            public void onCodeSent(@NonNull String verificationCode, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                Log.i(TAG, "OnCodeSend: " + verificationCode);
                MobileAuthAPI.this.verificationCode = verificationCode;
                MobileAuthAPI.this.resendingToken = forceResendingToken;
                Log.i(TAG, verificationCode);
                alertPopUp.changeButtonState(AlertPopUp.ENABLE_STATE);
            }
        };
    }

    public void signInWithPhone(String phoneNo) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth).setPhoneNumber("+94" + phoneNo).setTimeout(16L, TimeUnit.SECONDS).setActivity(context).setCallbacks(callbacks).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        alertPopUp = new AlertPopUp(context, AlertPopUp.OTP);
        alertPopUp.customButtonSetUp(new AlertButtonManager() {
            @Override
            public void process(AlertDialog dialog, AlertPopUp alertPopUp) {
                MobileAuthAPI.this.verifyOtp(alertPopUp.getCode().getText().toString());
            }
        });
        alertPopUp.show();
    }

    public void verifyOtp(String otp) {
        tryCount++;
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);
        signInWithPhoneAuth(credential);
    }


    private void signInWithPhoneAuth(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    MobileAuthAPI.this.alertPopUp.getDialog().dismiss();
                    updateUi(task.getResult().getUser());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (MobileAuthAPI.tryCount > 5) {
                    alertPopUp.getDialog().dismiss();
                    AlertPopUp secondAlert = new AlertPopUp(MobileAuthAPI.this.context, AlertPopUp.NORMAL).setUp("You Has The OTP Request Limit, Wait 5m Before Try Again", AlertPopUp.ERROR);
                    secondAlert.show();
                    new Thread(() -> {
                        try {
                            Thread.sleep((1000 * 5));
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        } finally {
                            MobileAuthAPI.tryCount = 0;
                        }
                    });
                } else {
                    AlertPopUp secondAlert = new AlertPopUp(MobileAuthAPI.this.context, AlertPopUp.NORMAL).setUp("OTP Code Is Invalid, Try Again!!", AlertPopUp.ERROR);
                    secondAlert.show();
                }
            }
        });
    }

    private void updateUi(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(context, SignInActivity.class);
            context.startActivity(intent);
            context.finish();
        }
    }
}
