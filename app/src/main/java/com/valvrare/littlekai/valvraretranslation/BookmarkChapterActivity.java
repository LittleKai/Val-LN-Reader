package com.valvrare.littlekai.valvraretranslation;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.valvrare.littlekai.valvraretranslation.adapter.BookmarkChapterAdapter;
import com.valvrare.littlekai.valvraretranslation.database.ValvrareDatabaseHelper;
import com.valvrare.littlekai.valvraretranslation.model.Chapter;
import com.valvrare.littlekai.valvraretranslation.model.Novel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BookmarkChapterActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {
    ArrayList<Chapter> chapters, chapters_copy;
    private String TAG = "Kai";
    ListView listView;
    Toolbar toolbar;
    BookmarkChapterAdapter adapter;
    Context context;
    ValvrareDatabaseHelper db;
    private LinearLayout bookmarkdNovelEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_chapter);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitleTextColor(0xffffffff);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        chapters = new ArrayList<>();
        setTitle("Bookmarks");
        db = new ValvrareDatabaseHelper(this);
        chapters = db.getAllFavChapters();
        chapters_copy = new ArrayList<>();

        if (chapters != null)
            chapters_copy.addAll(chapters);
        bookmarkdNovelEmpty = (LinearLayout) findViewById(R.id.bookmarkdNovelEmpty);
        listView = (ListView) findViewById(R.id.listView);
        listView.setVisibility(View.GONE);
        bookmarkdNovelEmpty.setVisibility(View.GONE);
        if (chapters != null) {
            listView.setVisibility(View.VISIBLE);
            adapter = new BookmarkChapterAdapter(this, R.layout.item_bookmark_chapter, chapters);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
//            registerForContextMenu(listView);
        } else
            bookmarkdNovelEmpty.setVisibility(View.VISIBLE);
    }

    private boolean a_z_sort = true, decrease_time = true;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            case R.id.action_sort:
                PopupMenu popup = new PopupMenu(BookmarkChapterActivity.this, findViewById(R.id.action_sort));
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.bookmark_chapter_popup_menu, popup.getMenu());
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                                     public boolean onMenuItemClick(MenuItem item) {
                                                         switch (item.getItemId()) {
                                                             case R.id.name_sort:
                                                                 if (adapter != null)
                                                                     if (chapters != null) {
                                                                         a_z_sort = !a_z_sort;
                                                                         if (a_z_sort)
                                                                             Collections.sort(chapters, new Comparator<Chapter>() {
                                                                                 @Override
                                                                                 public int compare(Chapter lhs, Chapter rhs) {
                                                                                     return lhs.getNovelName().compareTo(rhs.getNovelName());
                                                                                 }
                                                                             });
                                                                         else Collections.sort(chapters, new Comparator<Chapter>() {
                                                                             @Override
                                                                             public int compare(Chapter lhs, Chapter rhs) {
                                                                                 return rhs.getNovelName().compareTo(lhs.getNovelName());
                                                                             }
                                                                         });
                                                                         adapter.notifyDataSetChanged();
                                                                     }
                                                                 break;
                                                             case R.id.time_sort:
                                                                 if (adapter != null) {
                                                                     decrease_time = !decrease_time;
                                                                     if (chapters_copy != null) {
                                                                         if (decrease_time) {
                                                                             if (chapters != null) {
                                                                                 chapters.clear();
                                                                                 chapters.addAll(chapters_copy);
                                                                             }
                                                                         } else if (chapters_copy.size() > 0) {
                                                                             if (chapters != null) chapters.clear();
                                                                             for (int i = chapters_copy.size() - 1; i >= 0; i--) {
                                                                                 chapters.add(chapters_copy.get(i));
                                                                             }
                                                                         }
                                                                     }
                                                                     adapter.notifyDataSetChanged();
                                                                 }
                                                                 break;
                                                         }
                                                         return true;
                                                     }
                                                 }
                );
                popup.show(); //showing popup menu
                return true;
        }
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Chapter chapter = adapter.getItem(info.position);
            assert chapter != null;
            menu.setHeaderTitle(chapter.getName());
            String[] menuItems = {"Bỏ Dấu Chương"};
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        final Chapter chapter = adapter.getItem(info.position);
        String[] menuItems = {"Bỏ Dấu Chương"};

        String menuItemName = menuItems[menuItemIndex];

        if (menuItemName.equals("Bỏ Dấu Chương")) {
            if (db.deleteFavChapter(chapter)) {
                chapters.remove(info.position);
                Log.d(TAG, "onContextItemSelected: " + searchView.getQuery());
                if (searchView.getQuery().toString().isEmpty())
                    adapter.notifyDataSetChanged();
                else {
                    adapter.notifyDataSetChanged();
                    adapter.getFilter().filter(searchView.getQuery());
                }
//adapter.getFilter().g
                if (chapters == null | chapters.size() == 0) {
                    if (bookmarkdNovelEmpty.getVisibility() == View.GONE)
                        bookmarkdNovelEmpty.setVisibility(View.VISIBLE);
                    if (listView.getVisibility() == View.VISIBLE)
                        listView.setVisibility(View.GONE);
                } else {
                    if (bookmarkdNovelEmpty.getVisibility() == View.VISIBLE)
                        bookmarkdNovelEmpty.setVisibility(View.GONE);
                    if (listView.getVisibility() == View.GONE)
                        listView.setVisibility(View.VISIBLE);
                }
                Toast.makeText(context, "Đã Bỏ Dấu Chương", Toast.LENGTH_SHORT).show();
            }
        }

        return true;
    }

    private SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //   getMenuInflater().inflate(R.menu.setting_menu,menu);
        getMenuInflater().inflate(R.menu.bookmark_chapter_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Nhập Tên Truyện");
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Chapter chapter = adapter.getItem(position);
        Novel novel = new Novel();
        assert chapter != null;
        novel.setNovelName(chapter.getNovelName());
        novel.setUrl(chapter.getNovelUrl());
        novel.setImage(chapter.getNovelImageUrl());
        Intent intent = new Intent(BookmarkChapterActivity.this, ChapterReadingActivity.class);
        intent.putExtra("chapter", chapter);
        intent.putExtra("novel", novel);
        startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String key) {
        if (adapter != null)
//            adapter.filter(key);
            adapter.getFilter().filter(key);
        return true;

    }
}
