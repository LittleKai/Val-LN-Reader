package com.valvrare.littlekai.valvraretranslation.task;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.valvrare.littlekai.valvraretranslation.NovelDescriptionActivity;
import com.valvrare.littlekai.valvraretranslation.api.SonakoLibrary;
import com.valvrare.littlekai.valvraretranslation.database.ValvrareDatabaseHelper;
import com.valvrare.littlekai.valvraretranslation.helper.FunctionHelper;
import com.valvrare.littlekai.valvraretranslation.model.Chapter;
import com.valvrare.littlekai.valvraretranslation.model.Novel;
import com.valvrare.littlekai.valvraretranslation.utils.Constants;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;


public class GetSnkNovelChaptersListTask {
    private int currpage = 0;
    private int maxpage = 1;
    private String TAG = "Kai";
    private ValvrareDatabaseHelper db;
    private String chapters_api;
    private String category_exhibition_sort_alphabet = "sort=alphabetical&display=exhibition";
    private int maxretried = 3;
    private int retried = 1;
    private ArrayList<Chapter> chapters;
    private Document content;
    private NovelDescriptionActivity activity;
    private Novel novel;
    private String categories;
    private boolean isStop = false;

    public void setStop(boolean stop) {
        isStop = stop;
        Log.d(TAG, "setStop: " + isStop);
    }

    public GetSnkNovelChaptersListTask(NovelDescriptionActivity activity, Novel novel) {
        this.activity = activity;
        this.novel = novel;
//        new getNovelContent().execute(novel.getUrl());
        new getNovelContent().execute(FunctionHelper.sanitizeSnkLink(novel.getUrl()));
        Log.d(TAG, "GetSnkNovelChaptersListTask: " + novel.getNovelName() + ", " + novel.getUrl());
    }

