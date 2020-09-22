package com.leadnsolutions.saloonappointmentscheduling.utils.sharedPref;

import android.content.Context;
import android.content.SharedPreferences;

import com.leadnsolutions.saloonappointmentscheduling.utils.AppContext;

public class SharedPrefHelper implements ISharedPrefHelper {
    private static final String PREF_NAME = "PREF_NAME";
    private static final String PREF_SALOON_MODEL = "PREF_SALOON_MODEL";
    private static final String PREF_Customer_MODEL = "PREF_CUSTOMER_MODEL";
    private static final String PREF_F_USER_UID = "PREF_F_USER_UID";


    public static SharedPrefHelper mHelper;
    private SharedPreferences mPreferences;

    public SharedPrefHelper() {
        mPreferences = AppContext.getmContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPrefHelper getmHelper() {
        if (mHelper == null) {
            mHelper = new SharedPrefHelper();
        }
        return mHelper;
    }

    @Override
    public String getSaloonModel() {
        return mPreferences.getString(PREF_SALOON_MODEL, null);
    }

    @Override
    public void setSaloonModel(String saloonModel) {
        mPreferences.edit().putString(PREF_SALOON_MODEL, saloonModel).apply();
    }

    @Override
    public String getCustomerModel() {
        return mPreferences.getString(PREF_Customer_MODEL, null);
    }

    @Override
    public void setCustomerModel(String customerModel) {
        mPreferences.edit().putString(PREF_Customer_MODEL, customerModel).apply();

    }

    @Override
    public void clearPreferences() {
        mPreferences.edit().clear().apply();
    }

    @Override
    public void setFUserUID(String UID) {
        mPreferences.edit().putString(PREF_F_USER_UID, UID).apply();
    }

    @Override
    public String getFUserUID() {
        return mPreferences.getString(PREF_F_USER_UID, null);
    }
}
