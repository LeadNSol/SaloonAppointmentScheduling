package com.leadnsolutions.saloonappointmentscheduling.notification.retrofit;

import com.leadnsolutions.saloonappointmentscheduling.notification.NotificationApi;

public class RetrofitHelper implements IRetrofitHelper {

    private static RetrofitHelper mHelper;

    public static RetrofitHelper getInstance() {
        if (mHelper == null)
            mHelper = new RetrofitHelper();
        return mHelper;
    }


    @Override
    public NotificationApi getNotificationApiClient() {
        return RetrofitClient.getClient().create(NotificationApi.class);
    }
}
