package com.sandun.efoodsaver.dto;

import com.google.firebase.firestore.Exclude;

public class InvoiceDTO {
    @Exclude
    private String id;
    private String uId;
    private double total;

    public InvoiceDTO(String uId, double total) {
        this.uId = uId;
        this.total = total;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
