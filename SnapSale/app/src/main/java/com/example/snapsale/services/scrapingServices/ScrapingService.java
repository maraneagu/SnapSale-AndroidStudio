package com.example.snapsale.services.scrapingServices;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.example.snapsale.R;
import com.example.snapsale.helpers.ProgressDialogHandler;
import com.example.snapsale.services.basketsServices.BasketsService;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

public class ScrapingService {

    public static void check(Activity activity, Runnable runnable) {
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());

        SharedPreferences sharedPreferences = activity.getSharedPreferences("SHARED_PREFERENCES", 0);
        String salesDate = sharedPreferences.getString("salesDate", "");

        if (!currentDate.equals(salesDate)) {
            AlertDialog dialog = ProgressDialogHandler.getDialog(activity, R.layout.scraping_progress_layout, R.id.scraping_spinning_logo);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("salesDate", currentDate);
            editor.apply();

            final AtomicInteger completedChecks = new AtomicInteger(0);
            final Runnable checkCompletedCallback = () -> {
                int completedCount = completedChecks.incrementAndGet();
                if (completedCount == 4) {
                    activity.runOnUiThread(() -> {
                        BasketsService.check(activity, runnable, dialog);
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
        else runnable.run();
    }
}
