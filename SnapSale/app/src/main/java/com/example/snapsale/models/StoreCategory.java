package com.example.snapsale.models;

import java.util.HashMap;
import java.util.Map;

public class StoreCategory extends Category {
    private Map<String, Sale> sales;

    public StoreCategory() {}

    public StoreCategory(String name) {
        super(name);
        this.sales = new HashMap<>();
    }

    public StoreCategory(String name, String period, Map<String, Sale> sales) {
        super(name, period);
        this.sales = sales;
    }

    public Map<String, Sale> getSales() {
        return sales;
    }

    public void setSales(Map<String, Sale> sales) {
        this.sales = sales;
    }
}
