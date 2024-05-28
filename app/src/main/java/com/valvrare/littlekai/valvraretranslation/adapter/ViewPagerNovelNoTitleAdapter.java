package com.valvrare.littlekai.valvraretranslation.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Kai on 8/20/2016.
 */
public class ViewPagerNovelNoTitleAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragments = new ArrayList<>();
    ArrayList<String> tabTitles = new ArrayList<>();

    public void addFragments(Fragment fragment, String tabTitles) {
        this.fragments.add(fragment);
        this.tabTitles.add(tabTitles);
    }

    public void addFragments(Fragment fragment) {
        this.fragments.add(fragment);
     }


    public ViewPagerNovelNoTitleAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

//    @Override
//    public CharSequence getPageTitle(int position) {
//        return tabTitles.get(position);
//    }
}
