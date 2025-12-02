package com.example.snapsale.services.basketsServices;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class CarrefourService implements BasketCallback {
    private final String[] carrefourBasketTypes, carrefourBasketNames, carrefourBasketCategories, carrefourBasketNumbers;

    private final Activity activity;
    private static StoresRepository storesRepository;
    private static BasketsRepository basketsRepository;

    private Integer basketsToCheck;
    private AlertDialog dialog;
    private Runnable runnable;

    public CarrefourService(Activity activity) {
        this.activity = activity;

        storesRepository = StoresRepository.getInstance();
        basketsRepository = BasketsRepository.getInstance();

        carrefourBasketTypes = activity.getResources().getStringArray(R.array.carrefour_basket_types);
        carrefourBasketNames = activity.getResources().getStringArray(R.array.carrefour_basket_names);
        carrefourBasketCategories = activity.getResources().getStringArray(R.array.carrefour_basket_categories);
        carrefourBasketNumbers = activity.getResources().getStringArray(R.array.carrefour_basket_numbers);

        basketsToCheck = carrefourBasketTypes.length;
    }


    public void check(Runnable runnable) {
        this.runnable = runnable;
        storesRepository.getStore("carrefour", (store) -> {
            for (int p = 0; p < carrefourBasketTypes.length; p++) {
                String name = carrefourBasketNames[p];
                String type = carrefourBasketTypes[p];

                List<String> categories = new ArrayList<>(Arrays.asList(carrefourBasketCategories[p].split(" ")));
                List<Integer> numbers = new ArrayList<>();
                String[] numberList = carrefourBasketNumbers[p].split(" ");
                for (String number : numberList) {
                    numbers.add(Integer.parseInt(number));
                }

                basketsRepository.checkBaskets(store, name, type, categories, numbers, this);
            }
        });
    }

    @Override
    public void onBasketAdded() {
        if (dialog == null) dialog = ProgressDialogHandler.getDialog(activity, R.layout.baskets_carrefour_progress_layout, R.id.baskets_carrefour_spinning_logo);
        basketsToCheck--;

        if (basketsToCheck == 0) {
            dialog.dismiss();
            runnable.run();
        }
    }

    @Override
    public void onBasketNotFound() {
        basketsToCheck--;

        if (basketsToCheck == 0) {
            if (dialog != null) dialog.dismiss();
            runnable.run();
        }
    }

    @Override
    public void onGetBasket() {
        basketsToCheck--;

        if (basketsToCheck == 0) {
            if (dialog != null) dialog.dismiss();
            runnable.run();
        }
    }
}
