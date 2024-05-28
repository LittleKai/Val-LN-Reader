package com.valvrare.littlekai.valvraretranslation.utils;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.valvrare.littlekai.valvraretranslation.database.Var;
import com.valvrare.littlekai.valvraretranslation.model.Chapter;
import com.valvrare.littlekai.valvraretranslation.model.Novel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

public class LoadJson {
    private String    LINK = "http://valvrareteam.com/get_chapter_list.php";
    //    private final String LINK_PRODUCT = "http://www.luoithepthanhthanh.info/product_ws/product_addition.php";
//    private final String LINK_IMG = "http://www.luoithepthanhthanh.info/product_ws/add_img.php";
    private Context context;
    private Novel novel;
//    private ValvrareDatabaseHelper db;

    public LoadJson(Context context, Novel novel) {
        this.context = context;
        this.novel = novel;
//        db = new ValvrareDatabaseHelper(context);
    }

    public LoadJson(Context context) {
        this.context = context;
    }

    public void setNovel(Novel novel) {
        this.novel = novel;
    }

    public void sendDataToServer(String novel_name, HashMap<String, String> map, final boolean isLast) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put(Var.KEY_METHOD, 101);
        params.put(Var.KEY_NOVEL_NAME, novel_name.replace("–", "-").replace("’", "\\'"));
//        Log.d("Kai", "sendDataToServer: "+novel_name.replace("–","-").replace("’","\\'"));
        if (map != null) {
            for (String key : map.keySet()) {
                params.put(key, map.get(key));
            }
        }

        client.post(LINK, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                String json = new String(responseBody);
                System.out.println("onSuccess:" + json);
                onFinishLoadJSonListener.finishLoadJSon(null, json, isLast);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("onFailure:" + statusCode);

                String e;

                if (statusCode == 404) {
                    e = "Không thể tìm thấy tài nguyên yêu cầu";
                } else if (statusCode == 500) {
                    e = "Server không phản hồi";
                } else {
                    e = "Không có kết nối Internet";
                }
                onFinishLoadJSonListener.finishLoadJSon(e, null, isLast);
            }
        });
    }

    //    private Chapter jsonToChapter(JSONObject jsonObject, ValvrareDatabaseHelper db, Novel novel) {
    private Chapter jsonToChapter(JSONObject jsonObject, int i) {
        try {
            String name = jsonObject.getString(Var.KEY_NAME);
//            String novel_name = jsonObject.getString(Var.KEY_NOVEL_NAME);
            String second_url = "http://valvrareteam.com/story/" + jsonObject.getString(Var.KEY_URL);
            String url = "http://valvrareteam.com/story/" +jsonObject.getString(Var.KEY_URL);
//            url = ;
//            String url = "http://valvrareteam.com/?p=" + jsonObject.getString(Var.KEY_ID);

            String date = jsonObject.getString(Var.KEY_PUBLISH_DATE);
            if (date.indexOf(" ") > 0)
                date = date.substring(0, date.indexOf(" "));
            if (date.indexOf("-") > 0) {
                String[] time_parts = date.split("-");
                date = "<b>Ngày đăng:</b> " + time_parts[2] + "/" + time_parts[1] + "/" + time_parts[0] + " ";
            }

//            if (novel_name.equals(novel.getNovelName()))
            {
                Chapter chapter = new Chapter(name, url, novel.getNovelName(), date, false);
                chapter.setOrderNo(i);
                chapter.setNovelImageUrl(novel.getImage());
                chapter.setNovelUrl(novel.getUrl());
//                chapter.setSecond_url(second_url);
                return chapter;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Chapter> jsonToListChapter(String json) {
        ArrayList<Chapter> list = new ArrayList<>();
        try {
            JSONObject jObj = new JSONObject(json);
            JSONArray arraySMSJson = jObj.getJSONArray("chapters");
            for (int i = arraySMSJson.length() - 1; i >= 0; i--) {
                JSONObject jsonObject = arraySMSJson.getJSONObject(i);
                Chapter chapter = jsonToChapter(jsonObject, i + 1);
                if (chapter != null) {
                    list.add(chapter);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<Chapter> jsonToListChapterFromChapter(String json) {
        ArrayList<Chapter> list = new ArrayList<>();
        try {
            JSONObject jObj = new JSONObject(json);
            JSONArray arraySMSJson = jObj.getJSONArray("chapters");
            for (int i = 0; i < arraySMSJson.length(); i++) {
                JSONObject jsonObject = arraySMSJson.getJSONObject(i);
                Chapter chapter = jsonToChapter(jsonObject, i + 1);
                if (chapter != null) {
                    list.add(chapter);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public interface OnFinishLoadJSonListener {
        void finishLoadJSon(String error, String json, boolean isLast);
    }

    private OnFinishLoadJSonListener onFinishLoadJSonListener;

    public void setOnFinishLoadJSonListener(OnFinishLoadJSonListener onFinishLoadJSonListener) {
        this.onFinishLoadJSonListener = onFinishLoadJSonListener;
    }
}
