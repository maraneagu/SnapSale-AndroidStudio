package com.example.snapsale.models;

import java.util.Map;

public class Basket {
    private String key;
    private String name;
    private String type;
    private String period;
    private Map<String, Sale> sales;

    Basket() {}

    public Basket(String name, String type, String period, Map<String, Sale> sales) {
        this.name = name;
        this.type = type;
        this.period = period;
        this.sales = sales;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Map<String, Sale> getSales() {
        return sales;
    }

    public void setSales(Map<String, Sale> sales) {
        this.sales = sales;
    }
}
