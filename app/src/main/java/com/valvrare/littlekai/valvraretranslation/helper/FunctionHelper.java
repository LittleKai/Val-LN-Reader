package com.valvrare.littlekai.valvraretranslation.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.valvrare.littlekai.valvraretranslation.model.Chapter;
import com.valvrare.littlekai.valvraretranslation.model.Novel;
import com.valvrare.littlekai.valvraretranslation.utils.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Kai on 12/27/2016.
 */

public class FunctionHelper {
    public static Document getDocumentFromUrl(String u) throws IOException, SAXException, ParserConfigurationException {
        Log.d("Kai", "getDocumentFromUrl: " + u);
        URL url = new URL(u);
        URLConnection conn = url.openConnection();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(conn.getInputStream());
    }

    public static String getContentFromUrl(String u) throws MalformedURLException {
        Log.d("Kai", "getContentFromUrl: " + u);
        URL url = new URL(u);
        BufferedReader in = null;
        String content = null;
        try {
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content += inputLine;
            }
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return content;
    }

    public static String replaceImagePath(String content) {
        String root = UIHelper.getImageRoot(LNReaderApplication.getInstance().getApplicationContext());

        // standard image
        String imagePath = "src=\"file://" + root + "/project/images/";
        content = content.replace("src=\"/project/images/", imagePath);

        // /project/thumb.php?f=Biblia1_011.png&width=300
        // thumb.php
        String thumbPath = "src=\"file://" + root + "/project/thumb.php_";
        content = content.replace("src=\"/project/thumb.php?", thumbPath);

        // remove srcset
        content = content.replace("srcset=", "srcset-disabled=");

        //Log.v(TAG, content);
        return content;
    }

    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static boolean getContainArrayString(String[] list, String object) {
        for (String aList : list) {
            if (object.equals(aList))
                return true;
        }
        return false;
    }

    public static void checkEnableDownloadChapters(ArrayList<Chapter> list) {
        if (list != null)
            if (list.size() > 0) {
                int size = list.size();
                if (size <= 5) {
                    for (Chapter c : list)
                        c.setEnableDownload(true);
                } else if (size < 100) {
                    for (int i = 0; i < 5; i++)
                        list.get(i).setEnableDownload(false);
                } else if (size >= 100) {
                    for (int i = 0; i < 10; i++)
                        list.get(i).setEnableDownload(false);
                }
            }
    }

    public static String snkChapterList_to_json(ArrayList<Chapter> listChapters) {
        if (listChapters != null)
            if (listChapters.size() > 0) {
                JSONObject jsonObj = new JSONObject();
                JSONArray jsonArr = new JSONArray();
                for (int i = 0; i < listChapters.size(); i++) {
                    JSONObject cObj = new JSONObject();
                    try {
                        cObj.put("name", listChapters.get(i).getName()); // Set the first name/pair
                        cObj.put("url", listChapters.get(i).getUrl());
                        if (listChapters.get(i).getImg() != null)
                            cObj.put("image", listChapters.get(i).getImg());
                        else
                            cObj.put("image", "");
//                        if (listChapters.get(i).getSecond_name() != null)
//                            cObj.put("2nd_name", listChapters.get(i).getSecond_name());
//                        else
//                            cObj.put("2nd_name", "");
                        jsonArr.put(cObj);
                        jsonObj.put("chapterlist", jsonArr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return String.valueOf(jsonObj);
            }
        return null;
    }

    public ArrayList<Chapter> json_to_ChapterList(String data, Novel novel) {

        ArrayList<Chapter> listChapters = new ArrayList<>();
        if (data != null) {
            try {
                JSONObject jObj = new JSONObject(data);
                JSONArray jArr = jObj.getJSONArray("chapterlist");
                for (int i = 0; i < jArr.length(); i++) {
//                        for (int i = jArr.length() - 1; i >= 0; i--) {
                    try {
                        JSONObject obj = jArr.getJSONObject(i);
                        Chapter chapter = jsonToChapter(obj, novel, jArr.length() - i);
                        if (chapter != null)
                            listChapters.add(chapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return listChapters;
    }

    private Chapter jsonToChapter(JSONObject obj, Novel novel, int i) {
        try {
            Chapter chapter = new Chapter(obj.getString("name"), obj.getString("url"), novel.getNovelName(), "", false);

//            chapter.setImg(obj.getString("image"));
//            chapter.setSecond_name(obj.getString("2nd_name"));

            chapter.setOrderNo(i);
            chapter.setNovelImageUrl(novel.getImage());
            chapter.setNovelUrl(novel.getUrl());
            return chapter;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Chapter> json_to_ValChapterList(String data, Novel novel) {
        ArrayList<Chapter> listChapters = new ArrayList<>();
        if (data != null) {
            try {
                JSONObject jObj = new JSONObject(data);
                JSONArray jArr = jObj.getJSONArray("chapterlist");

                for (int i = 0; i < jArr.length(); i++) {
//                        for (int i = jArr.length() - 1; i >= 0; i--) {
                    try {
                        JSONObject obj = jArr.getJSONObject(i);
                        Chapter chapter = jsontoValChapter(obj, novel, jArr.length() - i);
                        if (chapter != null)
                            listChapters.add(chapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return listChapters;
    }

    private Chapter jsontoValChapter(JSONObject obj, Novel novel, int i) {
        try {
            Chapter chapter = new Chapter(obj.getString("name"), obj.getString("url"), novel.getNovelName(), "", false);
            chapter.setOrderNo(i);
            chapter.setNovelImageUrl(novel.getImage());
            chapter.setNovelUrl(novel.getUrl());
            return chapter;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String sanitizeFilename(String filename) {
        return filename.replaceAll("[\\|\\\\?*<\\\":>]", "_");
    }

    public static String sanitizeSnkLink(String url) {
        return url.replace("?", "%3F").replace(",", "%2C");
    }

    public static void expand(final View v, int time) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        if (time == 0)
            // 1dp/ms
            a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        else a.setDuration(time);
        v.startAnimation(a);
    }

    public static void collapse(final View v, int time) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        if (time == 0)
            // 1dp/ms
            a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        else a.setDuration(time);
        v.startAnimation(a);
    }
}

