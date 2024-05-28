package com.valvrare.littlekai.valvraretranslation.inner_fragment;


import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.valvrare.littlekai.valvraretranslation.NovelDescriptionActivity;
import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.fragment.LoginFacebookFragment;
import com.valvrare.littlekai.valvraretranslation.helper.LNReaderApplication;
import com.valvrare.littlekai.valvraretranslation.model.Novel;

/**
 * A simple {@link Fragment} subclass.
 */
public class NovelFragment3 extends Fragment implements Updateable {
    private final String TAG = "Kai";
    public String APP_KEY = "516731191856957";
    public String BASE_DOMAIN = "http://www.valvrareteam.com";
    public String url = "http://www.valvrareteam.com";

    String userId = null;
    private WebView webView;
    private WebView mWebviewPop;
    FrameLayout mContainer;
    ProgressBar progressBar;
    boolean isLoading;
    boolean isLogin = true;
    boolean isStarted = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_novel_fragment3, container, false);
        // Inflate the layout for this fragment
        Bundle args = getArguments();
        if (args != null) {
            url = args.getString("url", "");
        } else {
            LNReaderApplication  lnReaderApplication = (LNReaderApplication) getActivity().getApplicationContext();
            NovelDescriptionActivity   activity = lnReaderApplication.getNovelDescriptionActivity();
            Novel novel = activity.getNovel();
            if (novel != null)
                url = novel.getUrl();
        }

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar_FaceComment);
        progressBar.setVisibility(View.VISIBLE);
//        userId = LoginFacebookFragment.userId;
        mContainer = (FrameLayout) view.findViewById(R.id.flout_novelFacebookPlugin);
        webView = (WebView) view.findViewById(R.id.wv_novelcomment);
//        webView.clearCache(true);

        // Settings for the webview
        loadPlugin(false);

        setLoading(true);
        return view;
    }

    private void setLoading(boolean isLoading) {
        this.isLoading = isLoading;

        if (isLoading)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);

//        invalidateOptionsMenu();
    }

    private void loadPlugin(boolean uriEnable) {
        WebSettings webSettings = webView.getSettings();

//        webView.setWebViewClient(new UriWebViewClient());
//        if(uriEnable)

        webView.setWebChromeClient(new UriChromeClient());

        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
//        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setBuiltInZoomControls(false);
//        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        CookieManager.getInstance().setAcceptCookie(true);

        if (Build.VERSION.SDK_INT >= 21) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }

        webView.loadDataWithBaseURL(BASE_DOMAIN,
                "<html><head></head><body>" +
                        "<div id=\"fb-root\"></div><div id=\"fb-root\"></div><script>" +
                        "(function(d, s, id) {var js, fjs = d.getElementsByTagName(s)[0];if (d.getElementById(id)) return;js = d.createElement(s); js.id = id;" +
                        "js.src = \"http://connect.facebook.net/vi_VN/all.js#xfbml=1&appId="
                        + APP_KEY
                        + "\";fjs.parentNode.insertBefore(js, fjs);}(document, 'script', 'facebook-jssdk'));</script>" +
                        "<div class=\"fb-comments\" data-href=\""
                        + url
                        + "\" data-width=\"470\"></div> </body></html>", "text/html", null, null);

        webView.setMinimumHeight(200);
    }


    private class UriWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            if (Uri.parse(url).getHost().equals(url)) {
//                // This is your web site, so do not override; let the WebView to load the page
//                return false;
//            }
//            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs

//            return true;
            String host = Uri.parse(url).getHost();
            Log.d(TAG, "url: " + url + ", host: " + host);

//            if (host.contains("m.facebook.com")|host.contains("facebook.com")) {
                if (host.contains("m.facebook.com")) {
                    mContainer.addView(mWebviewPop);
//                    WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
//                    transport.setWebView(mWebviewPop);
//                    resultMsg.sendToTarget();
                    isLogin=true;
                Log.d(TAG, "url: " + url + ", host: " + host);
                return false;
            }
//
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);

            return true;
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            setLoading(false);
            Log.d(TAG, "onPageFinished: removeVie");
//            String host = Uri.parse(url).getHost();
            if (url.contains("/plugins/close_popup.php?reload")) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //Do something after 100ms
                        mContainer.removeView(mWebviewPop);
                        loadPlugin(true);
                    }
                }, 500);
            }

        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
            Log.d("Kai", "onReceivedSslError");
        }

    }


    private class UriChromeClient extends WebChromeClient {

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog,
                                      boolean isUserGesture, Message resultMsg) {
            mWebviewPop = new WebView(getActivity());
            mWebviewPop.setVerticalScrollBarEnabled(false);
            mWebviewPop.setHorizontalScrollBarEnabled(false);
            mWebviewPop.setWebViewClient(new UriWebViewClient());
            mWebviewPop.setWebChromeClient(this);
            mWebviewPop.getSettings().setJavaScriptEnabled(true);
            mWebviewPop.getSettings().setDomStorageEnabled(true);
            mWebviewPop.getSettings().setSupportZoom(true);
            mWebviewPop.getSettings().setBuiltInZoomControls(true);
            mWebviewPop.getSettings().setSupportMultipleWindows(true);
            mWebviewPop.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
//            mContainer.addView(mWebviewPop);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(mWebviewPop);
            resultMsg.sendToTarget();

            return true;
//            }
//            else return false;
        }

        @Override
        public void onProgressChanged(WebView view, int progress) {
            if (progress == 100) {
                Log.d(TAG, "onProgressChanged: ");
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: ");
                        setLoading(false);
                    }
                });
            }
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage cm) {
            Log.d("Kai", cm.message() + " -- From line "
                    + cm.lineNumber() + " of "
                    + cm.sourceId());
            return true;
        }

        @Override
        public void onCloseWindow(WebView window) {
            Log.d("Kai", "onCloseWindow: called");
        }

    }

    public void update() {
        loadPlugin(true);
        Log.d(TAG, "Fragment updated: ");
    }

}

interface Updateable {
    void update();
}