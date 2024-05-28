package com.valvrare.littlekai.valvraretranslation.helper;

import android.app.Application;

import com.valvrare.littlekai.valvraretranslation.DownloadActivity;
import com.valvrare.littlekai.valvraretranslation.NovelDescriptionActivity;
import com.valvrare.littlekai.valvraretranslation.inner_fragment.NovelFragment2;
import com.valvrare.littlekai.valvraretranslation.utils.UIHelper;

import java.util.Hashtable;

/**
 * Created by Kai on 10/3/2016.
 */

public class LNReaderApplication extends Application {
    private static LNReaderApplication instance;
    private NovelFragment2 novelFragment2;
    private boolean isDownloading;
    private int max_chapter;
    private NovelDescriptionActivity novelDescriptionActivity;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

     public NovelDescriptionActivity getNovelDescriptionActivity() {
        return novelDescriptionActivity;
    }

    public void setNovelDescriptionActivity(NovelDescriptionActivity novelDescriptionActivity) {
        this.novelDescriptionActivity = novelDescriptionActivity;
    }

    public static LNReaderApplication getInstance() {
        return instance;
    }
    private Hashtable<Integer, String> cssCache = null;

    public NovelFragment2 getNovelFragment2() {
        return novelFragment2;
    }

    public void setNovelFragment2(NovelFragment2 novelFragment2) {
        this.novelFragment2 = novelFragment2;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }

    public String ReadCss(int styleId) {
        if (cssCache == null)
            cssCache = new Hashtable<Integer, String>();
        if (!cssCache.containsKey(styleId)) {
            cssCache.put(styleId, UIHelper.readRawStringResources(getApplicationContext(), styleId));
        }
        return cssCache.get(styleId);
    }
}
