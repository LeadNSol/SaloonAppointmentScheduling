package com.leadnsolutions.saloonappointmentscheduling.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.leadnsolutions.saloonappointmentscheduling.R;
import com.leadnsolutions.saloonappointmentscheduling.customer.CustomerDashboardActivity;
import com.leadnsolutions.saloonappointmentscheduling.customer.model.CustomerModel;
import com.leadnsolutions.saloonappointmentscheduling.saloon.SaloonDashboardActivity;
import com.leadnsolutions.saloonappointmentscheduling.saloon.model.SaloonModel;
import com.leadnsolutions.saloonappointmentscheduling.utils.AppConstant;
import com.leadnsolutions.saloonappointmentscheduling.utils.UtilClass;
import com.leadnsolutions.saloonappointmentscheduling.utils.sharedPref.SharedPrefHelper;

import java.util.Objects;


public class LoginFragment extends Fragment {

    String type;
    EditText edEmail, edPassword;
    TextView tvLogin, tvRegisterNow, tvForgotPassword;
    View view;
    Button btnLogin;
    FirebaseAuth auth;
    DatabaseReference dbRef;
    private AppCompatActivity mActivity;

    public LoginFragment(String btnText) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        type = Objects.requireNonNull(getActivity()).getIntent().getStringExtra("type");

        view = inflater.inflate(R.layout.fragment_login, container, false);

        initViews();

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void initViews() {

        mActivity = (AppCompatActivity) getActivity();


        edEmail = view.findViewById(R.id.edEmail);
        edPassword = view.findViewById(R.id.edPassword);
        tvLogin = view.findViewById(R.id.tvLogin);
        tvLogin.setText(type + " Login");
        tvRegisterNow = view.findViewById(R.id.tv_register_now);
        tvForgotPassword = view.findViewById(R.id.tv_forgot_password);
        btnLogin = view.findViewById(R.id.btnLogin);

        if (type.equals("Saloon")) {
            if (SharedPrefHelper.getmHelper().getSaloonModel() != null) {
                Intent intent = new Intent(mActivity, SaloonDashboardActivity.class);
                mActivity.startActivity(intent);
                mActivity.finish();
            } else {
                tvRegisterNow.setOnClickListener(v -> {
                    UtilClass.loadFragment(new SaloonRegistrationFragment(), mActivity, R.id.credentials_frame_layout);
                    //Toast.makeText(appCompatActivity, "Hello", Toast.LENGTH_SHORT).show();
                });
            }
        } else {
            if (SharedPrefHelper.getmHelper().getCustomerModel() != null) {
                Intent intent = new Intent(mActivity, CustomerDashboardActivity.class);
                mActivity.startActivity(intent);
                mActivity.finish();
            } else
                tvRegisterNow.setOnClickListener(v -> {
                    UtilClass.loadFragment(new CustomerRegistrationFragment(), mActivity, R.id.credentials_frame_layout);
                    //Toast.makeText(appCompatActivity, "Hello", Toast.LENGTH_SHORT).show();
                });
        }


        tvForgotPassword.setOnClickListener(v -> {

            UtilClass.loadFragment(new RestPasswordFragment(), mActivity, R.id.credentials_frame_layout);

            Toast.makeText(mActivity, "Forgot Password!", Toast.LENGTH_SHORT).show();
        });

        btnLogin.setOnClickListener(v -> loginUser());

    }

    private void loginUser() {

        auth = FirebaseAuth.getInstance();

        String email = edEmail.getText().toString();
        String pass = edPassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            Toast.makeText(mActivity, "All fields are required!", Toast.LENGTH_SHORT).show();
        } else {

            auth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            if (type.equals("Saloon")) {
                                checkSaloonLogin(email, pass);
                            } else {
                                checkCustomerLogin(email, pass);
                            }

                        } else {
                            Toast.makeText(mActivity, "Authentication failed!", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    private void checkCustomerLogin(String email, String pass) {
        dbRef = FirebaseDatabase.getInstance().getReference(AppConstant.CUSTOMER);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    if (Objects.equals(snap.child("email").getValue(), email)
                            && Objects.equals(snap.child("password").getValue(), pass)) {
                        CustomerModel customerModel = snap.getValue(CustomerModel.class);

                        if (customerModel != null) {
                            SharedPrefHelper.getmHelper().setCustomerModel(new Gson().toJson(customerModel));
                            navigateToActivity(CustomerDashboardActivity.class);
                        } else {
                            Toast.makeText(mActivity, "User is a Customer", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkSaloonLogin(String email, String pass) {
        dbRef = FirebaseDatabase.getInstance().getReference(AppConstant.SALOON);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    if (Objects.equals(snap.child("email").getValue(), email)
                            && Objects.equals(snap.child("password").getValue(), pass)) {
                        SaloonModel saloonModel = snap.getValue(SaloonModel.class);

                        if (saloonModel != null) {
                            SharedPrefHelper.getmHelper().setSaloonModel(new Gson().toJson(saloonModel));
                            navigateToActivity(SaloonDashboardActivity.class);
                        } else {
                            Toast.makeText(mActivity, "User is not a Saloon Owner", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void navigateToActivity(Class activity) {
        mActivity.startActivity(new Intent(mActivity, activity));
        mActivity.finish();
    }

}