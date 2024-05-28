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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.valvrare.littlekai.valvraretranslation.MainActivity;
import com.valvrare.littlekai.valvraretranslation.NovelDescriptionActivity;
import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.adapter.NovelAdapter;
import com.valvrare.littlekai.valvraretranslation.api.NovelLibrary;
import com.valvrare.littlekai.valvraretranslation.helper.FunctionHelper;
import com.valvrare.littlekai.valvraretranslation.model.Novel;
import com.valvrare.littlekai.valvraretranslation.utils.OnLoadMoreListener;

import java.io.IOException;
import java.util.ArrayList;

public class Fragment1 extends Fragment implements NovelAdapter.OnItemClickListener {
    private int page = 1;
    private int max_page = 1;
    private ArrayList<Novel> moreNovel;
    private NovelAdapter adapter;

    private   TextView tv_empty;
    private ArrayList<Novel> novels;
    private    RecyclerView recyclerView;
    private    RecyclerView.LayoutManager layoutManager;
    private    ProgressBar progressBar;
    private    String ApiUrl = "http://valvrareteam.com/page/";
    private    String NovelType = null;
    private    Context context;
    private boolean isStarted = true;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment1_layout, container, false);
        context = getActivity();

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar_NovelList);
        recyclerView = (RecyclerView) v.findViewById(R.id.rv_homeNovel);

        tv_empty = (TextView) v.findViewById(R.id.tv_frag1Empty);
        tv_empty.setVisibility(TextView.GONE);
        new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                NovelLibrary novelLibrary = new NovelLibrary(ApiUrl + "1");
                try {
                    max_page = novelLibrary.getMaxPage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
        changeList();
        return v;
    }

    public void setUpAdapter(Bundle savedInstanceState) {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(RecyclerView.GONE);
        novels = savedInstanceState.getParcelableArrayList("list");
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NovelAdapter(getContext(), novels, recyclerView, (LinearLayoutManager) layoutManager);
        adapter.setOnItemClickListener(Fragment1.this);
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (page < max_page) {
                    novels.add(null);
                    adapter.notifyItemInserted(novels.size() - 1);
                    page++;
                    if(isStarted)
                    new LoadMoreNovel().execute(ApiUrl + page);
                } else
                    Toast.makeText(getContext(), "Bạn Đã Đến Trang Cuối Cùng", Toast.LENGTH_LONG).show();
            }
        });
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isStarted = false;
    }

    @Override
    public void onStart() {
        super.onStart();
        isStarted = true;
    }

    public void changeList() {
        NovelType = MainActivity.novelType;
        if (NovelType != null)
            switch (NovelType) {
                case "Home":
                    ApiUrl = "http://valvrareteam.com/page/";
                    break;
                case "LightNovel":
                    ApiUrl = "http://valvrareteam.com/light-novel/page/";
                    break;
                case "OriginalNovel":
                    ApiUrl = "http://valvrareteam.com/light-novel-original-novel/page/";
                    break;
                case "TeaserNovel":
                    ApiUrl = "http://valvrareteam.com/light-novel-teaser-project/page/";
                    break;
                case "Manga":
                    ApiUrl = "http://valvrareteam.com/manga/page/";
                    break;
            }
        page = 1;
        String url = ApiUrl + page;

        if (FunctionHelper.isNetworkAvailable(context)) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(RecyclerView.GONE);
            if(isStarted)
            new getNovel().execute(url);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(RecyclerView.GONE);
            tv_empty.setVisibility(TextView.VISIBLE);
            Toast.makeText(context, "Xin hãy kiểm tra lại kết nối mạng!", Toast.LENGTH_SHORT).show();
        }
    }
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
//    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putParcelableArrayList("list", novels);
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

//    public void getType(String type) {
//NovelType = type;
//    }


    private class LoadMoreNovel extends AsyncTask<String, Void, ArrayList<Novel>> {

        @Override
        public ArrayList<Novel> doInBackground(String... params) {
            NovelLibrary download = new NovelLibrary(params[0]);
            ArrayList<Novel> result = null;
            try {
                result = download.getNovel();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        public void onPostExecute(ArrayList<Novel> result) {
            if(isStarted){
            novels.remove(novels.size() - 1);
            adapter.notifyItemRemoved(novels.size());

            if (result != null) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(RecyclerView.VISIBLE);
                moreNovel = result;
//                page++;
//                if (page < max_page) {
                for (int i = 0; i < moreNovel.size(); i++) {
                    novels.add(moreNovel.get(i));
                    adapter.notifyItemInserted(novels.size());
//                    }
                }
//                else Toast.makeText(getActivity(), "Bạn Đã Đến Cuối Danh Sách Truyện!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Lỗi Kết Nối!", Toast.LENGTH_LONG).show();
            }
            adapter.setLoaded();
        }}
    }

    private class getNovel extends AsyncTask<String, Void, ArrayList<Novel>> {

        @Override
        protected ArrayList<Novel> doInBackground(String... params) {

            NovelLibrary novelLibrary = new NovelLibrary(params[0]);
            ArrayList<Novel> result = null;
            try {
                result = novelLibrary.getNovel();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<Novel> result) {
            super.onPostExecute(novels);
            if(isStarted)
            if (result != null) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(RecyclerView.VISIBLE);
                novels = result;
//                recyclerView.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(layoutManager);
                adapter = new NovelAdapter(getContext(), novels, recyclerView, (LinearLayoutManager) layoutManager);
                adapter.setOnItemClickListener(Fragment1.this);
                adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {
                        if(isStarted)
                        if (page < max_page) {
                            novels.add(null);
                            page++;
                            adapter.notifyItemInserted(novels.size() - 1);
                            new LoadMoreNovel().execute(ApiUrl + page);
                        }
//                        else
//                            Toast.makeText(getContext(), "Bạn Đã Đến Trang Cuối Cùng", Toast.LENGTH_LONG).show();
                    }
                });
                recyclerView.setAdapter(adapter);
            } else {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(RecyclerView.GONE);
                tv_empty.setVisibility(TextView.VISIBLE);
                Toast.makeText(getContext(), "Xin hãy kiểm tra lại kết nối!", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onItemClick(View view, Novel novel) {
        //        Intent intent = new Intent(getActivity(), ChapterReadingActivity.class);
        Intent intent = new Intent(getActivity(), NovelDescriptionActivity.class);

        intent.putExtra("novel", novel);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

}

