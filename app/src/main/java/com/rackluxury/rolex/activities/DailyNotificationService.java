package com.rackluxury.rolex.activities;

import static com.rackluxury.rolex.activities.DailyNotificationApp.FCM_CHANNEL_ID;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rackluxury.rolex.R;


public class DailyNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        BitmapDrawable drawable = (BitmapDrawable) ContextCompat.getDrawable(getApplicationContext(), R.drawable.splashscreen);
        Bitmap bitmap = drawable.getBitmap();


        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();


            Intent resultIntent = new Intent(this, DailyLoginActivity.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 1,resultIntent,PendingIntent.FLAG_MUTABLE);

            Notification notification = new NotificationCompat.Builder(this, FCM_CHANNEL_ID)
                    .setLargeIcon(bitmap)
                    .setSmallIcon(R.drawable.splashscreen)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentIntent(resultPendingIntent)
                    .build();



            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(1002, notification);



        }


    }




    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        Log.d("TOKENFIREBASE",s);
    }
}

