package com.leadnsolutions.saloonappointmentscheduling.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.leadnsolutions.saloonappointmentscheduling.R;

import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {
    private final static int SPLASH_DURATION = 1000;
    private static final int RC_VIDEO_APP_PERM = 124;
    private static final int RC_SETTINGS_SCREEN_PERM = 123;


    private String TAG = SplashActivity.class.getSimpleName();

    Button btnSaloon, btnCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Objects.requireNonNull(getSupportActionBar()).hide();


    }

    @Override
    protected void onResume() {
        super.onResume();
        checkInternetConn();
    }

    private void checkInternetConn() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.
                getActiveNetworkInfo().isConnected()) {
            //Toast.makeText(this, "Internet Available", Toast.LENGTH_SHORT).show();
            requestPermissions();


        } else {

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Alert!");
            alertDialogBuilder.setMessage("Check Your  Internet Connection!");
            alertDialogBuilder.setPositiveButton("Retry", (dialog, id) -> checkInternetConn());
            alertDialogBuilder.setNegativeButton("Exit", (dialog, which) -> finish());
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.show();

        }
    }

    @Override
    public void onClick(View v) {
        String btnText;
        if (R.id.btn_Saloon == v.getId())
            btnText = btnSaloon.getText().toString();
        else
            btnText = btnCustomer.getText().toString();

        Intent intent = new Intent(this, CredentialsActivity.class);
        intent.putExtra("type", btnText);
        startActivity(intent);
        finish();
    }

    private void requestPermissions() {
        String[] perms = {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };
        if (EasyPermissions.hasPermissions(this, perms)) {
            //connectMethod();
            btnSaloon = findViewById(R.id.btn_Saloon);
            btnCustomer = findViewById(R.id.btn_Customer);

            btnSaloon.setOnClickListener(this);
            btnCustomer.setOnClickListener(this);

        } else {
            EasyPermissions.requestPermissions(this, "Permissions are needed", RC_VIDEO_APP_PERM, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setRationale(getString(R.string.rationale_ask_again))
                    .setPositiveButton("Settings")
                    .setNegativeButton("Cancel")
                    .setRequestCode(RC_SETTINGS_SCREEN_PERM)
                    .build()
                    .show();
        }
    }

}