package com.leadnsolutions.saloonappointmentscheduling.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.leadnsolutions.saloonappointmentscheduling.R;
import com.leadnsolutions.saloonappointmentscheduling.fragments.LoginFragment;
import com.leadnsolutions.saloonappointmentscheduling.utils.UtilClass;
import com.leadnsolutions.saloonappointmentscheduling.utils.sharedPref.SharedPrefHelper;

public class CredentialsActivity extends AppCompatActivity implements View.OnClickListener {

    Fragment fragment;
    private String btnText;
    private ConstraintLayout loginOptionLayout;
    private FrameLayout frameLayout;
    Button btnSaloon, btnCustomer;
    private boolean isLoginOptionShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentials);
        getSupportActionBar().setTitle("Login Option");

        loginOptionLayout = findViewById(R.id.login_option_layout);
        btnCustomer = findViewById(R.id.btn_customer);
        btnSaloon = findViewById(R.id.btn_saloon);

        frameLayout = findViewById(R.id.credentials_frame_layout);


        btnSaloon.setOnClickListener(this);
        btnCustomer.setOnClickListener(this);

        if (loginOptionLayout.getVisibility() == View.VISIBLE)
            isLoginOptionShown = true;

    }

    @Override
    public void onClick(View v) {
        String btnText;
        if (R.id.btn_saloon == v.getId())
            btnText = btnSaloon.getText().toString();
        else
            btnText = btnCustomer.getText().toString();

        if (isLoginOptionShown)
            loginOptionLayout.setVisibility(View.GONE);

        frameLayout.setVisibility(View.VISIBLE);
        fragment = new LoginFragment(btnText);
        UtilClass.loadFragment(fragment, this, R.id.credentials_frame_layout);

        //storing login type for get access to main without choosing login type
        SharedPrefHelper.getmHelper().setUserLoginType(btnText);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}