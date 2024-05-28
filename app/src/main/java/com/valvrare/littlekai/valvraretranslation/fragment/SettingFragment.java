package com.valvrare.littlekai.valvraretranslation.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.valvrare.littlekai.valvraretranslation.R;

/**
 * Created by Kai on 8/4/2016.
 */
public class SettingFragment extends Fragment {
    public SettingFragment(){

    }
@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
    View rootView =inflater.inflate(R.layout.fragment_setting, container, false);
    return rootView;
}

}
