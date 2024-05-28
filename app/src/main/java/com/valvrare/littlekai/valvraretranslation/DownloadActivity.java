package com.valvrare.littlekai.valvraretranslation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.valvrare.littlekai.valvraretranslation.adapter.ChapterDownloadAdapter;
import com.valvrare.littlekai.valvraretranslation.database.ValvrareDatabaseHelper;
import com.valvrare.littlekai.valvraretranslation.helper.FunctionHelper;
import com.valvrare.littlekai.valvraretranslation.helper.LNReaderApplication;
import com.valvrare.littlekai.valvraretranslation.model.Chapter;
import com.valvrare.littlekai.valvraretranslation.model.Novel;
import com.valvrare.littlekai.valvraretranslation.service.DownloadService;
import com.valvrare.littlekai.valvraretranslation.widget.adapter.AbstractWheelTextAdapter;
import com.valvrare.littlekai.valvraretranslation.widget.wheelwidget.OnWheelChangedListener;
import com.valvrare.littlekai.valvraretranslation.widget.wheelwidget.OnWheelScrollListener;
import com.valvrare.littlekai.valvraretranslation.widget.wheelwidget.WheelView;


import java.util.ArrayList;


public class DownloadActivity extends AppCompatActivity {
    ArrayList<Chapter> chapters, all_chapters, chapter_copy;
    private String TAG = "Kai";
    Novel novel;
    ListView listView;
    Toolbar toolbar;
    ChapterDownloadAdapter adapter;
    Button btn_download, btn_download_range;
    int listSize;
    int total = 0;
    Context context;
    ValvrareDatabaseHelper db;
    //    LNReaderApplication lnReaderApplication;
    private boolean scrolling = false;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

//        lnReaderApplication = (LNReaderApplication) getApplication();
//        lnReaderApplication.setDownloadActivity(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar_activity);
        toolbar.setTitleTextColor(0xffffffff);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        chapters = new ArrayList<>();
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            chapter_copy = new ArrayList<>();
            all_chapters = extras.getParcelableArrayList("chapters");
            novel = extras.getParcelable("novel");
        }

        if (all_chapters != null) {
            chapter_copy.addAll(all_chapters);
            setTitle(all_chapters.get(0).getNovelName());
            listSize = all_chapters.size();
            for (int i = all_chapters.size() - 1; i >= 0; i--) {
                if (!all_chapters.get(i).isDown())
                    chapters.add(all_chapters.get(i));
            }
            all_chapters.clear();
            for (int i = chapter_copy.size() - 1; i >= 0; i--) {
                all_chapters.add(chapter_copy.get(i));
            }
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        db = new ValvrareDatabaseHelper(this);
        btn_download_range = (Button) findViewById(R.id.btn_download_range);
        btn_download = (Button) findViewById(R.id.btn_download);
        listView = (ListView) findViewById(R.id.listView);

        if (chapters != null) {
            adapter = new ChapterDownloadAdapter(this, R.layout.item_chapter_download, chapters);
            adapter.setActivity(this);
            listView.setAdapter(adapter);
        }

        btn_download_range.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDialog();
            }
        });

        btn_download.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (FunctionHelper.isNetworkAvailable(context)) {
                                                    ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                                                    NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
                                                    boolean isWifi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
//            boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
                                                    if (isWifi | prefs.getBoolean("3g_enable", true)) {
                                                        ArrayList<Chapter> downChapters = adapter.getSelectChapters();
                                                        Intent intent = new Intent(DownloadActivity.this, DownloadService.class);
//                intent.putExtra("activity",this);
                                                        intent.putExtra("novel", novel);
                                                        intent.putParcelableArrayListExtra("chapters", downChapters);
                                                        context.startService(intent);
                                                        finish();
                                                    } else {
                                                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                                                        builder.setTitle("Thông Báo")
                                                                .setMessage("Hiện bạn đang tắt chức năng Tải Xuống Bằng 3G, bạn có muốn bật lại không?.")
                                                                .setCancelable(false)
                                                                .setNegativeButton("Từ Chối", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.cancel();
                                                                    }
                                                                })
                                                                .setPositiveButton("Đồng Ý", new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        SharedPreferences.Editor editor = prefs.edit();
                                                                        editor.putBoolean("3g_enable", true);
                                                                        editor.apply();
                                                                        ArrayList<Chapter> downChapters = adapter.getSelectChapters();
                                                                        Intent intent = new Intent(DownloadActivity.this, DownloadService.class);
//                intent.putExtra("activity",this);
                                                                        intent.putExtra("novel", novel);
                                                                        intent.putParcelableArrayListExtra("chapters", downChapters);
                                                                        context.startService(intent);
                                                                        finish();
                                                                        dialog.cancel();
                                                                    }
                                                                });
                                                        android.app.AlertDialog alert = builder.create();
                                                        alert.show();
                                                    }
                                                } else Toast.makeText(context, "Không có kết nối!", Toast.LENGTH_SHORT).show();
                                            }
                                        }

        );

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()

                                        {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                if (chapters.get(i).isChecked())
                                                    chapters.get(i).setChecked(false);
                                                else
                                                    chapters.get(i).setChecked(true);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }

        );
        if(adapter!=null)
        total = adapter.getCount();

        changeStatus();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cab_download, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.cab_deselect_all:
                adapter.deselectAll();
                return true;
            case R.id.cab_select_all:
                adapter.selectAll();
                return true;
        }
        return false;
    }

    public void changeStatus() {
        int count = adapter.getCheckedCount();
        if (count != 0) {
            if (!btn_download.isClickable()) {
                btn_download.setClickable(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    btn_download.setBackground(getResources().getDrawable(R.drawable.color_button_state));
                } else
                    btn_download.setBackgroundDrawable(getResources().getDrawable(R.drawable.color_button_state));
            }
            btn_download.setText("Tải Về (" + count + "/" + total + ")");
        } else {
            btn_download.setText("Tải Về (0" + "/" + total + ")");
            btn_download.setClickable(false);
            btn_download.setBackgroundColor(0xffdde4ed);
        }
    }


    void initDialog() {

//        String str[] = new String[all_chapters.size()];
//        for (int i = 0; i < all_chapters.size(); i++) {
//            str[i] = all_chapters.get(i).getName();
//        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = this.getLayoutInflater().inflate(R.layout.layout_from_to_selector, null);
        final WheelView from_selector = (WheelView) v.findViewById(R.id.from_selector);
        final WheelView to_selector = (WheelView) v.findViewById(R.id.to_selector);
        final TextView tv_from_chapter = (TextView) v.findViewById(R.id.tv_from_chapter);
        final TextView tv_to_chapter = (TextView) v.findViewById(R.id.tv_to_chapter);

        from_selector.setVisibleItems(5);
        from_selector.setViewAdapter(new WheelAdapter(this, R.layout.item_wheel, all_chapters));

        to_selector.setVisibleItems(5);
        to_selector.setViewAdapter(new WheelAdapter(this, R.layout.item_wheel, all_chapters));

        from_selector.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (!scrolling) {
                    int from_curr = from_selector.getCurrentItem();
                    if (to_selector.getCurrentItem() < from_curr)
                        to_selector.setCurrentItem(from_curr);

                    tv_from_chapter.setText("#" + all_chapters.get(from_curr).getOrderNo() + ": " + all_chapters.get(from_curr).getName());
                }
            }
        });

        from_selector.addScrollingListener(new OnWheelScrollListener() {
            public void onScrollingStarted(WheelView wheel) {
                scrolling = true;
                tv_from_chapter.setText("#" + all_chapters.get(from_selector.getCurrentItem()).getOrderNo() + ": " + all_chapters.get(from_selector.getCurrentItem()).getName());
            }

            @Override
            public void onScrollingTask(WheelView wheel) {
                int from_curr = from_selector.getCurrentItem();
                if (to_selector.getCurrentItem() < from_curr)
                    to_selector.setCurrentItem(from_curr);
                tv_from_chapter.setText("#" + all_chapters.get(from_curr).getOrderNo() + ": " + all_chapters.get(from_curr).getName());
            }

            public void onScrollingFinished(WheelView wheel) {
                scrolling = false;
                int from_curr = from_selector.getCurrentItem();
                if (to_selector.getCurrentItem() < from_curr)
                    to_selector.setCurrentItem(from_curr);
                tv_from_chapter.setText("#" + all_chapters.get(from_curr).getOrderNo() + ": " + all_chapters.get(from_curr).getName());
            }
        });

        to_selector.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (!scrolling) {
                    int to_curr = to_selector.getCurrentItem();
                    if (from_selector.getCurrentItem() > to_curr)
                        from_selector.setCurrentItem(to_curr);
                    tv_to_chapter.setText("#" + all_chapters.get(to_curr).getOrderNo() + ": " + all_chapters.get(to_curr).getName());
                }
            }
        });

        to_selector.addScrollingListener(new OnWheelScrollListener() {
            public void onScrollingStarted(WheelView wheel) {
                scrolling = true;
            }

            @Override
            public void onScrollingTask(WheelView wheel) {
                int to_curr = to_selector.getCurrentItem();
                if (from_selector.getCurrentItem() > to_curr)
                    from_selector.setCurrentItem(to_curr);
                tv_to_chapter.setText("#" + all_chapters.get(to_curr).getOrderNo() + ": " + all_chapters.get(to_curr).getName());
            }

            public void onScrollingFinished(WheelView wheel) {
                scrolling = false;
                int to_curr = to_selector.getCurrentItem();
                if (from_selector.getCurrentItem() > to_curr)
                    from_selector.setCurrentItem(to_curr);
                tv_to_chapter.setText("#" + all_chapters.get(to_curr).getOrderNo() + ": " + all_chapters.get(to_curr).getName());
            }
        });
        for (int i = 0; i < all_chapters.size(); i++) {
            if (!all_chapters.get(i).isDown()) {
                Log.d(TAG, "initDialog: " + i + "," + all_chapters.get(i).isDown());
                from_selector.setCurrentItem(i);
                to_selector.setCurrentItem(i);
                break;
            }
        }


        tv_from_chapter.setText("#" + all_chapters.get(from_selector.getCurrentItem()).getOrderNo() + ": " + all_chapters.get(from_selector.getCurrentItem()).getName());
        tv_to_chapter.setText("#" + all_chapters.get(to_selector.getCurrentItem()).getOrderNo() + ": " + all_chapters.get(to_selector.getCurrentItem()).getName());
        builder.setMessage("Chọn Khoảng")
                .setView(v)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                fromChapter = all_chapters.get(from_selector.getCurrentItem());
                                toChapter = all_chapters.get(to_selector.getCurrentItem());
                                final ArrayList<Chapter> items = new ArrayList<>();
                                for (int i = fromChapter.getOrderNo(); i <= toChapter.getOrderNo(); i++) {
                                    if (!all_chapters.get(i - 1).isDown() & all_chapters.get(i - 1).isEnableDownload())
                                        items.add(all_chapters.get(i - 1));
                                }
                                dialog.dismiss();
                                if (items.size() == 0) {
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                    builder1.setMessage("Toàn bộ những chương bạn chọn đã được tải hoàn tất. Xin hãy chọn lại.")
                                            .setCancelable(false)
                                            .setNegativeButton("Đã Hiểu", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog alert1 = builder1.create();
                                    alert1.show();
                                } else {
                                    if (FunctionHelper.isNetworkAvailable(context)) {
                                        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            boolean isWifi = false;
                                        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
                                        boolean isWifi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
//            boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
                                        if (isWifi | prefs.getBoolean("3g_enable", true)) {

                                            Intent intent = new Intent(DownloadActivity.this, DownloadService.class);
                                            intent.putExtra("novel", novel);
                                            intent.putParcelableArrayListExtra("chapters", items);
                                            context.startService(intent);
                                            finish();
                                        } else {
                                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                                            builder.setTitle("Thông Báo")
                                                    .setMessage("Hiện bạn đang tắt chức năng Tải Xuống Bằng 3G, bạn có muốn bật lại không?.")
                                                    .setCancelable(false)
                                                    .setNegativeButton("Từ Chối", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.cancel();
                                                        }
                                                    })
                                                    .setPositiveButton("Đồng Ý", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            SharedPreferences.Editor editor = prefs.edit();
                                                            editor.putBoolean("3g_enable", true);
                                                            editor.apply();
                                                            Intent intent = new Intent(DownloadActivity.this, DownloadService.class);
                                                            intent.putExtra("novel", novel);
                                                            intent.putParcelableArrayListExtra("chapters", items);
                                                            context.startService(intent);
                                                            finish();
                                                            dialog.cancel();
                                                        }
                                                    });
                                            android.app.AlertDialog alert = builder.create();
                                            alert.show();
                                        }
                                    } else
                                        Toast.makeText(context, "Không có kết nối!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                )
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private class WheelAdapter extends AbstractWheelTextAdapter {
        // Image size

        ArrayList<Chapter> items;
        // Cached images
        private int resId;

        // Layout inflater

        /**
         * Constructor
         */
        WheelAdapter(Context context, int res, ArrayList<Chapter> l) {
            super(context, res, NO_RESOURCE);

            items = l;
            resId = res;
            setItemTextResource(R.id.tv_wheel);
        }

        @Override
        protected CharSequence getItemText(int index) {
            int position = items.get(index).getOrderNo();
            String addNo = "";
            if (position > -1 & position < 10)
                addNo = "00";
            if (position > 9 & position < 100)
                addNo = "0";
            return addNo + position;
        }

        /**
         * Loads image from resources
         */

        public int getItemsCount() {
            return items.size();
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View v = super.getItem(index, cachedView, parent);
            Chapter chapter = items.get(index);
//            TextView tv_wheel = (TextView) v.findViewById(R.id.tv_wheel);
//            tv_wheel.setText(chapter.getOrderNo());
            return v;
        }

    }

    Chapter fromChapter, toChapter;

    void updateTextView(int pos, TextView tv) {
        tv.setText("#" + all_chapters.get(pos).getName());
    }
}
