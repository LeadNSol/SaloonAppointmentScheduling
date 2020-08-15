package com.leadnsolutions.saloonappointmentscheduling.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.net.Uri;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


public class UtilClass {



    public static void loadFragment(Fragment fragment, AppCompatActivity appCompatActivity, int container) {

        FragmentTransaction transaction = appCompatActivity.getSupportFragmentManager().beginTransaction();

        transaction.replace(container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

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


}
