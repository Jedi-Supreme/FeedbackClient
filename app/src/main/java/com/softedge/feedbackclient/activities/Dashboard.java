package com.softedge.feedbackclient.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.softedge.feedbackclient.R;
import com.softedge.feedbackclient.common;
import com.softedge.feedbackclient.models.Company_details;
import com.softedge.feedbackclient.models.Feedback_class;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Dashboard extends AppCompatActivity {

    WeakReference<Dashboard> weak_dash;
    TextView tv_dash_name;
    ConstraintLayout const_dash_layout;

    String comp_name, branch_name;

    //===========================================ON CREATE==========================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        weak_dash = new WeakReference<>(this);

        ActionBar actionBar = getSupportActionBar();

        tv_dash_name = findViewById(R.id.tv_dash_branchname);
        const_dash_layout = findViewById(R.id.const_dash_layout);

        SharedPreferences app_pref = common.app_pref(weak_dash.get());
        comp_name = app_pref.getString(Company_details.COMPANY_NAME,"");
        branch_name = app_pref.getString(Company_details.BRANCH_NAME,"");

        String all = comp_name + " - " + branch_name;
        tv_dash_name.setText(all);

        if (actionBar != null){
            actionBar.setTitle(all);
            actionBar.hide();
        }
    }
    //===========================================ON CREATE==========================================

    public void Click_listener(View view) {

        switch (view.getId()){

            case R.id.card_bt_pos:
                save_feedback_online(common.GOOD_REVIEW);
                break;

            case R.id.card_bt_neg:
                save_feedback_online(common.BAD_REVIEW);
                break;

        }
    }

    //-------------------------------------------------DEFINED METHODS------------------------------
    void save_feedback_online(Boolean userfb){

        String uid;

        DatabaseReference feedbackref =
                FirebaseDatabase
                .getInstance()
                .getReference(getResources().getString(R.string.fb_feedback));

        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MMM/dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        String timeMillis = String.valueOf(calendar.getTimeInMillis());
        String date = dateformat.format(calendar.getTime());

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Feedback_class feedback  = new Feedback_class(userfb,timeMillis);
            feedbackref.child(uid).child(branch_name.toUpperCase()).child(date).child(timeMillis).setValue(feedback);

            common.Mysnackbar(const_dash_layout,"Thank you for Choosing us, We wish you a speedy recovery",
                    Snackbar.LENGTH_LONG).show();
        }
    }
    //-------------------------------------------------DEFINED METHODS------------------------------
}
