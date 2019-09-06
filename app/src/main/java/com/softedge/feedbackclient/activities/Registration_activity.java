package com.softedge.feedbackclient.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.softedge.feedbackclient.R;
import com.softedge.feedbackclient.common;
import com.softedge.feedbackclient.models.Company_details;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class Registration_activity extends AppCompatActivity {

    ArrayList<String> enterprice_list;
    WeakReference<Registration_activity> weak_comp_reg;
    ConstraintLayout const_comp_reg;
    TextInputEditText et_reg_branchname, et_reg_compname;

    //===========================================ON CREATE==========================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_details);

        weak_comp_reg = new WeakReference<>(this);
        enterprice_list = new ArrayList<>();

        const_comp_reg = findViewById(R.id.const_comp_reg);
        et_reg_branchname = findViewById(R.id.et_reg_bn);
        et_reg_compname = findViewById(R.id.et_reg_entp);

        try {
            load_company_Data();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error loading name: " + e.toString(), Toast.LENGTH_LONG).show();
        }

    }
    //===========================================ON CREATE==========================================

    //-------------------------------------------------DEFINED METHODS------------------------------

    void test_input(){
        if (et_reg_branchname.getText().toString().isEmpty() || et_reg_branchname.getText().toString().equals("")){
            common.Mysnackbar(const_comp_reg,"Enter branch name.", Snackbar.LENGTH_SHORT).show();
        }else {

            Company_details companyDetails = new Company_details(
                    et_reg_compname.getText().toString(),
                    et_reg_branchname.getText().toString()
                    );
            save_toPref(companyDetails);
        }
    }

    void save_toPref(Company_details companyDetails){

        String uid;

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            SharedPreferences.Editor pref_editor = common.app_pref(weak_comp_reg.get()).edit();

            pref_editor.putString(Company_details.COMPANY_NAME,companyDetails.getComp_name());
            pref_editor.putString(Company_details.BRANCH_NAME,companyDetails.getBranch_name());
            pref_editor.putString(Company_details.COMPANY_ID,uid);
            pref_editor.apply();

            toDashboard();
        }

    }

    void load_company_Data(){

        String uid;

        DatabaseReference comp_ref =
                FirebaseDatabase
                        .getInstance()
                        .getReference(getResources().getString(R.string.fb_reg_comp));

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            comp_ref.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Company_details company = dataSnapshot.getValue(Company_details.class);

                    if (company != null){
                        et_reg_compname.setText(company.getComp_name());
                    }else {
                        common.Mysnackbar(const_comp_reg,
                                "Company not Registered, Please contact Administrator", Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }
    //-------------------------------------------------DEFINED METHODS------------------------------

    public void VerifyInfo(View view) {
        test_input();
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=INTENTS-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=---=-
    void toDashboard(){
        Intent dash_intent = new Intent(getApplicationContext(),Dashboard.class);
        dash_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(dash_intent);
        finish();
    }
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=INTENTS-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=---=-
}
