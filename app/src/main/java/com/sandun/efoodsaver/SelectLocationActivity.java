package com.sandun.efoodsaver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
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

public class SelectLocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 10;
    private Location currentLocation;
    private static final String TAG = SelectLocationActivity.class.getName();
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker locationPicker;
    private com.sandun.efoodsaver.entities.User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);
        IsLogIn isLogIn = new IsLogIn(this);
        if (IsLogIn.user == null) {
            isLogIn.toSignIn();
        } else {
            user = IsLogIn.user;
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button saveLocationBtn = findViewById(R.id.get_picked_location);
        saveLocationBtn.setOnClickListener(v -> {
            saveLocation();
        });
    }

    private void saveLocation() {
        if (locationPicker != null && user != null) {
            RequestClient<User> client = new RequestClient<>(User.class, "user/", getApplicationContext().getSharedPreferences("auth", Context.MODE_PRIVATE));
            User userService = client.createService();
            JsonObject requestBody = new JsonObject();
            LatLng location = locationPicker.getPosition();
            requestBody.addProperty("id", user.getId());
            requestBody.addProperty("latitude", location.latitude);
            requestBody.addProperty("longitude", location.longitude);
            Call<JsonObject> call = userService.setLocation(requestBody);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    WaitAlertPopUp waitAlertPopUp = new WaitAlertPopUp(getApplicationContext());
                    if (response.isSuccessful()) {
                        JsonObject obj = response.body();
                        JsonElement element = obj.get("response");
                        if (element != null && element.getAsString().equals("Success")) {
                            waitAlertPopUp.setUp("Success", WaitAlertPopUp.SUCCESS);
                            waitAlertPopUp.customButtonSetUp(new WaitAlertButtonManager() {
                                @Override
                                public void process(AlertDialog dialog, WaitAlertPopUp alertPopUp) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                        } else {
                            String error = "Something Went Wrong!";
                            if (element != null) {
                                error = element.getAsString();
                            }
                            waitAlertPopUp.setUp(error, WaitAlertPopUp.ERROR);
                            waitAlertPopUp.customButtonSetUp(new WaitAlertButtonManager() {
                                @Override
                                public void process(AlertDialog dialog, WaitAlertPopUp alertPopUp) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                        }
                        waitAlertPopUp.show();
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
                    Log.e(TAG, "Error " + t.getMessage());
                }
            });
        }
    }
    private void logOut() {
        IsLogIn isLogIn = new IsLogIn(this);
        isLogIn.toSignIn();
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        if (checkPermission()) {
            map.setMyLocationEnabled(true);
            getLastLocation();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                LatLng center = map.getCameraPosition().target;
                updateMarker(center);
            }
        });
    }

    private void updateMarker(LatLng latLng) {
        if (locationPicker != null) {
            locationPicker.setPosition(latLng);
        } else {
            locationPicker = map.addMarker(new MarkerOptions().position(latLng).title("Picked Location"));
        }
    }

    private boolean checkPermission() {
        boolean permission = false;
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            permission = true;
        }
        return permission;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);
                getLastLocation();
            } else {
                Snackbar.make(findViewById(R.id.container), "Location Permission Denied", Snackbar.LENGTH_INDEFINITE).
                        setAction("Setting", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }).show();
            }
        }
    }

    public void getLastLocation() {
        if (checkPermission()) {
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Log.i(TAG, "Setting My Location");
                        SelectLocationActivity.this.currentLocation = location;
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        System.out.println("working");
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13.0f));
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println(e);
                }
            });

        }
    }

}