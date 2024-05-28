package com.valvrare.littlekai.valvraretranslation.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.valvrare.littlekai.valvraretranslation.R;

public class TeaserNovelFragment extends Fragment {
    public TeaserNovelFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_teaser_novel, container, false);
        return rootView;
    }

}