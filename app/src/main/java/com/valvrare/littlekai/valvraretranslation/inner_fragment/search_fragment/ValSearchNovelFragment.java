package com.valvrare.littlekai.valvraretranslation.inner_fragment.search_fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.valvrare.littlekai.valvraretranslation.NovelDescriptionActivity;
import com.valvrare.littlekai.valvraretranslation.NovelSearchActivity;
import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.adapter.SearchNovelAdapter;
import com.valvrare.littlekai.valvraretranslation.database.ValvrareDatabaseHelper;
import com.valvrare.littlekai.valvraretranslation.model.Novel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Handler;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Kai on 4/9/2017.
 */
public class ValSearchNovelFragment extends Fragment implements SearchNovelAdapter.OnItemClickListener {
    private String TAG = "Kai";
    private RecyclerView recyclerView;
    private SearchNovelAdapter adapter;
    private Context context;
    private ValvrareDatabaseHelper db;
    private ArrayList<Novel> novels;
    private TextView tv_resultAmount;
    private LinearLayoutManager layoutManager;
    private SharedPreferences sharedPreferences;
    private boolean listType;
    private ArrayList<Novel> itemsCopy;
    private String search_key = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_val_search_novel, container, false);
        context = getActivity();
        db = new ValvrareDatabaseHelper(context);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        tv_resultAmount = (TextView) view.findViewById(R.id.tv_resultAmount);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        listType = sharedPreferences.getBoolean("ValType", true);

        final ImageView im_listType = (ImageView) view.findViewById(R.id.im_listType);
        im_listType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter != null) {
                    ArrayList<Novel> new_item = new ArrayList<>();
                    new_item.addAll(itemsCopy);

                    recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
                    listType = !listType;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("ValType", !sharedPreferences.getBoolean("ValType", true));
                    editor.apply();

                    if (sharedPreferences.getBoolean("ValType", true)) {
                        im_listType.setImageResource(R.drawable.icon_grid);
                        recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
                    } else {
                        im_listType.setImageResource(R.drawable.icon_list);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    }

                    adapter = new SearchNovelAdapter(context, new_item, recyclerView);
                    recyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener(ValSearchNovelFragment.this);
                    adapter.filter(search_key);
                    tv_resultAmount.setText(Html.fromHtml("Tìm được " + "<b>" + adapter.getItemCount() + "</b>" + " Truyện."));
                }
            }
        });

//        tv_resultAmount.setText("");
//        Handler handler = new Handler(){};
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                novels = db.getAllNovels();
//
//                if (novels != null) {
//                    Collections.sort(novels, new Comparator<Novel>() {
//                        @Override
//                        public int compare(Novel lhs, Novel rhs) {
//                            return lhs.getNovelName().compareTo(rhs.getNovelName());
//                        }
//                    });
//                    if (sharedPreferences.getBoolean("ValType", true)) {
//                        im_listType.setImageResource(R.drawable.icon_grid);
//                        recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
//                    } else {
//                        im_listType.setImageResource(R.drawable.icon_list);
//                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
//                    }
//                    adapter = new SearchNovelAdapter(context, novels, recyclerView);
//                    adapter.setOnItemClickListener(ValSearchNovelFragment.this);
//                    recyclerView.setAdapter(adapter);
//                    tv_resultAmount.setText(Html.fromHtml("Tìm được " + "<b>" + adapter.getItemCount() + "</b>" + " Truyện."));
//                } else {
//                    tv_resultAmount.setText(Html.fromHtml("Tìm được " + "<b>" + "0" + "</b>" + " Truyện. Lỗi lấy dữ liệu."));
//                }
//            }
//        }).start();

        new AsyncTask<Void, Void, ArrayList<Novel>>() {
            @Override
            protected ArrayList<Novel> doInBackground(Void... params) {
                novels = db.getAllNovels();

                return novels;
            }

            @Override
            protected void onPostExecute(ArrayList<Novel> results) {
                super.onPostExecute(novels);
                if (novels != null) {
                    Collections.sort(novels, new Comparator<Novel>() {
                        @Override
                        public int compare(Novel lhs, Novel rhs) {
                            return lhs.getNovelName().compareTo(rhs.getNovelName());
                        }
                    });
                    itemsCopy = new ArrayList<>();
                    itemsCopy.addAll(novels);
                    if (sharedPreferences.getBoolean("ValType", true)) {
                        im_listType.setImageResource(R.drawable.icon_grid);
                        recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
                    } else {
                        im_listType.setImageResource(R.drawable.icon_list);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    }
                    adapter = new SearchNovelAdapter(context, novels, recyclerView);
                    adapter.setOnItemClickListener(ValSearchNovelFragment.this);
                    recyclerView.setAdapter(adapter);
                    tv_resultAmount.setText(Html.fromHtml("Tìm được " + "<b>" + adapter.getItemCount() + "</b>" + " Truyện."));
                } else {
                    tv_resultAmount.setText(Html.fromHtml("Tìm được " + "<b>" + "0" + "</b>" + " Truyện. Lỗi lấy dữ liệu."));
                }
            }
        }.execute();

        return view;
    }

    public void filterList(String key) {
        search_key = key;
        if (adapter != null) {
            adapter.filter(key);
            tv_resultAmount.setText(Html.fromHtml("Tìm được " + "<b>" + adapter.getItemCount() + "</b>" + " Truyện."));
        }
    }

    @Override
    public void onItemClick(View view, Novel novel) {
        Intent intent = new Intent(getActivity(), NovelDescriptionActivity.class);
        intent.putExtra("novel", novel);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }
}