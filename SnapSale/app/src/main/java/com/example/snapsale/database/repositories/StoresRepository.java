package com.example.snapsale.database.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.snapsale.callbacks.DataCallback;
import com.example.snapsale.database.managers.FirebaseManager;
import com.example.snapsale.models.Store;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


// HELPFUL LINKS:
// 1). "Store, Retrieve, Search, Delete and Update Data using Firebase Realtime Database in Android Studio", Android Knowledge, URL: https://www.youtube.com/watch?v=DWIGAkYkpg8&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=13&t=2s&ab_channel=AndroidKnowledge


public class StoresRepository {
    private static final String TAG = StoresRepository.class.getName();
    private static StoresRepository instance;
    private static DatabaseReference storesReference;

    private StoresRepository() {
        storesReference = FirebaseManager.getInstance().getStoresReference();
    }

    public static synchronized StoresRepository getInstance() {
        if (instance == null) {
            instance = new StoresRepository();
        }
        return instance;
    }

    public void getStore(String storeName, DataCallback<Store> callback) {
        Query query = storesReference.orderByChild("name").equalTo(storeName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Store store;

                if (!snapshot.exists()) {
                    store = StoresRepository.addStore(storeName);
                }
                else {
                    DataSnapshot storeSnapshot = snapshot.getChildren().iterator().next();
                    store = storeSnapshot.getValue(Store.class);
                }

                callback.onGetData(store);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error getting the store: " + databaseError.getMessage());
            }
        });
    }

    public static Store addStore(String storeName) {
        String storeKey = storesReference.push().getKey();
        assert storeKey != null;

        Store store = new Store(storeKey, storeName);
        storesReference.child(storeKey).setValue(store);

        return store;
    }

    public static void checkStore(Store store) {
        final String storeKey = store.getKey();
        DatabaseReference favoredReference = FirebaseManager.getInstance().getFavoredSalesReference();

        Query storeQuery = favoredReference.child(storeKey);
        storeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child("categories").exists()) {
                    favoredReference.child(storeKey).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error checking store existence: " + error.getMessage());
            }
        });
    }

    public static void checkStore(String storeKey) {
        DatabaseReference favoredReference = FirebaseManager.getInstance().getFavoredSalesReference();

        Query storeQuery = favoredReference.child(storeKey);
        storeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child("categories").exists()) {
                    favoredReference.child(storeKey).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error checking store existence: " + error.getMessage());
            }
        });
    }

    public static void checkStore(String userKey, String storeKey) {
        DatabaseReference usersReference = FirebaseManager.getInstance().getUsersReference();;
        DatabaseReference favoredReference = usersReference.child(userKey).child("favoredSales");

        Query storeQuery = favoredReference.child(storeKey);
        storeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child("categories").exists()) {
                    favoredReference.child(storeKey).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error checking store existence: " + error.getMessage());
            }
        });
    }
}
