package com.sandun.efoodsaver.model;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.sandun.efoodsaver.HomeActivity;
import com.sandun.efoodsaver.SignInActivity;
import com.sandun.efoodsaver.dao.UserDAO;
import com.sandun.efoodsaver.entities.User;
import com.sandun.efoodsaver.util.AppDatabase;

import java.util.List;

public class IsLogIn {
    private AppCompatActivity context;
    public static User user;
    private static AppDatabase database;

    public IsLogIn(AppCompatActivity context) {
        this.context = context;
        try {
            database = new InternalDB<AppDatabase>(context, AppDatabase.class).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        UserDAO userDAO = database.userDAO();
        List<User> list = userDAO.getAll();
        if (list != null && !list.isEmpty()) {
            user = list.get(0);
        } else {
            user = null;
        }
    }

    public boolean check() {
        Thread thread = new Thread(() -> {
            synchronized (this) {
                loadData();
                this.notify();
            }
        });
        try {
            synchronized (this) {
                thread.start();
                this.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return user != null;
    }

    public static User getUser() {
        return user;
    }

    public void toHome() {
        new Thread(() -> {
            Intent intent = new Intent(context, HomeActivity.class);
            context.startActivity(intent);
            context.finish();
        }).start();
    }

    public void toSignIn() {
        new Thread(() -> {
            synchronized (this) {
                database.userDAO().deleteAll();
                this.notify();
            }
        }).start();
        try {
            this.wait();
        } catch (Exception e) {
            e.printStackTrace();
        }
        synchronized (this) {
            Intent intent = new Intent(context, SignInActivity.class);
            context.startActivity(intent);
            context.finish();
        }
    }

    public void logInCheck() {
        if (getUser() == null) {
            toSignIn();
        }
    }

    public void deleteUser(DoProcess process) {
        new Thread(() -> {
            database.userDAO().deleteAll();
            process.process();
        }).start();
    }
}
