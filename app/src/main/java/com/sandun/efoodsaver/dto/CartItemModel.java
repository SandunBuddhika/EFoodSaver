package com.sandun.efoodsaver.dto;

public class CartItemModel {
    private Product product;
    private int count;
    private int index;

    public CartItemModel(Product product, int count, int index) {
        this.product = product;
        this.count = count;
        this.index = index;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
