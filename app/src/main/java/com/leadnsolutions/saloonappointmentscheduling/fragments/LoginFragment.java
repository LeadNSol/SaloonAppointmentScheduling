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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.leadnsolutions.saloonappointmentscheduling.R;
import com.leadnsolutions.saloonappointmentscheduling.activity.MainActivity;
import com.leadnsolutions.saloonappointmentscheduling.fragments.customer.fragments.CustomerRegistrationFragment;
import com.leadnsolutions.saloonappointmentscheduling.fragments.customer.model.CustomerModel;
import com.leadnsolutions.saloonappointmentscheduling.fragments.saloon.SaloonRegistrationFragment;
import com.leadnsolutions.saloonappointmentscheduling.fragments.saloon.model.SaloonModel;
import com.leadnsolutions.saloonappointmentscheduling.notification.models.Token;
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

    public LoginFragment() {

    }

    public LoginFragment(String btnText) {
        this.type = btnText;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


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

        tvRegisterNow.setOnClickListener(v -> {
            if (type.equals("Saloon")) {
                UtilClass.loadFragment(new SaloonRegistrationFragment(), mActivity, R.id.credentials_frame_layout);
            } else
                UtilClass.loadFragment(new CustomerRegistrationFragment(), mActivity, R.id.credentials_frame_layout);

            //Toast.makeText(appCompatActivity, "Hello", Toast.LENGTH_SHORT).show();
        });

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
                            navigateToActivity(MainActivity.class);
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
                            navigateToActivity(MainActivity.class);
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
        Intent intent = new Intent(mActivity, activity);
        intent.putExtra("type", type);
        mActivity.startActivity(intent);

        mActivity.finish();


        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult ->
                updateToken(instanceIdResult.getToken()));
    }

    private void updateToken(String newToken) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(newToken);
        dbRef.child(firebaseUser.getUid()).setValue(token);
    }

}