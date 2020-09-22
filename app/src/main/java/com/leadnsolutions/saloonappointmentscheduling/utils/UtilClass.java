package com.leadnsolutions.saloonappointmentscheduling.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.leadnsolutions.saloonappointmentscheduling.notification.models.Token;


public class UtilClass {



    public static void loadFragment(Fragment fragment, AppCompatActivity appCompatActivity, int container) {

        FragmentTransaction transaction = appCompatActivity.getSupportFragmentManager().beginTransaction();

        transaction.replace(container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public static void updateToken(String userUID, String newToken) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(newToken);
        dbRef.child(userUID).setValue(token);
    }


    public static String getFileExtension(Uri uri, AppCompatActivity activity) {

        ContentResolver contentResolver = activity.getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();

        return map.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    public static void hideSoftKeyboard(AppCompatActivity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
    }


    public static void locationDisableAlert(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                   context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                })
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }

}
