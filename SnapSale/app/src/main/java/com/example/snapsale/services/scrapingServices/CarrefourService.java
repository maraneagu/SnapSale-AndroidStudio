package com.example.snapsale.services.scrapingServices;

import android.app.Activity;
import android.app.AlertDialog;

import com.example.snapsale.R;
import com.example.snapsale.database.repositories.FavoredCategoriesRepository;
import com.example.snapsale.database.repositories.StoreCategoriesRepository;
import com.example.snapsale.database.repositories.StoresRepository;
import com.example.snapsale.callbacks.StoreCategoryCallback;
import com.example.snapsale.helpers.ProgressDialogHandler;
import com.example.snapsale.models.Store;

public class CarrefourService implements StoreCategoryCallback {
    private static String[] carrefourCategories, carrefourCategoryNames, carrefourUrls;

    private final Activity activity;
    private static StoresRepository storesRepository;
    private static StoreCategoriesRepository storeCategoriesRepository;

    private int categoriesToCheck;
    private static AlertDialog dialog;
    private Runnable runnable;


    public CarrefourService(Activity activity) {
        this.activity = activity;

        storesRepository = StoresRepository.getInstance();
        storeCategoriesRepository = StoreCategoriesRepository.getInstance();

        carrefourCategories = activity.getResources().getStringArray(R.array.carrefour_categories);
        carrefourCategoryNames = activity.getResources().getStringArray(R.array.carrefour_category_names);
        carrefourUrls = activity.getResources().getStringArray(R.array.carrefour_urls);

        categoriesToCheck = carrefourCategories.length;
    }


    public void check(Runnable runnable) {
        this.runnable = runnable;

        storesRepository.getStore("carrefour", (Store store) -> {
            for (int i = 0; i < carrefourCategories.length; i++)
                storeCategoriesRepository.checkStoreCategoryCarrefour(store, carrefourCategories[i], carrefourCategoryNames[i], carrefourUrls[i], this);
        });
    }

    @Override
    public void onStoreCategoryScraped() {
        if (dialog == null) dialog = ProgressDialogHandler.getDialog(activity, R.layout.carrefour_scraping_progress_layout, R.id.carrefour_scraping_spinning_logo);
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
