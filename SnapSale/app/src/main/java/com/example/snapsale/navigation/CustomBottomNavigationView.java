package com.example.snapsale.navigation;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.material.bottomnavigation.BottomNavigationView;


// HELPFUL LINKS:
// 1). "Can i add more than 5 menu items in bottom navigation view?", URL: https://stackoverflow.com/questions/57354874/can-i-add-more-than-5-menu-items-in-bottom-navigation-view


public class CustomBottomNavigationView extends BottomNavigationView {

    public CustomBottomNavigationView(Context context) {
        super(context);
    }

    public CustomBottomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomBottomNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getMaxItemCount() {
        return 6;
    }
}
