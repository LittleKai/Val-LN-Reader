package com.valvrare.littlekai.valvraretranslation.utils;


import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.valvrare.littlekai.valvraretranslation.NovelDescriptionActivity;
import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.fragment.LoginFacebookFragment;
import com.valvrare.littlekai.valvraretranslation.inner_fragment.NovelFragment3;
import com.valvrare.littlekai.valvraretranslation.model.Novel;

/**
 * Created by Kai on 12/25/2016.
 */

public class FacebookCommentDialog extends DialogFragment {
    private static final String TAG = "Kai";
    public static String APP_KEY = "516731191856957";
    public static String BASE_DOMAIN = "http://www.valvrareteam.com";
    public static String url = "http://www.valvrareteam.com";

    String userId = null;
    private WebView webView;
    private WebView mWebviewPop;
    FrameLayout mContainer;
    ProgressBar progressBar;
    boolean isLoading;

    public static FacebookCommentDialog newInstance(String mgs) {
        FacebookCommentDialog f = new FacebookCommentDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("mgs", mgs);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isWorking = true;
    }

    boolean isWorking;

    @Override
    public void onPause() {
        super.onPause();
        isWorking = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isWorking = false;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.facebook);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle("Bình Luận");
        getDialog().requestWindowFeature(Window.FEATURE_LEFT_ICON);
        getDialog().setCanceledOnTouchOutside(false);
        View view = inflater.inflate(R.layout.fragment_novel_fragment3, container, false);
        url = getArguments().getString("mgs");

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar_FaceComment);
        progressBar.setVisibility(View.VISIBLE);
//        userId = LoginFacebookFragment.userId;
        mContainer = (FrameLayout) view.findViewById(R.id.flout_novelFacebookPlugin);
        webView = (WebView) view.findViewById(R.id.wv_novelcomment);
//        webView.clearCache(true);

        // Settings for the webview
        loadPlugin();

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

    private void loadPlugin() {
        WebSettings webSettings = webView.getSettings();

//        webView.setWebViewClient(new FacebookCommentDialog.UriWebViewClient());
        webView.setWebChromeClient(new FacebookCommentDialog.UriChromeClient());

        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
//        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setBuiltInZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

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
                        + url + "\" data-width=\"470\"></div> </body></html>", "text/html", null, null);

//        webView.loadDataWithBaseURL(BASE_DOMAIN,
//                "<html><head> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"></head><body><div id=\"content\"><div id=\"fb-root\"></div><div id=\"fb-root\"></div><script>(function(d, s, id) {var js, fjs = d.getElementsByTagName(s)[0];if (d.getElementById(id)) return;js = d.createElement(s); js.id = id;js.src = \"http://connect.facebook.net/en_US/sdk.js#xfbml=1&version=v2.5&appId="
//                        + APP_KEY + "\";fjs.parentNode.insertBefore(js, fjs);}(document, 'script', 'facebook-jssdk'));</script><div class=\"fb-comments\" data-href=\""
//                        + BASE_DOMAIN + PATH_URL + "\"></div></div> </body></html>", "text/html", null, null);
//        webView.loadDataWithBaseURL(url,
//                "<html><head> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"></head><body><div id=\"content\"><div id=\"fb-root\"></div><div id=\"fb-root\"></div><script>(function(d, s, id) {var js, fjs = d.getElementsByTagName(s)[0];if (d.getElementById(id)) return;js = d.createElement(s); js.id = id;js.src = \"http://connect.facebook.net/en_US/sdk.js#xfbml=1&version=v2.5&appId="
//                        + APP_KEY + "\";fjs.parentNode.insertBefore(js, fjs);}(document, 'script', 'facebook-jssdk'));</script><div class=\"fb-comments\" data-href=\""
//                        + BASE_DOMAIN + "\"></div></div> </body></html>", "text/html", null, null);

        webView.setMinimumHeight(300);
    }


    private class UriWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            if (Uri.parse(url).getHost().equals(url)) {
//                // This is your web site, so do not override; let the WebView to load the page
//                return false;
//            }
//            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//            startActivity(intent);
//            return true;
            String host = Uri.parse(url).getHost();
            Log.d(TAG, "url: " + url + ", host: " + host);

            if (host.equals("m.facebook.com")) {
                mContainer.addView(mWebviewPop);
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }


        @Override
        public void onPageFinished(WebView view, String url) {

            super.onPageFinished(view, url);
//            setLoading(false);
            String host = Uri.parse(url).getHost();
            if (isWorking)
                if (url.contains("/plugins/close_popup.php?reload")) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            mContainer.removeView(mWebviewPop);
                            loadPlugin();
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
            mWebviewPop.setWebViewClient(new FacebookCommentDialog.UriWebViewClient());
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
        }

        @Override
        public void onProgressChanged(WebView view, int progress) {
            if (isWorking)
                if (progress == 100) {
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            setLoading(false);
                            DisplayMetrics metrics = getResources().getDisplayMetrics();
                            int width = metrics.widthPixels;
                            int height = metrics.heightPixels;
//                getDialog().getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
                            getDialog().getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, height * 4 / 5);
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
}
