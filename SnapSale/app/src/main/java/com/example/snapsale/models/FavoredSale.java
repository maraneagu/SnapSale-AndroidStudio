package com.example.snapsale.models;

public class FavoredSale extends Sale {
    private long favoredTimestamp;
    private Recipe recipe;

    public FavoredSale() {}
    public FavoredSale(String key, String store, String category, String imageSrc, String title, String newPrice, String period) {
        super(key, store, category, imageSrc, title, newPrice, period);
    }

    public long getFavoredTimestamp() {
        return favoredTimestamp;
    }

    public void setFavoredTimestamp(long favoredTimestamp) {
        this.favoredTimestamp = favoredTimestamp;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
}
