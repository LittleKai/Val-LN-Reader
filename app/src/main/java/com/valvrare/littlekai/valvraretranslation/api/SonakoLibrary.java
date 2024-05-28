package com.valvrare.littlekai.valvraretranslation.api;

import android.util.Log;

import com.valvrare.littlekai.valvraretranslation.helper.FunctionHelper;
import com.valvrare.littlekai.valvraretranslation.model.Chapter;
import com.valvrare.littlekai.valvraretranslation.model.Novel;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Kai on 9/25/2016.
 */
public class SonakoLibrary {
    private static final String TAG = "Kai";
    private String url;
    private Document document;
    //    private ArrayList<Novel> allNovels;
    private ArrayList<String> allNovels;
    private String URL_ROOT = "http://sonako.wikia.com/wiki/";
    private String URL_Category = "http://sonako.wikia.com/wiki/Category:";
    private String category_exhibition_sort_alphabet = "?sort=alphabetical&display=exhibition";
    private String URL_Completed_LightNovel = URLDecoder.decode("http://sonako.wikia.com/wiki/Category:Ho%C3%A0n_th%C3%A0nh", "utf8");
    private String URL_Active_LightNovel = "http://sonako.wikia.com/wiki/Category:Active_Projects";
    private String URL_Idle_LightNovel = "http://sonako.wikia.com/wiki/Category:Idle_Projects";
    private String URL_Teaser_LightNovel = "http://sonako.wikia.com/wiki/Category:Teaser";
    private String URL_Original_Novel = "http://sonako.wikia.com/wiki/Category:Original_Light_Novel";

    public SonakoLibrary() throws UnsupportedEncodingException {
//        ValvrareDatabaseHelper db = new ValvrareDatabaseHelper(LNReaderApplication.getInstance());
//        allNovels = db.getAllNovelName();
    }

    public int getMaxPage(String url) {
        int pages = 1;
        Document document = null;
        try {
            document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0").referrer("http://www.google.com").method(Connection.Method.GET)
                    .timeout(60 * 1000).get();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                document = Jsoup.parse(FunctionHelper.getContentFromUrl(url));
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
        }
        Log.d(TAG, "getMaxPage: " + url);
        if (document != null) {
        Elements elements = document.select("div.wikia-paginator").select("ul").select("li");
        if (elements.size() > 0) {
            pages = Integer.parseInt(elements.get(elements.size() - 2).select("a").text());
        }}
//        pages = elements.size();
        Log.d(TAG, "getMaxPage: " + pages);
        return pages;
    }

    public ArrayList<Novel> getAllNovel(String u) throws UnsupportedEncodingException {
        ArrayList<Novel> novels = new ArrayList<>();

        Document document = null;
        try {
            document = Jsoup.connect(u)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0").referrer("http://www.google.com").method(Connection.Method.GET)
                    .timeout(60 * 1000).get();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                document = Jsoup.parse(FunctionHelper.getContentFromUrl(u));
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
        }

//            Elements elements = document.select("div.article").select("div");
//            Element content = elements.get(0);
        if (document != null) {
        Elements novelList = document.select("div.category-gallery-item");
        if (novelList.size() > 0)
            for (int i = 0; i < novelList.size(); i++) {
                String novelName = novelList.get(i).select("div.title").text();
                String url = novelList.get(i).select("a").attr("href");
                String img = null;
                if (novelList.get(i).select("div.category-gallery-item-image").select("img").size() > 0)
                    img = novelList.get(i).select("div.category-gallery-item-image").select("img").attr("src");
                Log.d(TAG, "getAllNovel: " + i + "," + novelName);
                if (img != null)
                    img = URLDecoder.decode(img, "utf8");
                if (url != null)
                    url = URLDecoder.decode(url, "utf8");

                novels.add(new Novel(novelName, url, img));
            }}
        return novels;
    }

    public Document getMainNovelContent(String url) {
//        url = URLEncoder.encode(url,"UTF-8");
        Log.d(TAG, "getMainNovelContent URL: " + url);
        Document document = null;
//        Connection.Response response = Jsoup.connect(url).execute();
//        int statusCode = response.statusCode();
//        Log.d(TAG, "getMainNovelContent: " + statusCode);
//        if (statusCode == 400) {

        try {
            document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0").referrer("http://www.google.com").method(Connection.Method.GET)
                    .timeout(60 * 1000).get();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                document = Jsoup.parse(FunctionHelper.getContentFromUrl(url));
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
        }

//        Log.d(TAG, "document: " + document.text());

//        Log.d(TAG, "get Document: " + FunctionHelper.getContentFromUrl(url));
//                FunctionHelper.getDocumentFromUrl(url);

//        Document document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0").referrer("http://www.google.com").method(Connection.Method.GET).timeout(60 * 1000).get();
        return document;
    }

    void removeElement(Elements elements, String source) {
        Elements e = elements.select(source);
        if (e.size() > 0)
            e.remove();
    }

    public ArrayList<Chapter> getChapterList(String url) {
        Log.d(TAG, "getChapterList: " + url);
        ArrayList<Chapter> chapters = new ArrayList<>();
        Document document = null;
        try {
            document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com").method(Connection.Method.GET).timeout(60 * 1000).get();
        } catch (IOException e) {
            e.printStackTrace();

            try {
                document = Jsoup.parse(FunctionHelper.getContentFromUrl(url));
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
        }

        if (document != null) {
            Elements rmvElements = document.select("div#mw-blogs");
            if (rmvElements.size() > 0) rmvElements.remove();
            Elements list = document.select("div.category-gallery-item");
            if (list.size() > 0)
                for (int i = 0; i < list.size(); i++) {
                    String novelName = list.get(i).select("div.title").text();
                    String u = list.get(i).select("a").attr("href");
                    String img = null;
                    if (list.get(i).select("div.category-gallery-item-image").select("img").size() > 0)
                        img = list.get(i).select("div.category-gallery-item-image").select("img").attr("src");

//                if (img != null)
//                    img = URLDecoder.decode(img, "utf8");
//                if (url != null)
//                    url = URLDecoder.decode(url, "utf8");

                    chapters.add(new Chapter(novelName, u, img));
                }
        }

        return chapters;
    }

}
