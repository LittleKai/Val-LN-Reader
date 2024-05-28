package com.valvrare.littlekai.valvraretranslation;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.valvrare.littlekai.valvraretranslation.adapter.ChapterSelectorAdapter;
import com.valvrare.littlekai.valvraretranslation.adapter.FontSelectorAdapter;
import com.valvrare.littlekai.valvraretranslation.adapter.ToolbarShowingAdapter;
import com.valvrare.littlekai.valvraretranslation.api.NovelLibrary;
import com.valvrare.littlekai.valvraretranslation.database.ValvrareDatabaseHelper;
import com.valvrare.littlekai.valvraretranslation.helper.DisplayNovelContentHtmlHelper;
import com.valvrare.littlekai.valvraretranslation.helper.FunctionHelper;
import com.valvrare.littlekai.valvraretranslation.helper.WebViewChapterContentClient;
import com.valvrare.littlekai.valvraretranslation.model.Chapter;
import com.valvrare.littlekai.valvraretranslation.model.ImageModel;
import com.valvrare.littlekai.valvraretranslation.model.Novel;
import com.valvrare.littlekai.valvraretranslation.utils.Constants;
import com.valvrare.littlekai.valvraretranslation.utils.FacebookCommentDialog;
import com.valvrare.littlekai.valvraretranslation.utils.FacebookLoginDialog;
import com.valvrare.littlekai.valvraretranslation.utils.LoadJson;
import com.valvrare.littlekai.valvraretranslation.utils.UIHelper;
import com.valvrare.littlekai.valvraretranslation.widget.NonLeakingWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChapterReadingActivity extends AppCompatActivity implements LoadJson.OnFinishLoadJSonListener {
    private CallbackManager callbackManager;
    private static final String TAG = "Kai";
    private static SharedPreferences sharedPreferences;
    private String url = null;
    private SharedPreferences.Editor editor;
    private RelativeLayout layout_toolbar;
    private LinearLayout layoutMenu, llout_setting_reading, ll_reload, wv_contain;
    private Toolbar toolbar;
    private TextView tv_showedFontName, tv_showedToolName;
    private ContentResolver contentResolver;
    private SeekBar brightBar;
    private Window window;
    private int brightness, lastPos;
    private int TEXT_SIZE_DEFAULT = 20;
    private int textSizeCurrent = TEXT_SIZE_DEFAULT;
    static int dpWidth;
    static int dp8AsPixel;
    private ProgressBar progressBar;
    private ValvrareDatabaseHelper db;
    private Novel novel;
    private double textRatio = 1.0;
    private float totalPosY;
    private float currZoom = 100;
    private ProgressDialog progressDialog, loadingDialog;
    private Chapter chapter;
    static boolean active = true;

    private String chapterName, novelName = null;
    private boolean firstLoad = false;
    private boolean isStop = false;
    static String htmlBackUp = null;
    private NonLeakingWebView wv;
    private Context context;
    private float lastHeight;
    private boolean textSizeChanged = false;
    private Profile profile;
    private ArrayList<Chapter> chapterList;
    private ImageButton ib_previous_chapter, ib_next_chapter, webview_go_top;
    private Button btn_reload;
    private AsyncTask getChapterContentFromDb;
    private TextView tv_comment_count;
    private boolean isZoomEnable;
    private getChapterContent getChapterContent;
    private AdView av;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_reading);
        toolbar = (Toolbar) findViewById(R.id.toolbar_activity_reading);
        toolbar.setContentInsetStartWithNavigation(0);
//        tv_chapterName = (TextView) findViewById(R.id.tv_ChapterName);
        context = this;
        facebookSDKInitialize();
        db = new ValvrareDatabaseHelper(context);
        Bundle extras = getIntent().getExtras();

        MobileAds.initialize(this, getResources().getString(R.string.ads_app_id));

        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        av = (AdView) findViewById(R.id.adView);
        Log.d(TAG, "android_id: "+android_id);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice(android_id)
                .build();