    private class getNovelContent extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... params) {
            try {
                SonakoLibrary library = new SonakoLibrary();
                return library.getMainNovelContent(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Document s) {
            super.onPostExecute(s);
            if (!isStop)
                if (s != null) {
                    content = s;
                    Elements es1 = s.select("ul.categories").select("li.category.normal");
                    if (es1.size() > 0)
                        for (int i = 0; i < es1.size(); i++) {
                            String category = es1.get(i).select("span.name").text();
                            if (chapters_api == null && !FunctionHelper.getContainArrayString(Constants.SNK_SUB_CATEGOPRIES, category) & !FunctionHelper.getContainArrayString(Constants.SNK_NOVEL_TYPES, category)) {
//                                category = "(a) " + category;
                                chapters_api = es1.get(i).select("span.name").select("a").attr("href");
                                try {
                                    chapters_api = FunctionHelper.sanitizeSnkLink(URLDecoder.decode(chapters_api, "utf8"));
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (category != null)
                                    if (categories == null)
                                        categories = category;
                                    else
                                        categories = categories + ", " + category;
                            }
                        }
                    Log.d(TAG, "onPostExecute: " + categories);
                    Log.d(TAG, novel.getNovelName() + ": " + chapters_api);

                    if (chapters_api != null) {
                        if (!chapters_api.contains("http://sonako.wikia.com"))
                            chapters_api = "http://sonako.wikia.com" + chapters_api;
                        chapters_api = FunctionHelper.sanitizeSnkLink(chapters_api);
                        new getMaxpage().execute(chapters_api + "?" + category_exhibition_sort_alphabet);
                        Log.d(TAG, "Getting max page: ");
                    } else
                        activity.getSnkNovelCallback(novel);
                } else
                    activity.getSnkNovelCallback(novel);
        }
    }

    private class getMaxpage extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            int result = 0;
            try {
                SonakoLibrary library = new SonakoLibrary();
                result = library.getMaxPage(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (!isStop)
                if (integer != 0) {
                    maxpage = integer;
                    currpage++;
                    chapters = new ArrayList<>();
                    new getChaptersList().execute(chapters_api + "?page=" + currpage + "&" + category_exhibition_sort_alphabet);
                } else
                    activity.getSnkNovelCallback(novel);
        }
    }

    private class getChaptersList extends AsyncTask<String, Void, ArrayList<Chapter>> {
        @Override
        protected ArrayList<Chapter> doInBackground(String... params) {
            ArrayList<Chapter> result = null;
            try {
                SonakoLibrary library = new SonakoLibrary();
                result = library.getChapterList(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<Chapter> result) {
            super.onPostExecute(result);
            if (!isStop)
                if (result != null) {
                    chapters.addAll(result);
                    retried = 1;
                    currpage++;
                    activity.getSnkNovelUpdateProgressCallback("" + (currpage * 100 / maxpage));
                    if (currpage <= maxpage)
                        new getChaptersList().execute(chapters_api + "?page=" + currpage + "&" + category_exhibition_sort_alphabet);
                    else try {
                        finishTask();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    retried++;
                    if (retried > maxretried)
                        Toast.makeText(activity, "Tải bị lỗi, xin hãy thử lại!", Toast.LENGTH_SHORT).show();
                    else
                        new getChaptersList().execute(chapters_api + "?page=" + currpage + "&" + category_exhibition_sort_alphabet);
                }
        }
    }

    private void finishTask() throws UnsupportedEncodingException {
        if (!isStop)
            if (chapters != null && chapters.size() > 1) {
                if (content != null) {
                    String novelName = URLDecoder.decode(novel.getUrl(), "utf8");
                    String html = content.html();
                    for (int i = 0; i < chapters.size(); i++) {
                        if (URLDecoder.decode(chapters.get(i).getUrl(), "utf8").equals(novelName)) {
                            chapters.remove(i);
                            i--;
                        } else {
                            int pos;
                            pos = html.indexOf(chapters.get(i).getUrl());
                            if (pos == -1) {
                                String source = "a[href=" + chapters.get(i).getUrl().replace("http://sonako.wikia.com", "") + "]";
                                Elements elements = content.select(source);
                                if (elements.size() > 0)
                                    if (elements.last().text() != null)
                                        chapters.get(i).setName(elements.last().text());

                                pos = html.indexOf(chapters.get(i).getUrl().replace("http://sonako.wikia.com", ""));
                                if (pos == -1) {
                                    Log.d(TAG, "Chapter " + chapters.get(i).getName() + ": not found!");
                                }
                            } else {
                                String source = "a[href=" + chapters.get(i).getUrl();
                                Elements elements = content.select(source);
                                if (elements.size() > 0)
                                    if (elements.last().text() != null)
                                        chapters.get(i).setName(elements.last().text());
                            }
                            if (pos >= 0)
                                chapters.get(i).setPosState(pos);
                            else chapters.get(i).setPosState(0);
                        }
                    }

                    Collections.sort(chapters, new Comparator<Chapter>() {
                        @Override
                        public int compare(Chapter lhs, Chapter rhs) {
                            return rhs.getPosState() - lhs.getPosState();
                        }
                    });

                    String summary = content.select("meta[property=og:description").attr("content");
                    String new_img_url = content.select("meta[property=og:image").attr("content");

                    novel.setSummary(summary);
                    novel.setSecond_image(new_img_url);
                    novel.setCatelogies(categories);

                    String json = FunctionHelper.snkChapterList_to_json(chapters);
                    novel.setChapterList(json);

                    Calendar c = Calendar.getInstance();
                    String time = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
                    String date = c.get(Calendar.DATE) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR);
                    novel.setTime(time);
                    novel.setDate(date);
                    if (json != null) {
                        db = new ValvrareDatabaseHelper(activity.getApplicationContext());
                        db.updateSnkNovel(novel);
                        activity.getSnkNovelCallback(novel);
                    }
                }
            } else if (chapters != null && chapters.size() == 1)
                activity.getSnkNovelCallback(novel);
    }
}
