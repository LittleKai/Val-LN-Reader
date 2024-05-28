package com.valvrare.littlekai.valvraretranslation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.adapter.MyFragmentPagerAdapter;
import com.valvrare.littlekai.valvraretranslation.inner_fragment.Fragment1;
import com.valvrare.littlekai.valvraretranslation.inner_fragment.Fragment2;
import com.valvrare.littlekai.valvraretranslation.inner_fragment.Fragment3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kai on 8/4/2016.
 */
public class HomeFragment extends Fragment implements ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener {
    private TabHost tabHost;
    private ViewPager viewPager;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    View rootView;
    public HomeFragment(){
    }
@Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
    rootView =inflater.inflate(R.layout.item_tab_viewer, container, false);
    /**Tab View Code**/
    //tao fragment pager view
    this.initViewPager();
    this.initTabHost();
    return rootView;
}
    private void initViewPager(){
        List<Fragment> listFragments = new ArrayList<Fragment>();
        listFragments.add(new Fragment1());
        listFragments.add(new Fragment2());
        listFragments.add(new Fragment3());
//Change getSupportFragmentManager to getChildFragmentManager()
        this.myFragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), listFragments);
        this.viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        this.viewPager.setAdapter(this.myFragmentPagerAdapter);
        this.viewPager.addOnPageChangeListener(this);
    }
    private void initTabHost() {
        tabHost = (TabHost) rootView.findViewById(android.R.id.tabhost);
        tabHost.setup();

        String[] tabNames = {"Danh Sách", "Lịch Sử", "Yêu Thích"};
        for (int i=0; i < tabNames.length;i++)
        {
            TabHost.TabSpec tabSpec;
            tabSpec = tabHost.newTabSpec(tabNames[i]);
            tabSpec.setIndicator(tabNames[i]);
            tabSpec.setContent(new FakeContent(getActivity()));
            tabHost.addTab(tabSpec);
        }
        tabHost.setOnTabChangedListener(this);
    }
    public class FakeContent implements TabHost.TabContentFactory {
        private final Context context;
        public FakeContent(Context mcontext){
            context = mcontext;
        }
        @Override
        public View createTabContent(String tag) {
            View v = new View(context);
            v.setMinimumHeight(0);
            v.setMinimumWidth(0);
            return v;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }
//ViewPager Listener

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageSelected(int selectedItem) {
        tabHost.setCurrentTab(selectedItem);
    }

    //TabHost Listener
    @Override
    public void onTabChanged(String tabId) {
        int pos = tabHost.getCurrentTab();
        viewPager.setCurrentItem(pos);
    }
}
