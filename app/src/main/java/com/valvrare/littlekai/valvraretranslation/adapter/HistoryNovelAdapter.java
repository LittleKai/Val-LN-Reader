package com.valvrare.littlekai.valvraretranslation.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.model.Chapter;
import com.valvrare.littlekai.valvraretranslation.model.Novel;
import com.valvrare.littlekai.valvraretranslation.utils.BigCircleTransform;
import com.valvrare.littlekai.valvraretranslation.widget.MyCircleImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Kai on 8/4/2016.
 */
public class HistoryNovelAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    private List<Novel> items;
    private OnItemClickListener onItemClickListener;
    private Context context;
    int resLayout;
    private ArrayList<Novel> list;
    private int lastPosition = -1;

    public HistoryNovelAdapter(Context c, ArrayList<Novel> list) {
        context = c;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_novel_list, parent, false);
        v.setOnClickListener(this);
        vh = new NovelViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        final Novel homeNovel = list.get(position);
        final Chapter chapter = homeNovel.getChapter();
        if (position % 2 == 0)
            ((NovelViewHolder) holder).container.setBackgroundColor(0xffffffff);
        else
            ((NovelViewHolder) holder).container.setBackgroundColor(0xffdde4ed);
        ((NovelViewHolder) holder).tv_novelName.setText(homeNovel.getNovelName());
        ((NovelViewHolder) holder).tv_LastReadChapter.setText(Html.fromHtml("Chương Vừa Đọc: " + "<b>" + homeNovel.getChapter().getName() + "</b>"));
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
            ((NovelViewHolder) holder).tv_HistoryDate.setText(Html.fromHtml("Thời Gian Đọc: " + "<font color = #000000>" + hourS + ":" + minuteS + ", ngày " + date + " " + dateStatus + "</font>"));

        }
        //get Novel's Avatar Image
        String imgUrl = homeNovel.getImage();

//            if (!homeNovel.getImage().equals("")) {
        Glide.with(((NovelViewHolder) holder).image.getContext()).load(imgUrl)
                .placeholder(R.drawable.placeholder).error(R.drawable.error_image)
                .transform(new BigCircleTransform(context)).into(((NovelViewHolder) holder).image);
//            } else {
//                Glide.with(((NovelViewHolder) holder).image.getContext()).load(cvErrIcon)
//                        .into(((NovelViewHolder) holder).image);
//            }

        holder.itemView.setTag(homeNovel);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    private static class NovelViewHolder extends RecyclerView.ViewHolder {
        TextView tv_novelName, tv_LastReadChapter, tv_HistoryDate;
        ImageView image;
        LinearLayout container;

        NovelViewHolder(View v) {
            super(v);
            container = (LinearLayout) v.findViewById(R.id.llout_HistoryNovel);
            tv_novelName = (TextView) v.findViewById(R.id.tv_novelName);
            tv_LastReadChapter = (TextView) v.findViewById(R.id.tv_LastestHistoryChapter);
            image = (ImageView) v.findViewById(R.id.iv_novelIcon);
            tv_HistoryDate = (TextView) v.findViewById(R.id.tv_HistoryNovelDate);
        }

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public void onClick(final View v) {
        if (onItemClickListener != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onItemClickListener.onItemClick(v, (Novel) v.getTag());
                }
            }, 200);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(View view, Novel novel);

    }

}

