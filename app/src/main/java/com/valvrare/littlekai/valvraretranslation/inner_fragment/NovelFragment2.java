package com.valvrare.littlekai.valvraretranslation.inner_fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.valvrare.littlekai.valvraretranslation.ChapterReadingActivity;
import com.valvrare.littlekai.valvraretranslation.NovelDescriptionActivity;
import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.adapter.ChapterListAdapter;
import com.valvrare.littlekai.valvraretranslation.api.NovelLibrary;
import com.valvrare.littlekai.valvraretranslation.database.ValvrareDatabaseHelper;
import com.valvrare.littlekai.valvraretranslation.database.Var;
import com.valvrare.littlekai.valvraretranslation.helper.FunctionHelper;
import com.valvrare.littlekai.valvraretranslation.helper.LNReaderApplication;
import com.valvrare.littlekai.valvraretranslation.model.Chapter;
import com.valvrare.littlekai.valvraretranslation.model.Novel;
import com.valvrare.littlekai.valvraretranslation.utils.Constants;
import com.valvrare.littlekai.valvraretranslation.utils.FileDownloader;
import com.valvrare.littlekai.valvraretranslation.utils.LoadJson;
import com.valvrare.littlekai.valvraretranslation.utils.NotificationUtils;
import com.valvrare.littlekai.valvraretranslation.utils.UIHelper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

//import com.valvrare.littlekai.valvraretranslation.utils.OnFinishLoadJSonListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class NovelFragment2 extends Fragment implements AdapterView.OnItemClickListener, ChapterStatusUpdate, LoadJson.OnFinishLoadJSonListener {
    private ArrayList<Chapter> listChapters, copy_list;
    private Novel novel;
    ListView lv_ChapterList;
    RelativeLayout llout_chapterlistfragment;
    private ValvrareDatabaseHelper db;
    private ChapterListAdapter adapter;
    Context context;
    LoadJson loadJson;
    String TAG = "Kai";
    private ProgressBar progressBar;
    NovelDescriptionActivity activity;
    LNReaderApplication lnReaderApplication;
    boolean isStarted = false;
    private boolean isDownloadedNovel = false;

    //    public OnFinishLoadJSonListener onFinishLoadJSonListener;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_novel_fragment2, container, false);
        llout_chapterlistfragment = (RelativeLayout) v.findViewById(R.id.llout_chapterlistfragment);
        lv_ChapterList = (ListView) v.findViewById(R.id.lv_ChapterList);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar_chapterlist);
        progressBar.setVisibility(View.GONE);
        context = getActivity();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        db = new ValvrareDatabaseHelper(context);
//        db= new ChapterContentHelper(context);
        lnReaderApplication = (LNReaderApplication) getActivity().getApplicationContext();
        activity = lnReaderApplication.getNovelDescriptionActivity();
        novel = activity.getNovel();
        isDownloadedNovel = activity.isDownloadedNovel();
        copy_list = new ArrayList<>();
        listChapters = new ArrayList<>();
        loadJson = new LoadJson(context, novel);
//        LoadJson.setContext(context);
        loadJson.setOnFinishLoadJSonListener(this);
        if (novel.getUrl().contains(Constants.VAL_URL_ROOT)) {
            if (FunctionHelper.isNetworkAvailable(context) & !isDownloadedNovel) {
                progressBar.setVisibility(View.VISIBLE);
                loadJson.sendDataToServer(novel.getNovelName(), null, false);
            } else if(isDownloadedNovel) {
                loadChapterListFromDB();
            }
        } else if (novel.getUrl().contains(Constants.SNK_URL_ROOT)) {
            if (novel.getChapterList() != null) {
                listChapters.clear();
                listChapters.addAll(new FunctionHelper().json_to_ChapterList(novel.getChapterList(), novel));
//                                listChapters.addAll(json_to_ChapterList(novel.getChapterList(), novel));
                if (listChapters.size() > 0) {
//                    adapter1 = new SnkChapterListAdapter(getActivity(), R.layout.item_snk_chapter_list, listChapters);

                    adapter = new ChapterListAdapter(getActivity(), R.layout.item_snk_chapter_list, listChapters);
                    lv_ChapterList.setAdapter(adapter);
                    lv_ChapterList.setOnItemClickListener(NovelFragment2.this);
                    registerForContextMenu(lv_ChapterList);
                    if (progressBar.getVisibility() == View.VISIBLE)
                        progressBar.setVisibility(View.GONE);
                    activity.setNovelLastest(listChapters.get(0).getName(), false);
                    FunctionHelper.checkEnableDownloadChapters(listChapters);

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
//                    for (Chapter chapter : listChapters) {
                            for (int i = 0; i < listChapters.size(); i++) {
                                Chapter chapter = listChapters.get(i);
                                boolean isRead = db.isChapterRead(chapter);
                                boolean isFav = db.isChapterFav(chapter);
                                boolean isDown = db.isDownloadedChapterContentExist(chapter);
                                chapter.setDown(isDown);
                                chapter.setFav(isFav);
                                chapter.setRead(isRead);
                                if (i % 10 == 0)
                                    publishProgress();
                            }
                            return null;
                        }

                        @Override
                        protected void onProgressUpdate(Void... values) {
                            if (adapter != null)
                                adapter.notifyDataSetChanged();
                            super.onProgressUpdate(values);
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            if (adapter != null)
                                adapter.notifyDataSetChanged();
                            if (listChapters != null)
                                if (listChapters.size() > 0) {
                                    copy_list.clear();
                                    copy_list.addAll(listChapters);
                                    activity.changeMaxChapter(listChapters.size());
                                }
                        }
                    }.execute();
                }
            }
        }

        return v;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.lv_ChapterList) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Chapter chapter = adapter.getItem(info.position);
            menu.setHeaderTitle(chapter.getName());
