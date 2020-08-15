package com.leadnsolutions.saloonappointmentscheduling.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.leadnsolutions.saloonappointmentscheduling.R;
import com.leadnsolutions.saloonappointmentscheduling.fragments.LoginFragment;
import com.leadnsolutions.saloonappointmentscheduling.utils.UtilClass;

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