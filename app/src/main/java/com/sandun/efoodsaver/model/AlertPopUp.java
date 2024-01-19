package com.sandun.efoodsaver.model;

import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieListener;
import com.airbnb.lottie.LottieTask;
import com.sandun.efoodsaver.R;

public class AlertPopUp {
    private ConstraintLayout layout;
    private View view;
    private Button button;
    private Button resendButton;
    private LottieAnimationView img;
    private EditText code;
    private TextView text;
    private AppCompatActivity context;
    private AlertDialog dialog;
    private boolean isCustomButton;
    public final static int ERROR = 1;
    public final static int SUCCESS = 2;
    public final static int WAITING = 3;
    public final static int OTP = 1;
    public final static int NORMAL = 2;
    public final static int DISABLE_STATE = 1;
    public final static int ENABLE_STATE = 2;

    public AlertPopUp(AppCompatActivity context) {
        this.context = context;
    }

    public AlertPopUp(AppCompatActivity context, int alertType) {
        this.context = context;
        if (alertType == OTP) {
            layout = context.findViewById(R.id.constraintLayout2);
            view = LayoutInflater.from(context).inflate(R.layout.otp_code_alert, layout);
            button = view.findViewById(R.id.success_buttom);
            resendButton = view.findViewById(R.id.resend_button);
            img = view.findViewById(R.id.alert_img);
            text = view.findViewById(R.id.alert_text);
            code = view.findViewById(R.id.otp_input);
        } else if (alertType == NORMAL) {
            layout = context.findViewById(R.id.constraintLayout);
            view = LayoutInflater.from(context).inflate(R.layout.success_alert, layout);
            button = view.findViewById(R.id.success_buttom);
            img = view.findViewById(R.id.alert_img);
            text = view.findViewById(R.id.alert_text);
        }
    }


    public AlertPopUp setUp(String error) {
        new Handler(Looper.myLooper()).post(()->{
            text.setText(error);
        });
        return this;
    }

    public AlertPopUp setUp(String error, int icon) {
        int colorValue = ContextCompat.getColor(context, R.color.success_green);
        LottieTask<LottieComposition> composition = null;
        switch (icon) {
            case (ERROR): {
                colorValue = ContextCompat.getColor(context, R.color.error_red);
                composition = LottieCompositionFactory.fromRawRes(context, R.raw.error_anim);
                break;
            }
            case (SUCCESS): {
                colorValue = ContextCompat.getColor(context, R.color.success_green);
                composition = LottieCompositionFactory.fromRawRes(context, R.raw.success_anim);
                break;
            }
            case (WAITING): {
                colorValue = ContextCompat.getColor(context, R.color.wating_green);
                composition = LottieCompositionFactory.fromRawRes(context, R.raw.waiting_anim);
                break;
            }
        }
        ColorStateList colorStateList = ColorStateList.valueOf(colorValue);
        button.setBackgroundTintList(colorStateList);
        text.setText(error);
        if (composition != null) {
            composition.addListener(new LottieListener<LottieComposition>() {
                @Override
                public void onResult(LottieComposition composition) {
                    img.setComposition(composition);
                }
            });
        }
        return this;
    }

    public void changeButtonState(int state) {
        int colorValue = 0;
        if (state == ENABLE_STATE) {
            colorValue = ContextCompat.getColor(context, R.color.success_green);
            button.setEnabled(true);
        } else if (state == DISABLE_STATE) {
            colorValue = ContextCompat.getColor(context, R.color.disable_color);
            button.setEnabled(false);
        }
        if (colorValue != 0) {
            ColorStateList colorStateList = ColorStateList.valueOf(colorValue);
            button.setBackgroundTintList(colorStateList);
        }
    }

    public void changeResendButtonState(int state) {
        int colorValue = 0;
        if (state == ENABLE_STATE) {
            colorValue = ContextCompat.getColor(context, R.color.resend_btn);
            resendButton.setEnabled(true);
        } else if (state == DISABLE_STATE) {
            colorValue = ContextCompat.getColor(context, R.color.disable_color);
            resendButton.setEnabled(false);
        }
        if (colorValue != 0) {
            ColorStateList colorStateList = ColorStateList.valueOf(colorValue);
            resendButton.setBackgroundTintList(colorStateList);
        }
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public void setIcon(int icon) {
        context.runOnUiThread(() -> {
            int colorValue = ContextCompat.getColor(context, R.color.success_green);
            LottieTask<LottieComposition> composition = null;
            switch (icon) {
                case (ERROR): {
                    colorValue = ContextCompat.getColor(context, R.color.error_red);
                    composition = LottieCompositionFactory.fromRawRes(context, R.raw.error_anim);
                    break;
                }
                case (SUCCESS): {
                    colorValue = ContextCompat.getColor(context, R.color.success_green);
                    composition = LottieCompositionFactory.fromRawRes(context, R.raw.success_anim);
                    break;
                }
                case (WAITING): {
                    colorValue = ContextCompat.getColor(context, R.color.wating_green);
                    composition = LottieCompositionFactory.fromRawRes(context, R.raw.waiting_anim);
                    break;
                }
            }
            ColorStateList colorStateList = ColorStateList.valueOf(colorValue);
            button.setBackgroundTintList(colorStateList);
        });
    }

    private void buttonSetUp(AlertDialog dialog) {
        context.runOnUiThread(() -> {
            button.setOnClickListener(v -> {
                dialog.dismiss();
            });
        });
    }

    public void customButtonSetUp(AlertButtonManager manager) {
        button.setOnClickListener(v -> {
            manager.process(dialog, this);
        });
        isCustomButton = true;
    }

    public void customResendButtonSetUp(AlertButtonManager manager) {
        resendButton.setOnClickListener(v -> {
            manager.process(dialog, this);
        });
        isCustomButton = true;
    }

    public void customSetUp(AlertManager alertManager) {
        alertManager.display(context, view);
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        img.animate();
        dialog = builder.create();
        if (!isCustomButton) {
            buttonSetUp(dialog);
        }
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
    }

    public EditText getCode() {
        return code;
    }

    public AlertDialog getDialog() {
        return dialog;
    }


    public Button getButton() {
        return button;
    }


    public TextView getText() {
        return text;
    }

    public Button getResendButton() {
        return resendButton;
    }

}
