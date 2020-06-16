package com.example.viajerodelmundo;

import java.io.Serializable;

public class GreatDeals implements Serializable  {
    private String id;
    private String title;
    private String desc;
    private String price;
    private String location;
    private String imageurl;

    public GreatDeals(){}

    public GreatDeals(String title, String desc, String price, String location, String imageurl) {
        //this.id = id;
        this.title = title;
        this.desc = desc;
        this.price = price;
        this.location = location;
        this.imageurl = imageurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
