package com.valvrare.littlekai.valvraretranslation.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.database.ValvrareDatabaseHelper;
import com.valvrare.littlekai.valvraretranslation.model.Chapter;
import com.valvrare.littlekai.valvraretranslation.model.Novel;
import com.valvrare.littlekai.valvraretranslation.utils.BigCircleTransform;
import com.valvrare.littlekai.valvraretranslation.widget.MyCircleImageView;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Kai on 8/4/2016.
 */
public class ChapterHistoryAdapter extends ArrayAdapter<Novel> {

    private Context context;
    private int resId;
    private List<Novel> listNavItems;
    private ValvrareDatabaseHelper db;
    private BitmapDrawable cvHoldIcon, cvErrIcon;
    public ChapterHistoryAdapter(Context context, int resource, List<Novel> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resId = resource;
        this.listNavItems = objects;
        Bitmap bmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.placeholder);
        Bitmap roundedIcon = MyCircleImageView.decodeBitmap(bmap, 100, 100);
        cvHoldIcon = new BitmapDrawable(context.getResources(), roundedIcon);

        Bitmap bmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.error_image);
        Bitmap roundedIcon1 = MyCircleImageView.decodeBitmap(bmap1, 100, 100);
        cvErrIcon = new BitmapDrawable(context.getResources(), roundedIcon1);
    }

    @Override
    public int getCount() {
        return listNavItems.size();
    }

    @Nullable
    @Override
    public Novel getItem(int position) {
        return listNavItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listNavItems.indexOf(getItem(position));
    }

    private class ViewHolder {
        TextView tv_novelName, tv_LastReadChapter, tv_HistoryDate;
        ImageView image;
        LinearLayout container;

    }

    @NonNull
    @Override
    public View getView(final int pos, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        final Novel homeNovel = listNavItems.get(pos);
        final Chapter chapter = homeNovel.getChapter();

        if (convertView == null) {
            convertView = View.inflate(context, resId, null);
            holder = new ViewHolder();
            holder.container = (LinearLayout) convertView.findViewById(R.id.llout_HistoryNovel);
            holder.tv_novelName = (TextView) convertView.findViewById(R.id.tv_novelName);
            holder.tv_LastReadChapter = (TextView) convertView.findViewById(R.id.tv_LastestHistoryChapter);
            holder.image = (ImageView) convertView.findViewById(R.id.iv_novelIcon);
            holder.tv_HistoryDate = (TextView) convertView.findViewById(R.id.tv_HistoryNovelDate);

            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();


//        if (pos % 2 == 0)
//            holder.container.setBackgroundColor(0xffffffff);
//        else
//            holder.container.setBackgroundColor(0xffdde4ed);

        if (pos % 2 == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.container.setBackground(context.getResources().getDrawable(R.drawable.color_even_item_state));
            } else
                holder.container.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_even_item_state));
//            holder.main_content.setBackgroundColor(0xffffffff);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.container.setBackground(context.getResources().getDrawable(R.drawable.color_uneven_item_state));
            } else
                holder.container.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.color_uneven_item_state));
        }
        holder.tv_novelName.setText(homeNovel.getNovelName());
        holder.tv_LastReadChapter.setText(Html.fromHtml("Chương Vừa Đọc: " + "<b>" + homeNovel.getChapter().getName() + "</b>"));
//get Date and Time
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
            String hourS;
            String minuteS;
            hourS = "" + hour;
            minuteS = "" + minute;
            if (hour < 10)
                hourS = "0" + hour;
            if (minute < 10)
                minuteS = "0" + minute;
            holder.tv_HistoryDate.setText(Html.fromHtml("Thời Gian Đọc: " + "<font color = #000000>" + hourS + ":" + minuteS + ", ngày " + date + " " + dateStatus + "</font>"));

        }
        //get Novel's Avatar Image
        String imgUrl = homeNovel.getImage();
        holder.image.setImageBitmap(null);
        Glide.with(holder.image.getContext()).load(imgUrl)
                .placeholder(cvHoldIcon).error(cvErrIcon)
                .transform(new BigCircleTransform(context)).into(holder.image);

        return convertView;
    }

}
