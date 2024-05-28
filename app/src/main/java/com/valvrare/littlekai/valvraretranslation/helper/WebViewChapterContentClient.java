package com.valvrare.littlekai.valvraretranslation.helper;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.valvrare.littlekai.valvraretranslation.ChapterReadingActivity;
import com.valvrare.littlekai.valvraretranslation.DisplayImageActivity;
import com.valvrare.littlekai.valvraretranslation.utils.Constants;

import java.lang.ref.WeakReference;

/**
 * Created by Kai on 5/16/2017.
 */

public class WebViewChapterContentClient extends WebViewClient {
    private ChapterReadingActivity activity;

    public WebViewChapterContentClient(ChapterReadingActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.d("Kai", "shouldOverrideUrlLoading: " + url);
        if (isImageUrl(url) || url.startsWith("file:\\")) {
            handleImageLinkActivity(url, activity);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            activity.startActivity(intent);
        }
            return true;
    }

    String[] img_types = {".jpg", ".jpe", ".jfif", ".gif", ".bmp", ".tif", ".png", ".exif", ".dif"};

    private boolean isImageUrl(String url) {
        String img_url = url.toLowerCase();
        for (String img_type : img_types) {
            if (img_url.contains(img_type))
                return true;
        }
        return false;
    }

    private void handleImageLinkActivity(String url, ChapterReadingActivity caller) {
        Intent intent = new Intent(caller, DisplayImageActivity.class);
        intent.putExtra(Constants.EXTRA_IMAGE_URL, url);
//        intent.putExtra(Constants.EXTRA_PAGE, caller.content.getPage());
        caller.startActivity(intent);
    }
}
