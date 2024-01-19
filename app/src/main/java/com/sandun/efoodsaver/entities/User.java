package com.sandun.efoodsaver.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.JsonObject;

@Entity
public class User {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String fName;
    private String lName;
    private String emailOrCno;
    private String userType;
    private int status;

    public User() {

    }

    public User(JsonObject obj) {
        this.id = obj.get("id").getAsLong();
        this.fName = obj.get("fName").getAsString();
        this.lName = obj.get("lName").getAsString();
        this.emailOrCno = obj.get("emailOrCno").getAsString();
        this.userType = obj.get("userType").getAsString();
        this.status = obj.get("status").getAsInt();
    }

    public User(long id, String fName, String lName, String emailOrCno, String userType, int status) {
        this.id = id;
        this.fName = fName;
        this.lName = lName;
        this.emailOrCno = emailOrCno;
        this.userType = userType;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFName() {
        return fName;
    }

    public void setFName(String fName) {
        this.fName = fName;
    }

    public String getLName() {
        return lName;
    }

    public void setLName(String lName) {
        this.lName = lName;
    }

    public String getEmailOrCno() {
        return emailOrCno;
    }

    public void setEmailOrCno(String emailOrCno) {
        this.emailOrCno = emailOrCno;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}