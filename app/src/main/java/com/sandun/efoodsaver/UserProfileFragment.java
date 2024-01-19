package com.sandun.efoodsaver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.play.core.integrity.e;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sandun.efoodsaver.model.IsLogIn;
import com.sandun.efoodsaver.model.RequestClient;
import com.sandun.efoodsaver.model.WaitAlertButtonManager;
import com.sandun.efoodsaver.model.WaitAlertPopUp;
import com.sandun.efoodsaver.service.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileFragment extends Fragment {
    private static final String TAG = UserProfileFragment.class.getName();
    private com.sandun.efoodsaver.entities.User user;
    private View view;
    private EditText fName;
    private EditText lName;
    private EditText emOrCno;
    private EditText addLine1;
    private EditText addLine2;
    private EditText city;
    private EditText district;
    private EditText province;
    private RequestClient<User> client;
    private
    WaitAlertPopUp waitAlertPopUp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        if (IsLogIn.user != null) {
            client = new RequestClient<>(User.class, "user/", getActivity().getApplicationContext().getSharedPreferences("auth", Context.MODE_PRIVATE));
            waitAlertPopUp = new WaitAlertPopUp(getContext());
            waitAlertPopUp.changeButtonState(WaitAlertPopUp.VISIBLE_STATE);

            Button button = view.findViewById(R.id.user_update_btn);
            button.setOnClickListener(v -> {
                updateUser();
            });
            ImageView button2 = view.findViewById(R.id.log_out);
            button2.setOnClickListener(v -> {
                logOut();
            });
            loadUserData();
        } else {
            IsLogIn isLogIn = new IsLogIn((HomeActivity) getActivity());
            isLogIn.toSignIn();
        }
        return view;
    }


    private void updateUser() {
        if (validate()) {
            User service = client.createService();
            JsonObject obj = new JsonObject();
            obj.addProperty("id", user.getId());
            obj.addProperty("fName", fName.getText().toString());
            obj.addProperty("lName", lName.getText().toString());
            obj.addProperty("addLine1", addLine1.getText().toString());
            obj.addProperty("addLine2", addLine2.getText().toString());
            obj.addProperty("city", city.getText().toString());
            obj.addProperty("district", district.getText().toString());
            obj.addProperty("province", province.getText().toString());
            Call<JsonObject> call = service.updateUser(obj);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        loadUserData();
                    } else if (response.code() == 401) {
                        waitAlertPopUp.setUp("Please Log In First, Your Are Un Authorized..");
                        waitAlertPopUp.customButtonSetUp(new WaitAlertButtonManager() {
                            @Override
                            public void process(AlertDialog dialog, WaitAlertPopUp alertPopUp) {
                                dialog.dismiss();
                                logOut();
                            }
                        });
                        waitAlertPopUp.show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    waitAlertPopUp.setUp("Something Went Wrong, Please Try Again Later!");
                }
            });
        }
    }

    private boolean validate() {
        boolean isErrorFound = false;
        if (fName.getText().toString().isEmpty()) {
            waitAlertPopUp.setUp("Please Enter Your First Name", WaitAlertPopUp.ERROR);
            isErrorFound = true;
        } else if (lName.getText().toString().isEmpty()) {
            waitAlertPopUp.setUp("Please Enter Your Last Name", WaitAlertPopUp.ERROR);
            isErrorFound = true;
        } else if (addLine1.getText().toString().isEmpty()) {
            waitAlertPopUp.setUp("Please Enter Your Address Line 1", WaitAlertPopUp.ERROR);
            isErrorFound = true;
        } else if (city.getText().toString().isEmpty()) {
            waitAlertPopUp.setUp("Please Type And Select Your City", WaitAlertPopUp.ERROR);
            isErrorFound = true;
        } else if (district.getText().toString().isEmpty()) {
            waitAlertPopUp.setUp("Please Type And Select Your District", WaitAlertPopUp.ERROR);
            isErrorFound = true;
        } else if (province.getText().toString().isEmpty()) {
            waitAlertPopUp.setUp("Please Type And Select Your Province", WaitAlertPopUp.ERROR);
            isErrorFound = true;
        }
        if (isErrorFound) {
            waitAlertPopUp.show();
        }
        return !isErrorFound;
    }

    private void loadUserData() {
        user = IsLogIn.getUser();
        if (user != null && user.getId() != 0) {
            User service = client.createService();
            JsonObject object = new JsonObject();
            object.addProperty("id", String.valueOf(user.getId()));
            Call<JsonObject> call = service.getUserDetails(object);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    System.out.println(response.code());
                    if (response.isSuccessful()) {
                        System.out.println(response.code());
                        fName = view.findViewById(R.id.f_name);
                        lName = view.findViewById(R.id.l_name);
                        emOrCno = view.findViewById(R.id.em_or_cno);
                        addLine1 = view.findViewById(R.id.add_line_1);
                        addLine2 = view.findViewById(R.id.add_line_2);
                        city = view.findViewById(R.id.city);
                        district = view.findViewById(R.id.district);
                        province = view.findViewById(R.id.province);
                        System.out.println(response.code());
                        JsonObject obj = response.body();
                        if (obj != null) {
                            System.out.println(obj);
                            JsonObject data = obj.getAsJsonObject("data");
                            if (obj.get("response") != null && data != null && obj.get("response").getAsString().equals("Success")) {
                                String fNameText = data.get("fName").getAsString();
                                String lNameText = data.get("lName").getAsString();
                                String emOrCnoText = data.get("em_or_cno").getAsString();
                                if (!fNameText.isEmpty() && !lNameText.isEmpty() && !emOrCnoText.isEmpty()) {
                                    fName.setText(fNameText);
                                    lName.setText(lNameText);
                                    emOrCno.setText(emOrCnoText);
                                }
                                JsonElement addLine1Text = data.get("add_line_1");
                                JsonElement addLine2Text = data.get("add_line_2");
                                JsonElement cityText = data.get("city");
                                JsonElement districtText = data.get("district");
                                JsonElement provinceText = data.get("province");
                                if (addLine1Text != null && !addLine1Text.getAsString().isEmpty()) {
                                    addLine1.setText(addLine1Text.getAsString());
                                }
                                if (addLine2Text != null && !addLine2Text.getAsString().isEmpty()) {
                                    addLine2.setText(addLine2Text.getAsString());
                                }
                                if (cityText != null && !cityText.getAsString().isEmpty()) {
                                    city.setText(cityText.getAsString());
                                }
                                if (districtText != null && !districtText.getAsString().isEmpty()) {
                                    district.setText(districtText.getAsString());
                                }
                                if (provinceText != null && !provinceText.getAsString().isEmpty()) {
                                    province.setText(provinceText.getAsString());
                                }
                            } else {
                                logOut();
                            }
                        }
                    } else if (response.code() == 401) {
                        waitAlertPopUp.setUp("Please Log In First, Your Are Un Authorized..");
                        waitAlertPopUp.customButtonSetUp(new WaitAlertButtonManager() {
                            @Override
                            public void process(AlertDialog dialog, WaitAlertPopUp alertPopUp) {
                                dialog.dismiss();
                                logOut();
                            }
                        });
                        waitAlertPopUp.show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e(TAG, "Error : " + t.getMessage());
                }
            });
        } else {

        }
    }

    private void logOut() {
        HomeActivity activity = (HomeActivity) getActivity();
        IsLogIn isLogIn = new IsLogIn(activity);
        isLogIn.toSignIn();
    }

}