package com.leadnsolutions.saloonappointmentscheduling.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.leadnsolutions.saloonappointmentscheduling.appointments.AppointmentsActivity;
import com.leadnsolutions.saloonappointmentscheduling.notification.models.OreoAndAboveNotification;
import com.leadnsolutions.saloonappointmentscheduling.notification.models.Token;
import com.leadnsolutions.saloonappointmentscheduling.saloon.SaloonDashboardActivity;
import com.leadnsolutions.saloonappointmentscheduling.utils.AppConstant;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final String CHANNEL_ID = "Saloon_channel";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            String newToken = instanceIdResult.getToken();
            if (user != null)
                updateToken(newToken, user);
        });
    }

    private void updateToken(String newToken, FirebaseUser user) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(AppConstant.TOKENS);
        Token token = new Token(newToken);
        dbRef.child(user.getUid()).setValue(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String sent = remoteMessage.getData().get("sent");
        String user = remoteMessage.getData().get("user");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null && sent != null && sent.equalsIgnoreCase(firebaseUser.getUid())) {
            if (user != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendOreoAndAboveNotification(remoteMessage);
                } else {
                    sendNotification(remoteMessage);
                }
            }
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String body = remoteMessage.getData().get("body");
        String title = remoteMessage.getData().get("title");
        String icon = remoteMessage.getData().get("icon");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        assert user != null;
        int i = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, AppointmentsActivity.class); // will be set according to
        Bundle bundle = new Bundle();
        bundle.putString("hisUid", user);
        intent.putExtras(bundle);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification.Builder notiBuilder = new Notification.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentText(body)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int j = 0;
        if (i > 0) {
            j = i;
        }
        notificationManager.notify(j, notiBuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendOreoAndAboveNotification(RemoteMessage remoteMessage) {
        String userId = remoteMessage.getData().get("myId");
        String body = remoteMessage.getData().get("body");
        String title = remoteMessage.getData().get("title");
        String icon = remoteMessage.getData().get("icon");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int i = Integer.parseInt(userId.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, SaloonDashboardActivity.class); // will be set according to
        Bundle bundle = new Bundle();
        bundle.putString("myId", userId);
        intent.putExtras(bundle);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        OreoAndAboveNotification oreoAndAboveNotification = new OreoAndAboveNotification(getApplicationContext());
        Notification.Builder notiBuilder = oreoAndAboveNotification
                .getOreoAboveNotification(title,
                        body,
                        pendingIntent,
                        soundUri,
                        Integer.parseInt(icon));

        int j = 0;
        if (i > 0) {
            j = i;
        }
        oreoAndAboveNotification.getNotificationManager().notify(j, notiBuilder.build());
    }
}
