package com.example.snapsale.activities.authentificationActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.MotionEvent;

import com.example.snapsale.R;
import com.example.snapsale.activities.guestActivities.HomeGuestActivity;
import com.example.snapsale.database.managers.FirebaseManager;
import com.example.snapsale.activities.mainActivities.HomeActivity;
import com.example.snapsale.helpers.ActivityNavigator;
import com.google.firebase.auth.FirebaseUser;


// HELPFUL LINKS:
// 1). "Android Studio Tutorial - Gestures | Swipe - LEFT, RIGHT, TOP, DOWN", deep code, URL: https://www.youtube.com/watch?v=oFl7WwEX2Co&t=293s&ab_channel=deepcode


public class MainActivity extends AppCompatActivity {
    private float initialTouchY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.start_btn);
        startButton.setOnClickListener(view -> ActivityNavigator.navigateToStart(this));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialTouchY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float finalTouchY = event.getY();
                if (initialTouchY - finalTouchY > 100) {
                    ActivityNavigator.navigateToStart(this);
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}