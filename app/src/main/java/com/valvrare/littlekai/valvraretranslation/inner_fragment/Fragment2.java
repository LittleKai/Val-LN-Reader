package com.valvrare.littlekai.valvraretranslation.inner_fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.valvrare.littlekai.valvraretranslation.ChapterReadingActivity;
import com.valvrare.littlekai.valvraretranslation.DisplayChapterContentActivity;
import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.adapter.ChapterHistoryAdapter;
import com.valvrare.littlekai.valvraretranslation.adapter.HistoryNovelAdapter;
import com.valvrare.littlekai.valvraretranslation.database.ValvrareDatabaseHelper;
import com.valvrare.littlekai.valvraretranslation.model.Chapter;
import com.valvrare.littlekai.valvraretranslation.model.Novel;

import java.util.ArrayList;


public class Fragment2 extends Fragment implements HistoryNovelAdapter.OnItemClickListener, HistoryClear, AdapterView.OnItemClickListener {
    private Context context;
    ListView listView;
    private LinearLayout empty;
    private ValvrareDatabaseHelper db;
    private ArrayList<Novel> recentNovels = new ArrayList<>();
    private ChapterHistoryAdapter adapter;
    private static final String TAG = "Kai";
    boolean isStarted = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment2_layout, container, false);
        listView = (ListView) v.findViewById(R.id.lv_historyNovel);

        listView.setVisibility(RecyclerView.GONE);
        empty = (LinearLayout) v.findViewById(R.id.historyNovelEmpty);
        context = getActivity();
        db = new ValvrareDatabaseHelper(context);
        recentNovels = db.getRecentChapters();

        if (recentNovels != null) {
            adapter = new ChapterHistoryAdapter(context, R.layout.item_history_novel_list, recentNovels);
            listView.setOnItemClickListener(Fragment2.this);
            listView.setAdapter(adapter);
            registerForContextMenu(listView);
            listView.setVisibility(RecyclerView.VISIBLE);
            empty.setVisibility(View.GONE);
        } else {
            listView.setVisibility(RecyclerView.GONE);
            empty.setVisibility(View.VISIBLE);
        }

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isStarted)
//            new GetFavoriteNovel().execute();
        {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    recentNovels = db.getRecentChapters();
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    if (recentNovels != null) {
//                        if (adapter != null)
//                            adapter.notifyDataSetChanged();
//                        else {
                            listView.setVisibility(RecyclerView.VISIBLE);
                            empty.setVisibility(View.GONE);
                            adapter = new ChapterHistoryAdapter(context, R.layout.item_history_novel_list, recentNovels);
                            listView.setOnItemClickListener(Fragment2.this);
                            listView.setAdapter(adapter);
                            registerForContextMenu(listView);
//                        }
                    } else {
                        listView.setVisibility(RecyclerView.GONE);
                        empty.setVisibility(View.VISIBLE);
                    }
                }
            }.execute();
        } else isStarted = true;
    }

    void refreshAdapter() {
        recentNovels = db.getRecentChapters();

        if (recentNovels != null) {
            listView.setVisibility(RecyclerView.VISIBLE);
            empty.setVisibility(View.GONE);
            adapter = new ChapterHistoryAdapter(context, R.layout.item_history_novel_list, recentNovels);
            listView.setOnItemClickListener(Fragment2.this);
            listView.setAdapter(adapter);
            registerForContextMenu(listView);
        } else {
            listView.setVisibility(RecyclerView.GONE);
            empty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(View view, Novel novel) {
        Chapter chapter = novel.getChapter();
        chapter.setNovelName(novel.getNovelName());
        chapter.setNovelUrl(novel.getUrl());
        chapter.setNovelImageUrl(novel.getImage());
        chapter.setChapterList(novel.getLatestChapName());
        boolean isRead = db.isChapterRead(chapter);
        boolean isFav = db.isChapterFav(chapter);
        boolean isDown = db.isDownloadedChapterContentExist(chapter);
        chapter.setDown(isDown);
        chapter.setFav(isFav);
        chapter.setRead(isRead);
        chapter.setNovelImageUrl(novel.getImage());
        chapter.setNovelUrl(novel.getUrl());

        Intent intent = new Intent(getActivity(), ChapterReadingActivity.class);
//        Intent intent = new Intent(getActivity(), DisplayChapterContentActivity.class);
        intent.putExtra("chapter", chapter);
        intent.putExtra("novel", novel);
        startActivity(intent);
    }

    @Override
    public void historyClear() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.myDialog));
        builder.setMessage("Xác nhận xóa toàn bộ lịch sử đọc?")
                .setCancelable(false)
                .setPositiveButton("Đồng Ý", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        db = new ValvrareDatabaseHelper(context);
                        db.deleteAllRecentChapter();
                        db.deleteAllNovelHistory();
                        refreshAdapter();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Novel novel = recentNovels.get(position);
        Chapter chapter = novel.getChapter();
        chapter.setNovelName(novel.getNovelName());
        chapter.setNovelUrl(novel.getUrl());
        chapter.setNovelImageUrl(novel.getImage());
        chapter.setNovelUrl(novel.getUrl());
        Intent intent = new Intent(getActivity(), ChapterReadingActivity.class);
//        Intent intent = new Intent(getActivity(), DisplayChapterContentActivity.class);
        intent.putExtra("chapter", chapter);
        intent.putExtra("novel", novel);
        startActivity(intent);
    }
    String[] menuItems = {"Xóa Lịch Sử Đọc"};

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.lv_historyNovel) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Novel novel = adapter.getItem(info.position);
            assert novel != null;
            menu.setHeaderTitle(novel.getNovelName());
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        final Novel novel = adapter.getItem(info.position);

        String menuItemName = menuItems[menuItemIndex];

        if (menuItemName.equals("Xóa Lịch Sử Đọc")) {
            db.deleteNovelHistory(novel);
            db.deleteChaptersHistory(novel);
            refreshAdapter();
    }
        return true;
}

}

interface HistoryClear {
    public void historyClear();
}
