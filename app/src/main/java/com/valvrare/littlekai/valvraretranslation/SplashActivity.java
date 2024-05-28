package com.valvrare.littlekai.valvraretranslation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.valvrare.littlekai.valvraretranslation.api.NovelLibrary;
import com.valvrare.littlekai.valvraretranslation.api.SonakoLibrary;
import com.valvrare.littlekai.valvraretranslation.database.ValvrareDatabaseHelper;
import com.valvrare.littlekai.valvraretranslation.model.Novel;
import com.valvrare.littlekai.valvraretranslation.utils.Constants;
import com.valvrare.littlekai.valvraretranslation.utils.DbBitmapUtility;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class SplashActivity extends Activity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ValvrareDatabaseHelper db;
    private Context context;
    private TextView tv_update;
    private final int IMAGE_SIZE = 150;
    private ArrayList<Novel> allValNovels;
    private ProgressBar progressBar;
    private int snkMaxPage = 1;
    private int snkCurrPage = 0;
    private int valMaxPage = 1;
    private int valCurrPage = 0;
    private int maxretried = 3;
    private int retried = 1;
    private int SAVE_VAL_NOVEL_CODE = 101;
    private int SAVE_SNK_NOVEL_CODE = 202;
    private int saveNovelCode = 0;
    private ArrayList<Novel> allSnkNovels;
    private String snkTypeNovelURL;
    private String valTypeNovelURL;
    private String snkNovelType;
    private String valNovelType;
    private int snkTypeListCode = -1;
    private boolean loadValLib, loadSnkLib;

    private int valTypeListCode = -1;
    private String[] snkTypeList = {"Active", "Idle", "Completed", "Inactive", "Original", "Teaser"};
    private String[] valTypeList = {"Light", "Teaser", "Original", "Manga", "Completed/Drop"};
    private String TAG = "Kai";

    private String getValNovelTypeURL(String type) {
        String url = null;
        if (type != null)
            switch (type) {
                case "Light":
                    url = "http://valvrareteam.com/light-novel/page/";
                    break;
                case "Original":
                    url = "http://valvrareteam.com/light-novel-original-novel/page/";
                    break;
                case "Teaser":
                    url = "http://valvrareteam.com/light-novel-teaser-project/page/";
                    break;
                case "Manga":
                    url = "http://valvrareteam.com/manga/page/";
                    break;
                case "Completed/Drop":
                    url = "http://valvrareteam.com/anime/page/";
                    break;
                default:
                    url = "http://valvrareteam.com/manga/page/";
                    break;
            }
        return url;
    }

    private String getSnkNovelTypeURL(String type) throws UnsupportedEncodingException {
        String url = null;
        switch (type) {
            case "Active":
                url = Constants.URL_Active_LightNovel;
                break;
            case "Inactive":
                url = Constants.URL_Inactive_LightNovel;
                break;
            case "Idle":
                url = Constants.URL_Idle_LightNovel;
                break;
            case "Completed":
                url = Constants.getURL_Completed_LightNovel();
                break;
            case "Teaser":
                url = Constants.URL_Teaser_LightNovel;
                break;
            case "Host":
                url = Constants.URL_Host_LightNovel;
                break;
            case "Original":
                url = Constants.URL_Original_Novel;
                break;
            default:
                url = Constants.URL_Original_Novel;
                break;
        }
        return url;
    }

    void finishActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_fade_in, R.anim.splash_fade_out);
                finish();
            }
        }, 2500);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView imageView = (ImageView) findViewById(R.id.image_splash);
        Glide.with(this).load(R.drawable.valvrare_logo).into(imageView);

        sharedPreferences = getSharedPreferences("LibraryInfo", MODE_PRIVATE);

        context = this;
        db = new ValvrareDatabaseHelper(context);
        tv_update = (TextView) findViewById(R.id.tv_update);
        allValNovels = new ArrayList<>();

        loadValLib = sharedPreferences.getBoolean("loadValLib", false);
        loadSnkLib = sharedPreferences.getBoolean("loadSnkLib", true);
        Log.d(TAG, "onCreate Splash: " + loadValLib + "," + loadSnkLib);

