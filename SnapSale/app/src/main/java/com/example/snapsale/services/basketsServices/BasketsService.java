package com.example.snapsale.services.basketsServices;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

public class BasketsService {

    public static void check(Activity activity, Runnable runnable, AlertDialog dialog) {
        final AtomicInteger completedChecks = new AtomicInteger(0);
        final Runnable checkCompletedCallback = () -> {
            int completedCount = completedChecks.incrementAndGet();
            if (completedCount == 4) {
                activity.runOnUiThread(() -> {
                    StoresService storesService = new StoresService(activity);
                    storesService.check(() -> {
                        if (dialog != null) dialog.dismiss();
                        runnable.run();
                    });
                });
            }
        };

        KauflandService kauflandService = new KauflandService(activity);
        kauflandService.check(checkCompletedCallback);

        LidlService lidlService = new LidlService(activity);
        lidlService.check(checkCompletedCallback);

        CarrefourService carrefourService = new CarrefourService(activity);
        carrefourService.check(checkCompletedCallback);

        PennyService pennyService = new PennyService(activity);
        pennyService.check(checkCompletedCallback);
    }
}
