package com.leadnsolutions.saloonappointmentscheduling.fragments.customer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.leadnsolutions.saloonappointmentscheduling.R;
import com.leadnsolutions.saloonappointmentscheduling.fragments.customer.adapter.AppointmentsAdapter;
import com.leadnsolutions.saloonappointmentscheduling.fragments.customer.model.AppointmentModel;
import com.leadnsolutions.saloonappointmentscheduling.fragments.customer.model.CustomerModel;
import com.leadnsolutions.saloonappointmentscheduling.utils.AppConstant;
import com.leadnsolutions.saloonappointmentscheduling.utils.sharedPref.SharedPrefHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CustomerAppointmentsFragment extends Fragment {


    public CustomerAppointmentsFragment() {
        // Required empty public constructor
    }

    private AppCompatActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_appointments, container, false);
    }

    private TabLayout tabLayout;
    private RecyclerView mRvAppointments;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = (AppCompatActivity) getActivity();
        if (mActivity != null && mActivity.getSupportActionBar() != null) {
            mActivity.getSupportActionBar().setTitle("Customer Appointments");
        }

        mRvAppointments = view.findViewById(R.id.rv_appointments);
        mRvAppointments.setLayoutManager(new LinearLayoutManager(mActivity));

        tabLayout = view.findViewById(R.id.tabLayout);
        if (tabLayout.getSelectedTabPosition() == 0) {
            populateData(tabLayout.getTabAt(0).getText().toString());
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mRvAppointments.removeAllViews();
                populateData(Objects.requireNonNull(tab.getText()).toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    private List<AppointmentModel> mPendingAppointmentList;
    private List<AppointmentModel> mScheduledAppointmentList;

    private void populateData(String tabText) {
        if (SharedPrefHelper.getmHelper().getCustomerModel() != null) {
            CustomerModel customerModel = new Gson().fromJson(SharedPrefHelper.getmHelper().getCustomerModel(), CustomerModel.class);
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(AppConstant.APPOINTMENTS);
            Query query = dbRef.orderByChild("senderId").equalTo(customerModel.getId());

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        mPendingAppointmentList = new ArrayList<>();
                        mScheduledAppointmentList = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            AppointmentModel appointmentModel = child.getValue(AppointmentModel.class);
                            assert appointmentModel != null;

                            if (appointmentModel.getStatus().equalsIgnoreCase("Pending")) {
                                mPendingAppointmentList.add(appointmentModel);
                            } else
                                mScheduledAppointmentList.add(appointmentModel);
                        }
                        AppointmentsAdapter appointmentsAdapter;
                        if (tabText.equalsIgnoreCase("Pending")) {
                            appointmentsAdapter = new AppointmentsAdapter(mActivity, tabText, mPendingAppointmentList);
                        } else {
                            appointmentsAdapter = new AppointmentsAdapter(mActivity, tabText, mScheduledAppointmentList);
                        }
                        mRvAppointments.setAdapter(appointmentsAdapter);
                        appointmentsAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

}