//        AdRequest adRequest = new AdRequest.Builder().build();
        av.loadAd(adRequest);



        if (extras != null) {

            chapter = getIntent().getParcelableExtra("chapter");
            novel = getIntent().getParcelableExtra("novel");
            chapterName = chapter.getName();
            novelName = chapter.getNovelName();
            db.getLastYPos(chapter);
            lastHeight = chapter.getLastYHeight();
            lastPos = chapter.getLastY();

            chapter.setDown(db.isDownloadedChapterContentExist(chapter));

            url = chapter.getUrl();
            loadJson = new LoadJson(context, novel);
            loadJson.setOnFinishLoadJSonListener(this);
            chapterList = new ArrayList<>();
            String data = db.getChapterList(novel);
            if (data != null) {
                try {
                    JSONObject jObj = new JSONObject(data);
                    JSONArray jArr = jObj.getJSONArray("chapterlist");
                    for (int i = jArr.length() - 1; i >= 0; i--) {
//                        for (int i = 0; i < jArr.length() ; i++) {
                        JSONObject obj = jArr.getJSONObject(i);
                        Chapter ct = new Chapter(obj.getString("name"), obj.getString("url"));
                        ct.setOrderNo(i + 1);
                        chapterList.add(ct);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (url.contains(Constants.VAL_URL_ROOT))
                loadJson.sendDataToServer(novel.getNovelName(), null, false);

            Calendar c = Calendar.getInstance();
            String time = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
            String date = c.get(Calendar.DATE) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR);
            novel.setTime(time);
            novel.setDate(date);
            db.insertRecentChapter(novel, chapter);

        }

        isZoomEnable = UIHelper.getZoomPreferences(this);
        toolbar.setTitle(novelName);
        toolbar.setSubtitle(chapterName);
        toolbar.setTitleTextColor(0xffffffff);
        toolbar.setSubtitleTextColor(0xffffffff);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FrameLayout customLayout = (FrameLayout) findViewById(R.id.customBackground);
        TextView tv_customLayout = (TextView) findViewById(R.id.tv_customBackground);

        ((GradientDrawable) customLayout.getBackground()).setColor(Color.parseColor(UIHelper.getBackgroundColor(context)));
        tv_customLayout.setTextColor(Color.parseColor(UIHelper.getForegroundColor(context)));

        wv = (NonLeakingWebView) findViewById(R.id.wv_chapterContent);
        webview_go_top = (ImageButton) findViewById(R.id.webview_go_top);

        if (!UIHelper.getDynamicButtonsPreferences(context)) {
            webview_go_top.setVisibility(View.GONE);
        } else {
            webview_go_top.setVisibility(View.VISIBLE);
        }
        wv_contain = (LinearLayout) findViewById(R.id.wv_contain);
        ib_next_chapter = (ImageButton) findViewById(R.id.ib_next_chapter);
        ib_previous_chapter = (ImageButton) findViewById(R.id.ib_previous_chapter);
        tv_showedToolName = (TextView) findViewById(R.id.tv_showedToolName);
        tv_showedFontName = (TextView) findViewById(R.id.tv_showedFontName);
        //     android:typeface in layout XML or setTypeface()
        llout_setting_reading = (LinearLayout) findViewById(R.id.llout_setting_reading);

        if (llout_setting_reading.getVisibility() == View.VISIBLE) {
            llout_setting_reading.setVisibility(LinearLayout.GONE);//INVISIBLE | VISIBLE
        }

        ll_reload = (LinearLayout) findViewById(R.id.ll_reload);
        btn_reload = (Button) findViewById(R.id.btn_reload);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        layout_toolbar = (RelativeLayout) findViewById(R.id.layout_toolbar);
        layoutMenu = (LinearLayout) findViewById(R.id.layoutMenu);

        tv_comment_count = (TextView) findViewById(R.id.tv_comment_count);
//        ib_comment = (ImageButton) findViewById(R.id.ib_comment);
        sharedPreferences = getSharedPreferences("ReadingOption", MODE_PRIVATE);
        editor = sharedPreferences.edit();
//        editor.commit();
        textSizeCurrent = getTextSizePref();
        String fontName = getFontNamePref();
        String showedToolbarType = getShowTypePref();
//        String backgroundColor = sharedPreferences.getString("BackgroundColor", "WhiteBackground");

        switch (fontName) {
            case "Palatino":
                tv_showedFontName.setText("Palatino");
                break;
            case "Tahoma":
                tv_showedFontName.setText("Tahoma");
                break;
            case "Georgia":
                tv_showedFontName.setText("Georgia");
                break;
            case "Roboto":
                tv_showedFontName.setText("Roboto");
                break;
            case "Times N.Roman":
                tv_showedFontName.setText("Times N.Roman");
                break;
        }

        brightBar = (SeekBar) findViewById(R.id.brightBar);
        contentResolver = getContentResolver();
        window = getWindow();
        brightBar.setMax(255);
        brightBar.setKeyProgressIncrement(1);
        try {
            brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            Log.e("Error", "can't access system brigtness.");
            e.printStackTrace();
        }

        brightBar.setProgress(brightness);
        brightBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 20)
                    progress = 20;
                else
                    brightness = progress;

//                float perc = (brightness / (float) 255) * 100;
//                tv_brightness.setText("Độ sáng "+perc);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (Settings.System.canWrite(getApplicationContext())) {
//                        ContentResolver cResolver = ChapterReadingActivity.this.getApplicationContext().getContentResolver();
//                        Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
//                    }
//                } else {
//                    Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
//                    WindowManager.LayoutParams layoutParams = window.getAttributes();
//                    layoutParams.screenBrightness = brightness / (float) 255;
//                    window.setAttributes(layoutParams);
//                }
                if (isSettingPermissionGranted()) {
                    Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
                    WindowManager.LayoutParams layoutParams = window.getAttributes();
                    layoutParams.screenBrightness = brightness / (float) 255;
                    window.setAttributes(layoutParams);
                }

//                else {
//                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
//                    intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                Settings.System.putInt(contentResolver,Settings.System.SCREEN_BRIGHTNESS,brightness);
//                WindowManager.LayoutParams layoutParams = window.getAttributes();
//                layoutParams.screenBrightness = brightness/(float)255;
//                window.setAttributes(layoutParams);
            }
        });

        switch (showedToolbarType) {
            case "Scroll":
                scrollShowHideMenuBar();
                tv_showedToolName.setText("Kiểu Vuốt");
                break;
            case "Touch":
                touchShowHideMenuBar();
                tv_showedToolName.setText("Kiểu Chạm");
                break;
        }

        loadingDialog = new ProgressDialog(context);
        loadingDialog.setMessage("Đang Lấy Dữ Liệu");
        loadingDialog.setCanceledOnTouchOutside(false);

        loadingDialog.setCancelable(true);
        loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (getChapterContentFromDb != null)
                    getChapterContentFromDb.cancel(true);
                if (getChapterContent != null)
                    getChapterContent.cancel(true);
