package com.sandun.efoodsaver.dto;

public class Review {
    private long id;
    private long uId;
    private int stars;
    private String message;

    public Review() {
    }

    public Review(long uId, int stars, String message) {
        this.uId = uId;
        this.stars = stars;
        this.message = message;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getuId() {
        return uId;
    }

    public void setuId(long uId) {
        this.uId = uId;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
