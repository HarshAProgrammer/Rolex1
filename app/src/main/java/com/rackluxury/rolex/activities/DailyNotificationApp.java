package com.rackluxury.rolex.activities;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class DailyNotificationApp extends Application {

    public static final String FCM_CHANNEL_ID = "FCM_CHANNEL_DAILY_LOGIN_ID";

    @Override
    public void onCreate() {
        super.onCreate();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel fcmChannel = new NotificationChannel(
                    FCM_CHANNEL_ID, "FCM_Channel", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            manager.createNotificationChannel(fcmChannel);
        }

    }
}