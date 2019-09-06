package com.softedge.feedbackclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.view.View;

public class common {

    public static final Boolean GOOD_REVIEW = true;
    public static final Boolean BAD_REVIEW = false;

    public static Snackbar Mysnackbar(View parent_view, String message, int lenght) {

        final Snackbar snackbar = Snackbar.make(parent_view, message, lenght);
        snackbar.setActionTextColor(parent_view.getContext().getResources().getColor(R.color.colorPrimary));
        snackbar.setAction("Close", v -> snackbar.dismiss());

        return snackbar;
    }

    public static SharedPreferences app_pref(Context context){
        return context.getSharedPreferences("Feedback_pref", Context.MODE_PRIVATE);
    }
}