//                if(!firstLoad)
                finish();
            }
        });

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.setCancelable(false);
        //code here...
        progressBar.setVisibility(ProgressBar.GONE);
        isStop = false;
        btn_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginTask(chapter);
            }
        });
        //wv_contain.setVisibility(View.GONE);
        ll_reload.setVisibility(View.GONE);

        checkChapterComment();
        loadContentChapter();
    }

    private int curr_chapter_pos = 0;
    boolean isBadConnection = false;

    void badConnection(boolean failedLoad) {
        isBadConnection = failedLoad;
        if (failedLoad) {
            if (loadingDialog.isShowing())
                loadingDialog.dismiss();
            progressBar.setVisibility(View.GONE);
            //wv_contain.setVisibility(View.GONE);
            ll_reload.setVisibility(View.VISIBLE);
        } else {
//            progressBar.setVisibility(View.VISIBLE);
            wv_contain.setVisibility(View.VISIBLE);
            ll_reload.setVisibility(View.GONE);
        }
    }

    private LoadJson loadJson;

    void get_chapter_list() {
        if (loadJson != null)
            loadJson.sendDataToServer(novel.getNovelName(), null, false);
    }

    int first_hour = 100, first_date = 100;
    GetCommentNumberAsync getCommentNumber;

    void checkChapterComment() {
//        if (first_date == 100 | first_hour == 100) {
//            Calendar c = Calendar.getInstance();
//            first_hour = Calendar.HOUR_OF_DAY;
//            first_date = c.get(Calendar.DATE);
//            get_chapter_list();
//        } else {
//            Calendar c = Calendar.getInstance();
//            int curr_hour = Calendar.HOUR_OF_DAY;
//            int curr_date = c.get(Calendar.DATE);
//            if (curr_date != first_date | curr_hour - first_hour > 2 | curr_hour - first_hour < -2) {
//                get_chapter_list();
//            }
//        }
        if (getCommentNumber != null)
            getCommentNumber.cancel(true);
        getCommentNumber = new GetCommentNumberAsync();
        getCommentNumber.execute(url);
        check_chapter_nav_status();
    }

    void check_chapter_nav_status() {
        if (chapterList != null & chapterList.size() != 0) {
            for (int i = 0; i < chapterList.size(); i++) {
                if (chapter.getName().equals(chapterList.get(i).getName()) & chapter.getUrl().equals(chapterList.get(i).getUrl())) {
                    curr_chapter_pos = i;
                    if (i == 0) {
                        ib_previous_chapter.setClickable(false);
                        ib_previous_chapter.setBackgroundColor(0xffdde4ed);
                    } else {
                        ib_previous_chapter.setClickable(true);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            ib_previous_chapter.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.color_image_buton_state, null));
                        } else
                            ib_previous_chapter.setBackgroundDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.color_image_buton_state, null));
                    }
                    if (i == chapterList.size() - 1) {
                        ib_next_chapter.setClickable(false);
                        ib_next_chapter.setBackgroundColor(0xffdde4ed);
                    } else {
                        ib_next_chapter.setClickable(true);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            ib_next_chapter.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.color_image_buton_state, null));
                        } else
                            ib_next_chapter.setBackgroundDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.color_image_buton_state, null));
                    }
                }
            }
        } else {
            ib_previous_chapter.setClickable(false);
            ib_previous_chapter.setBackgroundColor(0xffdde4ed);
            ib_next_chapter.setClickable(false);
            ib_next_chapter.setBackgroundColor(0xffdde4ed);
        }
    }

    void beginTask(Chapter lchapter) {

        if (!isBadConnection) {
            if (!isZoomEnable)
                lastPos = wv.getScrollY();
            else lastPos = (int) (wv.getScrollY() / wv.getScale() * currZoom);
            chapter.setLastY(lastPos);
            chapter.setLastYHeight(getResources().getDisplayMetrics().density * wv.getContentHeight());
            final float currentScale = wv.getScale();
//            chapter.setLastZoom(currentScale);
            db.setStateChapter(chapter);
        }

        badConnection(false);
        isStop = false;
        firstLoad = false;

        //wv_contain.setVisibility(View.GONE);
//        wv = (NonLeakingWebView) findViewById(R.id.wv_chapterContent);
//        wv.loadDataWithBaseURL("file:///android_asset/", "", "text/html", "utf-8", NonLeakingWebView.PREFIX_PAGEMODEL);
        wv.loadUrl("about:blank");

        chapter = new Chapter(lchapter.getName(), lchapter.getUrl());
//        chapter.setSecond_url(lchapter.getSecond_url());
        chapter.setNovelName(novel.getNovelName());
        chapter.setNovelUrl(novel.getUrl());
        chapter.setNovelImageUrl(novel.getImage());

        chapterName = chapter.getName();
        novelName = chapter.getNovelName();
        db.getLastYPos(chapter);
        lastHeight = chapter.getLastYHeight();
        lastPos = chapter.getLastY();
        Calendar c = Calendar.getInstance();
        String time = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
        String date = c.get(Calendar.DATE) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR);
        novel.setTime(time);
        novel.setDate(date);

        chapter.setDown(db.isDownloadedChapterContentExist(chapter));
        url = chapter.getUrl();
        checkChapterComment();
        if (bookmarkItem != null) {
            chapter.setFav(db.isChapterFav(chapter));
            if (chapter.isFav())
                bookmarkItem.setTitle("Bỏ Dấu Chương");
            else bookmarkItem.setTitle("Đánh Dấu Chương");
        }

        db.insertRecentChapter(novel, chapter);
        toolbar.setTitle(novelName);
        toolbar.setSubtitle(chapterName);

        if (llout_setting_reading.getVisibility() == View.VISIBLE) {
            llout_setting_reading.setVisibility(LinearLayout.GONE);//INVISIBLE | VISIBLE
        }
