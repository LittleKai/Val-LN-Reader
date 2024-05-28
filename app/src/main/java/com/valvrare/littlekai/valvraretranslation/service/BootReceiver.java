package com.valvrare.littlekai.valvraretranslation.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences("Alarm1", Context.MODE_PRIVATE);
        if (!prefs.getBoolean("Alarm1", false)) {
            Intent i = new Intent(context, FetchLatestService.class);
            PendingIntent pi = PendingIntent.getService(context, 233, i, 0);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.cancel(pi); // cancel any existing alarms
            am.setRepeating(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() +
                            180 * 60 * 1000,
                    180 * 60 * 1000, pi);
//            if (!prefs.getBoolean("Alarm1", false)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("Alarm1", true);

            editor.putBoolean("Alarm", false);
            editor.apply();
        }
    }

}
