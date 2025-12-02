package com.example.snapsale.activities.guestActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.snapsale.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.snapsale.helpers.ActivityNavigator;
import com.example.snapsale.helpers.Checker;
import com.example.snapsale.navigation.CustomBottomNavigationView;
import com.example.snapsale.activities.storeActivities.KauflandActivity;
import com.example.snapsale.activities.storeActivities.PennyActivity;
import com.google.android.material.navigation.NavigationView;


// HELPFUL LINKS:
// 1). "Android Studio Tutorial - Gestures | Swipe - LEFT, RIGHT, TOP, DOWN", deep code, URL: https://www.youtube.com/watch?v=oFl7WwEX2Co&t=293s&ab_channel=deepcode
// 2). "How to Change Toggle (Button) Color and ToolBar Text Color in Android Studio App", Any Technology, URL: https://www.youtube.com/watch?v=0CrhNNhQfgc&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=29&ab_channel=AnyTechnology
// 3). "How to Implement Bottom Navigation with Activities | Android Studio Tutorial", Android Knowledge, URL: https://www.youtube.com/watch?v=MUl19ppdu0o&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=22&ab_channel=AndroidKnowledge
// 4). "Can i add more than 5 menu items in bottom navigation view?", URL: https://stackoverflow.com/questions/57354874/can-i-add-more-than-5-menu-items-in-bottom-navigation-view
// 5). "Navigation Drawer Menu in Android Tutorial | How to Create Navigation Drawer in Android Studio", Code with Surya, URL: https://www.youtube.com/watch?v=uY9iZiamyZs&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=69&t=1187s&ab_channel=CodewithSurya


public class SearchGuestActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        CustomBottomNavigationView.OnNavigationItemSelectedListener {
    private CustomBottomNavigationView navigationBar;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_guest);

        navigationBar = findViewById(R.id.search_navigation_bar);
        navigationBar.setSelectedItemId(R.id.bn_search);
        navigationBar.setOnNavigationItemSelectedListener(this);

        drawerLayout = findViewById(R.id.search_navigation_drawer_layout);
        setNavigationDrawer();

        setStoreLayouts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationBar.setSelectedItemId(R.id.bn_search);

        Checker.checkMove(SearchGuestActivity.this);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setStoreLayouts() {
        RelativeLayout kauflandLayout = findViewById(R.id.search_kaufland);
        kauflandLayout.setOnTouchListener(new SwipeListener(kauflandLayout, -1, KauflandActivity.class));

        RelativeLayout lidlLayout = findViewById(R.id.search_lidl);
        lidlLayout.setOnTouchListener((view, motionEvent) -> {
            Toast.makeText(SearchGuestActivity.this, "Register to access the sales of this store.", Toast.LENGTH_LONG).show();
            return false;
        });

        RelativeLayout carrefourLayout = findViewById(R.id.search_carrefour);
        carrefourLayout.setOnTouchListener((view, motionEvent) -> {
            Toast.makeText(SearchGuestActivity.this, "Register to access the sales of this store.", Toast.LENGTH_LONG).show();
            return false;
        });

        RelativeLayout pennyLayout = findViewById(R.id.search_penny);
        pennyLayout.setOnTouchListener(new SwipeListener(pennyLayout, 1, PennyActivity.class));
    }

    private class SwipeListener implements View.OnTouchListener{
        final float SWIPE_THRESHOLD = 200;

        RelativeLayout storeLayout;
        int swipeDirection;
        Class<?> storeClass;

        float startX;
        float lastX;
        float endX;
        boolean movedOverThreshold = false;

        public SwipeListener(RelativeLayout storeLayout, int swipeDirection, Class<?> storeClass) {
            this.storeLayout = storeLayout;
            this.swipeDirection = swipeDirection;
            this.storeClass = storeClass;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    lastX = event.getX();
                    movedOverThreshold = false;

                    break;

                case MotionEvent.ACTION_MOVE:
                    float currentX = event.getX();
                    float deltaX = currentX - lastX;
                    float translationX = storeLayout.getTranslationX() + deltaX;

                    if (swipeDirection * translationX >= 0) {
                        storeLayout.setTranslationX(translationX);
                    }
                    lastX = currentX;

                    if (!movedOverThreshold && Math.abs(currentX - startX) > SWIPE_THRESHOLD) {
                        movedOverThreshold = true;
                    }

                    break;

                case MotionEvent.ACTION_UP:
                    endX = event.getX();
                    float deltaSwipe = endX - startX;

                    if (movedOverThreshold && Math.abs(deltaSwipe) > SWIPE_THRESHOLD) {
                        if (swipeDirection * deltaSwipe > 0) {
                            ActivityNavigator.navigate(SearchGuestActivity.this, storeClass);
                        }
                    }
                    storeLayout.animate().translationX(0).setDuration(800);

                    break;
            }
            return true;
        }
    }

    private void setNavigationDrawer() {
        Toolbar toolbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nd, R.string.close_nd);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.yellow));

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.search_navigation_drawer);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nd_home || id == R.id.bn_home) {
            ActivityNavigator.navigate(this, HomeGuestActivity.class);
        }
        else if (id == R.id.nd_location || id == R.id.bn_location) {
            ActivityNavigator.navigate(this, LocationGuestActivity.class);
        }
        else if (id == R.id.nd_favorites || id == R.id.bn_favorites) {
            Toast.makeText(SearchGuestActivity.this, "Register to access the favored sales.", Toast.LENGTH_LONG).show();
            navigationBar.postDelayed(() -> navigationBar.setSelectedItemId(R.id.bn_search), 400);
        }
        else if (id == R.id.nd_baskets || id == R.id.bn_baskets) {
            ActivityNavigator.navigate(this, BasketsGuestActivity.class);
        }
        else if (id == R.id.nd_profile || id == R.id.bn_profile) {
            ActivityNavigator.navigate(this, ProfileGuestActivity.class);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}