//        firstLoad = false;
        loadContentChapter();
    }

    void loadContentChapter() {
        if (getChapterContent != null)
            getChapterContent.cancel(true);
        if (chapter.isDown()) {

//            progressBar.setVisibility(ProgressBar.GONE);
            progressDialog.show();
            getChapterContentFromDb();
//            new getChapterContentFromDb().execute(url);
        } else {
            if (FunctionHelper.isNetworkAvailable(context)) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressBar.setVisibility(ProgressBar.VISIBLE);
//                new getChapterContent().execute(url);
                getChapterContent = new getChapterContent();
                getChapterContent.execute(url);

            } else {
                progressBar.setVisibility(ProgressBar.GONE);
//                if (!loadingDialog.isShowing())
//                    loadingDialog.dismiss();
                badConnection(true);
//                    Toast.makeText(context, "Xin hãy kiểm tra lại kết nối!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onDestroy() {
        if (av != null) {
            av.destroy();
        }
        active = false;

        super.onDestroy();
    }

    @Override
    public void onPause() {
        if (av != null) {
            av.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (av != null) {
            av.resume();
        }
    }

    public void onBackPressed() {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (newFragment != null) {
                    if (newFragment.getShowsDialog())
                        newFragment.dismiss();
                }

                if (llout_setting_reading.getVisibility() == View.VISIBLE) {
                    llout_setting_reading.setVisibility(LinearLayout.GONE);//INVISIBLE | VISIBLE
                } else {
                    finish();
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    MenuItem bookmarkItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chapter_reading_menu, menu);
        bookmarkItem = menu.findItem(R.id.action_bookmark);
        if (chapter != null) {
            chapter.setFav(db.isChapterFav(chapter));
            if (chapter.isFav())
                bookmarkItem.setTitle("Bỏ Dấu Chương");
            else bookmarkItem.setTitle("Đánh Dấu Chương");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();

                return true;
            case R.id.action_bookmark:
                String task = (String) bookmarkItem.getTitle();
                if (task.equals("Đánh Dấu Chương")) {

                    if (db.setChapterFav(chapter)) {
                        bookmarkItem.setTitle("Bỏ Dấu Chương");
                        Toast.makeText(context, "Đã Đánh Dấu Chương!", Toast.LENGTH_SHORT).show();
                    }
                } else if (db.deleteFavChapter(chapter)) {
                    Toast.makeText(context, "Đã Bỏ Dấu Chương!", Toast.LENGTH_SHORT).show();
                    bookmarkItem.setTitle("Đánh Dấu Chương");
                }
                return true;
            case R.id.action_descrip_share:
                if (FunctionHelper.isNetworkAvailable(context)) {

                    profile = Profile.getCurrentProfile();
                    if (profile == null) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                        if (prev != null) {
                            ft.remove(prev);
                        }
                        ft.addToBackStack(null);
                        // Create and show the dialog.
                        newFragment = FacebookLoginDialog.newInstance(url);
                        newFragment.show(ft, "dialog");
                    } else {
                        ShareDialog shareDialog = new ShareDialog(this);
                        if (ShareDialog.canShow(ShareLinkContent.class)) {
                            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                    .setContentUrl(Uri.parse(url))
                                    .build();

                            shareDialog.show(linkContent);  // Show facebook ShareDialog
                        }
                    }
                } else
                    Toast.makeText(context, "Xin hãy kiểm tra lại kết nối!", Toast.LENGTH_SHORT).show();

                return true;
        }
        return false;

    }

    public void touchShowHideMenuBar() {
//        scrollView.setOnTouchListener(new View.OnTouchListener() {
        wv.setOnTouchListener(new View.OnTouchListener() {
            final int DISTANCE = 20;
            float startY = 0;
            float startX = 0;
            float distX = 0;
            float distY = 0;
            boolean checkTouch = false;
            boolean isMenuHide = false;

            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    startY = event.getY();
                    startX = event.getX();
                    if (llout_setting_reading != null && llout_setting_reading.getVisibility() == LinearLayout.VISIBLE) {
                        llout_setting_reading.setVisibility(LinearLayout.GONE);//INVISIBLE | VISIBLE
                    }
                    checkTouch = true;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    distY = event.getY() - startY;
                    distX = event.getX() - startX;


                    if (distX < DISTANCE && distX > -DISTANCE && distY > -DISTANCE && distY < DISTANCE) {
                    } else checkTouch = false;
                } else if (action == MotionEvent.ACTION_UP && checkTouch && isMenuHide) {
                    isMenuHide = false;
                    checkTouch = false;
                    showMenuBar();
                } else if (action == MotionEvent.ACTION_UP && checkTouch && !isMenuHide) {
                    isMenuHide = true;
                    checkTouch = false;
                    hideMenuBar();
                }
                return false;
            }
        });

    }

    public void scrollShowHideMenuBar() {
//        scrollView.setOnTouchListener(new View.OnTouchListener() {
        wv.setOnTouchListener(new View.OnTouchListener() {
            final int DISTANCE = 25;
            float startY = 0;
            float dist = 0;
            boolean isMenuHide = false;

            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN) {
                    startY = event.getY();
                    if (llout_setting_reading != null && llout_setting_reading.getVisibility() == LinearLayout.VISIBLE) {
                        llout_setting_reading.setVisibility(LinearLayout.GONE);//INVISIBLE | VISIBLE
                    }

                } else if (action == MotionEvent.ACTION_MOVE) {
                    dist = event.getY() - startY;
                    if ((pxToDp((int) dist) <= -DISTANCE) && !isMenuHide) {
                        isMenuHide = true;
                        hideMenuBar();
                    } else if ((pxToDp((int) dist) > DISTANCE) && isMenuHide) {
                        isMenuHide = false;
                        showMenuBar();
                    }
                    if ((isMenuHide && (pxToDp((int) dist) <= -DISTANCE))
                            || (!isMenuHide && (pxToDp((int) dist) > 0))) {
                        startY = event.getY();
                    }

                } else if (action == MotionEvent.ACTION_UP) {
                    startY = 0;
                }
                return false;
            }
        });
    }

    public int pxToDp(int px) {
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        int dp = Math.round(px / (dm.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public void showMenuBar() {
        AnimatorSet animSet = new AnimatorSet();

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(layoutMenu, View.TRANSLATION_Y, 0);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(layout_toolbar, View.TRANSLATION_Y, 0);

        animSet.playTogether(anim1, anim2);
        animSet.setDuration(300);
        animSet.start();
    }

    public void hideMenuBar() {
//        llout_setting_reading.setVisibility(LinearLayout.GONE);//INVISIBLE | VISIBLE
        AnimatorSet animSet = new AnimatorSet();
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(layoutMenu, View.TRANSLATION_Y, layoutMenu.getHeight());
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(layout_toolbar, View.TRANSLATION_Y, -toolbar.getHeight());
        animSet.playTogether(anim1, anim2);
        animSet.setDuration(300);
        animSet.start();
    }


    public void setting(View view) {
        if (llout_setting_reading.getVisibility() == View.VISIBLE) {
            llout_setting_reading.setVisibility(LinearLayout.GONE);//INVISIBLE | VISIBLE
        } else {
            llout_setting_reading.setVisibility(LinearLayout.VISIBLE);//INVISIBLE | VISIBLE
        }
    }

    public void changeSepiaBackGround(View view) {

        if (!getBackgroundPref().equals("SepiaBackground")) {
            if (firstLoad) {
                lastPos = wv.getScrollY();
//                llout_setting_reading.setVisibility(LinearLayout.GONE);//INVISIBLE | VISIBLE
                editor.putString("BackgroundColor", "SepiaBackground");
                editor.commit();
                progressDialog.show();
                loadWebView();
            }
        }
    }

    public void changeWhiteBackGround(View view) {
        if (!getBackgroundPref().equals("WhiteBackground")) {
            if (firstLoad) {
                lastPos = wv.getScrollY();
//                llout_setting_reading.setVisibility(LinearLayout.GONE);//INVISIBLE | VISIBLE
                editor.putString("BackgroundColor", "WhiteBackground");
                editor.commit();
                progressDialog.show();
                loadWebView();
            }
        }
    }

    public void changeBlackBackGround(View view) {
        if (!getBackgroundPref().equals("BlackBackground")) {
            if (firstLoad) {
                lastPos = wv.getScrollY();
//                llout_setting_reading.setVisibility(LinearLayout.GONE);//INVISIBLE | VISIBLE
                editor.putString("BackgroundColor", "BlackBackground");
                editor.commit();
                if (!progressDialog.isShowing() & active)
                    progressDialog.show();
                loadWebView();
            }
        }
    }

    public void changeCustomBackGround(View view) {
        if (!getBackgroundPref().equals("CustomBackground")) {
            if (firstLoad) {
                lastPos = wv.getScrollY();
//                llout_setting_reading.setVisibility(LinearLayout.GONE);//INVISIBLE | VISIBLE
                editor.putString("BackgroundColor", "CustomBackground");
                editor.commit();
                progressDialog.show();
                loadWebView();
            }
        }
    }

    public void textSizeIncrease(View view) {
        if (firstLoad) {
            lastPos = wv.getScrollY();

            textSizeChanged = true;
            Log.d(TAG, "textSizeBefore: " + lastPos + ", " + (getResources().getDisplayMetrics().density * wv.getContentHeight()));
            totalPosY = (getResources().getDisplayMetrics().density * wv.getContentHeight());
            textRatio = lastPos / totalPosY;

            // 16 = height between lines
//            llout_setting_reading.setVisibility(LinearLayout.GONE);//INVISIBLE | VISIBLE
            textSizeCurrent = textSizeCurrent + 2;
            if (textSizeCurrent > 35)
                textSizeCurrent = 35;
            editor.putInt("TextSize", textSizeCurrent);
            editor.commit();
            progressDialog.show();
            loadWebView();
        }
    }

    public void textSizeDecrease(View view) {
        if (firstLoad) {
            lastPos = wv.getScrollY();

            textSizeChanged = true;
            totalPosY = (getResources().getDisplayMetrics().density * wv.getContentHeight());
            textRatio = lastPos / totalPosY;
            Log.d(TAG, "textSizeBefore: " + lastPos + ", " + (getResources().getDisplayMetrics().density * wv.getContentHeight()));
            // 16 = height between lines

//            llout_setting_reading.setVisibility(LinearLayout.GONE);//INVISIBLE | VISIBLE
            textSizeCurrent = textSizeCurrent - 2;
            if (textSizeCurrent < 10)
                textSizeCurrent = 10;
            editor.putInt("TextSize", textSizeCurrent);
            editor.commit();
            progressDialog.show();
            loadWebView();
        }
    }


    public void touchSetting(View view) {
    }

    public void setFonts(View view) {
        if (firstLoad) {
            final Dialog dialog = new Dialog(ChapterReadingActivity.this);
//        dialog.setTitle("Chọn Font Chữ:");
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.item_font_selection);
            dialog.show();

            ListView lv_fonts = (ListView) dialog.findViewById(R.id.lv_fonts);
            List<String> arr = new ArrayList<>();
            arr.add("Palatino");
            arr.add("Tahoma");
            arr.add("Georgia");
            arr.add("Roboto");
            arr.add("Times New Roman");
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,arr);
//        ArrayAdapter<String> adapter = new FontSelectorAdapter(this, R.layout.fonts_selection_list, arr);
            ArrayAdapter<String> adapter = new FontSelectorAdapter(this, R.layout.item_spanner_singlechoice, arr);
//        adapter.setDropDownViewResource(R.layout.item_spanner_singlechoice);
            lv_fonts.setAdapter(adapter);
            lv_fonts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    switch (position) {
                                                        case 0:
                                                            if (!getFontNamePref().equals("Palatino")) {
                                                                if (firstLoad) {
                                                                    lastPos = wv.getScrollY();
                                                                    progressDialog.show();
                                                                    textSizeChanged = true;
                                                                    totalPosY = (getResources().getDisplayMetrics().density * wv.getContentHeight());
                                                                    textRatio = lastPos / totalPosY;

                                                                    // llout_setting_reading.setVisibility(LinearLayout.GONE);//INVISIBLE | VISIBLE
                                                                    tv_showedFontName.setText("Palatino");
                                                                    dialog.cancel();
                                                                    editor.putString("FontName", "Palatino");
                                                                    editor.commit();
                                                                    loadWebView();
                                                                }
                                                            } else dialog.cancel();
                                                            break;
                                                        case 1:
                                                            if (!getFontNamePref().equals("Tahoma")) {
                                                                if (firstLoad) {
//                                                                lastPos = wv.getScrollY();  progressDialog.show();
                                                                    lastPos = wv.getScrollY();
                                                                    progressDialog.show();
                                                                    textSizeChanged = true;
                                                                    totalPosY = (getResources().getDisplayMetrics().density * wv.getContentHeight());
                                                                    textRatio = lastPos / totalPosY;
                                                                    //llout_setting_reading.setVisibility(LinearLayout.GONE);//INVISIBLE | VISIBLE
                                                                    tv_showedFontName.setText("Tahoma");
                                                                    dialog.cancel();
                                                                    editor.putString("FontName", "Tahoma");
                                                                    editor.commit();
                                                                    loadWebView();
                                                                }
                                                            } else dialog.cancel();
                                                            break;
                                                        case 2:
                                                            if (!getFontNamePref().equals("Georgia")) {
                                                                if (firstLoad) {
                                                                    lastPos = wv.getScrollY();
                                                                    progressDialog.show();
                                                                    textSizeChanged = true;
                                                                    totalPosY = (getResources().getDisplayMetrics().density * wv.getContentHeight());
                                                                    textRatio = lastPos / totalPosY;
                                                                    // llout_setting_reading.setVisibility(LinearLayout.GONE);//INVISIBLE | VISIBLE
                                                                    tv_showedFontName.setText("Georgia");
                                                                    dialog.cancel();
                                                                    editor.putString("FontName", "Georgia");
                                                                    editor.commit();
                                                                    loadWebView();
                                                                }
                                                            } else dialog.cancel();
                                                            break;
                                                        case 3:
                                                            if (!getFontNamePref().equals("Roboto")) {
                                                                if (firstLoad) {
                                                                    lastPos = wv.getScrollY();
                                                                    progressDialog.show();
                                                                    textSizeChanged = true;
                                                                    totalPosY = (getResources().getDisplayMetrics().density * wv.getContentHeight());
                                                                    textRatio = lastPos / totalPosY;
                                                                    //llout_setting_reading.setVisibility(LinearLayout.GONE);//INVISIBLE | VISIBLE
                                                                    tv_showedFontName.setText("Roboto");
                                                                    dialog.cancel();
                                                                    editor.putString("FontName", "Roboto");
                                                                    editor.commit();
                                                                    loadWebView();
                                                                }
                                                            } else dialog.cancel();
                                                            break;
                                                        case 4:
                                                            if (!getFontNamePref().equals("Times N.Roman")) {
                                                                if (firstLoad) {
                                                                    lastPos = wv.getScrollY();
                                                                    progressDialog.show();
                                                                    textSizeChanged = true;
                                                                    totalPosY = (getResources().getDisplayMetrics().density * wv.getContentHeight());
                                                                    textRatio = lastPos / totalPosY;
                                                                    // llout_setting_reading.setVisibility(LinearLayout.GONE);//INVISIBLE | VISIBLE
                                                                    tv_showedFontName.setText("Times N.Roman");
                                                                    editor.putString("FontName", "Times N.Roman");
                                                                    editor.commit();
                                                                    dialog.cancel();
                                                                    loadWebView();
                                                                }
                                                            } else dialog.cancel();
                                                            break;
                                                    }
                                                }
                                            }
            );
        }
    }

    public void setShowingToolbar(View view) {
        if (firstLoad) {
            final Dialog dialog = new Dialog(ChapterReadingActivity.this);
            //       dialog.setTitle("Kiểu Hiện Thanh ToolBar:");
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.item_toolbar_show_select);
            dialog.show();


            ListView lv_toolbarShow = (ListView) dialog.findViewById(R.id.lv_toolbarShow);
            List<String> arr = new ArrayList<String>();
            arr.add("Kiểu Vuốt");
            arr.add("Kiểu Chạm");
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,arr);
            ArrayAdapter<String> adapter = new ToolbarShowingAdapter(this, R.layout.item_spinner_with_icon, arr);
            lv_toolbarShow.setAdapter(adapter);
            lv_toolbarShow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                      @Override
                                                      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                          switch (position) {
                                                              case 0:
                                                                  scrollShowHideMenuBar();
                                                                  editor.putString("ShowedToolbarType", "Scroll");
                                                                  tv_showedToolName.setText("Kiểu Vuốt");
                                                                  dialog.cancel();
                                                                  editor.commit();
                                                                  break;
                                                              case 1:
                                                                  touchShowHideMenuBar();
                                                                  editor.putString("ShowedToolbarType", "Touch");
                                                                  tv_showedToolName.setText("Kiểu Chạm");
                                                                  dialog.cancel();
                                                                  editor.commit();
                                                                  break;
                                                          }
                                                      }
                                                  }
            );
        }
    }

    public void previousChapter(View view) {
//        if(isFetched)
        if (chapterList != null & chapterList.size() != 0) {
            Chapter lchapter = chapterList.get(curr_chapter_pos - 1);
            beginTask(lchapter);
        }
    }

    public void nextChapter(View view) {
//        if(isFetched)
        if (chapterList != null & chapterList.size() != 0) {
            Chapter lchapter = chapterList.get(curr_chapter_pos + 1);
            beginTask(lchapter);
        }
    }

    public void chapterList(View view) {

        final Dialog dialog = new Dialog(ChapterReadingActivity.this);
//        dialog.setTitle("Chọn Font Chữ:");
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_chapter_selection);
        dialog.show();
        final ListView lv_fonts = (ListView) dialog.findViewById(R.id.lv_fonts);
        if (chapterList != null & chapterList.size() != 0) {
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,arr);
//        ArrayAdapter<String> adapter = new FontSelectorAdapter(this, R.layout.fonts_selection_list, arr);
            final ChapterSelectorAdapter adapter = new ChapterSelectorAdapter(this, R.layout.item_chapter_list_spanner_singlechoice, chapterList, chapter);
