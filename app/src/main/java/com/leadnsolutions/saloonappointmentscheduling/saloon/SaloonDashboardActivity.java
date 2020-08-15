package com.leadnsolutions.saloonappointmentscheduling.saloon;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.leadnsolutions.saloonappointmentscheduling.R;
import com.leadnsolutions.saloonappointmentscheduling.saloon.fragments.DashboardFragment;
import com.leadnsolutions.saloonappointmentscheduling.saloon.fragments.HistoryFragment;
import com.leadnsolutions.saloonappointmentscheduling.saloon.fragments.NotificationFragment;
import com.leadnsolutions.saloonappointmentscheduling.saloon.fragments.ProfileFragment;
import com.leadnsolutions.saloonappointmentscheduling.utils.UtilClass;

public class SaloonDashboardActivity extends AppCompatActivity {

    private Fragment fragment;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:

                    fragment = new DashboardFragment();
                    UtilClass.loadFragment(fragment, SaloonDashboardActivity.this, R.id.frame_container);
                    return true;
                case R.id.navigation_profile:

                    fragment = new ProfileFragment();
                    UtilClass.loadFragment(fragment, SaloonDashboardActivity.this, R.id.frame_container);
                    return true;
                case R.id.navigation_history:
                    fragment = new HistoryFragment();
                    UtilClass.loadFragment(fragment, SaloonDashboardActivity.this, R.id.frame_container);

                    return true;
                case R.id.navigation_notification:
                    fragment = new NotificationFragment();
                    UtilClass.loadFragment(fragment, SaloonDashboardActivity.this, R.id.frame_container);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saloon_dashboard);
        getSupportActionBar().hide();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragment = new DashboardFragment();
        UtilClass.loadFragment(fragment, SaloonDashboardActivity.this, R.id.frame_container);

    }

}