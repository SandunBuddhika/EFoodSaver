package com.sandun.efoodsaver.dto;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

import java.util.List;

public class CartDTO {
    @Exclude
    private long uId;

    public CartDTO() {
    }
    public long getuId() {
        return uId;
    }

    public void setuId(long uId) {
        this.uId = uId;
    }

}
