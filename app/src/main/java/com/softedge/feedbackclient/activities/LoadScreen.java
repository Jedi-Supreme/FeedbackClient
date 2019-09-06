package com.softedge.feedbackclient.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.softedge.feedbackclient.R;
import com.softedge.feedbackclient.common;
import com.softedge.feedbackclient.models.Company_details;

import java.lang.ref.WeakReference;

public class LoadScreen extends AppCompatActivity {

    WeakReference<LoadScreen> weak_load;
    ImageView iv_loading;
    TextView tv_load_text;

    //===========================================ON CREATE==========================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadscreen);

        weak_load = new WeakReference<>(this);

        tv_load_text = findViewById(R.id.tv_load_txt);
        iv_loading = findViewById(R.id.iv_loading);

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1200); //You can manage the blinking time with this parameter
        anim.setStartOffset(200);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);

        iv_loading.startAnimation(anim);

        SharedPreferences app_pref = common.app_pref(weak_load.get());
        String branch_name = app_pref.getString(Company_details.BRANCH_NAME,"");

        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            login_intent();
        }else {

            if (branch_name.equals("")){
                toComp_details();
            }else {
                toDashboard();
            }
        }

    }
    //===========================================ON CREATE==========================================

    void login_intent(){
        Intent login_intent = new Intent(getApplicationContext(), LoginActivity.class);
        login_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(login_intent);
        finish();
    }

    private void toComp_details(){
        Intent comp_details_intent = new Intent(getApplicationContext(), Registration_activity.class);
        comp_details_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(comp_details_intent);
        finish();
    }

    void toDashboard(){
        Intent dash_intent = new Intent(getApplicationContext(),Dashboard.class);
        dash_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(dash_intent);
        finish();
    }
}
