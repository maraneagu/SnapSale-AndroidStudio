package com.example.snapsale.database.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.snapsale.callbacks.StoreCategoryCallback;
import com.example.snapsale.database.managers.FirebaseManager;
import com.example.snapsale.models.FavoredCategory;
import com.example.snapsale.models.Store;
import com.example.snapsale.network.scrapers.CarrefourScraper;
import com.example.snapsale.network.scrapers.KauflandScraper;
import com.example.snapsale.network.scrapers.LidlScraper;
import com.example.snapsale.network.scrapers.PennyScraper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


// HELPFUL LINKS:
// 1). "Store, Retrieve, Search, Delete and Update Data using Firebase Realtime Database in Android Studio", Android Knowledge, URL: https://www.youtube.com/watch?v=DWIGAkYkpg8&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=13&t=2s&ab_channel=AndroidKnowledge


public class FavoredCategoriesRepository {
    private static final String TAG = StoreCategoriesRepository.class.getName();
    private static FavoredCategoriesRepository instance;
    private static DatabaseReference usersReference;

    private FavoredCategoriesRepository() {
        usersReference = FirebaseManager.getInstance().getUsersReference();
    }

    public static synchronized FavoredCategoriesRepository getInstance() {
        if (instance == null) {
            instance = new FavoredCategoriesRepository();
        }
        return instance;
    }

    public void deleteFavoredCategory(Store store, String categoryKey) {
        final String storeKey = store.getKey();

        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userKey = userSnapshot.getKey();
                    assert userKey != null;

                    DatabaseReference favoredReference = usersReference.child(userKey).child("favoredSales");
                    Query categoryQuery = favoredReference.child(storeKey).child("categories").child(categoryKey);
                    categoryQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot categorySnapshot) {
                            if (categorySnapshot.exists()) {
                                favoredReference.child(storeKey).child("categories").child(categoryKey).removeValue();
                                StoresRepository.checkStore(userKey, storeKey);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "Error checking category existence: " + error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error getting the stores: " + error.getMessage());
            }
        });
    }
}
