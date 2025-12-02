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
import com.example.snapsale.services.basketsServices.KauflandService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LidlService implements BasketCallback {
    private final String[] lidlBasketTypes, lidlBasketNames;

    private final Activity activity;
    private static StoresRepository storesRepository;
    private static BasketsRepository basketsRepository;

    private Integer basketsToCheck;
    private AlertDialog dialog;
    private Runnable runnable;


    public LidlService(Activity activity) {
        this.activity = activity;

        storesRepository = StoresRepository.getInstance();
        basketsRepository = BasketsRepository.getInstance();

        lidlBasketTypes = activity.getResources().getStringArray(R.array.lidl_basket_types);
        lidlBasketNames = activity.getResources().getStringArray(R.array.lidl_basket_names);

        basketsToCheck = lidlBasketTypes.length;
    }


    public void check(Runnable runnable) {
        this.runnable = runnable;
        storesRepository.getStore("lidl", (store) -> {
            for (int p = 0; p < lidlBasketTypes.length; p++) {
                String name = lidlBasketNames[p];
                String type = lidlBasketTypes[p];

                basketsRepository.checkBaskets(store, name, type, this);
            }
        });
    }

    @Override
    public void onBasketAdded() {
        if (dialog == null) dialog = ProgressDialogHandler.getDialog(activity, R.layout.baskets_lidl_progress_layout, R.id.baskets_lidl_spinning_logo);
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
