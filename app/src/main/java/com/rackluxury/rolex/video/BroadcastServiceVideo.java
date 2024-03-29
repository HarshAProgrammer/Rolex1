package com.rackluxury.rolex.video;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;

import javax.annotation.Nullable;

public class BroadcastServiceVideo extends Service {
    private final String TAG = "BroadcastService";
    public static final String COUNTDOWN_BR = "com.rackluxury.rolex.activities";
    Intent intent = new Intent(COUNTDOWN_BR);

    CountDownTimer countDownTimer = null;
    SharedPreferences sharedPreferences;
    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences(getPackageName(),MODE_PRIVATE);
        long millis = sharedPreferences.getLong("timeVideo",300000);


        countDownTimer = new CountDownTimer(millis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                intent.putExtra("countdownVideo",millisUntilFinished);
                sendBroadcast(intent);

            }

            @Override
            public void onFinish() {

            }
        };
        countDownTimer.start();
    }

    @Override
    public void onDestroy(){
        countDownTimer.cancel();
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }




}
