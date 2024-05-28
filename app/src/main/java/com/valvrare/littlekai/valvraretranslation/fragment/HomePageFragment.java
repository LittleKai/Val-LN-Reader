package com.valvrare.littlekai.valvraretranslation.fragment;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.valvrare.littlekai.valvraretranslation.MainActivity;
import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.adapter.ViewPagerNovelNoTitleAdapter;
import com.valvrare.littlekai.valvraretranslation.inner_fragment.Fragment1;
import com.valvrare.littlekai.valvraretranslation.inner_fragment.Fragment2;
import com.valvrare.littlekai.valvraretranslation.inner_fragment.Fragment3;
import com.valvrare.littlekai.valvraretranslation.inner_fragment.Fragment4;

public class HomePageFragment extends Fragment implements deleteHistory {
    MainActivity activity;
    String novelType = "Danh Sách Truyện";
    String title = "Danh Sách Truyện";
    TabLayout tabLayout;
    ViewPager viewPager;
    Fragment2 fragment2;
    Fragment1 fragment1;
    ViewPagerNovelNoTitleAdapter viewPagerNovelAdapter;
    private int[] Icon_TabLayout = {R.drawable.icon_book_cover, R.drawable.ic_history_white_48dp, R.drawable.ic_heart_outline_white_48dp, R.drawable.ic_download_white_48dp};

    public HomePageFragment() {
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_page, container, false);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout_homepage);
        viewPager = (ViewPager) rootView.findViewById(R.id.view_home_page);

        viewPagerNovelAdapter = new ViewPagerNovelNoTitleAdapter(getChildFragmentManager());
        fragment2 = new Fragment2();
        fragment1 = new Fragment1();
        viewPagerNovelAdapter.addFragments(fragment1);
        viewPagerNovelAdapter.addFragments(fragment2);
        viewPagerNovelAdapter.addFragments(new Fragment3());
        viewPagerNovelAdapter.addFragments(new Fragment4());

        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(viewPagerNovelAdapter);

        activity = (MainActivity) getActivity();
        novelType = getType();
        activity.setTitle(novelType);

        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            int tabIconColor = ContextCompat.getColor(getContext(), R.color.myTabLayout_Unactivated);
            int tabIconColorAtctivated = ContextCompat.getColor(getContext(), R.color.myTabLayout_Activated);
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setIcon(Icon_TabLayout[i]);
                if (i != 0 & tab.getIcon() != null)
                    tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                if (i == 0 & tab.getIcon() != null)
                    tab.getIcon().setColorFilter(tabIconColorAtctivated, PorterDuff.Mode.SRC_IN);
            }
        }

//        tabLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < tabLayout.getTabCount(); i++) {
//                    int tabIconColor = ContextCompat.getColor(getContext(), R.color.myTabLayout_Unactivated);
//                    int tabIconColorAtctivated = ContextCompat.getColor(getContext(), R.color.myTabLayout_Activated);
//                    TabLayout.Tab tab = tabLayout.getTabAt(i);
//                    if(tab != null) {
//                        if (i != 0&tab.getIcon()!=null)
//                            tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
//                        if (i == 0&tab.getIcon()!=null)
//                            tab.getIcon().setColorFilter(tabIconColorAtctivated, PorterDuff.Mode.SRC_IN);
//                    }
//                }
//            }
//        });
        tabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        super.onTabUnselected(tab);
                        int tabIconColor = ContextCompat.getColor(getContext(), R.color.myTabLayout_Unactivated);
                        if (tab.getIcon() != null)
                            tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    }

                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                        int tabIconColor = ContextCompat.getColor(getContext(), R.color.myTabLayout_Activated);
                        if (tab.getIcon() != null)
                            tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                        activity = (MainActivity) getActivity();
                        switch (tabLayout.getSelectedTabPosition()) {
                            case 0:
                                title = novelType;
//                                title = getType();
                                activity.hideDeleteOption();
                                activity.showSearchOption();
//                                activity.hideOption(R.id.action_share);
                                break;
                            case 1:
                                title = "Lịch Sử Đọc";
                                activity.showDeleteOption();
                                activity.hideSearchOption();
                                break;
                            case 2:
                                title = "Đang Theo Dõi";
                                activity.hideDeleteOption();
                                activity.hideSearchOption();
                                break;
                            case 3:
                                title = "Tải Xuống";
                                activity.hideDeleteOption();
                                activity.hideSearchOption();
                                break;
                        }

                        activity.setTitle(title);
//                        numTab = tab.getPosition();
//                        prefs.edit().putInt("numTab", numTab).apply();
                    }
                });
        return rootView;
    }

    private String getType() {
        String getType = MainActivity.novelType;
        String novelType = null;
        if (getType != null) {
            switch (getType) {
                case "Home":
                    novelType = "Danh Sách Truyện";
                    break;
                case "LightNovel":
                    novelType = "Light Novel";
                    break;
                case "OriginalNovel":
                    novelType = "Original Novel";
                    break;
                case "TeaserNovel":
                    novelType = "Teaser Novel";
                    break;
                case "Manga":
                    novelType = "Manga";
                    break;
                default:
                    novelType = "Danh Sách Truyện";
                    break;
            }
        }
        return novelType;
    }

    public void changeTypeList() {
        TabLayout.Tab tab = tabLayout.getTabAt(0);
        if (tab != null)
            tab.select();
        fragment1.changeList();
    }

    @Override
    public void deleteHistoryTask() {
        fragment2.historyClear();
    }
}

interface deleteHistory {
    void deleteHistoryTask();
}