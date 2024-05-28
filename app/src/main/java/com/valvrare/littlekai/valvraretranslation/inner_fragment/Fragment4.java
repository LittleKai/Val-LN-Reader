package com.valvrare.littlekai.valvraretranslation.inner_fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.valvrare.littlekai.valvraretranslation.NovelDescriptionActivity;
import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.adapter.DownloadedNovelAdapter;
import com.valvrare.littlekai.valvraretranslation.adapter.HistoryNovelAdapter;
import com.valvrare.littlekai.valvraretranslation.database.ValvrareDatabaseHelper;
import com.valvrare.littlekai.valvraretranslation.model.Novel;
import com.valvrare.littlekai.valvraretranslation.widget.NonLeakingWebView;

import java.util.ArrayList;

public class Fragment4 extends Fragment implements DownloadedNovelAdapter.OnItemClickListener, refreshTask {

    private Context context;
    private RecyclerView recyclerView;
    private LinearLayout empty;
    private LinearLayoutManager layoutManager;
    private ValvrareDatabaseHelper db;
    private ArrayList<Novel> downloadedNovels = new ArrayList<>();
    private DownloadedNovelAdapter adapter;
    private static final String TAG = "Kai";
    boolean isStarted = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment4_layout, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.rv_downloadedNovel);
        recyclerView.setVisibility(RecyclerView.GONE);
        empty = (LinearLayout) v.findViewById(R.id.downloadNovelEmpty);
        context = getActivity();
        db = new ValvrareDatabaseHelper(context);
        downloadedNovels = db.getAllDownloadedNovel();

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DownloadedNovelAdapter(context, downloadedNovels, this);
        adapter.setOnItemClickListener(Fragment4.this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        if (downloadedNovels != null) {
            recyclerView.setVisibility(RecyclerView.VISIBLE);
            empty.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(RecyclerView.GONE);
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
                    downloadedNovels = db.getAllDownloadedNovel();
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    if (downloadedNovels != null) {
//                        if (adapter != null)
//                            adapter.notifyDataSetChanged();
//                        else {
                            recyclerView.setVisibility(RecyclerView.VISIBLE);
                            empty.setVisibility(View.GONE);

                            layoutManager = new LinearLayoutManager(getContext());
                            recyclerView.setLayoutManager(layoutManager);
                            adapter = new DownloadedNovelAdapter(context, downloadedNovels, Fragment4.this);
                            adapter.setOnItemClickListener(Fragment4.this);
                            recyclerView.setAdapter(adapter);
//                        }
//                if(adapter!=null)
//                    adapter.notifyDataSetChanged();
                    } else {
                        recyclerView.setVisibility(RecyclerView.GONE);
                        empty.setVisibility(View.VISIBLE);
                    }
                }
            }.execute();
        } else isStarted = true;
    }

    @Override
    public void onItemClick(View view, Novel novel) {
        Intent intent = new Intent(getActivity(), NovelDescriptionActivity.class);
        intent.putExtra("novel", novel);
        intent.putExtra("downloaded", true);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    @Override
    public void refresh() {
        Log.d(TAG, "refresh: ");
        downloadedNovels = db.getAllDownloadedNovel();

        if (downloadedNovels != null) {
            recyclerView.setVisibility(RecyclerView.VISIBLE);
            empty.setVisibility(View.GONE);

            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            adapter = new DownloadedNovelAdapter(context, downloadedNovels, this);
            adapter.setOnItemClickListener(Fragment4.this);
            recyclerView.setAdapter(adapter);
//                if(adapter!=null)
//                    adapter.notifyDataSetChanged();
        } else {
            recyclerView.setVisibility(RecyclerView.GONE);
            empty.setVisibility(View.VISIBLE);
        }
    }
}

interface refreshTask {
    public void refresh();
}