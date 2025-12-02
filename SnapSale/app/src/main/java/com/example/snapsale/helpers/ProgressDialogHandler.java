package com.example.snapsale.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;


// HELPFUL LINKS:
// 1). "Create Custom Alert Dialog Box in Android Studio using Java | Android Studio Tutorial", Android Knowledge, URL: https://www.youtube.com/watch?v=3RTpdB-RszY&ab_channel=AndroidKnowledge
// 2). "Android Studio - Rotate ImageView on Click", AvS, URL: https://www.youtube.com/watch?v=_RE8KEUC3Is&list=PL8THvO1F-Vpe9bxn65ziYvcMR0QUK7q8-&index=64&ab_channel=AvS


public class ProgressDialogHandler {
    public static AlertDialog getDialog(Activity activity, int layout, int logo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = activity.getWindow().getLayoutInflater().inflate(layout, null);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        ImageView spinningLogo = view.findViewById(logo);
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF,
                .5f, RotateAnimation.RELATIVE_TO_SELF,
                .5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        spinningLogo.startAnimation(rotateAnimation);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        dialog.show();
        return dialog;
    }
}
