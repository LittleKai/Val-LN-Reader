package com.valvrare.littlekai.valvraretranslation.inner_fragment;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.valvrare.littlekai.valvraretranslation.NovelDescriptionActivity;
import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.api.NovelLibrary;
import com.valvrare.littlekai.valvraretranslation.database.ValvrareDatabaseHelper;
import com.valvrare.littlekai.valvraretranslation.helper.LNReaderApplication;
import com.valvrare.littlekai.valvraretranslation.model.Novel;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class NovelFragment1 extends Fragment {

    ProgressBar progressBar;
    boolean isLoading;
    private static final String TAG = "Kai";
    private WebView wv_NovelIntroduce;
    private Novel novel;
    private String novelIntroduceURL = "http://valvrareteam.com";
    private String summary = "";
    private Button btn_moreDes, btn_reload;

    private LinearLayout ll_reload;
    private TextView tv_summary;
    private CardView cardView;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_novel_fragment1, container, false);
        LNReaderApplication lnReaderApplication = (LNReaderApplication) getActivity().getApplicationContext();
        NovelDescriptionActivity activity = lnReaderApplication.getNovelDescriptionActivity();

        novel = activity.getNovel();
        if (novel != null) {
            summary = novel.getSummary();
            novelIntroduceURL = novel.getUrl();
        }
        ll_reload = (LinearLayout) view.findViewById(R.id.ll_reload);
        tv_summary = (TextView) view.findViewById(R.id.tv_summary);
        btn_moreDes = (Button) view.findViewById(R.id.btn_morDes);
        btn_reload = (Button) view.findViewById(R.id.btn_reload);
        wv_NovelIntroduce = (WebView) view.findViewById(R.id.wv_NovelIntroduce);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar_NovelDescrip);
        cardView = (CardView) view.findViewById(R.id.cardView);
        if (summary == null)
            summary = new ValvrareDatabaseHelper(getContext()).getSummary(novel);
        tv_summary.setText(summary);

        btn_moreDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardView.setVisibility(View.GONE);
                btn_moreDes.setVisibility(View.GONE);
                loadWV();
            }
        });
        btn_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_reload.setVisibility(View.GONE);
                loadWV();
            }
        });

        return view;
    }
    @SuppressLint({"NewApi", "SetJavaScriptEnabled"})
    private void loadWV() {
        wv_NovelIntroduce.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        wv_NovelIntroduce.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.invalidate();
//                view.loadUrl(url);
                Log.d(TAG, "Novel Fragment 1: "+url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        WebSettings webSettings = wv_NovelIntroduce.getSettings();
        if (Build.VERSION.SDK_INT >= 19) {
            wv_NovelIntroduce.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            wv_NovelIntroduce.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

//        wv_NovelIntroduce.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        wv_NovelIntroduce.loadUrl(novelIntroduceURL);

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String html = null;
                NovelLibrary novelLibrary = new NovelLibrary(params[0]);
                try {
                    html = novelLibrary.getNovelItroduceHtml();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return html;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result != null) {
                    setLoading(false);
                    wv_NovelIntroduce.loadData(result, "text/html;charset=UTF-8", null);
                } else {
                    wv_NovelIntroduce.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    ll_reload.setVisibility(View.VISIBLE);
                }
            }
        }.execute(novelIntroduceURL);
        Log.d(TAG, "NF1 loadWV: "+novelIntroduceURL);
        setLoading(true);
    }

    private void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
        if (isLoading)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