//        final Animation animation = AnimationUtils.loadAnimation(context, R.anim.rotate);
//        imageView.startAnimation(animation);

        if (loadValLib) {
            if (loadSnkLib) {
                Random r = new Random();
                int a = r.nextInt(5 - 1) + 1;
                if (a == 1)
                    tv_update.setText("Trong tìm kiếm tên chương, nhập chữ \"bm\" sẽ lọc toàn bộ những chương đã đánh dấu");
                else if (a == 2)
                    tv_update.setText("Trong số những BackGround, có một cái là Custom BackGround. Hãy vào Setting để tùy chọn màu BackGround theo ý thích của bạn.");
                else if (a == 3)
                    tv_update.setText("Chức năng Zoom và Nút Động Lực được mặc định tắt, nếu bạn muốn bật chức năng đó lên hãy vào Setting để cài đặt lại.");
                else
                    tv_update.setText("Hãy bình luận và đánh giá truyện để thay lời cám ơn cho nhóm dịch.");

                finishActivity();
            } else {
                try {
                    Log.d(TAG, "onCreate: go to SNK Loader");
                    getSnkNovelTaskBegin();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else {
            tv_update.setText("Đang kết nối...");
            getValNovelTaskBegin();
        }
    }

    private void getSnkNovelTaskBegin() throws UnsupportedEncodingException {
        snkCurrPage = 0;
        snkMaxPage = 0;
        if (allSnkNovels == null || allSnkNovels.size() == 0) {
            saveNovelCode = SAVE_SNK_NOVEL_CODE;
            allSnkNovels = new ArrayList<>();
        }
        snkTypeListCode++;
        if (snkTypeListCode >= 0)
            snkNovelType = snkTypeList[snkTypeListCode];
        snkTypeNovelURL = getSnkNovelTypeURL(snkNovelType);
        new getSnkMaxpage().execute();
    }

    private void getValNovelTaskBegin() {
        valCurrPage = 0;
        valMaxPage = 0;
        if (allValNovels == null || allValNovels.size() == 0) {
            saveNovelCode = SAVE_VAL_NOVEL_CODE;
            allValNovels = new ArrayList<>();
        }
        valTypeListCode++;

        if (valTypeListCode >= 0)
            valNovelType = valTypeList[valTypeListCode];
        valTypeNovelURL = getValNovelTypeURL(valNovelType);

        new getValMaxpage().execute();
    }


    boolean active = false;

    @Override
    protected void onStart() {
        active = true;
        super.onStart();
    }

    ImageDownloadTask imageDownloadTask;

    @Override
    protected void onDestroy() {
        active = false;
        if (imageDownloadTask != null)
            imageDownloadTask.cancel(true);
        super.onDestroy();
    }


    int count = 0;

    private class ImageDownloadTask extends AsyncTask<ArrayList<Novel>, Integer, ArrayList<Novel>> {
        @Override
        protected void onProgressUpdate(Integer... values) {
            tv_update.setText("Đang tải hình minh họa: " + values[0] + "%");
            super.onProgressUpdate(values);
        }

        @SafeVarargs
        @Override
        protected final ArrayList<Novel> doInBackground(ArrayList<Novel>... params) {
            ArrayList<Novel> novels = params[0];
            for (int i = 0; i < novels.size(); i++) {
                if (!isCancelled()) {
                    Bitmap bmap = null;
                    long time = System.currentTimeMillis();

                    try {
                        if (novels.get(i).getImage() != null) {
                            bmap = Glide.with(context).load(novels.get(i).getImage()).asBitmap().into(IMAGE_SIZE, IMAGE_SIZE).get();
                            Log.d("Kai", "doInBackground: " + i + ", " + (System.currentTimeMillis() - time));
                        }
                        publishProgress((int) ((i + 1) * 100 / novels.size()));
                        novels.get(i).setImg_file(DbBitmapUtility.getBytes(bmap));

                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
            return novels;
        }

        @Override
        protected void onPostExecute(ArrayList<Novel> novels) {
            if (!isCancelled())
                if (active) {
                    if (saveNovelCode == SAVE_VAL_NOVEL_CODE) {
                        saveValNovelToDb(allValNovels);
                        finishActivity();
                        if (!loadSnkLib)
                            try {
                                getSnkNovelTaskBegin();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                    }
//                    else showDialog();

                    super.onPostExecute(novels);
                }
        }
    }

    private void saveValNovelToDb(final ArrayList<Novel> novels) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                tv_update.setText("Đang lưu dữ liệu...");
                db.deleteAllNovel();
            }

            @Override
            protected Void doInBackground(Void... params) {
                ArrayList<Novel> mNovels = novelsListChecker(novels);
                if (mNovels.size() > 0)
                    for (int i = 0; i < mNovels.size(); i++) {
                        db.insertAllNovel(mNovels.get(i));
                    }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                editor = sharedPreferences.edit();
                editor.putBoolean("loadValLib", true);
                editor.apply();
            }
        }.execute();
    }

    void saveSnkNovelsToDb(final ArrayList<Novel> allSnkNovels) {
        if (allSnkNovels != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    db.deleteAllSnkNovels();
                    tv_update.setText("(Sonako) Đang lưu dữ liệu...");
                }

                @Override
                protected Void doInBackground(Void... params) {
                    ArrayList<Novel> mNovels = novelsListChecker(allSnkNovels);
                    for (int i = 0; i < mNovels.size(); i++) {
                        db.insertSnkAllNovel(mNovels.get(i));
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("loadSnkLib", true);
                    editor.apply();
                    Log.d(TAG, "Goto Main Activity:");
                    finishActivity();
                }
            }.execute();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    void showDialog() {
        if (active) {
            tv_update.setText("Lỗi Kết Nối!");
            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
            builder.setTitle("Thông Báo")
                    .setMessage("Lỗi kết nối mạng bị lỗi. Xin hãy thử lại lần sau.")
                    .setCancelable(false)
                    .setNegativeButton("Đã Hiểu", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            Toast.makeText(context, "Lỗi Kết Nối!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    ////Sonako getList Begin Task
    private class getSnkMaxpage extends AsyncTask<String, Void, Integer> {
        @Override
        protected void onPreExecute() {
            progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
//            tv_update.setText("Đang lấy dữ liệu từ Sonako...");
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            int maxpage = 0;
            try {
                SonakoLibrary library = new SonakoLibrary();
                maxpage = library.getMaxPage(snkTypeNovelURL + "?" + Constants.category_exhibition_sort_alphabet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return maxpage;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer != 0) {
                snkMaxPage = integer;
                snkCurrPage = 1;
                new getSnkNovelList().execute(snkTypeNovelURL + "?page=" + snkCurrPage + "&" + Constants.category_exhibition_sort_alphabet);
            } else showDialog();
        }
    }

    private class getSnkNovelList extends AsyncTask<String, Void, ArrayList<Novel>> {
        @Override
        protected void onPreExecute() {
            progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
            tv_update.setText("(Sonako) Đang lấy danh sách " + snkNovelType + " Novel...");
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Novel> doInBackground(String... params) {
            ArrayList<Novel> result = null;
            try {
                SonakoLibrary library = new SonakoLibrary();
                result = library.getAllNovel(params[0]);
                if (result != null && result.size() > 0)
                    for (Novel novel : result) {
                        novel.setType(snkNovelType);
                    }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<Novel> novels) {
            super.onPostExecute(novels);
            if (novels != null) {
                allSnkNovels.addAll(novels);
                retried = 1;
                snkCurrPage++;
                if (snkCurrPage <= snkMaxPage)
                    new getSnkNovelList().execute(snkTypeNovelURL + "?page=" + snkCurrPage + "&" + Constants.category_exhibition_sort_alphabet);
                else {
                    Log.d(TAG, "snkTypeList.length: " + snkTypeList.length);
                    if (snkTypeListCode < snkTypeList.length - 1) {
                        try {
                            getSnkNovelTaskBegin();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else if (saveNovelCode == SAVE_SNK_NOVEL_CODE) {
                        saveSnkNovelsToDb(allSnkNovels);

                    }
                }
            } else {
                retried++;
                if (retried > maxretried)
                    showDialog();
                else
                    new getSnkNovelList().execute(snkTypeNovelURL + "?page=" + snkCurrPage + "&" + Constants.category_exhibition_sort_alphabet);
            }
        }
    }

    ////Valvrare getList Begin Task
    private class getValMaxpage extends AsyncTask<String, Void, Integer> {
        @Override
        protected void onPreExecute() {
            progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
//            tv_update.setText("Đang lấy dữ liệu từ Valvrare...");
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            int maxpage = 0;
            try {
                NovelLibrary library = new NovelLibrary(valTypeNovelURL + "1");
                maxpage = library.getMaxPage();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return maxpage;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer != 0) {
                valMaxPage = integer;
                valCurrPage = 1;
                new getValNovelList().execute(valTypeNovelURL + valCurrPage);
            } else showDialog();
        }
    }

    private class getValNovelList extends AsyncTask<String, Void, ArrayList<Novel>> {
        @Override
        protected void onPreExecute() {
            progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
            if (!valNovelType.equals("Manga"))
                tv_update.setText("Đang lấy danh sách " + valNovelType + " Novel...");
            else tv_update.setText("Đang lấy danh sách " + valNovelType + "...");
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Novel> doInBackground(String... params) {
            NovelLibrary novelLibrary = new NovelLibrary(params[0]);
            ArrayList<Novel> result = null;
            try {
                result = novelLibrary.getAllNovel();
                if (result != null && result.size() > 0)
                    for (Novel novel : result) {
                        novel.setType(valNovelType);
                        Log.d(TAG, novel.getNovelName() + ": " + novel.getType());
                    }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<Novel> novels) {
            super.onPostExecute(novels);
            if (novels != null) {
                allValNovels.addAll(novels);
                retried = 1;
                valCurrPage++;
                Log.d(TAG, "onPostExecute: " + valCurrPage + ", " + valMaxPage);
                if (valCurrPage <= valMaxPage)
                    new getValNovelList().execute(valTypeNovelURL + valCurrPage);
                else {
                    if (valTypeListCode < valTypeList.length - 1) {
                        getValNovelTaskBegin();
                    } else {
                        imageDownloadTask = new ImageDownloadTask();
                        imageDownloadTask.execute(allValNovels);
                    }
                }
            } else {
                retried++;
                if (retried > maxretried)
                    showDialog();
                else
                    new getValNovelList().execute(valTypeNovelURL + valCurrPage);
            }
        }
    }

    private ArrayList<Novel> novelsListChecker(ArrayList<Novel> novels) {

        Collections.sort(novels, new Comparator<Novel>() {
            @Override
            public int compare(Novel lhs, Novel rhs) {
                return lhs.getNovelName().compareTo(rhs.getNovelName());
            }
        });

        ArrayList<Novel> newNovels = new ArrayList<>();

        for (int i = 0; i < novels.size(); i++) {
            if (i == 0)
                newNovels.add(novels.get(i));
            else {
                if (newNovels.get(newNovels.size() - 1).getNovelName().equals(novels.get(i).getNovelName()))
                    newNovels.get(newNovels.size() - 1).setType(newNovels.get(newNovels.size() - 1).getType() + ", " + novels.get(i).getType());
                else newNovels.add(novels.get(i));
            }
        }

        return newNovels;
    }

}


