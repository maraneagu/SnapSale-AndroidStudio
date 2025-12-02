package com.example.snapsale.models;

public class Sale {
    private String key;
    private String store;
    private String category;
    private String image;
    private String title;
    private String subtitle = null;
    private String quantity = null;
    private String discount = null;
    private String oldPrice = null;
    private String newPrice;
    private String period;


    public Sale() {}
    public Sale(String store, String category, String imageSrc, String title, String newPrice, String period) {
        this.store = store;
        this.category = category;
        this.image = imageSrc;
        this.title = title;
        this.newPrice = newPrice;
        this.period = period;
    }

    public Sale(String key, String store, String category, String imageSrc, String title, String newPrice, String period) {
        this.key = key;
        this.store = store;
        this.category = category;
        this.image = imageSrc;
        this.title = title;
        this.newPrice = newPrice;
        this.period = period;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getNewPrice() {
        return newPrice;
    }
    public String getPeriod() {
        return period;
    }

    public String getSubtitle() {
        return subtitle;
    }
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getQuantity() {
        return quantity;
    }
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDiscount() {
        return discount;
    }
    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
    }
}
