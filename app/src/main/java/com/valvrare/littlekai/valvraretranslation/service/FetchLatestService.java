package com.valvrare.littlekai.valvraretranslation.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.valvrare.littlekai.valvraretranslation.MainActivity;
import com.valvrare.littlekai.valvraretranslation.NovelDescriptionActivity;
import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.api.NovelLibrary;
import com.valvrare.littlekai.valvraretranslation.database.ValvrareDatabaseHelper;
import com.valvrare.littlekai.valvraretranslation.model.Chapter;
import com.valvrare.littlekai.valvraretranslation.model.Novel;
import com.valvrare.littlekai.valvraretranslation.utils.Constants;
import com.valvrare.littlekai.valvraretranslation.utils.LoadJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class FetchLatestService extends Service {

    private PowerManager.WakeLock wakeLock;
    private ValvrareDatabaseHelper db;
    private int index = 1;
    SharedPreferences prefs;
    private LoadJson loadJson;
    //    private Novel notifNovel;
    private ArrayList<Novel> novels;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void handleIntent(Intent intent) {
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Wakelock service");
        wakeLock.acquire();
        loadJson = new LoadJson(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("notifications_new_message", true)) {
            db = new ValvrareDatabaseHelper(this);
            try {
                novels = db.getAllFavoritedNovels();
                if (novels != null) {
                    new FetchLatestTask().execute(novels.get(0));
                    Log.d("Kai", "handleIntent: Searching");
                } else Log.d("Kai", "handleIntent: No novels");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wakeLock.release();
    }

    private class FetchLatestTask extends AsyncTask<Novel, Void, Novel> implements LoadJson.OnFinishLoadJSonListener {
        Novel notifNovel;

        @Override
        protected Novel doInBackground(Novel... novel) {
            return novel[0];
        }

        @Override
        protected void onPostExecute(Novel novel) {
            super.onPostExecute(novel);
            notifNovel = novel;
            if (!notifNovel.getUrl().contains(Constants.SNK_URL_ROOT) && notifNovel.isNotify() & notifNovel.getLatestChapName() != null) {
                loadJson.setOnFinishLoadJSonListener(this);
                loadJson.setNovel(notifNovel);
                loadJson.sendDataToServer(notifNovel.getNovelName(), null, false);
            } else if (novels.size() > 1) {
                novels.remove(0);
                new FetchLatestTask().execute(novels.get(0));
            }
        }

        ArrayList<Chapter> list;

        @Override
        public void finishLoadJSon(String error, String json, boolean isLast) {
            if (json != null) {

                list = loadJson.jsonToListChapter(json);
                Chapter lastestChapter = list.get(0);

                if (lastestChapter.getName() != null && !lastestChapter.getName().equals(""))
                    if (!notifNovel.getLatestChapName().equals(lastestChapter.getName()))
                        showNotification(lastestChapter, notifNovel);
            }
            if (novels.size() > 1) {
                novels.remove(0);
                new FetchLatestTask().execute(novels.get(0));
            }
        }
    }

    private int count = 0;

    private void showNotification(ArrayList<Novel> novels) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        if (novels.size() == 1) {
            mBuilder.setContentTitle(novels.get(0).getNovelName());
            mBuilder.setContentText("New chapter: " + novels.get(0).getLatestChapName());
        } else if (novels.size() > 1) {
            mBuilder.setContentTitle("New chapters");
            mBuilder.setContentText("New chapters found for your favorite novel");
        }

        mBuilder.setSmallIcon(R.drawable.icon_book);

        mBuilder.setNumber(novels.size());

        mBuilder.setDefaults(Notification.DEFAULT_ALL);

        mBuilder.setAutoCancel(true);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        for (int i = 0; i < novels.size(); i++) {
            inboxStyle.addLine(novels.get(i).toString());
        }

        mBuilder.setStyle(inboxStyle);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("favorite", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        mBuilder.setContentIntent(pendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification note = mBuilder.build();
        if (prefs.getBoolean("notifications_new_message_vibrate", true))
            note.defaults |= Notification.DEFAULT_VIBRATE;
        if (prefs.getBoolean("notifi_new_message_ringtone", true))
            note.defaults |= Notification.DEFAULT_SOUND;
        note.defaults |= Notification.DEFAULT_LIGHTS;

        /* notificationID allows you to update the notification later on. */
        mNotificationManager.notify(1, mBuilder.build());
    }

    private void showNotification(Chapter chapter, Novel notifNovel) {
        count++;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(notifNovel.getNovelName());
        mBuilder.setContentText(Html.fromHtml("Chương mới: " + "<b>" + chapter.getName() + "</b>"))
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.light_novel_icon);

        Bitmap bitmap = db.getNovelImage(notifNovel);
        if (bitmap != null)
            mBuilder.setLargeIcon(bitmap);

        Intent notificationIntent = new Intent(this, NovelDescriptionActivity.class);
//        notificationIntent.putExtra("downloaded", false);
        notificationIntent.putExtra("novel", notifNovel);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, count, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        mBuilder.setContentIntent(pendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification note = mBuilder.build();
        if (prefs.getBoolean("notifications_new_message_vibrate", true))
            note.defaults |= Notification.DEFAULT_VIBRATE;
        if (prefs.getBoolean("notifi_new_message_ringtone", true))
            note.defaults |= Notification.DEFAULT_SOUND;
        note.defaults |= Notification.DEFAULT_LIGHTS;

        /* notificationID allows you to update the notification later on. */
        mNotificationManager.notify(123 + count, mBuilder.build());

        db.updateLastestChapter(chapter);

    }

    private void writeLog() {
        try {
            OutputStreamWriter out = new OutputStreamWriter(openFileOutput("jump_service_log.txt", MODE_APPEND));


            SimpleDateFormat format = new SimpleDateFormat(
                    "dd-M-yyyy hh:mm:ss", Locale.US);
            Date date = new Date();
            String dateFormat = "Service #" + index + ": at " + format.format(date);

            index++;

            out.write(dateFormat);
            out.write('\n');

            out.close();

        } catch (java.io.IOException e) {
        }
    }


//    private void initDownload(ArrayList<Novel> result) {
//        for (int i = 0; i < result.size(); i++) {
//            ArrayList<Chapter> list = new ArrayList<>();
//            list.add(result.get(i).getChapter());
//            for(Chapter c: list) c.setNovelName(result.get(i).getNovelName());
//            Intent intent = new Intent(FetchLatestService.this, DownloadService.class);
//            intent.putExtra("image", result.get(i).getImage());
//            intent.putParcelableArrayListExtra("list", list);
//            startService(intent);
//        }
//    }
}
