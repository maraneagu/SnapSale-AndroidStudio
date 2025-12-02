package com.example.snapsale.database.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.snapsale.callbacks.BasketCallback;
import com.example.snapsale.callbacks.CountCallback;
import com.example.snapsale.callbacks.DataCallback;
import com.example.snapsale.database.managers.FirebaseManager;
import com.example.snapsale.models.Basket;
import com.example.snapsale.models.Sale;
import com.example.snapsale.models.Store;
import com.example.snapsale.network.requests.BasketsRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


// HELPFUL LINKS:
// 1). "Store, Retrieve, Search, Delete and Update Data using Firebase Realtime Database in Android Studio", Android Knowledge, URL: https://www.youtube.com/watch?v=DWIGAkYkpg8&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=13&t=2s&ab_channel=AndroidKnowledge


public class BasketsRepository {
    private static final String TAG = BasketsRepository.class.getName();
    private static BasketsRepository instance;
    private static BasketsRequest basketsRequest;
    private final DatabaseReference storesReference;

    private BasketsRepository() {
        basketsRequest = BasketsRequest.getInstance();
        storesReference = FirebaseManager.getInstance().getStoresReference();
    }

    public static synchronized BasketsRepository getInstance() {
        if (instance == null) {
            instance = new BasketsRepository();
        }
        return instance;
    }

