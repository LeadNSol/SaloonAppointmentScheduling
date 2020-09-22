package com.leadnsolutions.saloonappointmentscheduling.utils.sharedPref;

public interface ISharedPrefHelper {

    String getSaloonModel();

    void setSaloonModel(String saloonModel);

    String getCustomerModel();

    void setCustomerModel(String customerModel);

    void clearPreferences();

    void setFUserUID(String UID);
    String getFUserUID();
}
