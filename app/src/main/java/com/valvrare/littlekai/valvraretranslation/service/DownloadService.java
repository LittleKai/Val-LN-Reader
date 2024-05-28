///*
// * Copyright (C) 2015 Hai Nguyen Thanh
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
//// */

package com.valvrare.littlekai.valvraretranslation.service;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.valvrare.littlekai.valvraretranslation.DownloadActivity;
import com.valvrare.littlekai.valvraretranslation.NovelDescriptionActivity;
import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.api.NovelLibrary;
import com.valvrare.littlekai.valvraretranslation.database.ValvrareDatabaseHelper;
import com.valvrare.littlekai.valvraretranslation.helper.LNReaderApplication;
import com.valvrare.littlekai.valvraretranslation.inner_fragment.NovelFragment2;
import com.valvrare.littlekai.valvraretranslation.model.Chapter;
import com.valvrare.littlekai.valvraretranslation.model.Novel;
import com.valvrare.littlekai.valvraretranslation.utils.Constants;
import com.valvrare.littlekai.valvraretranslation.utils.FileDownloader;
import com.valvrare.littlekai.valvraretranslation.utils.FileFunctions;
import com.valvrare.littlekai.valvraretranslation.utils.NotificationUtils;
import com.valvrare.littlekai.valvraretranslation.utils.UIHelper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;


public class DownloadService extends Service {

    private ArrayList<Chapter> list;
    private FileFunctions fileUtils;
    private String image;
    private static int numberOfDownload = 0;
    private int maxDownload = 3;
    private boolean isNotificationEnabled = true;
    NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    SharedPreferences prefs;
    private int numberOfCompleted = 0;
    private int numberOfFailed = 0;
    private boolean stop;
    private boolean isSdCardAvailable = true;
    ArrayList<String> imageUrls;
    Context context;
    Chapter chapter;
    Novel novel;
    boolean isFinished = false;
    ValvrareDatabaseHelper db;
    String TAG = "Kai";
    LNReaderApplication lnReaderApplication;
    NovelFragment2 novelFragment2;
    private ServiceCallbacks serviceCallbacks;
    Bitmap bitmap;

    public void setCallbacks(ServiceCallbacks callbacks) {
        serviceCallbacks = callbacks;
    }

    public DownloadService() {

    }

    private Drawable img;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected BroadcastReceiver stopServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stop = true;
            task.cancel(true);
            notificationManager = (NotificationManager) DownloadService.this.getSystemService(Context.NOTIFICATION_SERVICE);
            builder = new NotificationCompat.Builder(DownloadService.this);

            if (bitmap != null) {
                builder.setLargeIcon(bitmap);
            }

            builder.setContentTitle(novel.getNovelName())
                    .setSmallIcon(R.drawable.light_novel_icon)
                    .setAutoCancel(true);
            Intent notificationIntent = new Intent(DownloadService.this, NovelDescriptionActivity.class);
            notificationIntent.putExtra("downloaded", true);
            notificationIntent.putExtra("novel", novel);
            PendingIntent pendingIntent = PendingIntent.getActivity(DownloadService.this, 1337, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

            notificationManager.notify(1337, builder.build());
            builder.setContentIntent(pendingIntent);
            builder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText("Đã Tải Hoàn Thành!\n" + "Số Chương Đã Tải: " + numberOfCompleted + "\n" + "Số Chương Tải Thất Bại: " + numberOfFailed));
            notificationManager.notify(1337, builder.build());
            lnReaderApplication.setDownloading(false);
            stopSelf();
        }
    };

    @Override
    public void onCreate() {
        context = getApplicationContext();
        lnReaderApplication = (LNReaderApplication) context;
        novelFragment2 = lnReaderApplication.getNovelFragment2();
        db = new ValvrareDatabaseHelper(context);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(stopServiceReceiver);
//        list = new ArrayList<>();
        stopForeground(true);
    }

    private void handleIntent(Intent intent) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        isNotificationEnabled = true;
        list = intent.getParcelableArrayListExtra("chapters");
        novel = intent.getParcelableExtra("novel");


        if (list.size() > 0 && list != null) {
            if (notificationManager == null && builder == null) {
                registerReceiver(stopServiceReceiver, new IntentFilter("stopFilter"));
                PendingIntent stopIntent = PendingIntent.getBroadcast(this, 0, new Intent("stopFilter"), 0);
                bitmap = db.getNovelImage(novel);
//img = new BitmapDrawable(getResources(),bitmap);
                notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                builder = new NotificationCompat.Builder(this);
                if (bitmap != null) {
                    builder.setLargeIcon(bitmap);
                }
                builder.setContentTitle(novel.getNovelName())

                        .setSmallIcon(R.drawable.light_novel_icon)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
//                    .addAction(R.drawable.ic_stop_circle_grey600_24dp, "Dừng Tải", stopIntent)
                        .addAction(R.drawable.ic_stop_circle_grey600_24dp, "Hủy", stopIntent);
                builder.setProgress(100, 0, false);
//                            adapter.getCheckedCount()));

                notificationManager.notify(1337, builder.build());
//            startForeground(1337, note);
            }
            downloadTask();
        }
    }

    DownloadTask task;

    void downloadTask() {
        lnReaderApplication.setDownloading(true);
//        synchronized (list)
        {
            if (list.size() > 0) {
//                if (stop)
//                    break;
//        ArrayList<Chapter> downChapters = adapter.getSelectChapters();

                isFinished = true;
                task = new DownloadTask(list.get(0), true);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, list.get(0).getUrl());
            }
        }
    }

