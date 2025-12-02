package com.example.snapsale.database.repositories;

import com.example.snapsale.database.managers.FirebaseManager;
import com.example.snapsale.models.FavoredSale;
import com.example.snapsale.network.requests.RecipesRequest;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;


// HELPFUL LINKS:
// 1). "Store, Retrieve, Search, Delete and Update Data using Firebase Realtime Database in Android Studio", Android Knowledge, URL: https://www.youtube.com/watch?v=DWIGAkYkpg8&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=13&t=2s&ab_channel=AndroidKnowledge


public class RecipesRepository {
    private static RecipesRepository instance;
    private final DatabaseReference favoredSalesReference;

    private RecipesRepository() {
        favoredSalesReference = FirebaseManager.getInstance().getFavoredSalesReference();
    }

    public static synchronized RecipesRepository getInstance() {
        if (instance == null) {
            instance = new RecipesRepository();
        }
        return instance;
    }

    public void addRecipe(String storeKey, String categoryKey, FavoredSale favoredSale) throws JSONException {
        RecipesRequest recipesRequest = RecipesRequest.getInstance();
        recipesRequest.request(favoredSale, (recipe) -> favoredSalesReference.child(storeKey).child("categories").child(categoryKey).child("sales").child(favoredSale.getKey()).child("recipe").setValue(recipe));
    }
}
