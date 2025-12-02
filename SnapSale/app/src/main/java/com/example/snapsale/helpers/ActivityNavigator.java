package com.example.snapsale.helpers;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.snapsale.R;
import com.example.snapsale.activities.authentificationActivities.MainActivity;
import com.example.snapsale.activities.guestActivities.HomeGuestActivity;
import com.example.snapsale.activities.authentificationActivities.LoginActivity;
import com.example.snapsale.activities.authentificationActivities.SignUpActivity;
import com.example.snapsale.activities.guestActivities.ProfileGuestActivity;
import com.example.snapsale.activities.mainActivities.HomeActivity;
import com.example.snapsale.database.managers.FirebaseManager;
import com.google.firebase.auth.FirebaseUser;


// HELPFUL LINKS:
// 1). How to Make a Button Open a New Activity - Android Studio Tutorial, URL: https://www.youtube.com/watch?v=bgIUdb-7Rqo&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=17&ab_channel=CodinginFlow
// 2). Slide Animation Between Activites - Android Studio Tutorial, URL: https://www.youtube.com/watch?v=0s6x3Sn4eYo&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=21&ab_channel=CodinginFlow
// 3). Activities, Tasks and Stacks - Part 8, Intent Flags [The Finale], URL: https://www.youtube.com/watch?v=ZVW9jZgfm7k&ab_channel=Codetutor


public class ActivityNavigator {
    public static void navigate(Activity originActivity, Class<?> destinationClass) {
        Intent intent = new Intent(originActivity, destinationClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        originActivity.startActivity(intent);
        originActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void navigateToStart(Activity originActivity) {
        FirebaseUser currentUser = FirebaseManager.getInstance().getCurrentUser();
        Intent intent;

        if (currentUser != null) {
            if (currentUser.isAnonymous()) intent = new Intent(originActivity, HomeGuestActivity.class);
            else intent = new Intent(originActivity, HomeActivity.class);
        }
        else intent = new Intent(originActivity, SignUpActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        originActivity.startActivity(intent);
        originActivity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
    }

    public static void navigateToMainActivity(Activity originActivity) {
        Intent intent = new Intent(originActivity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        originActivity.startActivity(intent);
        originActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void navigateToSignUpActivity(Activity originActivity) {
        Intent intent = new Intent(originActivity, SignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        originActivity.startActivity(intent);
        originActivity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public static void navigateToSignUpActivityAnonymously(Activity originActivity) {
        Intent intent = new Intent(originActivity, SignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        originActivity.startActivity(intent);
        originActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        originActivity.finish();
    }

    public static void navigateToLoginActivity(Activity originActivity) {
        Intent intent = new Intent(originActivity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        originActivity.startActivity(intent);
        originActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public static void navigateToHomeActivity(Activity originActivity) {
        Toast.makeText(originActivity, "Welcome back to SnapSale.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(originActivity, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        originActivity.startActivity(intent);
        originActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        originActivity.finish();
    }

    public static void navigateToHomeGuestActivity(Activity originActivity) {
        Toast.makeText(originActivity, "Welcome to SnapSale.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(originActivity, HomeGuestActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        originActivity.startActivity(intent);
        originActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        originActivity.finish();
    }

    public static void navigateToStore(Activity originActivity, Class<?> destinationClass, String storeName) {
        Intent intent = new Intent(originActivity, destinationClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("store", storeName);

        originActivity.startActivity(intent);
        originActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void navigateToCategory(Activity originActivity, Class<?> destinationClass, String categoryName) {
        Intent intent = new Intent(originActivity, destinationClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("category", categoryName);

        originActivity.startActivity(intent);
        originActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
