package com.walmart.gshop.models;

/**
 * Created by schatu2 on 4/2/16.
 */
public class Product {
    public String name;
    public String description;
    public String price;
    public int photoId;

    public Product(String name, String description, String age, int photoId) {
        this.name = name;
        this.description = description;
        this.price = age;
        this.photoId = photoId;
    }
}