//            String[] menuItems = {"Tải Xuống", "Đánh Dấu Chương"};
            String[] menuItems = {"Tải Xuống"};
            if (chapter.isDown())
                menuItems[0] = "Xóa Tải Xuống";
//            if (chapter.isFav())
//                menuItems[1] = "Bỏ Dấu Chương";
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        final Chapter chapter = adapter.getItem(info.position);
        String[] menuItems = {"Tải Xuống", "Đánh Dấu Chương"};
        assert chapter != null;
        if (chapter.isDown())
            menuItems[0] = "Xóa Tải Xuống";
//        if (chapter.isFav())
//            menuItems[1] = "Bỏ Dấu Chương";
        String menuItemName = menuItems[menuItemIndex];

        if (menuItemName.equals("Xóa Tải Xuống")) {
            if (db.deleteDownloadedChapter(chapter)) {
                new AsyncTask<Novel, Void, Void>() {
                    @Override
                    protected Void doInBackground(Novel... novels) {
                        FileDownloader fl = new FileDownloader();
                        fl.deleteChapter(chapter);
                        return null;
                    }
                }.execute(novel);
                chapter.setDown(false);
                adapter.notifyDataSetChanged();
                Toast.makeText(context, "Đã Xóa Chương", Toast.LENGTH_SHORT).show();
            }
        }

        if (menuItemName.equals("Tải Xuống")) {
            if (lnReaderApplication.isDownloading()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Thông Báo")
                        .setMessage("Hiện đang tải chương, hãy đợi đến khi đã Hoàn Thành Tải Chương hoặc chọn Hủy Tiến Trình Tải.")
                        .setCancelable(false)
                        .setNegativeButton("Đã Hiểu", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else if (chapter.isEnableDownload()) {
                ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
                boolean isWifi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
//            boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
                if (isWifi | prefs.getBoolean("3g_enable", true)) {
                    ImageDownloadTask task = new ImageDownloadTask(chapter, isNotificationEnabled);
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, chapter.getUrl());
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Thông Báo")
                            .setMessage("Hiện bạn đang tắt chức năng Tải Xuống Bằng 3G, bạn có muốn bật lại không?")
                            .setCancelable(false)
                            .setNegativeButton("Từ Chối", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton("Đồng Ý", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putBoolean("3g_enable", true);
                                    editor.apply();
                                    ImageDownloadTask task = new ImageDownloadTask(chapter, isNotificationEnabled);
                                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, chapter.getUrl());
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Thông Báo")
                        .setMessage("Chương này không được phép tải!\n" +
                                "Số chương được phép tải bị giới hạn là để hạn chế việc người đọc chỉ đọc Offline mà không bình luận hay đánh giá gì đối với những chương mới. " +
                                "Việc giới hạn này chính là vì mục đích tạo nguồn động lực cho nhóm dịch.\nXin lỗi vì sự bất tiện này.")
                        .setCancelable(false)
                        .setNegativeButton("Đã Hiểu", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
        return true;
    }

    SharedPreferences prefs;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Chapter listChapter = (Chapter) parent.getItemAtPosition(position);
//                listChapters.get(position);
        Intent intent = new Intent(getActivity(), ChapterReadingActivity.class);
        intent.putExtra("chapter", listChapter);
        intent.putExtra("novel", novel);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isStarted) {
            isStarted = true;
        } else if (listChapters != null)
            if (listChapters.size() > 0) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        for (int i = 0; i < listChapters.size(); i++) {
                            long time = System.currentTimeMillis();
//                    Chapter chapter = listChapters.get(i);
                            boolean isRead = db.isChapterRead(listChapters.get(i));
                            boolean isFav = db.isChapterFav(listChapters.get(i));
                            listChapters.get(i).setFav(isFav);
                            listChapters.get(i).setRead(isRead);
                            if (i % 30 == 0)
                                publishProgress();
                        }
                        return null;
                    }

                    @Override
                    protected void onProgressUpdate(Void... values) {
                        if (adapter != null & listChapters.size() != 0) {
                            adapter.notifyDataSetChanged();
                        }
                        super.onProgressUpdate(values);
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        if (adapter != null)
                            adapter.notifyDataSetChanged();
                    }
                }.execute();
            }
    }

    ArrayList<Chapter> list;

    void loadChapterListFromDB() {
        final String data = db.getChapterList(novel);
        if (data != null) {
            Log.d(TAG, "data: "+data);
//            ArrayList<Chapter> list = new ArrayList<>();
//            FunctionHelper.json_to_chapterList(data,novel,list);
            ArrayList<Chapter> list = new FunctionHelper().json_to_ChapterList(data, novel);
            Log.d(TAG, "loadChapterListFromDB: "+list.size());
            if (list.size() > 0)
                listChapters.addAll(list);

            if (listChapters.size() > 0) {
                Log.d(TAG, "loadChapterListFromDB: start adapter");
                FunctionHelper.checkEnableDownloadChapters(listChapters);
                activity.setNovelLastest(listChapters.get(0).getName(), false);
                adapter = new ChapterListAdapter(context, R.layout.item_val_chapter_list, listChapters);
                lv_ChapterList.setAdapter(adapter);
                lv_ChapterList.setOnItemClickListener(NovelFragment2.this);
                registerForContextMenu(lv_ChapterList);
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
//                    for (Chapter chapter : listChapters) {
                        for (int i = 0; i < listChapters.size(); i++) {
                            Chapter chapter = listChapters.get(i);
                            boolean isRead = db.isChapterRead(chapter);
                            boolean isFav = db.isChapterFav(chapter);
                            boolean isDown = db.isDownloadedChapterContentExist(chapter);
                            chapter.setDown(isDown);
                            chapter.setFav(isFav);
                            chapter.setRead(isRead);
                            if (i % 10 == 0)
                                publishProgress();
                        }
                        return null;
                    }

                    @Override
                    protected void onProgressUpdate(Void... values) {
                        if (adapter != null)
                            adapter.notifyDataSetChanged();
                        super.onProgressUpdate(values);
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        if (adapter != null)
                            adapter.notifyDataSetChanged();
                        if (listChapters != null)
                            if (listChapters.size() > 0) {
                                copy_list.clear();
                                copy_list.addAll(listChapters);
                                activity.changeMaxChapter(listChapters.size());
                            }
                    }
                }.execute();
                if (progressBar.getVisibility() == View.VISIBLE)
                    progressBar.setVisibility(View.GONE);
            }
        } else {
            loadJson.sendDataToServer(novel.getNovelName(), null, false);
            progressBar.setVisibility(View.GONE);
        }

