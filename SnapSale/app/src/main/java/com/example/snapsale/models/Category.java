package com.example.snapsale.models;

import java.util.HashMap;
import java.util.Map;

public abstract class Category {
    private String key;
    private String name;
    private String period;

    public Category() {}

    public Category(String name) {
        this.name = name;
    }

    public Category(String name, String period) {
        this.name = name;
        this.period = period;
    }

    public Category(String key, String name, String period) {
        this.key = key;
        this.name = name;
        this.period = period;
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

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getPeriod() {
        return period;
    }
}