    public void getBaskets(String storeName, DataCallback<List<Basket>> callback) {
        Query storeQuery = storesReference.orderByChild("name").equalTo(storeName);
        storeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot storeSnapshot = snapshot.getChildren().iterator().next();
                String storeKey = storeSnapshot.getKey();
                assert storeKey != null;

                List<Basket> baskets = new ArrayList<>();
                for (DataSnapshot basketSnapshot : storeSnapshot.child("baskets").getChildren()) {
                    Basket basket = basketSnapshot.getValue(Basket.class);
                    baskets.add(basket);
                }
                callback.onGetData(baskets);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error getting the store: " + error.getMessage());
            }
        });
    }

    public void getBasketsCount(String storeName, CountCallback<Integer> callback) {
        Query storeQuery = storesReference.orderByChild("name").equalTo(storeName);
        storeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int salesCount = 0;

                if (snapshot.exists()) {
                    DataSnapshot storeSnapshot = snapshot.getChildren().iterator().next();
                    String storeKey = storeSnapshot.getKey();
                    assert storeKey != null;

                    salesCount += storeSnapshot.child("baskets").getChildrenCount();
                }
                callback.onGetCount(salesCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error getting the store: " + error.getMessage());
            }
        });
    }

    public void createBasket(String storeName, String name, String type, List<String> categories, List<Integer> numbers, BasketCallback callback) {
        Query storeQuery = storesReference.orderByChild("name").equalTo(storeName);
        storeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot categoriesSnapshot = snapshot.getChildren().iterator().next().child("categories");
                    List<Sale> sales = new ArrayList<>();

                    for (DataSnapshot categorySnapshot : categoriesSnapshot.getChildren()) {
                        String categoryName = categorySnapshot.child("name").getValue(String.class);

                        if (categories.contains(categoryName)) {
                            int number = numbers.get(categories.indexOf(categoryName));
                            List<Sale> categorySales = new ArrayList<>();

                            for (DataSnapshot saleSnapshot : categorySnapshot.child("sales").getChildren()) {
                                categorySales.add(saleSnapshot.getValue(Sale.class));
                            }

                            if (categorySales.size() < number) sales.addAll(categorySales);
                            else {
                                Collections.shuffle(categorySales);
                                sales.addAll(categorySales.subList(0, number));
                            }
                        }
                    }

                    if (!sales.isEmpty()) {
                        try {
                            basketsRequest.request(name, type, sales, callback, (basket) -> addBasket(storeName, basket, callback));
                        } catch (JSONException e) {
                            callback.onBasketNotFound();
                        }
                    }
                    else callback.onBasketNotFound();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error getting the store: " + error.getMessage());
            }
        });
    }

    public void createBasket(String storeName, String name, String type, BasketCallback callback) {
        Query storeQuery = storesReference.orderByChild("name").equalTo(storeName);
        storeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot categoriesSnapshot = snapshot.getChildren().iterator().next().child("categories");
                    List<Sale> sales = new ArrayList<>();

                    for (DataSnapshot categorySnapshot : categoriesSnapshot.getChildren()) {
                        for (DataSnapshot saleSnapshot : categorySnapshot.child("sales").getChildren()) {
                            sales.add(saleSnapshot.getValue(Sale.class));
                        }
                    }

                    if (!sales.isEmpty()) {
                        try {
                            basketsRequest.request(name, type, sales, callback, (basket) -> addBasket(storeName, basket, callback));
                        } catch (JSONException e) {
                            callback.onBasketNotFound();
                        }
                    }
                    else callback.onBasketNotFound();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error getting the store: " + error.getMessage());
            }
        });
    }

    public void createBasket(String storeName, String name, String type, List<String> stores, List<String> categories, List<Integer> numbers, BasketCallback callback) {
        storesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<Sale> sales = new ArrayList<>();

                    for (DataSnapshot storeSnapshot : snapshot.getChildren()) {
                        String storeName = storeSnapshot.child("name").getValue(String.class);

                        if (stores.contains(storeName)) {
                            int number = numbers.get(stores.indexOf(storeName));
                            String categoryName = categories.get(stores.indexOf(storeName));
                            List<Sale> storeSales = new ArrayList<>();

                            for (DataSnapshot basketSnapshot : storeSnapshot.child("baskets").getChildren()) {
                                Basket basket = basketSnapshot.getValue(Basket.class);
                                assert basket != null;

                                if (basket.getType().equals(categoryName)) {
                                    for (Map.Entry<String, Sale> entry : basket.getSales().entrySet()) {
                                        Sale sale = entry.getValue();
                                        storeSales.add(sale);
                                    }
                                }
                            }

                            if (storeSales.size() < number) sales.addAll(storeSales);
                            else {
                                Collections.shuffle(storeSales);
                                sales.addAll(storeSales.subList(0, number));
                            }
                        }
                    }

                    if (!sales.isEmpty()) {
                        try {
                            String period = basketsRequest.getPeriod(sales);
                            if (period == null) callback.onBasketNotFound();
                            else {
                                HashMap<String, Sale> salesMap = new HashMap<>();
                                for (Sale sale : sales) salesMap.put(sale.getKey(), sale);

                                Basket basket = new Basket(name, type, period, salesMap);
                                addBasket(storeName, basket, callback);
                            }
                        }
                        catch (ParseException e) {
                            callback.onBasketNotFound();
                        }
                    }
                    else callback.onBasketNotFound();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error getting the store: " + error.getMessage());
            }
        });
    }

    private void addBasket(String storeName, Basket basket, BasketCallback callback) {
        Query query = storesReference.orderByChild("name").equalTo(storeName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String storeKey = snapshot.getChildren().iterator().next().getKey();
                    String basketKey = storesReference.push().getKey();

                    assert storeKey != null;
                    assert basketKey != null;

                    basket.setKey(basketKey);
                    storesReference.child(storeKey).child("baskets").child(basketKey).setValue(basket);

                    callback.onBasketAdded();
                }
                else {
                    String storeKey = storesReference.push().getKey();
                    String basketKey = storesReference.push().getKey();

                    assert storeKey != null;
                    assert basketKey != null;

                    Store store = new Store(storeKey, storeName);
                    storesReference.child(storeKey).setValue(store);

                    basket.setKey(basketKey);
                    storesReference.child(storeKey).child("baskets").child(basketKey).setValue(basket);

                    callback.onBasketAdded();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error getting the store: " + error.getMessage());
            }
        });
    }

    public void checkBaskets(Store store, String name, String type, List<String> categories, List<Integer> numbers, BasketCallback callback) {
        final String storeKey = store.getKey();

        Query storeQuery = storesReference.child(storeKey);
        storeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Query basketQuery = storesReference.child(storeKey).child("baskets").orderByChild("type").equalTo(type);
                    basketQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                DataSnapshot basketSnapshot = snapshot.getChildren().iterator().next();
                                Basket basket = basketSnapshot.getValue(Basket.class);
                                assert basket != null;

                                if (isBasketUnavailable(basket)) {
                                    basketSnapshot.getRef().removeValue();
                                    deleteBaskets("stores");

                                    createBasket(store.getName(), name, type, categories, numbers, callback);
                                }
                                else callback.onGetBasket();
                            }
                            else createBasket(store.getName(), name, type, categories, numbers, callback);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "Error getting the basket: " + error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error getting the store: " + error.getMessage());
            }
        });
    }

    public void checkBaskets(Store store, String name, String type, BasketCallback callback) {
        final String storeKey = store.getKey();

        Query storeQuery = storesReference.child(storeKey);
        storeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Query basketQuery = storesReference.child(storeKey).child("baskets").orderByChild("type").equalTo(type);
                    basketQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                DataSnapshot basketSnapshot = snapshot.getChildren().iterator().next();
                                Basket basket = basketSnapshot.getValue(Basket.class);
                                assert basket != null;

                                if (isBasketUnavailable(basket)) {
                                    basketSnapshot.getRef().removeValue();

                                    deleteBaskets("stores");
                                    createBasket(store.getName(), name, type, callback);
                                }
                                else callback.onGetBasket();
                            }
                            else createBasket(store.getName(), name, type, callback);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "Error getting the basket: " + error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error getting the store: " + error.getMessage());
            }
        });
    }

    public void checkBaskets(Store store, String name, String type, List<String> stores, List<String> categories, List<Integer> numbers, BasketCallback callback) {
        final String storeKey = store.getKey();

        Query storeQuery = storesReference.child(storeKey);
        storeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Query basketQuery = storesReference.child(storeKey).child("baskets").orderByChild("type").equalTo(type);
                    basketQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                DataSnapshot basketSnapshot = snapshot.getChildren().iterator().next();
                                Basket basket = basketSnapshot.getValue(Basket.class);
                                assert basket != null;

                                if (isBasketUnavailable(basket)) {
                                    basketSnapshot.getRef().removeValue();
                                    createBasket(store.getName(), name, type, stores, categories, numbers, callback);
                                }
                                else callback.onGetBasket();
                            }
                            else createBasket(store.getName(), name, type, stores, categories, numbers, callback);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "Error getting the basket: " + error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error getting the store: " + error.getMessage());
            }
        });
    }

    public boolean isBasketUnavailable(Basket basket) {
        String[] periodDates = basket.getPeriod().split(" - ");

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
            Date currentDate = new Date();

            Date lastPeriodDate = dateFormat.parse(periodDates[1]);
            assert lastPeriodDate != null;
            long oneDayInMillis = 24 * 60 * 60 * 1000;
            lastPeriodDate.setTime(lastPeriodDate.getTime() + oneDayInMillis);

            return currentDate.after(lastPeriodDate);
        } catch (ParseException e) {
            return false;
        }
    }

    public void deleteBaskets(String storeName) {
        Query query = storesReference.orderByChild("name").equalTo(storeName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    DataSnapshot storeSnapshot = snapshot.getChildren().iterator().next();
                    String storeKey = storeSnapshot.getKey();
                    assert storeKey != null;

                    if (storeSnapshot.hasChild("baskets")) {
                        storesReference.child(storeKey).child("baskets").removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error getting the store: " + error.getMessage());
            }
        });
    }
}
