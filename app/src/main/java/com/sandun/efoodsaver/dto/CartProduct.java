package com.sandun.efoodsaver.dto;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

public class CartProduct {
    @Exclude
    private String id;
    private int count;
    private DocumentReference product;

    public CartProduct() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CartProduct(int count, DocumentReference product) {
        this.count = count;
        this.product = product;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public DocumentReference getProduct() {
        return product;
    }

    public void setProduct(DocumentReference product) {
        this.product = product;
    }
}
