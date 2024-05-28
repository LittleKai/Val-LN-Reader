package com.valvrare.littlekai.valvraretranslation.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.database.ValvrareDatabaseHelper;
import com.valvrare.littlekai.valvraretranslation.model.Novel;
import com.valvrare.littlekai.valvraretranslation.utils.BigCircleTransform;
import com.valvrare.littlekai.valvraretranslation.utils.Constants;
import com.valvrare.littlekai.valvraretranslation.utils.OnLoadMoreListener;
import com.valvrare.littlekai.valvraretranslation.widget.MyCircleImageView;

import java.util.Calendar;
import java.util.List;

public class FavoritedNovelAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    private List<Novel> items;
    private OnItemClickListener onItemClickListener;
    private Context context;
    private ValvrareDatabaseHelper db;
    private BitmapDrawable cvHoldIcon, cvErrIcon;

    public FavoritedNovelAdapter(Context c, List<Novel> items) {
        context = c;
        this.items = items;
        db = new ValvrareDatabaseHelper(context);
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fav_novel_list, parent, false);
        v.setOnClickListener(this);
        vh = new NovelViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        final Novel novel = items.get(position);

        if (position % 2 == 0)
            ((NovelViewHolder) holder).container.setBackgroundColor(0xffffffff);
        else
            ((NovelViewHolder) holder).container.setBackgroundColor(0xffdde4ed);

        ((NovelViewHolder) holder).image.setImageBitmap(null);

        ((NovelViewHolder) holder).tv_novelName.setText(novel.getNovelName());
        ((NovelViewHolder) holder).tv_LastestChapter.setText(Html.fromHtml("Chương Mới Nhất: " + "<b><font color = #795548>" + novel.getLatestChapName() + "</font></b>"));
        String imgUrl = novel.getImage();

        Glide.with(((NovelViewHolder) holder).image.getContext()).load(imgUrl)
                .placeholder(cvHoldIcon).error(cvErrIcon)
                .transform(new BigCircleTransform(context)).into(((NovelViewHolder) holder).image);

        if (novel.getUrl().contains(Constants.VAL_URL_ROOT)) {
            ((NovelViewHolder) holder).iv_notify.setVisibility(View.VISIBLE);
            ((NovelViewHolder) holder).tv_UpdateDate.setVisibility(View.VISIBLE);
            ((NovelViewHolder) holder).tv_NovelRate.setVisibility(View.VISIBLE);
            ((NovelViewHolder) holder).tv_UpdateDate.setText(Html.fromHtml("Ngày Cập Nhật: " + "<font color = #000000>" + novel.getUpdateDate() + "</font>"));
            ((NovelViewHolder) holder).tv_NovelRate.setText(Html.fromHtml("Đánh Giá: " + "<font color = #000000><b>" + novel.getRate() + "/5" + "</b>" + " (" + novel.getRateCount() + " Lượt)</font>"));
            ((NovelViewHolder) holder).iv_notify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.unNotifiedNovel(novel);
                    if (novel.isNotify())
                        ((NovelViewHolder) holder).iv_notify.setImageResource(R.drawable.ic_notify_on);
                    else
                        ((NovelViewHolder) holder).iv_notify.setImageResource(R.drawable.ic_notify_off);
                }
            });
            if (novel.isNotify())
                ((NovelViewHolder) holder).iv_notify.setImageResource(R.drawable.ic_notify_on);
            else
                ((NovelViewHolder) holder).iv_notify.setImageResource(R.drawable.ic_notify_off);
        } else if (novel.getUrl().contains(Constants.SNK_URL_ROOT)) {
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
                        } else if (day < currDay)
                            dateStatus = "(" + (currDay - day) + " ngày trước)";
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
                ((NovelViewHolder) holder).tv_UpdateDate.setText(Html.fromHtml("Lần Kiểm Tra Gần Nhất: " + "<font color = #000000>" + hourS + ":" + minuteS + ", ngày " + date + " " + dateStatus + "</font>"));
            } else ((NovelViewHolder) holder).tv_UpdateDate.setText("");

            ((NovelViewHolder) holder).tv_NovelRate.setVisibility(View.GONE);
            ((NovelViewHolder) holder).iv_notify.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setTag(novel);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    private static class NovelViewHolder extends RecyclerView.ViewHolder {
        TextView tv_novelName, tv_LastestChapter, tv_NovelRate, tv_UpdateDate;
        ImageView image, iv_notify;
        LinearLayout container;

        NovelViewHolder(View v) {
            super(v);
            container = (LinearLayout) v.findViewById(R.id.llout_FavNovel);
            tv_novelName = (TextView) v.findViewById(R.id.tv_novelName);
            tv_UpdateDate = (TextView) v.findViewById(R.id.tv_UpdateDate);
//            tv_NovelView = (TextView) v.findViewById(R.id.tv_NovelView);
            tv_LastestChapter = (TextView) v.findViewById(R.id.tv_LastestChapter);
            tv_NovelRate = (TextView) v.findViewById(R.id.tv_NovelRate);
//            tv_Update = (TextView) v.findViewById(R.id.tv_Update);
            image = (ImageView) v.findViewById(R.id.iv_novelIcon);
            iv_notify = (ImageView) v.findViewById(R.id.iv_notify);
        }

    }

//    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
//        ProgressBar progressBar;
//
//        ProgressViewHolder(View v) {
//            super(v);
//            progressBar = (ProgressBar) v.findViewById(R.id.loadprogress_bar);
//            progressBar.getIndeterminateDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
//        }
//    }

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