//        if (error != null) Var.showToast(context, error);
//        else if (!isDownloadedNovel)
//            Toast.makeText(context, "Xin kiểm tra lại kết nối Internet!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finishLoadJSon(String error, String json, boolean isLast) {

        if (json == null) {
            progressBar.setVisibility(View.GONE);
            if (error != null) Var.showToast(context, error);
        } else {
//            json = json.substring(json.indexOf('{'), json.lastIndexOf('}') + 1);
            final String finalJson = json;
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    list = loadJson.jsonToListChapter(finalJson);
                    listChapters.clear();
                    if (list != null)
                        listChapters.addAll(list);
                    String json = FunctionHelper.snkChapterList_to_json(listChapters);
                    if (json != null) {
                        novel.setChapterList(json);
                        db.insertChapterList(novel);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    if (listChapters != null)
                        if (listChapters.size() > 0) {
                            FunctionHelper.checkEnableDownloadChapters(listChapters);
                            activity.setNovelLastest(listChapters.get(0).getName(), true);
                            adapter = new ChapterListAdapter(context, R.layout.item_val_chapter_list, listChapters);
                            lv_ChapterList.setAdapter(adapter);
                            lv_ChapterList.setOnItemClickListener(NovelFragment2.this);
                            registerForContextMenu(lv_ChapterList);

                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... params) {
//                    for (Chapter chapter : listChapters) {
                                    for (int i = 0; i < listChapters.size(); i++) {
                                        Chapter chapter = listChapters.get(i);
                                        boolean isRead = db.isChapterRead(chapter);
                                        boolean isFav = db.isChapterFav(chapter);
                                        boolean isDown = db.isDownloadedChapterContentExist(chapter);
                                        chapter.setDown(isDown);
                                        chapter.setFav(isFav);
                                        chapter.setRead(isRead);
                                        if (i % 10 == 0)
                                            publishProgress();
                                    }
                                    return null;
                                }

                                @Override
                                protected void onProgressUpdate(Void... values) {
                                    if (adapter != null)
                                        adapter.notifyDataSetChanged();
                                    super.onProgressUpdate(values);
                                }

                                @Override
                                protected void onPostExecute(Void aVoid) {
                                    super.onPostExecute(aVoid);
                                    if (adapter != null)
                                        adapter.notifyDataSetChanged();
                                    if (listChapters != null)
                                        if (listChapters.size() > 0) {
                                            copy_list.clear();
                                            copy_list.addAll(listChapters);
                                            activity.changeMaxChapter(listChapters.size());

                                        }
                                }
                            }.execute();
                        }
                    if (progressBar.getVisibility() == View.VISIBLE)
                        progressBar.setVisibility(View.GONE);
                }
            }.execute();
        }
