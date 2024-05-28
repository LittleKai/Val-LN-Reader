package com.valvrare.littlekai.valvraretranslation;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.valvrare.littlekai.valvraretranslation.adapter.SearchNovelAdapter;
import com.valvrare.littlekai.valvraretranslation.adapter.ViewPagerNovelAdapter;
import com.valvrare.littlekai.valvraretranslation.inner_fragment.search_fragment.SnkSearchNovelFragment;
import com.valvrare.littlekai.valvraretranslation.inner_fragment.search_fragment.ValSearchNovelFragment;
import com.valvrare.littlekai.valvraretranslation.widget.CustomSearchTextView;

public class NovelSearchActivity extends AppCompatActivity implements TextWatcher {
    private String TAG = "Kai";
    SearchNovelAdapter adapter;
    Context context;
    CustomSearchTextView et_search;
    TextView tv_resultAmount;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerNovelAdapter viewPagerNovelAdapter;
    private ValSearchNovelFragment valSearchNovelFragment;
    private SnkSearchNovelFragment snkSearchNovelFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel_search);

        context = this;
        et_search = (CustomSearchTextView) findViewById(R.id.et_search);
        et_search.addTextChangedListener(this);
        et_search.hideClearButton();


        tabLayout = (TabLayout) findViewById(R.id.tabLayout_novel);
        viewPager = (ViewPager) findViewById(R.id.view_pager_novel);
        valSearchNovelFragment = new ValSearchNovelFragment();
//        snkSearchNovelFragment = new SnkSearchNovelFragment();
//        viewPager.setActivity(this);
        viewPagerNovelAdapter = new ViewPagerNovelAdapter(getSupportFragmentManager());
        viewPagerNovelAdapter.addFragments(valSearchNovelFragment, "Valvrare Team");
//        novelFragment2 = new NovelFragment2();
//        viewPagerNovelAdapter.addFragments(snkSearchNovelFragment, "Sonako");
//        viewPagerNovelAdapter.addFragments(new SnkOriginalNovelFragment(), "ORIGINAL");

//        obj_frag = viewPagerNovelAdapter.getItem(2);
//        viewPager.requestDisallowInterceptTouchEvent(true);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(viewPagerNovelAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.requestDisallowInterceptTouchEvent(true);
//        viewPager.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.d(TAG, "onTouch: ");
//                return true;
//            }
//        });
    }


    public void cancelActivity(View view) {
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (String.valueOf(s).isEmpty())
            et_search.hideClearButton();
        else et_search.showClearButton();

//        if(snkSearchNovelFragment!=null)
//            snkSearchNovelFragment.filterList(String.valueOf(s));
        if(valSearchNovelFragment!=null)
            valSearchNovelFragment.filterList(String.valueOf(s));

//        if (adapter != null) {
//            adapter.filter(String.valueOf(s));
//            tv_resultAmount.setText(Html.fromHtml("Tìm được " + "<b>" + adapter.getItemCount() + "</b>" + " Truyện."));
//        }

    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
