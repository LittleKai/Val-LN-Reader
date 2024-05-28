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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.model.Novel;
import com.valvrare.littlekai.valvraretranslation.utils.BigCircleTransform;
import com.valvrare.littlekai.valvraretranslation.utils.OnLoadMoreListener;
import com.valvrare.littlekai.valvraretranslation.widget.MyCircleImageView;

import java.util.List;

/**
 * Created by Kai on 8/4/2016.
 */
public class NovelAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private int visibleThreshold = 3;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private List<Novel> items;
    private OnItemClickListener onItemClickListener;
    private Context context;
    private OnLoadMoreListener onLoadMoreListener;
    private int lastPosition = -1;
    //    private BitmapDrawable cvHoldIcon, cvErrIcon;
    private Bitmap roundedIcon, roundedIcon1;

    public NovelAdapter(Context c, List<Novel> items, RecyclerView recyclerView, final LinearLayoutManager layoutManager) {
        context = c;
        this.items = items;
        Bitmap bmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.placeholder);
        roundedIcon = MyCircleImageView.decodeBitmap(bmap, 110, 110);
//        cvHoldIcon = new BitmapDrawable(context.getResources(), roundedIcon);
//
        Bitmap bmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.error_image);
        roundedIcon1 = MyCircleImageView.decodeBitmap(bmap1, 110, 110);
//        cvErrIcon = new BitmapDrawable(context.getResources(), roundedIcon1);

//        final RecyclerView.LinearLayoutManager layoutManager = recyclerView.getLayoutManager();
//        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition();
                if (!loading && totalItemCount < (lastVisibleItem + visibleThreshold)) {
                    //reach the end
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    loading = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_novel_list, parent, false);
            v.setOnClickListener(this);
            vh = new NovelViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_view, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof NovelViewHolder) {
            final Novel homeNovel = items.get(position);
            ((NovelViewHolder) holder).tv_novelName.setText(homeNovel.getNovelName());
            ((NovelViewHolder) holder).tv_LastestChapter.setText(Html.fromHtml("Chương Mới: " + "<b><font color = #795548>" + homeNovel.getLatestChapName() + "</b></font>"));
            String imgUrl = homeNovel.getImage();
            String img_set = homeNovel.getSecond_image();

            if (img_set != null)
                if (!img_set.isEmpty())
                    imgUrl = img_set;

//            ((NovelViewHolder) holder).image.setImageBitmap(null);

            BitmapDrawable cvHoldIcon = new BitmapDrawable(((NovelViewHolder) holder).image.getContext().getResources(), roundedIcon);
            BitmapDrawable cvErrIcon = new BitmapDrawable(((NovelViewHolder) holder).image.getContext().getResources(), roundedIcon1);

            Glide.with(((NovelViewHolder) holder).image.getContext()).load(imgUrl)
                    .placeholder(cvHoldIcon).error(cvErrIcon)
                    .transform(new BigCircleTransform(context)).into(((NovelViewHolder) holder).image);

            ((NovelViewHolder) holder).tv_Update.setText(Html.fromHtml("Giới Thiệu: <font color = #000000>" + homeNovel.getSummary() + "</font>"));
            ((NovelViewHolder) holder).tv_NovelView.setText(Html.fromHtml("Lượt Xem: "));
            ((NovelViewHolder) holder).tv_NovelRate.setText(Html.fromHtml("Rate: "));

            setAnimation(((NovelViewHolder) holder).container, position);
            holder.itemView.setTag(homeNovel);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }


    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();

    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    private static class NovelViewHolder extends RecyclerView.ViewHolder {
        TextView tv_novelName, tv_NovelView, tv_LastestChapter, tv_NovelRate, tv_Update;
        ImageView image;
        CardView container;

        NovelViewHolder(View v) {
            super(v);
            container = (CardView) v.findViewById(R.id.cardview_container);
            tv_novelName = (TextView) v.findViewById(R.id.tv_novelName);
            tv_NovelView = (TextView) v.findViewById(R.id.tv_NovelView);
            tv_LastestChapter = (TextView) v.findViewById(R.id.tv_LastestChapter);
            tv_NovelRate = (TextView) v.findViewById(R.id.tv_NovelRate);
            tv_Update = (TextView) v.findViewById(R.id.tv_Update);
            image = (ImageView) v.findViewById(R.id.iv_novelIcon);

        }

        public void clearAnimation() {
            container.clearAnimation();
        }
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.loadprogress_bar);
            progressBar.getIndeterminateDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setLoaded() {
        loading = false;
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