//        if(progressBar.getVisibility()==View.VISIBLE)
//        progressBar.setVisibility(View.GONE);
    }


    private class ImageDownloadTask extends AsyncTask<String, Integer, ArrayList<Object>> {
        private boolean enabledNotification;
        private int id = NotificationUtils.getID();
        private NotificationManager mNotifyManager;
        private NotificationCompat.Builder mBuilder;
        private PendingIntent pendingIntent;
        Chapter chapter;
        ArrayList<String> urls;

        ImageDownloadTask(Chapter c, boolean isEnabled) {
            chapter = c;
            enabledNotification = isEnabled;
        }

        @Override
        public void onPreExecute() {
            if (enabledNotification) {
                mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mBuilder = new NotificationCompat.Builder(context);
                mBuilder.setContentTitle(chapter.getNovelName())
                        .setContentText("Đang Tải: " + " " + chapter.getName())
                        .setSmallIcon(android.R.drawable.stat_sys_download)
                        .setOngoing(true)
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
                Log.d(TAG, "onProgressUpdate: " + values[0]);
                super.onProgressUpdate(values);
            }
        }


        @Override
        protected final ArrayList<Object> doInBackground(String... params) {

            NovelLibrary novelLibrary = new NovelLibrary(params[0]);
            Document result = null;
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
//                        images.add(bmap);
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

        int dpWidth() {
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            float scale = getResources().getDisplayMetrics().density;
            return (int) (displayMetrics.widthPixels / displayMetrics.density);
        }

        @Override
        protected void onPostExecute(ArrayList<Object> result) {
            super.onPostExecute(result);
            if (enabledNotification) {
                mBuilder = new NotificationCompat.Builder(context);
                mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (result != null) {
                    mBuilder.setContentTitle(chapter.getNovelName())
                            .setContentText("Tải Xuống Hoàn Tất: " + chapter.getName())
//                            .setProgress(0, 0, false)
                            .setSmallIcon(android.R.drawable.stat_sys_download_done)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent);
                    mNotifyManager.notify(id, mBuilder.build());
                } else {
                    mBuilder.setContentTitle(chapter.getNovelName()).setContentText("Tải Xuống Thất Bại: " + chapter.getName());
                    mBuilder.setProgress(0, 0, false);
                    mBuilder.setAutoCancel(true);
                    mBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done);
                    mNotifyManager.notify(id, mBuilder.build());
                }
            }
            FileDownloader downloader = new FileDownloader(chapter.getNovelName(), chapter.getName(), context);
            downloader.setActivity(activity);

            if (result != null)
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
                            if (adapter != null)
                                adapter.notifyDataSetChanged();
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
                                if (adapter != null)
                                    adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
        }

    }

    private boolean isNotificationEnabled = true;

    public ArrayList<Chapter> getListChapters() {
        return listChapters;
    }

    public void chapterStatusUpdate(Chapter chapter) {
        if (chapter.getNovelName().equals(novel.getNovelName())) {
            for (int i = 0; i < listChapters.size(); i++) {
                if (listChapters.get(i).getOrderNo() == chapter.getOrderNo()) {
                    listChapters.get(i).setDown(true);
                    if (adapter != null)
                        adapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Override
    public void searchChapter(String key) {
        if (adapter != null)
            adapter.getFilter().filter(key);
//            adapter.filter(key);
    }


    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Kai", "Permission is granted");
                return true;
            } else {
                Log.v("Kai", "Permission is revoked");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Kai", "Permission is granted");
            return true;
        }
    }

}

interface ChapterStatusUpdate {
    public void chapterStatusUpdate(Chapter chapter);

    public void searchChapter(String key);
}



