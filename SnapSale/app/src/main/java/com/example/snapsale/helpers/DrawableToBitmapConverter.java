package com.example.snapsale.helpers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;


// HELPFUL LINKS:
// 1). "How to convert a Drawable to a Bitmap?", URL: https://stackoverflow.com/questions/3035692/how-to-convert-a-drawable-to-a-bitmap


public class DrawableToBitmapConverter {
    public BitmapDescriptor convert(Activity context, int drawableId) {
        Drawable drawable = ActivityCompat.getDrawable(context, drawableId);

        assert drawable != null;
        drawable.setBounds(0,0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
