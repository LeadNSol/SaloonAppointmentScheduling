package com.leadnsolutions.saloonappointmentscheduling.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.leadnsolutions.saloonappointmentscheduling.R;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnSaloon, btnCustomer;
    private String btnText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        checkInternetConn();
    }

    private void checkInternetConn() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.
                getActiveNetworkInfo().isConnected()) {
            //Toast.makeText(this, "Internet Available", Toast.LENGTH_SHORT).show();

            btnSaloon = findViewById(R.id.btn_Saloon);
            btnCustomer = findViewById(R.id.btn_Customer);

            btnSaloon.setOnClickListener(this);
            btnCustomer.setOnClickListener(this);

        } else {

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Alert!");
            alertDialogBuilder.setMessage("Check Your  Internet Connection!");
            alertDialogBuilder.setPositiveButton("Retry", (dialog, id) -> {
                checkInternetConn();

            });
            alertDialogBuilder.setNegativeButton("Exit", (dialog, which) -> {
                finish();
            });
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.show();

        }
    }

    @Override
    public void onClick(View v) {
        if (R.id.btn_Saloon == v.getId())
            btnText = btnSaloon.getText().toString();
        else
            btnText = btnCustomer.getText().toString();

        Intent intent = new Intent(this, CredentialsActivity.class);
        intent.putExtra("type", btnText);
        startActivity(intent);
        finish();
    }
}