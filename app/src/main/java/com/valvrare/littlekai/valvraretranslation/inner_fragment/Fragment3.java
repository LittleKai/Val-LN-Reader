package com.valvrare.littlekai.valvraretranslation.inner_fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.valvrare.littlekai.valvraretranslation.NovelDescriptionActivity;
import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.adapter.FavoritedNovelAdapter;
import com.valvrare.littlekai.valvraretranslation.adapter.NovelAdapter;
import com.valvrare.littlekai.valvraretranslation.database.ValvrareDatabaseHelper;
import com.valvrare.littlekai.valvraretranslation.model.Novel;

import java.util.ArrayList;

public class Fragment3 extends Fragment implements NovelAdapter.OnItemClickListener, FavoritedNovelAdapter.OnItemClickListener {
    private Context context;
    private static final String TAG = "Kai";
    private RecyclerView recyclerView;
    private LinearLayout empty;
    LinearLayoutManager layoutManager;
    private ValvrareDatabaseHelper db;
    private ArrayList<Novel> favNovels = new ArrayList<>();
    private FavoritedNovelAdapter adapter;
    boolean isStarted = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment3_layout, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.rv_favNovel);
        empty = (LinearLayout) v.findViewById(R.id.favNovelEmpty);
        context = getActivity();
        db = new ValvrareDatabaseHelper(context);
        if (savedInstanceState == null) {
//            new GetFavoriteNovel().execute();
            favNovels = db.getAllFavoritedNovels();
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            adapter = new FavoritedNovelAdapter(context, favNovels);
            adapter.setOnItemClickListener(Fragment3.this);
            recyclerView.setAdapter(adapter);
            if (favNovels != null) {
                recyclerView.setVisibility(RecyclerView.VISIBLE);
                empty.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(RecyclerView.GONE);
                empty.setVisibility(View.VISIBLE);
            }
        } else
            setUpAdapter(savedInstanceState);
        return v;
    }

    public void setUpAdapter(Bundle savedInstanceState) {
        favNovels = savedInstanceState.getParcelableArrayList("list");
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FavoritedNovelAdapter(getContext(), favNovels);
        adapter.setOnItemClickListener(Fragment3.this);
        recyclerView.setAdapter(adapter);
        isStarted = false;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putParcelableArrayList("list", favNovels);
        super.onSaveInstanceState(bundle);
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
                    favNovels = db.getAllFavoritedNovels();
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    if (favNovels != null) {
//                        if (adapter != null)
//                            adapter.notifyDataSetChanged();
//                        else {
                            recyclerView.setVisibility(RecyclerView.VISIBLE);
                            empty.setVisibility(View.GONE);
                            layoutManager = new LinearLayoutManager(getContext());
                            recyclerView.setLayoutManager(layoutManager);
                            adapter = new FavoritedNovelAdapter(context, favNovels);
                            adapter.setOnItemClickListener(Fragment3.this);
                            recyclerView.setAdapter(adapter);
//                        }
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
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void update() {
        new GetFavoriteNovel().execute();
    }


    public class GetFavoriteNovel extends AsyncTask<Void, Void, ArrayList<Novel>> {

        @Override
        public void onPreExecute() {
            recyclerView.setVisibility(RecyclerView.GONE);
            empty.setVisibility(View.GONE);
        }

        @Override
        public ArrayList<Novel> doInBackground(Void... params) {
            return db.getAllFavoritedNovels();
        }

        @Override
        public void onPostExecute(ArrayList<Novel> result) {
            if (result != null) {
                recyclerView.setVisibility(RecyclerView.VISIBLE);
                favNovels = result;
                layoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(layoutManager);
                adapter = new FavoritedNovelAdapter(context, favNovels);
                adapter.setOnItemClickListener(Fragment3.this);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            } else {
                recyclerView.setVisibility(RecyclerView.GONE);
                empty.setVisibility(TextView.VISIBLE);
            }
        }

    }


}
