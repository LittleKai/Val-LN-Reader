package com.valvrare.littlekai.valvraretranslation.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.valvrare.littlekai.valvraretranslation.R;

public class ChapterReadingFragment extends Fragment {
    public ChapterReadingFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chapter_reading, container, false);
        Snackbar.make(rootView.findViewById(R.id.coordinatorChapterAction), R.string.chapter_communicate_message,
                Snackbar.LENGTH_LONG)
                .show();

        return rootView;
    }



}