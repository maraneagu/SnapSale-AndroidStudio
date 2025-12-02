package com.example.snapsale.database.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.snapsale.callbacks.CountCallback;
import com.example.snapsale.callbacks.DoubleDataCallback;
import com.example.snapsale.callbacks.FavoredSaleCallback;
import com.example.snapsale.database.managers.FirebaseManager;
import com.example.snapsale.models.FavoredCategory;
import com.example.snapsale.models.FavoredSale;
import com.example.snapsale.models.Sale;
import com.example.snapsale.models.Store;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


// HELPFUL LINKS:
// 1). "Store, Retrieve, Search, Delete and Update Data using Firebase Realtime Database in Android Studio", Android Knowledge, URL: https://www.youtube.com/watch?v=DWIGAkYkpg8&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=13&t=2s&ab_channel=AndroidKnowledge


public class FavoredSalesRepository {
    private static final String TAG = SalesRepository.class.getName();
    private static FavoredSalesRepository instance;
    private final DatabaseReference favoredSalesReference;

    private FavoredSalesRepository() {
        favoredSalesReference = FirebaseManager.getInstance().getFavoredSalesReference();
    }

    public static synchronized FavoredSalesRepository getInstance() {
        if (instance == null) {
            instance = new FavoredSalesRepository();
        }
        return instance;
    }

    public FavoredSale getFavoredSale(Sale sale) {
        FavoredSale favoredSale = new FavoredSale(sale.getKey(),
                sale.getStore(),
                sale.getCategory(),
                sale.getImage(),
                sale.getTitle(),
                sale.getNewPrice(),
                sale.getPeriod());

        if (sale.getSubtitle() != null) favoredSale.setSubtitle(sale.getSubtitle());
        if (sale.getQuantity() != null) favoredSale.setQuantity(sale.getQuantity());

        if (sale.getDiscount() != null) favoredSale.setDiscount(sale.getDiscount());
        if (sale.getOldPrice() != null) favoredSale.setOldPrice(sale.getOldPrice());

        return favoredSale;
    }

    public void getFavoredSales(String storeName, DoubleDataCallback<List<FavoredSale>, List<String>> callback) {
        Query storeQuery = favoredSalesReference.orderByChild("name").equalTo(storeName);
        storeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<FavoredSale> sales = new ArrayList<>();
                List<String> categoryKeys = new ArrayList<>();

                if (snapshot.exists()) {
                    DataSnapshot storeSnapshot = snapshot.getChildren().iterator().next();
                    String storeKey = storeSnapshot.getKey();
                    assert storeKey != null;

                    for (DataSnapshot categorySnapshot : storeSnapshot.child("categories").getChildren()) {
                        for (DataSnapshot saleSnapshot: categorySnapshot.child("sales").getChildren()) {
                            FavoredSale sale = saleSnapshot.getValue(FavoredSale.class);

                            sales.add(sale);
                            categoryKeys.add(categorySnapshot.getKey());
                        }
                    }
                }
                callback.onGetDoubleData(sales, categoryKeys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error getting the store: " + error.getMessage());
            }
        });
    }

    public void getFavoredSalesCount(String storeName, CountCallback<Integer> callback) {
        Query storeQuery = favoredSalesReference.orderByChild("name").equalTo(storeName);
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

    public void checkIsFavored(Store store, FavoredCategory favoredCategory, String favoredSaleKey, FavoredSaleCallback favoredSaleCallback) {
        final String storeKey = store.getKey();
        final String categoryKey = favoredCategory.getKey();

        Query query = favoredSalesReference.child(storeKey).child("categories").child(categoryKey).child("sales").child(favoredSaleKey);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) favoredSaleCallback.onFavoredSaleFound();
                else favoredSaleCallback.onFavoredSaleNotFound();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error checking store existence: " + error.getMessage());
            }
        });
    }

    public void addFavoredSale(Store store, FavoredCategory favoredCategory, FavoredSale favoredSale) {
        final String storeKey = store.getKey();
        final String categoryKey = favoredCategory.getKey();
        final String saleKey = favoredSale.getKey();

        Query query = favoredSalesReference.child(storeKey);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long timestamp = System.currentTimeMillis();
                favoredSale.setFavoredTimestamp(timestamp);

                if (!snapshot.exists()) {
                    favoredSalesReference.child(storeKey).child("name").setValue(store.getName());
                    favoredSalesReference.child(storeKey).child("categories").child(categoryKey).setValue(favoredCategory);
                    favoredSalesReference.child(storeKey).child("categories").child(categoryKey).child("sales").child(saleKey).setValue(favoredSale);
                }
                else {
                    Query categoryQuery = favoredSalesReference.child(storeKey).child("categories").child(categoryKey);
                    categoryQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                favoredSalesReference.child(storeKey).child("categories").child(categoryKey).child("sales").child(saleKey).setValue(favoredSale);
                            }
                            else {
                                favoredSalesReference.child(storeKey).child("categories").child(categoryKey).setValue(favoredCategory);
                                favoredSalesReference.child(storeKey).child("categories").child(categoryKey).child("sales").child(saleKey).setValue(favoredSale);
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
                Log.e(TAG, "Error checking store existence: " + error.getMessage());
            }
        });
    }

    public void deleteFavoredSale(Store store, FavoredCategory favoredCategory, String favoredSaleKey) {
        final String storeKey = store.getKey();
        final String categoryKey = favoredCategory.getKey();

        Query categoryQuery = favoredSalesReference.child(storeKey).child("categories").child(categoryKey);
        categoryQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoredSalesReference.child(storeKey).child("categories").child(categoryKey).child("sales").child(favoredSaleKey).removeValue();

                if (snapshot.child("sales").getChildrenCount() == 1) {
                    favoredSalesReference.child(storeKey).child("categories").child(categoryKey).removeValue();
                }
                StoresRepository.checkStore(store);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error checking category existence: " + error.getMessage());
            }
        });
    }

    public void deleteFavoredSale(String storeName, String categoryKey, FavoredSale favoredSale) {
        Query query = favoredSalesReference.orderByChild("name").equalTo(storeName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot storeSnapshot = snapshot.getChildren().iterator().next();
                String storeKey = storeSnapshot.getKey();
                assert storeKey != null;

                favoredSalesReference.child(storeKey).child("categories").child(categoryKey).child("sales").child(favoredSale.getKey()).removeValue();
                if (storeSnapshot.child("categories").child(categoryKey).child("sales").getChildrenCount() == 1) {
                    favoredSalesReference.child(storeKey).child("categories").child(categoryKey).removeValue();
                }
                StoresRepository.checkStore(storeKey);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error checking store existence: " + error.getMessage());
            }
        });
    }
}
