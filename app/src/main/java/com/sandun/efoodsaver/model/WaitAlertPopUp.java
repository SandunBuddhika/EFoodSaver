package com.sandun.efoodsaver.model;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieListener;
import com.airbnb.lottie.LottieTask;
import com.sandun.efoodsaver.R;

public class WaitAlertPopUp {
    private ConstraintLayout layout;
    private View view;
    private LottieAnimationView img;
    private TextView text;
    private Context context;

    private Button button;
    private AlertDialog dialog;
    public boolean isShow = false;
    private boolean isCustomButton;
    public final static int PROGRESS = 1;
    public final static int WAIT = 2;
    public final static int ERROR = 3;
    public final static int SUCCESS = 4;
    public final static int DISABLE_STATE = 1;
    public final static int ENABLE_STATE = 2;
    public final static int HIDE_STATE = 3;
    public final static int VISIBLE_STATE = 4;

    public WaitAlertPopUp(Context context) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.wait_alert, null);
        img = view.findViewById(R.id.alert_img);
        text = view.findViewById(R.id.alert_text);
        button = view.findViewById(R.id.ok_btn);
    }


    public WaitAlertPopUp setUp(String error) {
        new Handler(Looper.myLooper()).post(() -> {
            text.setText(error);
        });
        return this;
    }

    public WaitAlertPopUp setUp(String error, int icon) {
        new Handler(Looper.myLooper()).post(() -> {
            LottieTask<LottieComposition> composition = null;
            switch (icon) {
                case (WAIT): {
                    composition = LottieCompositionFactory.fromRawRes(context, R.raw.loading_anim);
                    break;
                }
                case (PROGRESS): {
                    composition = LottieCompositionFactory.fromRawRes(context, R.raw.loading_anim2);
                    break;
                }
                case (ERROR): {
                    composition = LottieCompositionFactory.fromRawRes(context, R.raw.error_anim);
                    break;
                }
                case (SUCCESS): {
                    composition = LottieCompositionFactory.fromRawRes(context, R.raw.success_anim);
                    break;
                }
            }
            text.setText(error);
            if (composition != null) {
                composition.addListener(new LottieListener<LottieComposition>() {
                    @Override
                    public void onResult(LottieComposition composition) {
                        img.setComposition(composition);
                    }
                });
            }
        });
        return this;
    }


    public void dismiss() {
        dialog.dismiss();
    }

    public void setIcon(int icon) {
        new Handler(Looper.myLooper()).post(() -> {
            LottieTask<LottieComposition> composition = null;
            switch (icon) {
                case (WAIT): {
                    composition = LottieCompositionFactory.fromRawRes(context, R.raw.loading_anim);
                    break;
                }
                case (PROGRESS): {
                    composition = LottieCompositionFactory.fromRawRes(context, R.raw.loading_anim2);
                    break;
                }
                case (ERROR): {
                    composition = LottieCompositionFactory.fromRawRes(context, R.raw.error_anim);
                    break;
                }
                case (SUCCESS): {
                    composition = LottieCompositionFactory.fromRawRes(context, R.raw.success_anim);
                    break;
                }
            }
            if (composition != null) {
                composition.addListener(new LottieListener<LottieComposition>() {
                    @Override
                    public void onResult(LottieComposition composition) {
                        img.setComposition(composition);
                    }
                });
            }
        });
    }

    public void changeButtonState(int state) {
        new Handler(Looper.myLooper()).post(() -> {
            int colorValue = 0;
            if (state == ENABLE_STATE) {
                colorValue = ContextCompat.getColor(context, R.color.success_green);
                button.setEnabled(true);
            } else if (state == DISABLE_STATE) {
                colorValue = ContextCompat.getColor(context, R.color.disable_color);
                button.setEnabled(false);
            } else if (state == HIDE_STATE) {
                button.setVisibility(View.GONE);
            } else if (state == VISIBLE_STATE) {
                button.setVisibility(View.VISIBLE);
            }
            if (colorValue != 0) {
                ColorStateList colorStateList = ColorStateList.valueOf(colorValue);
                button.setBackgroundTintList(colorStateList);
            }
        });
    }

    public void customButtonSetUp(WaitAlertButtonManager manager) {
        button.setOnClickListener(v -> {
            manager.process(dialog, this);
        });
        isCustomButton = true;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        if (!isShow) {
            dialog = builder.create();
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            if (!isCustomButton) {
                button.setOnClickListener(v -> {
                    dialog.dismiss();
                });
            }
        }
        dialog.setCancelable(false);
        dialog.show();
        img.animate();
        isShow = true;
    }


    public AlertDialog getDialog() {
        return dialog;
    }

    public TextView getText() {
        return text;
    }

    public Button getButton() {
        return button;
    }

}
