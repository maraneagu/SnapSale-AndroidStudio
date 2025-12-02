package com.example.snapsale.models;

import java.util.Map;

public class FavoredCategory extends Category {
    private Map<String, FavoredSale> sales;

    public FavoredCategory() {}

    public FavoredCategory(String key, String name, String period) {
        super(key, name, period);
    }

    public Map<String, FavoredSale> getSales() {
        return sales;
    }

    public void setSales(Map<String, FavoredSale> sales) {
        this.sales = sales;
    }
}
