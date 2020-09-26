package com.leadnsolutions.saloonappointmentscheduling.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.leadnsolutions.saloonappointmentscheduling.R;
import com.leadnsolutions.saloonappointmentscheduling.fragments.customer.fragments.CustomerAppointmentsFragment;
import com.leadnsolutions.saloonappointmentscheduling.fragments.customer.fragments.CustomerProfileFragment;
import com.leadnsolutions.saloonappointmentscheduling.fragments.customer.fragments.HomeMapsFragment;
import com.leadnsolutions.saloonappointmentscheduling.fragments.saloon.fragments.DashboardFragment;
import com.leadnsolutions.saloonappointmentscheduling.fragments.saloon.fragments.HistoryFragment;
import com.leadnsolutions.saloonappointmentscheduling.fragments.saloon.fragments.ProfileFragment;
import com.leadnsolutions.saloonappointmentscheduling.utils.UtilClass;
import com.leadnsolutions.saloonappointmentscheduling.utils.sharedPref.SharedPrefHelper;

public class MainActivity extends AppCompatActivity {

    String userType, notificationIntent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (getIntent() != null) {
            notificationIntent = getIntent().getStringExtra("Notification");
        }
        if (SharedPrefHelper.getmHelper().getUserLoginType() != null) {
            userType = SharedPrefHelper.getmHelper().getUserLoginType();
            if (userType.equalsIgnoreCase("Saloon")) {
                if (notificationIntent != null) {
//                    UtilClass.loadFragment(new DashboardFragment(), this, R.id.frame_container);

                }
                else
                    UtilClass.loadFragment(new DashboardFragment(), this, R.id.frame_container);
            } else {
                if (notificationIntent != null) {
//                    UtilClass.loadFragment(new DashboardFragment(), this, R.id.frame_container);

                } else
                    UtilClass.loadFragment(new HomeMapsFragment(), this, R.id.frame_container);
            }
        }


    }

    private Fragment fragment;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        if (userType.equalsIgnoreCase("Saloon")) {
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    UtilClass.loadFragment( new DashboardFragment(), this, R.id.frame_container);
                    return true;
                case R.id.navigation_profile:
                    UtilClass.loadFragment( new ProfileFragment(), this, R.id.frame_container);
                    return true;
                case R.id.navigation_history:
                    UtilClass.loadFragment( new HistoryFragment(), this, R.id.frame_container);
                    return true;
            }

        } else if (userType.equalsIgnoreCase("Customer")) {
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    UtilClass.loadFragment( new HomeMapsFragment(), this, R.id.frame_container);
                    return true;
                case R.id.navigation_profile:
                    UtilClass.loadFragment( new CustomerProfileFragment(), this, R.id.frame_container);
                    return true;
                case R.id.navigation_history:
                    UtilClass.loadFragment(new CustomerAppointmentsFragment(), this, R.id.frame_container);
                    return true;

            }
         }

        return false;
    };

}