//    private void getChapterToDownload() {
//        Log.d("Kai", "getChapterToDownload: ");
//        builder.setContentText("Đang Tải Về...");
//        builder.setStyle(new NotificationCompat.BigTextStyle()
//                .bigText("Downloading" + "\n" + "In Queue:" + " "));
//        notificationManager.notify(1337, builder.build());
//        ImageDownloadTask task = new ImageDownloadTask(chapter, isNotificationEnabled);
//        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imageUrls);
//    }

    class DownloadTask extends AsyncTask<String, Integer, ArrayList<Object>> {
        private String novelName;
        private String chapterName;
        private boolean enabledNotification;
        private int id = NotificationUtils.getID();
        private NotificationManager mNotifyManager;
        private NotificationCompat.Builder mBuilder;
        private PendingIntent pendingIntent;
        Chapter chapter;
        ArrayList<Chapter> chapterList;
        ArrayList<String> urls;

        DownloadTask(ArrayList<Chapter> c, boolean isEnabled) {
            chapterList = c;
            enabledNotification = isEnabled;
        }

        DownloadTask(Chapter c, boolean isEnabled) {
            chapter = c;
            enabledNotification = isEnabled;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            builder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText("Đang Tải:" + " " + chapter.getName() + "\n" + "Đang Chờ:" + " " +
                            list.size()));
            notificationManager.notify(1337, builder.build());

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // Update progress
            if (enabledNotification) {
                builder.setProgress(100, values[0], false);
                notificationManager.notify(1337, builder.build());
                Log.d(TAG, "onProgressUpdate: " + values[0]);
                super.onProgressUpdate(values);
            }
        }

        @Override
        protected final ArrayList<Object> doInBackground(String... params) {
            publishProgress(0);
            NovelLibrary novelLibrary = new NovelLibrary(params[0]);
            Document result = null;
            ArrayList<ArrayList<Object>> resultArr = new ArrayList<>();
            ArrayList<Object> chapterContent = new ArrayList<>();
            try {
                result = novelLibrary.downloadChapterContent();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (result != null) {

                ArrayList<String> imageUrls = new ArrayList<>();
                Elements imgElements = null;
                if (chapter.getUrl().contains(Constants.VAL_URL_ROOT)) {
                    Elements e = result.select("div.thecontent");
                    imgElements = e.select("img");
                    for (Element ele : imgElements) {
                        ele.removeAttr("srcset");
                    }
                    String html = e.html().replace("<span style=\"font-family: ", "<span style =\"");
                    chapterContent.add(html);
                } else if (chapter.getUrl().contains(Constants.SNK_URL_ROOT)) {
                    Elements e = result.select("div.WikiaArticle");
                    imgElements = e.select("img");
                    String html = e.html();
                    chapterContent.add(html);
                }
                if (imgElements != null)
                    if (imgElements.size() > 0)
                        for (int i = 0; i < imgElements.size(); i++) {
                            Element imgE = imgElements.get(i);
                            String imgUrl = imgE.attr("src");
                            imageUrls.add(imgUrl);
                        }

                urls = imageUrls;
                if (urls.size() > 0) {
                    publishProgress((int) (100 / (urls.size() + 1)));
                    for (int i = 0; i < urls.size(); i++) {
                        try {
                            Bitmap bmap;
                            if (UIHelper.getBigImageDownloadPreferences(context))
                                bmap = Glide.with(context).load(urls.get(i)).asBitmap().into(-1, -1).get();
                            else {
                                int a = dpWidth();
                                bmap = Glide.with(context).load(urls.get(i)).asBitmap().into(a, a).get();
                            }
                            if (bmap != null)
                                chapterContent.add(bmap);
                            else chapterContent.add(null);
                            publishProgress((int) (((i + 1) * 100) / (urls.size()) + 1));
                        } catch (InterruptedException | ExecutionException e1) {
                            e1.printStackTrace();
                        }
                    }
                }

                return chapterContent;
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Object> result) {
            super.onPostExecute(result);
            if (!isCancelled())
                if (!stop) {

                    FileDownloader downloader = new FileDownloader(chapter.getNovelName(), chapter.getName(), context);
                    downloader.setActivity(lnReaderApplication.getNovelDescriptionActivity());
                    if (result != null) {
                        for (int i = 0; i < result.size(); i++) {
                            if (i == 0) {
                                if (result.size() == 1) {
                                    chapter.setDown(true);
                                    Calendar c = Calendar.getInstance();
                                    String time = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
                                    String date = c.get(Calendar.DATE) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR);
                                    chapter.setTime(time);
                                    chapter.setDate(date);
                                    db.saveChapterContent(novel, chapter, (String) result.get(i));
                                }
                            } else {
                                if (downloader.SaveImage((Bitmap) result.get(i), urls.get(i - 1))) {
                                    if (i == 1) {
                                        chapter.setDown(true);
                                        Calendar c = Calendar.getInstance();
                                        String time = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
                                        String date = c.get(Calendar.DATE) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR);

                                        chapter.setTime(time);
                                        chapter.setDate(date);
                                        db.saveChapterContent(novel, chapter, (String) result.get(0));
                                    }
                                }
//                        novelFragment2.chapterStatusUpdate(chapter);
                            }
                            lnReaderApplication.getNovelFragment2().chapterStatusUpdate(chapter);
                        }
                        numberOfCompleted++;
                    } else numberOfFailed++;
                    isFinished = false;
                    if (list.size() == 1) {
                        notificationManager = (NotificationManager) DownloadService.this.getSystemService(Context.NOTIFICATION_SERVICE);
                        builder = new NotificationCompat.Builder(DownloadService.this);

                        if (bitmap != null) {
                            builder.setLargeIcon(bitmap);
                        }

                        builder.setContentTitle(novel.getNovelName())
                                .setSmallIcon(R.drawable.light_novel_icon)
                                .setAutoCancel(true);
//                        .setContentText(novel.getNovelName());
//                        .setContentText(novel.getNovelName()+" \n" +"Đã Tải Xong!\n" + "Số Chương Đã Tải: " + numberOfCompleted + "\n" + "Số Chương Tải Thất Bại: "  + numberOfFailed);
//                            adapter.getCheckedCount()));
                        Notification note = builder.build();
                        note.defaults |= Notification.DEFAULT_VIBRATE;
                        note.defaults |= Notification.DEFAULT_SOUND;
                        note.defaults |= Notification.DEFAULT_LIGHTS;
//                        notificationManager.notify(1337, builder.build());
                        Intent notificationIntent = new Intent(DownloadService.this, NovelDescriptionActivity.class);
                        notificationIntent.putExtra("downloaded", true);
                        notificationIntent.putExtra("novel", novel);
                        PendingIntent pendingIntent = PendingIntent.getActivity(DownloadService.this, 1337, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

                        notificationManager.notify(1337, builder.build());
                        builder.setContentIntent(pendingIntent);
                        builder.setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Đã Tải Hoàn Thành!\n" + "Số Chương Đã Tải: " + numberOfCompleted + "\n" + "Số Chương Tải Thất Bại: " + numberOfFailed));
                        notificationManager.notify(1337, builder.build());
//                        Toast.makeText(context, "Đã Tải Xong", Toast.LENGTH_SHORT).show();
                        lnReaderApplication.setDownloading(false);
                        stopSelf();
                    }

                    list.remove(0);
                    //            progressDialog.hide();

                    if (list.size() > 0)
                        downloadTask();
                }
        }
    }

    class ImageDownloadTask extends AsyncTask<ArrayList<String>, Integer, ArrayList<Bitmap>> {
        private String novelName;
        private String chapterName;
        private boolean enabledNotification;
        private int id = NotificationUtils.getID();
        private NotificationManager mNotifyManager;
        private NotificationCompat.Builder mBuilder;
        private PendingIntent pendingIntent;
        Chapter chapter;

        public ImageDownloadTask(Chapter c, boolean isEnabled) {
            chapter = c;
            enabledNotification = isEnabled;
        }

        @Override
        public void onPreExecute() {
            if (enabledNotification) {
                mNotifyManager = (NotificationManager) DownloadService.this.getSystemService(Context.NOTIFICATION_SERVICE);
                mBuilder = new NotificationCompat.Builder(DownloadService.this);
                mBuilder.setContentTitle(chapter.getNovelName())
                        .setContentText("Đang Tải: " + " " + chapterName)
                        .setSmallIcon(android.R.drawable.stat_sys_download)
                        .setAutoCancel(false);
                mBuilder.setProgress(100, 0, false);
                mNotifyManager.notify(id, mBuilder.build());
                Log.d(TAG, "onPreExecute: ");
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // Update progress
            if (enabledNotification) {
                mBuilder.setProgress(100, values[0], false);
                mNotifyManager.notify(id, mBuilder.build());
                super.onProgressUpdate(values);
            }
        }

        @SafeVarargs
        @Override
        protected final ArrayList<Bitmap> doInBackground(ArrayList<String>... arrayLists) {
            ArrayList<Bitmap> images = new ArrayList<>();
            for (int i = 0; i < arrayLists.length; i++) {
                try {
                    Bitmap bmap = Glide.with(getApplicationContext()).load(arrayLists[i]).asBitmap().into(-1, -1).get();
                    images.add(bmap);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            return images;
        }

        @Override
        protected void onPostExecute(ArrayList<Bitmap> bitmaps) {
            super.onPostExecute(bitmaps);
            if (bitmaps != null) {
                for (int i = 0; i < bitmaps.size(); i++) {
                    Log.d("Kai", "onPostExecute: " + i);
                }
            }
        }
    }

    int dpWidth() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float scale = getResources().getDisplayMetrics().density;
        return (int) (displayMetrics.widthPixels / displayMetrics.density);
    }

}

