package com.haguepharmacy.model;

import io.realm.RealmObject;

public class ProductInfo extends RealmObject {

    private String id;
    private String name;
    private String price;
    private String Quantity;
    private String Total_price;

    public ProductInfo() {

    }

    public ProductInfo(String id, String name, String price) {
        this.id = id;
        this.name = name;
        this.price = price;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getTotal_price() {
        return Total_price;
    }

    public void setTotal_price(String total_price) {
        Total_price = total_price;
    }

    @Override
    public String toString() {
        return name ;
    }
}
