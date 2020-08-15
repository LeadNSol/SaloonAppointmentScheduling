package com.leadnsolutions.saloonappointmentscheduling.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.leadnsolutions.saloonappointmentscheduling.R;

import java.util.Objects;


public class RestPasswordFragment extends Fragment {

    EditText forgotPassword;
    Button btnReset;

    FirebaseAuth firebaseAuth;
    private View view;
    private AppCompatActivity compatActivity;

    public RestPasswordFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_rest_password, container, false);

        initViews();

        return view;
    }

    private void initViews() {


        compatActivity = (AppCompatActivity) getActivity();
        assert compatActivity != null;
        Objects.requireNonNull(compatActivity.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        forgotPassword = view.findViewById(R.id.ed_forgot_password_email);
        btnReset = view.findViewById(R.id.btn_forgot_password);

        firebaseAuth = FirebaseAuth.getInstance();

        btnReset.setOnClickListener(v -> {
            String email = forgotPassword.getText().toString();

            if (email.isEmpty()) {
                Toast.makeText(compatActivity, "Type Your Email!", Toast.LENGTH_SHORT).show();
            } else {
                final Task<Void> voidTask = firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {

                            if (task.isSuccessful()) {
                                Toast.makeText(compatActivity, "Please check your email!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(compatActivity, LoginFragment.class));
                            } else {
                                String error = Objects.requireNonNull(task.getException()).getMessage();
                                Toast.makeText(compatActivity, error, Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        });
    }

}