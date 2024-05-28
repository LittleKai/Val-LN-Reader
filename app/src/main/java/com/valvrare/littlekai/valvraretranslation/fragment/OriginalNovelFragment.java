package com.valvrare.littlekai.valvraretranslation.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.valvrare.littlekai.valvraretranslation.R;

public class OriginalNovelFragment extends Fragment {
    public OriginalNovelFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_original_novel, container, false);
        return rootView;
    }

}