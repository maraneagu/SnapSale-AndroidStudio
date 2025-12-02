package com.example.snapsale.activities.mainActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snapsale.R;
import com.example.snapsale.database.managers.FirebaseAuthentificationManager;
import com.example.snapsale.database.repositories.UsersRepository;
import com.example.snapsale.helpers.Checker;
import com.example.snapsale.navigation.CustomBottomNavigationView;
import com.example.snapsale.helpers.ActivityNavigator;


// HELPFUL LINKS:
// 1). "Create Custom Alert Dialog Box in Android Studio using Java | Android Studio Tutorial", Android Knowledge, URL: https://www.youtube.com/watch?v=3RTpdB-RszY&ab_channel=AndroidKnowledge
// 2). "How to Implement Bottom Navigation with Activities | Android Studio Tutorial", Android Knowledge, URL: https://www.youtube.com/watch?v=MUl19ppdu0o&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=22&ab_channel=AndroidKnowledge
// 3). "Can i add more than 5 menu items in bottom navigation view?", URL: https://stackoverflow.com/questions/57354874/can-i-add-more-than-5-menu-items-in-bottom-navigation-view


public class ProfileActivity extends AppCompatActivity implements UsersRepository.ChangePasswordCallback,
        CustomBottomNavigationView.OnNavigationItemSelectedListener {
    private static FirebaseAuthentificationManager firebaseAuthentificationManager;
    private static UsersRepository usersRepository;
    private CustomBottomNavigationView navigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuthentificationManager = FirebaseAuthentificationManager.getInstance();
        usersRepository = UsersRepository.getInstance();

        navigationBar = findViewById(R.id.profile_navigation_bar);
        navigationBar.setSelectedItemId(R.id.bn_profile);
        navigationBar.setOnNavigationItemSelectedListener(this);

        getUsername();
        getEmail();
        logout();
        changePassword();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationBar.setSelectedItemId(R.id.bn_profile);

        Checker.checkMove(ProfileActivity.this);
    }

    private void getUsername() {
        TextView username = findViewById(R.id.profile_username);
        usersRepository.getUsername(username::setText);
    }

    private void getEmail() {
        TextView email = findViewById(R.id.profile_email);
        email.setText(usersRepository.getEmail());
    }

    @Override
    public void onChangePasswordSuccess(AlertDialog dialog) {
        dialog.dismiss();
        Toast.makeText(ProfileActivity.this, "A reset email was sent to your inbox.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChangePasswordFailure(Exception exception) {
        Toast.makeText(ProfileActivity.this, "Unable to send the reset email.", Toast.LENGTH_SHORT).show();
    }

    private void changePassword() {
        LinearLayout changePassword = findViewById(R.id.profile_change_password);

        changePassword.setOnClickListener(view1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.profile_dialog_change_password, null);

            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            TextView dialogEmail = dialogView.findViewById(R.id.dialog_email);
            dialogEmail.setText(usersRepository.getEmail());

            dialogView.findViewById(R.id.dialog_reset_button).setOnClickListener(viewReset -> usersRepository.changePassword(dialog, this));
            dialogView.findViewById(R.id.dialog_cancel_button).setOnClickListener(viewCancel -> dialog.dismiss());

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            dialog.show();
        });
    }

    private void logout() {
        LinearLayout logout = findViewById(R.id.profile_logout);

        logout.setOnClickListener(v -> {
            firebaseAuthentificationManager.logout();
            ActivityNavigator.navigateToMainActivity(this);
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.bn_location) {
            ActivityNavigator.navigate(this, LocationActivity.class);
        }
        else if (id == R.id.bn_search) {
            ActivityNavigator.navigate(this, SearchActivity.class);
        }
        else if (id == R.id.bn_home) {
            ActivityNavigator.navigate(this, HomeActivity.class);
        }
        else if (id == R.id.bn_favorites) {
            ActivityNavigator.navigate(this, FavoritesActivity.class);
        }
        else if (id == R.id.bn_baskets) {
            ActivityNavigator.navigate(this, BasketsActivity.class);
        }

        return true;
    }
}