package com.valvrare.littlekai.valvraretranslation.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.database.ValvrareDatabaseHelper;
import com.valvrare.littlekai.valvraretranslation.inner_fragment.Fragment4;
import com.valvrare.littlekai.valvraretranslation.model.Novel;
import com.valvrare.littlekai.valvraretranslation.utils.BigCircleTransform;
import com.valvrare.littlekai.valvraretranslation.utils.FileDownloader;
import com.valvrare.littlekai.valvraretranslation.widget.MyCircleImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;


public class DownloadedNovelAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    private OnItemClickListener onItemClickListener;
    private Context context;
    private ArrayList<Novel> list;
    private ValvrareDatabaseHelper db;
    private Fragment4 f;
    private BitmapDrawable cvHoldIcon, cvErrIcon;

    public DownloadedNovelAdapter(Context c, ArrayList<Novel> list, Fragment4 fragment4) {
        context = c;
        f = fragment4;
        this.list = list;
        Bitmap bmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.placeholder);
        Bitmap roundedIcon = MyCircleImageView.decodeBitmap(bmap, 100, 100);
        cvHoldIcon = new BitmapDrawable(context.getResources(), roundedIcon);

        Bitmap bmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.error_image);
        Bitmap roundedIcon1 = MyCircleImageView.decodeBitmap(bmap1, 100, 100);
        cvErrIcon = new BitmapDrawable(context.getResources(), roundedIcon1);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_downloaded_novel_list, parent, false);
        v.setOnClickListener(this);
        vh = new NovelViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final Novel novel = list.get(position);
        if (position % 2 == 0)
            ((NovelViewHolder) holder).container.setBackgroundColor(0xffffffff);
        else
            ((NovelViewHolder) holder).container.setBackgroundColor(0xffdde4ed);
        ((NovelViewHolder) holder).tv_novelName.setText(novel.getNovelName());
        String a = "";
        if (novel.getDaoChapters() < 10)
            a = "0" + novel.getDaoChapters();
        else a = "" + novel.getDaoChapters();
        ((NovelViewHolder) holder).tv_chapterAmount.setText(Html.fromHtml("Số Chương Đã Tải: " + "<b><font color = #610ce8>" + a + "</font></b>"));

        String imgUrl = novel.getImage();

        ((NovelViewHolder) holder).image.setImageBitmap(null);

        Glide.with(((NovelViewHolder) holder).image.getContext()).load(imgUrl)
                .placeholder(cvHoldIcon).error(cvErrIcon)
                .transform(new BigCircleTransform(context)).into(((NovelViewHolder) holder).image);


        ((NovelViewHolder) holder).iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.myDialog));
                builder.setMessage("Xác nhận xóa toàn bộ số chương đã tải?")
                        .setCancelable(false)
                        .setPositiveButton("Đồng Ý", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                db = new ValvrareDatabaseHelper(context);
                                if (db.deleteDownloadedNovel(novel)) {
                                    new AsyncTask<Novel, Void, Void>() {
                                        @Override
                                        protected Void doInBackground(Novel... novels) {
                                            FileDownloader fl = new FileDownloader();
                                            try {
                                                fl.deleteNovel(novel);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            return null;
                                        }
                                    }.execute(novel);
                                    f.refresh();
//                                   list.remove(position);
                                }
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        String dateStatus = "(Không xác định)";
        String time = novel.getTime();
        String date = novel.getDate();

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
            ((NovelViewHolder) holder).tv_Date.setText(Html.fromHtml("Thời Gian Tải Truyện Gần Nhất: " + "<font color = #000000>" + hourS + ":" + minuteS + ", ngày " + date + " " + dateStatus + "</font>"));
            holder.itemView.setTag(novel);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    private static class NovelViewHolder extends RecyclerView.ViewHolder {
        TextView tv_novelName, tv_chapterAmount, tv_Date;
        ImageView image, iv_delete;
        LinearLayout container;

        NovelViewHolder(View v) {
            super(v);
            container = (LinearLayout) v.findViewById(R.id.llout_DownloadedNovel);
            tv_novelName = (TextView) v.findViewById(R.id.tv_novelName);
            image = (ImageView) v.findViewById(R.id.iv_novelIcon);
            iv_delete = (ImageView) v.findViewById(R.id.iv_delete);
            tv_chapterAmount = (TextView) v.findViewById(R.id.tv_chapterAmount);
            tv_Date = (TextView) v.findViewById(R.id.tv_Date);
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

