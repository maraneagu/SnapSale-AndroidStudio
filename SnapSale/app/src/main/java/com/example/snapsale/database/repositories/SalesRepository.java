package com.example.snapsale.database.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.snapsale.callbacks.CountCallback;
import com.example.snapsale.callbacks.DataCallback;
import com.example.snapsale.callbacks.DoubleDataCallback;
import com.example.snapsale.database.managers.FirebaseManager;
import com.example.snapsale.models.FavoredCategory;
import com.example.snapsale.models.Sale;
import com.example.snapsale.models.Store;
import com.example.snapsale.models.StoreCategory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


// HELPFUL LINKS:
// 1). "Store, Retrieve, Search, Delete and Update Data using Firebase Realtime Database in Android Studio", Android Knowledge, URL: https://www.youtube.com/watch?v=DWIGAkYkpg8&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=13&t=2s&ab_channel=AndroidKnowledge


public class SalesRepository {
    private static final String TAG = SalesRepository.class.getName();
    private static SalesRepository instance;
    private final DatabaseReference storesReference;

    private SalesRepository() {
        storesReference = FirebaseManager.getInstance().getStoresReference();
    }

    public static synchronized SalesRepository getInstance() {
        if (instance == null) {
            instance = new SalesRepository();
        }
        return instance;
    }

    public void getSales(DataCallback<List<Sale>> callback) {
        storesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<Sale> sales = new ArrayList<>();

                    for (DataSnapshot storeSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot categorySnapshot : storeSnapshot.child("categories").getChildren()) {
                            for (DataSnapshot saleSnapshot : categorySnapshot.child("sales").getChildren()) {
                                Sale sale = saleSnapshot.getValue(Sale.class);
                                assert sale != null;

                                sales.add(sale);
                            }
                        }
                    }
                    callback.onGetData(sales);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error getting the stores: " + error.getMessage());
            }
        });
    }

    public void getSales(Store store, String categoryName, DoubleDataCallback<FavoredCategory, ArrayList<Sale>> callback) {
        final String storeKey = store.getKey();

        Query categoryQuery = storesReference.child(storeKey).child("categories").orderByChild("name").equalTo(categoryName);
        categoryQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot categorySnapshot = snapshot.getChildren().iterator().next();

                    StoreCategory storeCategory = categorySnapshot.getValue(StoreCategory.class);
                    assert storeCategory != null;
                    FavoredCategory category = new FavoredCategory(storeCategory.getKey(), storeCategory.getName(), storeCategory.getPeriod());

                    ArrayList<Sale> sales = new ArrayList<>();
                    for (DataSnapshot saleSnapshot: categorySnapshot.child("sales").getChildren()) {
                        Sale sale = saleSnapshot.getValue(Sale.class);

                        assert sale != null;
                        sales.add(sale);
                    }

                    if (sales.isEmpty()) callback.onGetDoubleData(null, null);
                    else callback.onGetDoubleData(category, sales);
                }
                else callback.onGetDoubleData(null, null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error getting the category: " + error.getMessage());
            }
        });
    }

    public void getSalesCount(String storeName, CountCallback<Integer> callback) {
        Query storeQuery = storesReference.orderByChild("name").equalTo(storeName);
        storeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int salesCount = 0;

                if (snapshot.exists()) {
                    DataSnapshot storeSnapshot = snapshot.getChildren().iterator().next();
                    String storeKey = storeSnapshot.getKey();
                    assert storeKey != null;

                    for (DataSnapshot categorySnapshot : storeSnapshot.child("categories").getChildren()) {
                        salesCount += categorySnapshot.child("sales").getChildrenCount();
                    }
                }

                callback.onGetCount(salesCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error getting the store: " + error.getMessage());
            }
        });
    }

    public void addSale(Store store, StoreCategory category, Sale sale) {
        final String storeKey = store.getKey();

        Query query = storesReference.child(storeKey);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    final String categoryKey = category.getKey();

                    Query categoryQuery = storesReference.child(storeKey).child("categories").child(categoryKey);
                    categoryQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String saleKey = storesReference.push().getKey();
                            assert saleKey != null;

                            sale.setKey(saleKey);
                            storesReference.child(storeKey).child("categories").child(categoryKey).child("period").setValue(sale.getPeriod());
                            storesReference.child(storeKey).child("categories").child(categoryKey).child("sales").child(saleKey).setValue(sale);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "Error getting the category: " + error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error adding sale: " + databaseError.getMessage());
            }
        });
    }
}