//        adapter.setDropDownViewResource(R.layout.item_spanner_singlechoice);
            lv_fonts.setAdapter(adapter);
            lv_fonts.setSelection(adapter.current_position_chapter());
//            lv_fonts.post(new Runnable() {
//                @Override
//                public void run() {
//                    lv_fonts.setSelection(adapter.current_position_chapter());
//                }
//            });
//            lv_fonts.setSelection(adapter.current_position_chapter());
            lv_fonts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    if (position != curr_chapter_pos) {
                                                        Chapter lchapter = chapterList.get(position);
                                                        beginTask(lchapter);
                                                    }
                                                    dialog.cancel();
                                                }
                                            }
            );
        }

    }

    DialogFragment newFragment;

    public void commentTask(View view) {
//        Toast.makeText(context, "" + wv.getScrollY()+", "+wv.getScale(), Toast.LENGTH_SHORT).show();
        if (FunctionHelper.isNetworkAvailable(context)) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            newFragment = FacebookCommentDialog.newInstance(url);
            newFragment.show(ft, "dialog");

        } else if (active) Toast.makeText(context, "Không có kết nối!", Toast.LENGTH_SHORT).show();
    }

    private ArrayList<Chapter> list;

    @Override
    public void finishLoadJSon(String error, String json, boolean isLast) {
        if (json != null) {
            final String finalJson = json;
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    list = loadJson.jsonToListChapterFromChapter(finalJson);
                    if (list != null) {
                        chapterList.clear();
                        chapterList.addAll(list);
                    }
                    JSONObject jsonObj = new JSONObject();
                    JSONArray jsonArr = new JSONArray();
                    if (chapterList != null) {
                        for (int i = chapterList.size() - 1; i >= 0; i--) {
                            JSONObject cObj = new JSONObject();
                            try {
                                cObj.put("name", chapterList.get(i).getName()); // Set the first name/pair
                                cObj.put("url", chapterList.get(i).getUrl());
                                jsonArr.put(cObj);
                                jsonObj.put("chapterlist", jsonArr);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        novel.setChapterList(String.valueOf(jsonObj));
                        db.insertChapterList(novel);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    check_chapter_nav_status();
                }
            }.execute();
        }
    }

    public void goTop(View view) {
        if (firstLoad)
            wv.pageUp(true);
    }

    private class getChapterContent extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = null;
//            if (chapter.isDown()) {
//                result = db.getDownloadedChapterContent(chapterName, novelName);
//                return result;
//            } else
            {
                NovelLibrary novelLibrary = new NovelLibrary(params[0]);

                try {
                    result = novelLibrary.getChapterContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!isCancelled())
                if (active) {
                    if (result != null) {
//                        firstLoad = true;
                        editedHtmlTask(result);
                    } else {
                        if (loadingDialog.isShowing())
                            loadingDialog.dismiss();
                        firstLoad = false;
                        badConnection(true);
                        Toast.makeText(context, "Xin hãy kiểm tra lại kết nối!", Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }

    void getChapterContentFromDb() {

        String result = db.getDownloadedChapterContent(chapter);

        if (result != null) {
//            badConnection(false);
            ArrayList<ImageModel> imgArr = db.getDownloadedImage(chapter);
            if (imgArr != null) {
                for (int i = 0; i < imgArr.size(); i++) {
                    String imgUrl = imgArr.get(i).getUrl();
                    String imgPath = "file://" + sanitizeFilename(imgArr.get(i).getPath());
                    result = result.replace(imgUrl, imgPath);
                }
            }
            editedHtmlTask(result);
        } else {
            firstLoad = false;
            badConnection(true);
            Toast.makeText(context, "Dữ liệu đã tải bị lỗi!", Toast.LENGTH_SHORT).show();
        }
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    String sanitizeFilename(String filename) {
        return filename.replaceAll("[\\|\\\\?*<\\\":>]", "_");
    }

    void editedHtmlTask(String result) {

//        if (loadingDialog.isShowing())
//            loadingDialog.dismiss();
        if (progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(ProgressBar.GONE);
        if (wv.getVisibility() == View.GONE)
            wv_contain.setVisibility(View.VISIBLE);
        htmlBackUp = result;
        setWebViewSettings();
        StringBuilder html = new StringBuilder();
        currZoom = chapter.getLastZoom();
//        if (currZoom > 0) {
//            wv.setInitialScale((int) (currZoom * 100));
//        } else {
//            wv.setInitialScale(this.getResources().getInteger(R.integer.default_zoom));
//        }

        html.append("<html><head>");
        html.append("\n<style>img{display: inline;height: auto;max-width: 100%;}</style>\n");
        html.append(DisplayNovelContentHtmlHelper.getCSSSheet());
        html.append(DisplayNovelContentHtmlHelper.getViewPortMeta());
//        if (lastPos != 0) {
        html.append(DisplayNovelContentHtmlHelper.prepareJavaScript((int) (lastPos / wv.getScale()), textSizeChanged, textRatio));
        Log.d(TAG, "(int) (lastPos / wv.getScale()) = " + (int) (lastPos / wv.getScale()));
        html.append("</head><body onload='setup();'>");
//        } else html.append("</head><body>");
        html.append("<p>&nbsp;</p>");
        html.append(htmlBackUp);
        html.append("</body></html>");
        wv.loadDataWithBaseURL("file:///android_asset/", html.toString(), "text/html", "utf-8", NonLeakingWebView.PREFIX_PAGEMODEL);
//        wv.loadDataWithBaseURL("file:///android_asset/", htmlBackUp, "text/html", "utf-8", NonLeakingWebView.PREFIX_PAGEMODEL);
    }

    @SuppressLint({"NewApi", "SetJavaScriptEnabled"})
    private void setWebViewSettings() {
//        wv.setWebViewClient(new WebViewClient(){
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                Log.d(TAG, "shouldOverrideUrlLoading: "+ url);
//                return super.shouldOverrideUrlLoading(view, url);
//            }
//        });
        wv.setWebViewClient(new WebViewChapterContentClient(ChapterReadingActivity.this));
        wv.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, final int progress) {

                if (progress == 100) {
//                    if (loadingDialog.isShowing())
//                        loadingDialog.dismiss();
//                    if (wv.getVisibility() == View.GONE)
//                        wv_contain.setVisibility(View.VISIBLE);
                    wv.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!firstLoad) {
                                if (!db.isChapterRead(chapter))
                                    db.setStateChapter(chapter);
                                firstLoad = true;
                            }
                            currZoom = wv.getScale();
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
//        wv.setWebViewClient(new UriWebViewClient());
        wv.getSettings().setAllowFileAccess(true);
        wv.getSettings().setSupportZoom(isZoomEnable);
        wv.getSettings().setBuiltInZoomControls(isZoomEnable);

        wv.setDisplayZoomControl(UIHelper.getZoomControlPreferences(this));

        wv.getSettings().setLoadWithOverviewMode(true);
//        wv.getSettings().setUseWideViewPort(true);
//        wv.getSettings().setLoadsImagesAutomatically(getShowImagesPreferences());
        wv.getSettings().setLoadsImagesAutomatically(true);
//        if (getColorPreferences(this))
//        wv.setBackgroundColor(0);
        wv.getSettings().setJavaScriptEnabled(true);
    }

    public static String getFontNamePref() {
        return sharedPreferences.getString("FontName", "Georgia");
    }

    public static String getBackgroundPref() {
        return sharedPreferences.getString("BackgroundColor", "WhiteBackground");
    }

    public static String getShowTypePref() {
        return sharedPreferences.getString("ShowedToolbarType", "Scroll");
    }

    public static int getTextSizePref() {
        return sharedPreferences.getInt("TextSize", 20);
    }

    private void loadWebView() {
        if (firstLoad) {
            StringBuilder html = new StringBuilder();

            html.append("<html><head>");
            html.append("\n<style>img{display: inline;height: auto;max-width: 100%;}</style>\n");
            html.append(DisplayNovelContentHtmlHelper.getCSSSheet());
            html.append(DisplayNovelContentHtmlHelper.getViewPortMeta());
            html.append(DisplayNovelContentHtmlHelper.prepareJavaScript((int) (lastPos / wv.getScale()), textSizeChanged, textRatio));
            html.append("</head><body onload='setup();'>");
            html.append("<p>&nbsp;</p>");
            html.append(htmlBackUp);
            html.append("</body></html>");

//                wv.loadDataWithBaseURL(UIHelper.getBaseUrl(getActivity()), html.toString(), "text/html", "utf-8", NonLeakingWebView.PREFIX_PAGEMODEL);
            wv.loadDataWithBaseURL("file:///android_asset/", html.toString(), "text/html", "utf-8", NonLeakingWebView.PREFIX_PAGEMODEL);
            if (textSizeChanged) {
                Log.d(TAG, "onPageFinished(text): " + wv.getContentHeight() + ", " + getResources().getDisplayMetrics().density + ", HEIGHT: " + wv.getHeight());
//                                        lastPos = (int) (textRatio * (getResources().getDisplayMetrics().density * wv.getContentHeight()) - wv.getHeight()/2);
//                lastPos = (int) (textRatio * (getResources().getDisplayMetrics().density * wv.getContentHeight()));
//                                        wv.scrollTo(0, lastPos);
//                Log.d(TAG, "onPageFinished(text): " + textRatio + ", HEIGHT: " + wv.getHeight());
                textSizeChanged = false;
                textRatio = 1.0;
            }
        }
//        while(wv.getScrollY()!= lastPos)
//            wv.scrollTo(0, lastPos);
//        Toast.makeText(context, ""+lastPos, Toast.LENGTH_SHORT).show();
//        wv.scrollTo(0,lastYPos);
    }

    public void screenMeasure() {
//        Display display =getWindowManager
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float scale = getResources().getDisplayMetrics().density;
        dp8AsPixel = (int) (8 * scale + 0.5f);
        dpWidth = (int) (displayMetrics.widthPixels / displayMetrics.density);
    }

    protected void facebookSDKInitialize() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.d("Kai", data.toString());
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (getChapterContent != null)
            getChapterContent.cancel(true);
        if (getChapterContentFromDb != null)
            getChapterContentFromDb.cancel(true);
        progressDialog.dismiss();
//        loadingDialog.dismiss();
        if (!isBadConnection)
            if (firstLoad) {
                final int contentHeight = wv.getContentHeight();
//                final int lastY = wv.getScrollY() + wv.getBottom();
                wv.getScale();
                if (!isZoomEnable)
                    lastPos = wv.getScrollY();
                else lastPos = (int) (wv.getScrollY() / wv.getScale() * currZoom);

//                chapter.setLastZoom(currentScale);
                chapter.setLastY(lastPos);
                chapter.setLastYHeight(getResources().getDisplayMetrics().density * contentHeight);
//                double isReadThreshold = (contentHeight * currentScale) - currentScale;
                db.setStateChapter(chapter);
            }
    }

    private boolean isSettingPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Kai", "Permission is granted");
                return true;
            } else {
                Log.v("Kai", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_SETTINGS}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Kai", "Permission is granted");
            return true;
        }
    }

    private class GetCommentNumberAsync extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            tv_comment_count.setText("");
//                ib_comment.setImageResource(R.drawable.icon_speech_bubble);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            final String fbApi = "http://graph.facebook.com/?fields=og_object%7Blikes.summary(true).limit(0)%7D,share&id=";
            String url_select = fbApi + params[0];

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(url_select);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                return buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (!isCancelled())
                if (result != null) {
                    try {
                        JSONObject jObj = new JSONObject(result);
                        JSONObject obj = jObj.getJSONObject("share");
//                        ib_comment.setImageResource(R.drawable.comment_oval_bubble);
                        tv_comment_count.setText("" + obj.getInt("comment_count"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    super.onPostExecute(result);
                }
        }
    }
}