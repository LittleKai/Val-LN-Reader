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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.valvrare.littlekai.valvraretranslation.NovelDescriptionActivity;
import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.adapter.SearchNovelAdapter;
import com.valvrare.littlekai.valvraretranslation.database.ValvrareDatabaseHelper;
import com.valvrare.littlekai.valvraretranslation.model.Novel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class SnkSearchNovelFragment extends Fragment implements SearchNovelAdapter.OnItemClickListener {
    private String TAG = "Kai";
    private RecyclerView recyclerView;
    private SearchNovelAdapter adapter;
    private Context context;
    private ValvrareDatabaseHelper db;
    private ArrayList<Novel> novels;
    private TextView tv_resultAmount;
    private SharedPreferences sharedPreferences;
    private boolean listType;
    private ArrayList<Novel> itemsCopy;
    private String search_key = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_snk_search_novel, container, false);
        context = getActivity();
        db = new ValvrareDatabaseHelper(context);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        tv_resultAmount = (TextView) view.findViewById(R.id.tv_resultAmount);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
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
                    editor.putBoolean("SnkType", !sharedPreferences.getBoolean("SnkType", true));
                    editor.apply();

                    if (sharedPreferences.getBoolean("SnkType", true)) {
                        im_listType.setImageResource(R.drawable.icon_grid);
                        recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
                    } else {
                        im_listType.setImageResource(R.drawable.icon_list);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    }

                    adapter = new SearchNovelAdapter(context, new_item, recyclerView);
                    recyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener(SnkSearchNovelFragment.this);
                    adapter.filter(search_key);
                    tv_resultAmount.setText(Html.fromHtml("Tìm được " + "<b>" + adapter.getItemCount() + "</b>" + " Truyện."));
                }
            }
        });

        tv_resultAmount.setText("");
        novels = db.getAllSnkNovels();
        if (novels != null) {
            itemsCopy = new ArrayList<>();
            itemsCopy.addAll(novels);

            if (sharedPreferences.getBoolean("SnkType", true)) {
                im_listType.setImageResource(R.drawable.icon_grid);
                recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
            } else {
                im_listType.setImageResource(R.drawable.icon_list);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            }
            adapter = new SearchNovelAdapter(context, novels, recyclerView);
            adapter.setOnItemClickListener(SnkSearchNovelFragment.this);
            recyclerView.setAdapter(adapter);
            tv_resultAmount.setText(Html.fromHtml("Tìm được " + "<b>" + adapter.getItemCount() + "</b>" + " Truyện."));
        } else {
            tv_resultAmount.setText(Html.fromHtml("Tìm được " + "<b>" + "0" + "</b>" + " Truyện. Lỗi lấy dữ liệu."));
        }

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

//    public int calculateRange() {
//        int start = ((GridLayoutManager)        grv.getLayoutManager()).findFirstVisibleItemPosition();
//        int end = ((GridLayoutManager) grv.getLayoutManager()).findLastVisibleItemPosition();
//        if (start < 0)
//            start = 0;
//        if (end < 0)
//            end = getItemCount();
//        return end - start;
//    }
}