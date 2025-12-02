package com.example.snapsale.models;

import java.util.HashMap;
import java.util.Map;

public class Store {
    private String key;
    private String name;
    private Map<String, StoreCategory> categories;

    public Store() {}

    public Store(String key, String name) {
        this.key = key;
        this.name = name;
        this.categories = new HashMap<>();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, StoreCategory> getCategories() {
        return categories;
    }

    public void setCategories(Map<String, StoreCategory> categories) {
        this.categories = categories;
    }
}
