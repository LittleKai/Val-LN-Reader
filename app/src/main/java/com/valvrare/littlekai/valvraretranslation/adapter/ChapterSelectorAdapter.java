package com.valvrare.littlekai.valvraretranslation.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.pdf.PdfRenderer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.valvrare.littlekai.valvraretranslation.ChapterReadingActivity;
import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.model.Chapter;

import java.util.List;

/**
 * Created by Kai on 8/4/2016.
 */
public class ChapterSelectorAdapter extends ArrayAdapter<Chapter> {

    private Context context;
    private int resLayout;
    private List<Chapter> listNavItems;
    private Typeface textFont;
    private Chapter currChapter;
    private int curr_pos = 0;

    public ChapterSelectorAdapter(Context context, int resource, List<Chapter> objects, Chapter c) {
        super(context, resource, objects);
        this.context = context;
        this.resLayout = resource;
        this.listNavItems = objects;
        currChapter = c;
        for (int i = 0; i < listNavItems.size(); i++) {
            if (listNavItems.get(i).getName().equals(currChapter.getName()) & listNavItems.get(i).getUrl().equals(currChapter.getUrl()))
                curr_pos = i;
        }
    }

    @Override
    public int getCount() {
        return listNavItems.size();
    }

    @Nullable
    @Override
    public Chapter getItem(int position) {
        return listNavItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listNavItems.indexOf(getItem(position));
    }

    private class ViewHolder {
        CheckedTextView tvName;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, resLayout, null);
            holder = new ViewHolder();
            holder.tvName = (CheckedTextView) convertView.findViewById(R.id.checkTextView);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();
        Chapter chapter = listNavItems.get(position);
//        int position = chapter.getOrderNo();
        String addNo = "";
        if (position > -1 & position < 9)
            addNo = "00";
        if (position > 8 & position < 99)
            addNo = "0";

        holder.tvName.setText(Html.fromHtml("<b><font color = #24619d>" + addNo + (position + 1) + ". " + "</font></b>" + chapter.getName()));

        if (position == curr_pos)
            holder.tvName.setChecked(true);
        else holder.tvName.setChecked(false);

        return convertView;
    }

    public int current_position_chapter() {
        return curr_pos;
    }
}
