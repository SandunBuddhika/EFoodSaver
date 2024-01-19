package com.sandun.efoodsaver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.sandun.efoodsaver.model.IsLogIn;

import java.io.Serializable;

import io.ak1.BubbleTabBar;
import io.ak1.OnBubbleClickListener;

public class HomeActivity extends AppCompatActivity implements Serializable {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar materialToolbar;
    private BubbleTabBar bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigation_view);
        materialToolbar = findViewById(R.id.materialToolbar);
        bottomNavigationView = findViewById(R.id.bubbleTabBar);
        IsLogIn isLogIn = new IsLogIn(this);
        isLogIn.check();
        int cart = bottomNavigationView.findViewById(R.id.b_nav_cart).getId();
        int home = bottomNavigationView.findViewById(R.id.b_nav_home).getId();
        int search = bottomNavigationView.findViewById(R.id.b_nav_search).getId();
        int profile = bottomNavigationView.findViewById(R.id.b_nav_profile).getId();
        bottomNavigationView.addBubbleListener(new OnBubbleClickListener() {
            @Override
            public void onBubbleClick(int i) {
                if (i == search) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, SearchFragment.class, null).setReorderingAllowed(true).addToBackStack("search").commit();
                } else if (i == home) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment.class, null).setReorderingAllowed(true).addToBackStack("home").commit();
                } else if (i == cart) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, CartFragment.class, null).setReorderingAllowed(true).addToBackStack("cart").commit();
                } else if (i == profile) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, UserProfileFragment.class, null).setReorderingAllowed(true).addToBackStack("profile").commit();
                }
            }
        });
        MenuItem logIn = navigationView.getMenu().getItem(3);
        MenuItem logOut = navigationView.getMenu().getItem(4);
        if (IsLogIn.user == null) {
            logIn.setVisible(true);
            logOut.setVisible(false);
        } else {
            logIn.setVisible(false);
            logOut.setVisible(true);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.side_nav_search) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, SearchFragment.class, null).setReorderingAllowed(true).addToBackStack("search").commit();
                } else if (i == R.id.side_nav_home) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment.class, null).setReorderingAllowed(true).addToBackStack("home").commit();
                } else if (i == R.id.side_nav_profile) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, UserProfileFragment.class, null).setReorderingAllowed(true).addToBackStack("profile").commit();
                } else if (i == R.id.side_nav_logout) {
                    logOut();
                } else if (i == R.id.side_nav_login) {
                    IsLogIn isLogIn = new IsLogIn(HomeActivity.this);
                    isLogIn.toSignIn();
                }
                drawerLayout.close();

                return true;
            }
        });

        setSupportActionBar(materialToolbar);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(HomeActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        materialToolbar.setNavigationOnClickListener(v -> {
            drawerLayout.open();
        });
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment.class, null).setReorderingAllowed(true).addToBackStack("home").commit();

    }

    private void logOut() {
        IsLogIn isLogIn = new IsLogIn(this);
        isLogIn.toSignIn();
    }
}