package com.example.snapsale.activities.guestActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snapsale.R;
import com.example.snapsale.helpers.ActivityNavigator;
import com.example.snapsale.helpers.Checker;
import com.example.snapsale.navigation.CustomBottomNavigationView;
import com.example.snapsale.activities.authentificationActivities.SignUpActivity;


// HELPFUL LINKS:
// 1). "How to Implement Bottom Navigation with Activities | Android Studio Tutorial", Android Knowledge, URL: https://www.youtube.com/watch?v=MUl19ppdu0o&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=22&ab_channel=AndroidKnowledge
// 2). "Can i add more than 5 menu items in bottom navigation view?", URL: https://stackoverflow.com/questions/57354874/can-i-add-more-than-5-menu-items-in-bottom-navigation-view


public class ProfileGuestActivity extends AppCompatActivity implements CustomBottomNavigationView.OnNavigationItemSelectedListener {
    private CustomBottomNavigationView navigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_guest);

        navigationBar = findViewById(R.id.profile_guest_navigation_bar);
        navigationBar.setSelectedItemId(R.id.bn_profile);
        navigationBar.setOnNavigationItemSelectedListener(this);

        TextView signUpRedirect = findViewById(R.id.profile_guest_sign_up_button);
        signUpRedirect.setOnClickListener(view -> ActivityNavigator.navigateToSignUpActivityAnonymously(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationBar.setSelectedItemId(R.id.bn_profile);

        Checker.checkMove(ProfileGuestActivity.this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.bn_location) {
            ActivityNavigator.navigate(this, LocationGuestActivity.class);
        }
        else if (id == R.id.bn_search) {
            ActivityNavigator.navigate(this, SearchGuestActivity.class);
        }
        else if (id == R.id.bn_home) {
            ActivityNavigator.navigate(this, HomeGuestActivity.class);
        }
        else if (id == R.id.bn_favorites) {
            Toast.makeText(ProfileGuestActivity.this, "Register to access the favored sales.", Toast.LENGTH_LONG).show();
            navigationBar.postDelayed(() -> navigationBar.setSelectedItemId(R.id.bn_profile), 400);
        }
        else if (id == R.id.bn_baskets) {
            ActivityNavigator.navigate(this, BasketsGuestActivity.class);
        }

        return true;
    }
}