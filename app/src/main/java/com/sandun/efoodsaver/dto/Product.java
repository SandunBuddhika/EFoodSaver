package com.sandun.efoodsaver.dto;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    @Exclude
    private String id;
    private String name;
    private String description;
    private double price;
    private int discount;
    private int qty;
    private String status;
    private long uId;
    private String category;
    private String type;
    private String expire_date;
    private String manufacture_date;
    private String created_date;
    private String updated_date;
    private List<String> imgList;
    private List<Review> reviewList;


    public Product() {
    }

    public Product(String name, String description, double price, int discount, int qty, String category, String type, String expire_date, String manufacture_date) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.discount = discount;
        this.qty = qty;
        this.status = status;
        this.uId = uId;
        this.category = category;
        this.type = type;
        this.expire_date = expire_date;
        this.manufacture_date = manufacture_date;
        this.created_date = created_date;
        this.updated_date = updated_date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getuId() {
        return uId;
    }

    public void setuId(long uId) {
        this.uId = uId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExpire_date() {
        return expire_date;
    }

    public void setExpire_date(String expire_date) {
        this.expire_date = expire_date;
    }

    public String getManufacture_date() {
        return manufacture_date;
    }

    public void setManufacture_date(String manufacture_date) {
        this.manufacture_date = manufacture_date;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(String updated_date) {
        this.updated_date = updated_date;
    }

    public List<String> getImgList() {
        return imgList;
    }

    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
    }

    public List<Review> getReviewList() {
        return reviewList;
    }

    public void setReviewList(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", discount=" + discount +
                ", qty=" + qty +
                ", status='" + status + '\'' +
                ", uId=" + uId +
                ", category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", expire_date='" + expire_date + '\'' +
                ", manufacture_date='" + manufacture_date + '\'' +
                ", created_date='" + created_date + '\'' +
                ", updated_date='" + updated_date + '\'' +
                ", imgList=" + imgList +
                ", reviewList=" + reviewList +
                '}';
    }
}
