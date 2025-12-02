package com.example.snapsale.services.basketsServices;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.snapsale.R;
import com.example.snapsale.database.repositories.BasketsRepository;
import com.example.snapsale.database.repositories.StoresRepository;
import com.example.snapsale.callbacks.BasketCallback;
import com.example.snapsale.database.managers.FirebaseManager;
import com.example.snapsale.helpers.ProgressDialogHandler;
import com.example.snapsale.models.Basket;
import com.example.snapsale.models.Store;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StoresService implements BasketCallback {
    private final String[] storesBasketNames, storesBasketCategories, storesBasketStores, storesBasketNumbers, storesBasketTypes;

    private static StoresRepository storesRepository;
    private static BasketsRepository basketsRepository;

    private Integer basketsToCheck;
    private Runnable runnable;

    public StoresService(Activity activity) {
        storesRepository = StoresRepository.getInstance();
        basketsRepository = BasketsRepository.getInstance();

        storesBasketTypes = activity.getResources().getStringArray(R.array.stores_basket_types);
        storesBasketNames = activity.getResources().getStringArray(R.array.stores_basket_names);
        storesBasketCategories = activity.getResources().getStringArray(R.array.stores_basket_categories);
        storesBasketStores = activity.getResources().getStringArray(R.array.stores_basket_stores);
        storesBasketNumbers = activity.getResources().getStringArray(R.array.stores_basket_numbers);

        basketsToCheck = storesBasketTypes.length;
    }


    public void check(Runnable runnable) {
        this.runnable = runnable;
        storesRepository.getStore("stores", (store) -> {
            for (int p = 0; p < storesBasketTypes.length; p++) {
                String name = storesBasketNames[p];
                String type = storesBasketTypes[p];

                List<String> categories = new ArrayList<>(Arrays.asList(storesBasketCategories[p].split(" ")));
                List<String> stores = new ArrayList<>(Arrays.asList(storesBasketStores[p].split(" ")));
                List<Integer> numbers = new ArrayList<>();
                String[] numberList = storesBasketNumbers[p].split(" ");
                for (String number : numberList) {
                    numbers.add(Integer.parseInt(number));
                }

                basketsRepository.checkBaskets(store, name, type, stores, categories, numbers, this);
            }
        });
    }

    @Override
    public void onBasketAdded() {
        basketsToCheck--;

        if (basketsToCheck == 0) {
            runnable.run();
        }
    }

    @Override
    public void onBasketNotFound() {
        basketsToCheck--;

        if (basketsToCheck == 0) {
            runnable.run();
        }
    }

    @Override
    public void onGetBasket() {
        basketsToCheck--;

        if (basketsToCheck == 0) {
            runnable.run();
        }
    }
}
