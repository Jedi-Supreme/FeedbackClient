package com.softedge.feedbackclient.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.softedge.feedbackclient.R;
import com.softedge.feedbackclient.common;
import com.softedge.feedbackclient.models.Company_details;

import java.lang.ref.WeakReference;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText et_login_password,et_login_email;
    ConstraintLayout const_login;
    ProgressBar probar_login;
    WeakReference<LoginActivity> weak_login;

    //===========================================ON CREATE==========================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        weak_login = new WeakReference<>(this);

        et_login_password = findViewById(R.id.et_login_password);
        et_login_email = findViewById(R.id.et_login_email);
        const_login = findViewById(R.id.const_login);
        probar_login = findViewById(R.id.probar_login);

        TextInputLayout inpt_login_pass = findViewById(R.id.input_login_password);

        et_login_password.setOnFocusChangeListener((v, hasFocus) ->
                inpt_login_pass.setPasswordVisibilityToggleEnabled(hasFocus));

    }
    //===========================================ON CREATE==========================================

    //-------------------------------------------------DEFINED METHODS------------------------------
    void testInputs() {

        if (et_login_email.getText().toString().isEmpty() || et_login_email.getText().toString().equals("")) {
            common.Mysnackbar(const_login, "Enter Valid Email", Snackbar.LENGTH_SHORT).show();

        } else if (et_login_password.getText().toString().isEmpty() || et_login_password.getText().toString().equals("")) {
            common.Mysnackbar(const_login, "Enter Password", Snackbar.LENGTH_SHORT).show();

        } else {
            login_with_credentials(et_login_email.getText().toString(), et_login_password.getText().toString());


        }


    }

    void login_with_credentials(final String email, String password) {

        probar_login.setVisibility(View.VISIBLE);

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        //Before going to dashboard, fetch company data
                        load_company_Data();

                    } else {

                        // If sign in fails, display a message to the user.
                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {

                            common.Mysnackbar(const_login, "Invalid Email Address",
                                    Snackbar.LENGTH_SHORT).show();

                            probar_login.setVisibility(View.INVISIBLE);
                            probar_login.clearAnimation();

                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                            common.Mysnackbar(const_login, "Wrong Password",
                                    Snackbar.LENGTH_SHORT).show();

                            probar_login.setVisibility(View.INVISIBLE);
                            probar_login.clearAnimation();
                        }


                    }

                });

    }

    void load_company_Data(){

        String uid;

        DatabaseReference comp_ref = FirebaseDatabase.getInstance().getReference("Registered_Companies");


        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            //Toast.makeText(getApplicationContext(), uid, Toast.LENGTH_LONG).show();

            comp_ref.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Company_details company = dataSnapshot.getValue(Company_details.class);

                    if (company != null){

                        probar_login.setVisibility(View.INVISIBLE);
                        probar_login.clearAnimation();

                        SharedPreferences.Editor pref_editor = common.app_pref(weak_login.get()).edit();

                        pref_editor.putString(Company_details.COMPANY_NAME,company.getComp_name());

                        if (FirebaseAuth.getInstance().getCurrentUser() != null){
                            pref_editor.putString(Company_details.COMPANY_ID, FirebaseAuth.getInstance().getCurrentUser().getUid());
                        }

                        pref_editor.apply();
                        toComp_details();

                    }else {
                        probar_login.setVisibility(View.INVISIBLE);
                        probar_login.clearAnimation();
                        common.Mysnackbar(const_login,
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


    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=INTENT-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    private void toComp_details(){
        Intent comp_details_intent = new Intent(getApplicationContext(), Registration_activity.class);
        startActivity(comp_details_intent);
    }
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=INTENT-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=


    public void login(View view) {
        testInputs();
    }


}
