package com.valvrare.littlekai.valvraretranslation.api;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.valvrare.littlekai.valvraretranslation.database.ValvrareDatabaseHelper;
import com.valvrare.littlekai.valvraretranslation.helper.FunctionHelper;
import com.valvrare.littlekai.valvraretranslation.helper.LNReaderApplication;
import com.valvrare.littlekai.valvraretranslation.model.Novel;
import com.valvrare.littlekai.valvraretranslation.utils.Constants;
import com.valvrare.littlekai.valvraretranslation.utils.DbBitmapUtility;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Kai on 9/25/2016.
 */
public class NovelLibrary {
    private static final String TAG = "Kai";
    private String url;
    private Document document;
    private ValvrareDatabaseHelper db;
    //    private ArrayList<Novel> allNovels;
    private ArrayList<String> allNovels;

    public NovelLibrary(String u) {
        try {
            url = URLDecoder.decode(u, "utf8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public int getMaxPage() throws IOException {
        int pages = 0;
        Document document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0").referrer("http://www.google.com").method(Connection.Method.GET).timeout(60 * 1000).get();
        Elements elements = document.select("div.nav-links").select("a");

        if (elements.size() == 0)
            pages = 1;
        else
            pages = elements.size();
        Log.d(TAG, "getMaxPage: " + pages);
        return pages;
    }

    public ArrayList<Novel> getAllNovel() throws IOException {
        ArrayList<Novel> novels = new ArrayList<>();

        Document document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0").referrer("http://www.google.com").method(Connection.Method.GET).timeout(60 * 1000).get();

//            Elements elements = document.select("div.article").select("div");
//            Element content = elements.get(0);
        Elements novelList = document.select("div.article").select("div").select("article");

//            for (int i = 0; i < 10; i++) {
        for (int i = 0; i < novelList.size(); i++) {
            String novelName = novelList.get(i).select("a").attr("title");
            if (novelName.contains("Bảo vệ: ")) {
                Log.d(TAG, "getAllNovel: bảo vệ " + novelName);
                novelName = novelName.replace("Bảo vệ: ", "");
            }
            String url = novelList.get(i).select("a").attr("href");
            String img = novelList.get(i).select("a").select("div").select("img").attr("src");
            String lastestChap = novelList.get(i).select("div").select("div").select("a").get(2).attr("title");
            lastestChap = lastestChap.substring(11);
            String lastestChapUrl = novelList.get(i).select("div").select("div").select("a").get(2).attr("href");

            String scrset = novelList.get(i).select("a").select("div").select("img").attr("srcset");
            int width = 0;
            int moderate = 0;
            String img_set = null;
            if (scrset != null)
                if (!scrset.isEmpty()) {
                    String[] img_parts = scrset.split(",");
                    if (img_parts.length > 0)
                        for (int j = 0; j < img_parts.length; j++) {
                            String[] parts = img_parts[j].trim().split(" ");
                            int size = Integer.parseInt(parts[1].replaceAll("[^\\d]", ""));
                            if (j != 0) {
                                if (size < width & size > 100) {
                                    img_set = parts[0];
                                    width = size;
                                }
                            } else {
                                img_set = parts[0];
                                width = size;
                            }
                            if ((size > 400 & moderate == 0) | (size > 400 & size < moderate)) {
                                moderate = size;
                                img = parts[0];
                            }
                        }
                }

            Novel novel = new Novel(novelName, url, img, lastestChap, lastestChapUrl);
            novel.setSummary(novelList.get(i).select("div").select("div.des").select("a").get(0).text());

            novels.add(novel);
        }
        return novels;
    }

    public ArrayList<Novel> getNovel() throws IOException {
        final ArrayList<Novel> novels = new ArrayList<>();
        db = new ValvrareDatabaseHelper(LNReaderApplication.getInstance());
        allNovels = db.getAllNovelName();
        Document document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0").referrer("http://www.google.com").method(Connection.Method.GET).timeout(60 * 1000).get();

//            Elements elements = document.select("div.article").select("div");
//            Element content = elements.get(0);
        Elements novelList = document.select("div.article").select("div").select("article");

//            for (int i = 0; i < 10; i++) {
        for (int i = 0; i < novelList.size(); i++) {
            String novelName = novelList.get(i).select("a").attr("title");
            if (novelName.contains("Bảo vệ: ")) {
                Log.d(TAG, "getAllNovel: bảo vệ " + novelName);
                novelName = novelName.replace("Bảo vệ: ", "");
            }
            String url = novelList.get(i).select("a").attr("href");
            String img = novelList.get(i).select("a").select("div").select("img").attr("src");
            String scrset = novelList.get(i).select("a").select("div").select("img").attr("srcset");
            int width = 0;
            int moderate = 0;
            String img_set = null;
            if (scrset != null)
                if (!scrset.isEmpty()) {
                    String[] img_parts = scrset.split(",");
                    if (img_parts.length > 0)
                        for (int j = 0; j < img_parts.length; j++) {
                            String[] parts = img_parts[j].trim().split(" ");
                            int size = Integer.parseInt(parts[1].replaceAll("[^\\d]", ""));
                            if (j != 0) {
                                if (size < width & size > 150) {
                                    img_set = parts[0];
                                    width = size;
                                }
                            } else {
                                img_set = parts[0];
                                width = size;
                            }
                            if ((size > 400 & moderate == 0) | (size > 400 & size < moderate)) {
                                moderate = size;
                                img = parts[0];
                            }
                        }
                }
//            Log.d("Kai", "(" + i + ")" + novelName + "(" + moderate + ")" + ": " + img_set + ", " + width);

            String lastestChap = novelList.get(i).select("div").select("div").select("a").get(2).attr("title");
            lastestChap = lastestChap.substring(11);
            String lastestChapUrl = novelList.get(i).select("div").select("div").select("a").get(2).attr("href");
            if (lastestChap.equals(""))
                lastestChap = "Unknown";

            final Novel novel = new Novel(novelName, url, img, lastestChap, lastestChapUrl);
            novel.setSummary(novelList.get(i).select("div").select("div.des").select("a").get(0).text());
            novel.setSecond_image(img_set);
            int count = 0;

            if (allNovels != null) {
                if (!allNovels.contains(novel.getNovelName()))
                    if (novel.getImage() != null)
                        new AsyncTask<Novel, Void, Bitmap>() {
                            @Override
                            protected Bitmap doInBackground(Novel... params) {
                                Bitmap bitmap;
                                try {
                                    bitmap = Glide.with(LNReaderApplication.getInstance()).load(novel.getImage()).asBitmap().into(200, 200).get();
                                    return bitmap;
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Bitmap bitmap) {
                                if (bitmap != null) {
                                    novel.setImg_file(DbBitmapUtility.getBytes(bitmap));
                                    db.insertAllNovel(novel);
                                }
                                super.onPostExecute(bitmap);
                            }
                        }.execute(novel);
            } else {
                db.insertAllNovel(novel);
            }

            novels.add(novel);
        }
        return novels;
    }

    public String getNovelItroduceHtml() throws IOException {

        if (url.contains("http://sonako.wikia.com")) {
            try {
                document = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                        .referrer("http://www.google.com").method(Connection.Method.GET).timeout(60 * 1000).get();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    document = Jsoup.parse(FunctionHelper.getContentFromUrl(FunctionHelper.sanitizeSnkLink(url)));
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                }
            }

            if (document != null) {
                Elements elements = document.select("div.mw-content-ltr.mw-content-text");

                Elements table_elements = elements.select("table");
                Elements rmv_element = elements.select("span[style*=float:left; padding: 0.3em; display:inline-block;]");

                if (rmv_element.size() > 0) {
                    if (rmv_element.first().parent() != null)
                        rmv_element.first().parent().remove();
//                    rmv_element.remove();
                }

                if (table_elements.size() > 0) {
                    if (table_elements.first().parent() != null)
                        table_elements.first().parent().remove();
                    if (table_elements.first() != null)
                        table_elements.first().remove();
                }

                if (elements.size() > 0) {
//                    String a = "";
//                    Log.d(TAG, "getNovelItroduceHtml: " + a);
//                    removeElement(elements, a);
                    removeElement(elements, "div.home-top-right-ads");
                    removeElement(elements, "span.editsection");
                    removeElement(elements, "div.category-gallery-form");
                    removeElement(elements, "div.print-no.entry-unrelated");
                    return elements.html();
                }
            }
        } else if (url.contains("http://valvrareteam.com")) {
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0").referrer("http://www.google.com").method(Connection.Method.GET)
                    .timeout(60 * 1000).get();
            Elements elements = document.select("header");
            Element element;
            if (elements.size() > 0) {
                if (document.select("header").size() > 0) {
                    element = document.select("header").first();
                    if (element != null) element.remove();
                }
                if (document.select("div.bottomad").size() > 0) {
                    element = document.select("div.bottomad").first();
                    if (element != null) element.remove();
                }
                if (document.select("div.single_post").size() > 0) {
                    if (document.select("div.single_post").first().select("header").size() > 0) {
                        element = document.select("div.single_post").first().select("header").first();
                        if (element != null) element.remove();
                    }
                }
                if (document.select("div.fb-comments").size() > 0) {
                    element = document.select("div.fb-comments").first();
                    if (element != null) element.remove();
                }
            }
            String html = document.outerHtml();
            return html;
        }

        return null;
    }

    private void removeElement(Document document, String s) {
        if (document != null) {
            Elements rmvElements = document.select(s);
            if (rmvElements.size() > 0)
                rmvElements.remove();
        }
    }

    private void removeElement(Elements elements, String s) {
        Elements rmvElements = elements.select(s);
        if (rmvElements.size() > 0)
            rmvElements.remove();
    }


    public Novel getNovelStatus(Novel novel) throws IOException {
        Document document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0").referrer("http://www.google.com").method(Connection.Method.GET).timeout(60 * 1000).get();
        Novel novel1 = new Novel();
        Elements elements = document.select("span.thetime.modified").select("span").select("b");
        if (elements != null)
            if (elements.size() > 0)
                novel1.setUpdateDate(elements.text());
        Elements a = document.select("span.thetime.updated");
        if (a.size() > 1) {
            elements = a.get(1).select("span").select("span").select("b");
            if (elements != null)
                if (elements.size() > 0)
                    novel1.setView(Integer.parseInt(elements.first().text()));
        }

        if (document.select("div.kksr-legend").select("div").select("div").size() > 1) {
            a = document.select("div.kksr-legend").select("div").select("div");
            if (a.size() > 1) {
                Elements rateElements = a.get(1).select("span");
                if (rateElements.size() > 0)
                    novel1.setRate(Double.parseDouble(rateElements.get(0).text()));
                if (rateElements.size() > 0)
                    novel1.setRateCount(Integer.parseInt(rateElements.get(1).text()));
            }
        }
        return novel1;
    }

    public String getChapterContent() throws IOException {


        Log.d(TAG, "getChapterContent: " + url);
        if (url.contains(Constants.VAL_URL_ROOT)) {
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0").referrer("http://www.google.com").method(Connection.Method.GET)
                    .timeout(60 * 1000).get();
            Elements elements = document.select("div.thecontent");
            if (elements.size() == 0)
                return null;
            //select("div[name=thecontent"]")
            Elements rmv_elements = elements.select("div.show-page");
            if (rmv_elements.size() > 0) rmv_elements.remove();

            rmv_elements = elements.select("div.fb-comments");
            if (rmv_elements.size() > 0) rmv_elements.remove();

            rmv_elements = elements.select("script");
            if (rmv_elements.size() > 0) rmv_elements.remove();

            String html = elements.html();
            return html.replace("<span style=\"font-family: ", "<span style =\"");
        } else if (url.contains(Constants.SNK_URL_ROOT)) {
            try {
                document = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                        .referrer("http://www.google.com").method(Connection.Method.GET).timeout(60 * 1000).get();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    document = Jsoup.parse(FunctionHelper.getContentFromUrl(FunctionHelper.sanitizeSnkLink(url)));
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                }
            }

            if (document != null) {
                Elements elements = document.select("div.WikiaArticle");
                if (elements.size() == 0)
                    return null;
                //select("div[name=thecontent"]")
                Elements rmv_elements = elements.select("span.editsection");
                if (rmv_elements.size() > 0) rmv_elements.remove();
                rmv_elements = elements.select("div.print-no.entry-unrelated");
                if (rmv_elements.size() > 0) rmv_elements.remove();

//                rmv_elements = elements.select("a.image.lightbox");
//                if (rmv_elements.size() > 0) rmv_elements.remove();

                String content = elements.html().replace("</noscript>", "").replace("<noscript>", "");

                Elements imgElements = elements.select("img");
                if (imgElements.size() > 0)
                    for (int i = 0; i < imgElements.size(); i++) {
                        Element imgE = imgElements.get(i);
                        String imgUrl = imgE.attr("src");
//                        Log.d(TAG, "old ImgUrl: " + imgUrl);
                        if (imgUrl.contains("/revision/latest")) {
                            String new_imgUrl = imgUrl.substring(0, imgUrl.indexOf("/revision/latest"));
                            content = content.replace(imgUrl, new_imgUrl);

                            Elements e1 = elements.select("a[href*=" + new_imgUrl + "]");
                            if (e1.size() > 0)
                                for (int j = 0; j < e1.size(); j++) {
                                    content = content.replace(e1.get(j).attr("href"), new_imgUrl);
                                }

                            Elements e2 = elements.select("a[href*=/wiki/File:" + new_imgUrl.substring(new_imgUrl.lastIndexOf("/") + 1) + "]");
                            if (e2.size() > 0)
                                for (int j = 0; j < e2.size(); j++) {
                                    content = content.replace(e2.get(j).attr("href"), new_imgUrl);
                                }
                        }
                    }

                return content;
            }
//            return elements.html().replace("https://vignette3.wikia.nocookie.net/sonako/images/8/8f/LNv11.06.png/revision/latest?cb=20160524034528","https://vignette3.wikia.nocookie.net/sonako/images/8/8f/LNv11.06.png")
//                    .replace("https://vignette3.wikia.nocookie.net/sonako/images/8/8f/LNv11.06.png/revision/latest/scale-to-width-down/600?cb=20160524034528","https://vignette3.wikia.nocookie.net/sonako/images/8/8f/LNv11.06.png");
        }

        return null;
    }

    public Document downloadChapterContent() throws IOException {

        if (url.contains(Constants.VAL_URL_ROOT)) {
            Document document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0").referrer("http://www.google.com").method(Connection.Method.GET).timeout(60 * 1000).get();
            Elements elements = document.select("div.thecontent");
            //select("div[name=thecontent"]")
            if (elements == null)
                return null;
            Elements rmv_elements = elements.select("div.show-page");
            if (rmv_elements.size() > 0) rmv_elements.remove();

            rmv_elements = elements.select("div.fb-comments");
            if (rmv_elements.size() > 0) rmv_elements.remove();

            rmv_elements = elements.select("script");
            if (rmv_elements.size() > 0) rmv_elements.remove();

            return document;
        } else if (url.contains(Constants.SNK_URL_ROOT)) {
            try {
                document = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                        .referrer("http://www.google.com").method(Connection.Method.GET).timeout(60 * 1000).get();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    document = Jsoup.parse(FunctionHelper.getContentFromUrl(FunctionHelper.sanitizeSnkLink(url)));
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                }
            }

            if (document != null) {
                Elements elements = document.select("div.WikiaArticle");
                if (elements.size() < 1)
                    return null;
                //select("div[name=thecontent"]")
                Elements rmv_elements = elements.select("span.editsection");
                if (rmv_elements.size() > 0) rmv_elements.remove();
                rmv_elements = elements.select("div.print-no.entry-unrelated");
                if (rmv_elements.size() > 0) rmv_elements.remove();

                String content = document.html().replace("</noscript>", "").replace("<noscript>", "");

                Elements imgElements = elements.select("img");
                if (imgElements.size() > 0)
                    for (int i = 0; i < imgElements.size(); i++) {
                        Element imgE = imgElements.get(i);
                        String imgUrl = imgE.attr("src");
//                        Log.d(TAG, "old ImgUrl: " + imgUrl);
                        if (imgUrl.contains("/revision/latest")) {
                            String new_imgUrl = imgUrl.substring(0, imgUrl.indexOf("/revision/latest"));
                            content = content.replace(imgUrl, new_imgUrl);

                            Elements e1 = elements.select("a[href*=" + new_imgUrl + "]");
                            if (e1.size() > 0)
                                for (int j = 0; j < e1.size(); j++) {
                                    content = content.replace(e1.get(j).attr("href"), new_imgUrl);
                                }

                            Elements e2 = elements.select("a[href*=/wiki/File:" + new_imgUrl.substring(new_imgUrl.lastIndexOf("/") + 1) + "]");
                            if (e2.size() > 0)
                                for (int j = 0; j < e2.size(); j++) {
                                    content = content.replace(e2.get(j).attr("href"), new_imgUrl);
                                }
                        }
                    }
                return Jsoup.parse(content);

            }
//            String html = elements.html();
//            return html.replace("<span style=\"font-family: ", "<span style =\"");
        }

        return null;
//        return html.replace("<span style=\"font-family: ", "<span style =\"");
    }


}
