package com.example.snapsale.services.scrapingServices;

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
import com.example.snapsale.database.repositories.StoreCategoriesRepository;
import com.example.snapsale.helpers.ProgressDialogHandler;
import com.example.snapsale.models.FavoredCategory;
import com.example.snapsale.database.repositories.FavoredCategoriesRepository;
import com.example.snapsale.database.repositories.StoresRepository;
import com.example.snapsale.callbacks.StoreCategoryCallback;
import com.example.snapsale.database.managers.FirebaseManager;
import com.example.snapsale.models.Store;
import com.example.snapsale.network.scrapers.LidlScraper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class LidlService implements StoreCategoryCallback {
    private static String[] lidlCategories, lidlCategoryNames;

    private final Activity activity;
    private static StoresRepository storesRepository;
    private static StoreCategoriesRepository storeCategoriesRepository;

    private int categoriesToCheck;
    private static AlertDialog dialog;
    private Runnable runnable;

    public LidlService(Activity activity) {
        this.activity = activity;

        storesRepository = StoresRepository.getInstance();
        storeCategoriesRepository = StoreCategoriesRepository.getInstance();

        lidlCategories = activity.getResources().getStringArray(R.array.lidl_categories);
        lidlCategoryNames = activity.getResources().getStringArray(R.array.lidl_category_names);

        categoriesToCheck = lidlCategories.length;
    }


    public void check(Runnable runnable) {
        this.runnable = runnable;

        storesRepository.getStore("lidl", (Store store) -> {
            for (int i = 0; i < lidlCategories.length; i++)
                storeCategoriesRepository.checkStoreCategoryLidl(store, lidlCategories[i], lidlCategoryNames[i], this);
        });
    }

    @Override
    public void onStoreCategoryScraped() {
        if (dialog == null) dialog = ProgressDialogHandler.getDialog(activity, R.layout.lidl_scraping_progress_layout, R.id.lidl_scraping_spinning_logo);
        categoriesToCheck--;

        if (categoriesToCheck == 0) {
            dialog.dismiss();
            runnable.run();
        }
    }

    @Override
    public void onStoreCategoryNotFound() {
        categoriesToCheck--;

        if (categoriesToCheck == 0) {
            if (dialog != null) dialog.dismiss();
            runnable.run();
        }
    }

    @Override
    public void onGetStoreCategory() {
        categoriesToCheck--;

        if (categoriesToCheck == 0) {
            if (dialog != null) dialog.dismiss();
            runnable.run();
        }
    }
}
