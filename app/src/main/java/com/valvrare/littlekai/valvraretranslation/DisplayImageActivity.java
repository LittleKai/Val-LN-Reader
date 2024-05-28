package com.valvrare.littlekai.valvraretranslation;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.valvrare.littlekai.valvraretranslation.helper.DisplayNovelContentHtmlHelper;
import com.valvrare.littlekai.valvraretranslation.utils.Constants;
import com.valvrare.littlekai.valvraretranslation.utils.UIHelper;
import com.valvrare.littlekai.valvraretranslation.widget.NonLeakingWebView;

import java.util.ArrayList;

public class DisplayImageActivity extends AppCompatActivity {

    private String url;
    private String imageUrl;
    private String parent;
    private NonLeakingWebView imgWebView;
    private ArrayList<String> images;
    private int currentImageIndex = 0;
    private Menu _menu;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setContentInsetStartWithNavigation(0);
        toolbar.setTitleTextColor(0xffffffff);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imgWebView = (NonLeakingWebView) findViewById(R.id.webViewImage);
        setupWebView();
        Intent intent = getIntent();
        imageUrl = intent.getStringExtra(Constants.EXTRA_IMAGE_URL);

        if (imageUrl != null) {
//            if (imageUrl.startsWith("file://"))
//                imageUrl = imageUrl.replace("file://", "http://sonako.wikia.com");
            setTitle(imageUrl.substring(imageUrl.lastIndexOf("/") + 1));
        }

        if (imgWebView != null) {
            StringBuilder html = new StringBuilder();
            html.append("<html><head>");
            html.append(DisplayNovelContentHtmlHelper.getViewPortMeta());
            html.append("</head><body>");
            html.append("<img src=\"" + imageUrl + "\"></img>");
            html.append("</body></html>");
            //imgWebView.loadUrl(imageUrl);
            imgWebView.loadDataWithBaseURL("file://", html.toString(), "text/html", "utf-8", null);
            Log.d("Kai", "onCreate: " + html.toString());
        }

    }

    public void setupWebView() {
        imgWebView.getSettings().setAllowFileAccess(true);
        imgWebView.getSettings().setLoadWithOverviewMode(true);
        imgWebView.getSettings().setUseWideViewPort(true);
        imgWebView.setBackgroundColor(0);
        imgWebView.getSettings().setBuiltInZoomControls(true);
        imgWebView.setDisplayZoomControl(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        setupWebView();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("currentImageIndex", currentImageIndex);
        savedInstanceState.putString(Constants.EXTRA_IMAGE_URL, url);
        savedInstanceState.putStringArrayList("image_list", images);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentImageIndex = savedInstanceState.getInt("currentImageIndex", 0);
        url = savedInstanceState.getString(Constants.EXTRA_IMAGE_URL);
        images = savedInstanceState.getStringArrayList("image_list");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NonLeakingWebView imgWebView = (NonLeakingWebView) findViewById(R.id.webViewImage);
        if (imgWebView != null) {
            RelativeLayout rootView = (RelativeLayout) findViewById(R.id.rootView);
            rootView.removeView(imgWebView);
            imgWebView.removeAllViews();
            imgWebView.destroy();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return false;
    }

}
