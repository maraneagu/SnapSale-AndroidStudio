package com.example.snapsale.database.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.snapsale.callbacks.StoreCategoryCallback;
import com.example.snapsale.database.managers.FirebaseManager;
import com.example.snapsale.models.FavoredCategory;
import com.example.snapsale.models.Store;
import com.example.snapsale.models.StoreCategory;
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


public class StoreCategoriesRepository {
    private static final String TAG = StoreCategoriesRepository.class.getName();
    private static StoreCategoriesRepository instance;
    private static DatabaseReference storesReference;
    private static FavoredCategoriesRepository favoredCategoriesRepository;

    private StoreCategoriesRepository() {
        storesReference = FirebaseManager.getInstance().getStoresReference();
        favoredCategoriesRepository = FavoredCategoriesRepository.getInstance();
    }

    public static synchronized StoreCategoriesRepository getInstance() {
        if (instance == null) {
            instance = new StoreCategoriesRepository();
        }
        return instance;
    }

    public void addStoreCategory(Store store, StoreCategory category) {
        final String storeKey = store.getKey();

        Query query = storesReference.child(storeKey);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String categoryKey = storesReference.push().getKey();
                    assert categoryKey != null;

                    category.setKey(categoryKey);
                    storesReference.child(storeKey).child("categories").child(categoryKey).setValue(category);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error checking store existence: " + databaseError.getMessage());
            }
        });
    }

    public void checkStoreCategoryKaufland(Store store, String categoryName, String categoryUrl, StoreCategoryCallback callback) {
        final String storeKey = store.getKey();

        Query storeQuery = storesReference.child(storeKey);
        storeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Query categoryQuery = storesReference.child(storeKey).child("categories").orderByChild("name").equalTo(categoryName);
                    categoryQuery.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                DataSnapshot categorySnapshot = snapshot.getChildren().iterator().next();
                                StoreCategory category = categorySnapshot.getValue(StoreCategory.class);
                                assert category != null;

                                if (category.getPeriod() == null || isStoreCategoryUnavailable(category)) {
                                    categorySnapshot.getRef().removeValue();
                                    favoredCategoriesRepository.deleteFavoredCategory(store, category.getKey());

                                    KauflandScraper kauflandScraper = new KauflandScraper(store, callback);
                                    kauflandScraper.getSales(categoryName, categoryUrl);
                                }
                                else callback.onGetStoreCategory();
                            }
                            else {
                                KauflandScraper kauflandScraper = new KauflandScraper(store, callback);
                                kauflandScraper.getSales(categoryName, categoryUrl);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "Error getting the category: " + error.getMessage());
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

    public void checkStoreCategoryLidl(Store store, String categoryName, String categoryFullName, StoreCategoryCallback callback) {
        final String storeKey = store.getKey();

        Query storeQuery = storesReference.child(storeKey);
        storeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Query categoryQuery = storesReference.child(storeKey).child("categories").orderByChild("name").equalTo(categoryName);
                    categoryQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                DataSnapshot categorySnapshot = snapshot.getChildren().iterator().next();
                                StoreCategory category = categorySnapshot.getValue(StoreCategory.class);
                                assert category != null;

                                if (category.getPeriod() == null || isStoreCategoryUnavailable(category)) {
                                    categorySnapshot.getRef().removeValue();
                                    favoredCategoriesRepository.deleteFavoredCategory(store, category.getKey());

                                    LidlScraper lidlScraper = new LidlScraper(store, callback);
                                    lidlScraper.getLink(categoryName, categoryFullName);
                                }
                                else callback.onGetStoreCategory();
                            }
                            else {
                                LidlScraper lidlScraper = new LidlScraper(store, callback);
                                lidlScraper.getLink(categoryName, categoryFullName);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "Error getting the category: " + error.getMessage());
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

    public void checkStoreCategoryCarrefour(Store store, String categoryName, String categoryFullName, String categoryUrl, StoreCategoryCallback callback) {
        final String storeKey = store.getKey();

        Query storeQuery = storesReference.child(storeKey);
        storeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Query categoryQuery = storesReference.child(storeKey).child("categories").orderByChild("name").equalTo(categoryName);

                    categoryQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                DataSnapshot categorySnapshot = snapshot.getChildren().iterator().next();
                                StoreCategory category = categorySnapshot.getValue(StoreCategory.class);
                                assert category != null;

                                if (category.getPeriod() == null || isStoreCategoryUnavailable(category)) {
                                    categorySnapshot.getRef().removeValue();
                                    favoredCategoriesRepository.deleteFavoredCategory(store, category.getKey());

                                    CarrefourScraper carrefourScraper = new CarrefourScraper(store, callback);
                                    carrefourScraper.execute(categoryName, categoryFullName, categoryUrl);
                                }
                                else callback.onGetStoreCategory();
                            }
                            else {
                                CarrefourScraper carrefourScraper = new CarrefourScraper(store, callback);
                                carrefourScraper.execute(categoryName, categoryFullName, categoryUrl);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "Error getting the category: " + error.getMessage());
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

    public void checkStoreCategoryPenny(Store store, String categoryName, String categoryUrl, StoreCategoryCallback callback) {
        final String storeKey = store.getKey();

        Query storeQuery = storesReference.child(storeKey);
        storeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Query categoryQuery = storesReference.child(storeKey).child("categories").orderByChild("name").equalTo(categoryName);
                    categoryQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                DataSnapshot categorySnapshot = snapshot.getChildren().iterator().next();
                                StoreCategory category = categorySnapshot.getValue(StoreCategory.class);
                                assert category != null;

                                if (category.getPeriod() == null || isStoreCategoryUnavailable(category)) {
                                    categorySnapshot.getRef().removeValue();
                                    favoredCategoriesRepository.deleteFavoredCategory(store, category.getKey());

                                    PennyScraper pennyScraper = new PennyScraper(store, callback);
                                    pennyScraper.getLink(categoryName, categoryUrl);
                                }
                                else callback.onGetStoreCategory();
                            }
                            else {
                                PennyScraper pennyScraper = new PennyScraper(store, callback);
                                pennyScraper.getLink(categoryName, categoryUrl);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "Error getting the category: " + error.getMessage());
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

    public boolean isStoreCategoryUnavailable(StoreCategory category) {
        String[] periodDates = category.getPeriod().split(" - ");

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
            Date currentDate = new Date();

            Date lastPeriodDate = dateFormat.parse(periodDates[1]);
            assert lastPeriodDate != null;

            long oneDayInMillis = 24 * 60 * 60 * 1000;
            lastPeriodDate.setTime(lastPeriodDate.getTime() + oneDayInMillis);

            return currentDate.after(lastPeriodDate);
        }
        catch (ParseException e) {
            return false;
        }
    }
}
