package com.valvrare.littlekai.valvraretranslation.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.valvrare.littlekai.valvraretranslation.DownloadActivity;
import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.database.ValvrareDatabaseHelper;
import com.valvrare.littlekai.valvraretranslation.model.Chapter;
import com.valvrare.littlekai.valvraretranslation.model.Novel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Kai on 8/4/2016.
 */
public class BookmarkChapterAdapter extends ArrayAdapter<Chapter> {

    private Context context;
    private int resId;
    private List<Chapter> listNavItems, items_copy;
    private Chapter lChapter;
    private ValvrareDatabaseHelper db;
    private ProgressDialog progressDialog;
    private int lPosition;
    private DownloadActivity activity;
    private View item, item1;
//    private ChapterContentHelper db;

    public void setActivity(DownloadActivity activity) {
        this.activity = activity;
    }

    public BookmarkChapterAdapter(Context context, int resource, List<Chapter> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resId = resource;
        this.listNavItems = objects;
        items_copy = new ArrayList<>();
        items_copy.addAll(listNavItems);
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
        TextView tv_novelName, tv_FavChapter, tv_FavDate;
        LinearLayout main_content;
        ImageView image;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        final Chapter chapter = listNavItems.get(position);

        db = new ValvrareDatabaseHelper(context);
        if (convertView == null) {
            convertView = View.inflate(context, resId, null);
            holder = new ViewHolder();
            holder.tv_novelName = (TextView) convertView.findViewById(R.id.tv_novelName);
            holder.tv_FavChapter = (TextView) convertView.findViewById(R.id.tv_FavChapter);
            holder.tv_FavDate = (TextView) convertView.findViewById(R.id.tv_FavDate);
            holder.main_content = (LinearLayout) convertView.findViewById(R.id.llout_Bookmark);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();
//        if (position % 2 == 0)
//            holder.main_content.setBackgroundColor(0xffffffff);
//        else
//            holder.main_content.setBackgroundColor(0xffdde4ed);

        Glide.with(context).load(chapter.getNovelImageUrl()).
                placeholder(R.drawable.placeholder).error(R.drawable.error_image)
                .into(holder.image);

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

        holder.tv_novelName.setText(chapter.getNovelName());
        holder.tv_FavChapter.setText(Html.fromHtml("Tên Chương: " + "\"" + "<b>" + chapter.getName() + "</b>" + "\""));
        String dateStatus = "(Không xác định)";
        String time = chapter.getTime();
        String date = chapter.getDate();
        if (time != null & date != null) {
            String[] time_parts = time.split(":");
            int hour = Integer.parseInt(time_parts[0]);
            int minute = Integer.parseInt(time_parts[1]);

            String[] date_parts = date.split("/");
            int day = Integer.parseInt(date_parts[0]);
            int month = Integer.parseInt(date_parts[1]);
            int year = Integer.parseInt(date_parts[2]);

            if (day < 10)
                date_parts[0] = "0" + date_parts[0];
            if (month < 10)
                date_parts[1] = "0" + date_parts[1];
            date = date_parts[0] + "/" + date_parts[1] + "/" + date_parts[2];

            Calendar c = Calendar.getInstance();
            int currYear = c.get(Calendar.YEAR);
            int currMonth = c.get(Calendar.MONTH);
            int currDay = c.get(Calendar.DATE);
            int currHour = c.get(Calendar.HOUR_OF_DAY);
            int currMinute = c.get(Calendar.MINUTE);

            if (year == currYear) {
                if (month == currMonth) {
                    if (day == currDay) {
                        if (hour == currHour) {
                            if (minute == currMinute) {
                                dateStatus = "(vài giây trước)";
                            } else if (minute < currMinute)
                                dateStatus = "(" + (currMinute - minute) + " phút trước)";
                        } else if (hour < currHour)
                            dateStatus = "(" + (currHour - hour) + " giờ trước)";
                    } else if (day < currDay) dateStatus = "(" + (currDay - day) + " ngày trước)";
                } else if (month < currMonth)
                    dateStatus = "(" + (currMonth - month) + " tháng trước)";
            } else if (year < currYear) dateStatus = "(" + (currYear - year) + " năm trước)";
            String hourS = "";
            String minuteS = "";
            hourS = "" + hour;
            minuteS = "" + minute;
            if (hour < 10)
                hourS = "0" + hour;
            if (minute < 10)
                minuteS = "0" + minute;
            holder.tv_FavDate.setText(Html.fromHtml("Thời Gian Lưu Chương: " + "<b>" + hourS + ":" + minuteS + ", ngày " + date + " " + dateStatus + "</b>"));
        }
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Chapter> filteredList = new ArrayList<>();
                String key = constraint.toString().toLowerCase();
//                if (key.isEmpty())
//                    filteredList.addAll(items_copy);
//                else {
                key = key.toLowerCase();
                for (Chapter i : items_copy) {
                    if (i.getNovelName().toLowerCase().contains(key) | i.getName().toLowerCase().contains(key)) {
                        filteredList.add(i);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listNavItems = (ArrayList<Chapter>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void filter(String key) {
        listNavItems.clear();
        if (key.isEmpty())
            listNavItems.addAll(items_copy);
        else {
            key = key.toLowerCase();
            for (Chapter i : items_copy) {
                if (i.getNovelName().toLowerCase().contains(key) | i.getName().toLowerCase().contains(key)) {
                    listNavItems.add(i);
                }
            }
        }
        notifyDataSetChanged();
    }

}
