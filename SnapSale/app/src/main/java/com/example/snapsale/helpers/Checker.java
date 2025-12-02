package com.example.snapsale.helpers;

import android.app.Activity;
import android.content.SharedPreferences;

import com.example.snapsale.activities.mainActivities.HomeActivity;

import java.text.DateFormat;
import java.util.Calendar;


// HELPFUL LINKS:
// 1). "Run code only once a day in Android Studio", Tihomir Radev, URL: https://www.youtube.com/watch?v=MZjIhaMKnlo&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=67&ab_channel=TihomirRadev


public class Checker {
    public static void checkMove(Activity activity) {
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());

        SharedPreferences sharedPreferences = activity.getSharedPreferences("SHARED_PREFERENCES", 0);
        String salesDate = sharedPreferences.getString("salesDate", "");

        if (!currentDate.equals(salesDate)) {
            ActivityNavigator.navigate(activity, HomeActivity.class);
        }
    }

    public static void checkMove(Activity activity, Runnable runnable) {
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());

        SharedPreferences sharedPreferences = activity.getSharedPreferences("SHARED_PREFERENCES", 0);
        String salesDate = sharedPreferences.getString("salesDate", "");

        if (!currentDate.equals(salesDate)) {
            ActivityNavigator.navigate(activity, HomeActivity.class);
        }
        else runnable.run();
    }
}
