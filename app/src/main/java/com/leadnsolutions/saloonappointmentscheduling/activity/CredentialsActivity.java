package com.leadnsolutions.saloonappointmentscheduling.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.leadnsolutions.saloonappointmentscheduling.R;
import com.leadnsolutions.saloonappointmentscheduling.fragments.LoginFragment;
import com.leadnsolutions.saloonappointmentscheduling.notification.models.Token;
import com.leadnsolutions.saloonappointmentscheduling.saloon.model.SaloonModel;
import com.leadnsolutions.saloonappointmentscheduling.utils.UtilClass;
import com.leadnsolutions.saloonappointmentscheduling.utils.sharedPref.SharedPrefHelper;

public class CredentialsActivity extends AppCompatActivity {

    Fragment fragment;
    private String btnText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentials);
        getSupportActionBar().setTitle("Login");
        if (getIntent() != null) {
            btnText = getIntent().getStringExtra("type");
        }
        fragment = new LoginFragment(btnText);
        UtilClass.loadFragment(fragment, this, R.id.credentials_frame_layout);


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}