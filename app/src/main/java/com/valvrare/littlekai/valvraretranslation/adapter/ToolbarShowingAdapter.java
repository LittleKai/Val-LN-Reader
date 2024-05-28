package com.valvrare.littlekai.valvraretranslation.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.valvrare.littlekai.valvraretranslation.ChapterReadingActivity;
import com.valvrare.littlekai.valvraretranslation.R;

import java.util.List;

/**
 * Created by Kai on 8/4/2016.
 */
public class ToolbarShowingAdapter extends ArrayAdapter<String> {

    private Context context;
    int resLayout;
    private List<String> listNavItems;
    private Typeface textFont;

    public ToolbarShowingAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resLayout = resource;
        this.listNavItems = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(context, resLayout, null);
        CheckedTextView tvName = (CheckedTextView) v.findViewById(R.id.checkTextView);
        ImageView imageView = (ImageView) v.findViewById(R.id.ctv_image);
        String type = listNavItems.get(position);
        String name = null;
        tvName.setText(type);
        if (type.equals("Kiểu Chạm")){
            imageView.setImageResource(R.drawable.icon_tap);
            name = "Touch";
        }
        else if (type.equals("Kiểu Vuốt")){
            name = "Scroll";
            imageView.setImageResource(R.drawable.icon_drag);}
        if (ChapterReadingActivity.getShowTypePref().equals(name))
            tvName.setChecked(true);
        return v;
    }


}
