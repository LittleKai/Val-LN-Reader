package com.valvrare.littlekai.valvraretranslation.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.valvrare.littlekai.valvraretranslation.DownloadActivity;
import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.database.ValvrareDatabaseHelper;
import com.valvrare.littlekai.valvraretranslation.model.Chapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kai on 8/4/2016.
 */
public class ChapterDownloadAdapter extends ArrayAdapter<Chapter> {

    private Context context;
    private int resId;
    private List<Chapter> listNavItems;
    private DownloadActivity activity;
//    private ChapterContentHelper db;

    public void setActivity(DownloadActivity activity) {
        this.activity = activity;
    }

    public ChapterDownloadAdapter(Context context, int resource, List<Chapter> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resId = resource;
        this.listNavItems = objects;
//        activity = new DownloadActivity();

//        db = new ChapterContentHelper(context);
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
        TextView textView;
        LinearLayout main_content;
        CheckBox checkBox;
    }

    @NonNull
    @Override
    public View getView(final int pos, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        final Chapter chapter = listNavItems.get(pos);
        int position = chapter.getOrderNo();
//        db = new ValvrareDatabaseHelper(context);
        if (convertView == null) {
            convertView = View.inflate(context, resId, null);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.textView);
            holder.main_content = (LinearLayout) convertView.findViewById(R.id.main_content);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);

            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();

        if (!chapter.isEnableDownload()) {
            if (position % 2 == 1)
                holder.main_content.setBackgroundColor(0xffffffff);
            else
                holder.main_content.setBackgroundColor(0xffdde4ed);
            chapter.setChecked(false);
            holder.checkBox.setClickable(false);
            holder.checkBox.setChecked(true);
            holder.checkBox.setEnabled(false);
        } else {
            holder.checkBox.setClickable(true);
            holder.checkBox.setEnabled(true);
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    activity.changeStatus();
                }
            });
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!holder.checkBox.isChecked())
                        chapter.setChecked(false);
                    else
                        chapter.setChecked(true);
                    notifyDataSetChanged();
                    activity.changeStatus();
                }
            });
            if (position % 2 == 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    holder.main_content.setBackground(context.getResources().getDrawable(R.drawable.color_even_item_state));
                } else
                    holder.main_content.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_even_item_state));
//            holder.main_content.setBackgroundColor(0xffffffff);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    holder.main_content.setBackground(context.getResources().getDrawable(R.drawable.color_uneven_item_state));
                } else
                    holder.main_content.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_uneven_item_state));
            }
            if (chapter.isChecked())
                holder.checkBox.setChecked(true);
            else
                holder.checkBox.setChecked(false);
        }

        String addNo = "";
        if (position > -1 & position < 10)
            addNo = "00";
        if (position > 9 & position < 100)
            addNo = "0";
//        if(position>99&position<1000)
//            addNo = "0";


        holder.textView.setText(Html.fromHtml("<b><font color = #24619d>" + addNo + position + ". " + "</font></b>" + chapter.getName()));

        return convertView;
    }

    public void selectAll() {
        for (int i = 0; i < listNavItems.size(); i++) {
            if (listNavItems.get(i).isEnableDownload())
                listNavItems.get(i).setChecked(true);
        }
        notifyDataSetChanged();
    }

    public void deselectAll() {
        for (int i = 0; i < listNavItems.size(); i++) {
            listNavItems.get(i).setChecked(false);
        }
        notifyDataSetChanged();
    }

    public int getCheckedCount() {
        int count = 0;
        for (int i = 0; i < listNavItems.size(); i++) {
            if (listNavItems.get(i).isChecked())
                count++;
        }
        return count;
    }

    public ArrayList<Chapter> getSelectChapters() {
        ArrayList<Chapter> arr = new ArrayList<>();
        for (int i = 0; i < listNavItems.size(); i++) {
            if (listNavItems.get(i).isChecked())
                arr.add(listNavItems.get(i));
        }
        return arr;
    }
}
