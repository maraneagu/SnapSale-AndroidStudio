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

public class KauflandService implements BasketCallback {
    private final String[] kauflandBasketTypes, kauflandBasketNames, kauflandBasketCategories, kauflandBasketNumbers;

    private final Activity activity;
    private static StoresRepository storesRepository;
    private static BasketsRepository basketsRepository;

    private int basketsToCheck;
    private AlertDialog dialog;
    private Runnable runnable;


    public KauflandService(Activity activity) {
        this.activity = activity;

        storesRepository = StoresRepository.getInstance();
        basketsRepository = BasketsRepository.getInstance();

        kauflandBasketTypes = activity.getResources().getStringArray(R.array.kaufland_basket_types);
        kauflandBasketNames = activity.getResources().getStringArray(R.array.kaufland_basket_names);
        kauflandBasketCategories = activity.getResources().getStringArray(R.array.kaufland_basket_categories);
        kauflandBasketNumbers = activity.getResources().getStringArray(R.array.kaufland_basket_numbers);

        basketsToCheck = kauflandBasketTypes.length;
    }


    public void check(Runnable runnable) {
        this.runnable = runnable;
        storesRepository.getStore("kaufland", (store) -> {
            for (int p = 0; p < kauflandBasketTypes.length; p++) {
                String name = kauflandBasketNames[p];
                String type = kauflandBasketTypes[p];

                List<String> categories = new ArrayList<>(Arrays.asList(kauflandBasketCategories[p].split(" ")));
                List<Integer> numbers = new ArrayList<>();
                String[] numberList = kauflandBasketNumbers[p].split(" ");
                for (String number : numberList) {
                    numbers.add(Integer.parseInt(number));
                }

                basketsRepository.checkBaskets(store, name, type, categories, numbers, this);
            }
        });
    }

    @Override
    public void onBasketAdded() {
        if (dialog == null) dialog = ProgressDialogHandler.getDialog(activity, R.layout.baskets_kaufland_progress_layout, R.id.baskets_kaufland_spinning_logo);
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
