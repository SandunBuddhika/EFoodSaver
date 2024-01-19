package com.sandun.efoodsaver.dto;

public class User {
    private String fName;
    private String lName;
    private String emailOrCno;
    private String password;
    private String isGoogle ="false";
    private String type;
    private String img;

    public User() {
    }

    public User(String emailOrCno, String password, String type) {
        this.emailOrCno = emailOrCno;
        this.password = password;
        this.type = type;
    }

    public User(String fName, String lName, String emailOrCno, String password, String isGoogle, String type) {
        this.fName = fName;
        this.lName = lName;
        this.emailOrCno = emailOrCno;
        this.type = type;
        this.password = password;
        this.isGoogle = isGoogle;
    }

    public User(String fName, String lName, String emailOrCno, String password, String isGoogle, String img, String type) {
        this.fName = fName;
        this.lName = lName;
        this.emailOrCno = emailOrCno;
        this.type = type;
        this.password = password;
        this.isGoogle = isGoogle;
        this.img = img;
    }


    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getEmailOrCno() {
        return emailOrCno;
    }

    public void setEmailOrCno(String emailOrCno) {
        this.emailOrCno = emailOrCno;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIsGoogle() {
        return isGoogle;
    }

    public void setIsGoogle(String isGoogle) {
        this.isGoogle = isGoogle;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
