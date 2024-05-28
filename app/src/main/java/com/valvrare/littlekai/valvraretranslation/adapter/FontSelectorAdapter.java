package com.valvrare.littlekai.valvraretranslation.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.valvrare.littlekai.valvraretranslation.ChapterReadingActivity;
import com.valvrare.littlekai.valvraretranslation.R;

import java.util.List;

/**
 * Created by Kai on 8/4/2016.
 */
public class FontSelectorAdapter extends ArrayAdapter<String> {

    private Context context;
    int resLayout;
    private List<String> listNavItems;
    private Typeface textFont;

    public FontSelectorAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resLayout = resource;
        this.listNavItems = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(context, resLayout, null);
//        TextView tvName = (TextView) v.findViewById(R.id.tv_fontname);
        CheckedTextView tvName = (CheckedTextView) v.findViewById(R.id.checkTextView);
        String font = listNavItems.get(position);
        tvName.setText(font);
        if (ChapterReadingActivity.getFontNamePref().equals(font))
            tvName.setChecked(true);
        else if (ChapterReadingActivity.getFontNamePref().equals("Times N.Roman") && font.equals("Times New Roman"))
            tvName.setChecked(true);
        switch (position) {
            case 0:
                textFont = Typeface.createFromAsset(context.getAssets(),
                        "fonts/palatino.otf");
                tvName.setTypeface(textFont);
                break;
            case 1:
                textFont = Typeface.createFromAsset(context.getAssets(),
                        "fonts/tahoma.ttf");
                tvName.setTypeface(textFont);
                break;
            case 2:
                textFont = Typeface.createFromAsset(context.getAssets(),
                        "fonts/georgia.ttf");
                tvName.setTypeface(textFont);
                break;
            case 3:
                textFont = Typeface.createFromAsset(context.getAssets(),
                        "fonts/Roboto-Regular.ttf");
                tvName.setTypeface(textFont);
                break;
            case 4:
                textFont = Typeface.createFromAsset(context.getAssets(),
                        "fonts/times.ttf");
                tvName.setTypeface(textFont);
                break;
        }
        return v;
    }
}
