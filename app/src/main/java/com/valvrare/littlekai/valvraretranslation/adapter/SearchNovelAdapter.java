package com.valvrare.littlekai.valvraretranslation.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.util.SortedList;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.database.ValvrareDatabaseHelper;
import com.valvrare.littlekai.valvraretranslation.model.Novel;
import com.valvrare.littlekai.valvraretranslation.utils.Constants;
import com.valvrare.littlekai.valvraretranslation.utils.DbBitmapUtility;
import com.valvrare.littlekai.valvraretranslation.utils.OnLoadMoreListener;
import com.valvrare.littlekai.valvraretranslation.widget.SquareImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Kai on 8/4/2016.
 */
public class SearchNovelAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    private final int GRID_ITEM_SIZE = 150;

    private ArrayList<Novel> items;
    private ArrayList<Novel> itemsCopy;
    private OnItemClickListener onItemClickListener;
    private Context context;
    private int resLayout;
    private ValvrareDatabaseHelper db;

    public SearchNovelAdapter(Context c, ArrayList<Novel> items) {
        context = c;
        this.items = items;
    }

    public SearchNovelAdapter(Context c, ArrayList<Novel> items, RecyclerView recyclerView) {
        context = c;
        this.items = items;
        itemsCopy = new ArrayList<>();
        itemsCopy.addAll(items);
        db = new ValvrareDatabaseHelper(context);
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            int columns;
            columns = dpWidth() / GRID_ITEM_SIZE;
            recyclerView.setLayoutManager(new GridLayoutManager(context, columns));
            resLayout = R.layout.item_search_novel_gridview;
        } else resLayout = R.layout.item_search_novel_listview;
    }

    private int dpWidth() {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) (displayMetrics.widthPixels / displayMetrics.density);
    }


    @Override
    public int getItemViewType(int position) {
        int VIEW_PROG = 0;
        int VIEW_ITEM = 1;
        return items.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(resLayout, parent, false);
        v.setOnClickListener(this);
        vh = new NovelViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof NovelViewHolder) {
            final Novel novel = items.get(position);

            String imgUrl = novel.getImage();
            if (novel.getUrl().contains(Constants.VAL_URL_ROOT)) {
                if (imgUrl == null || imgUrl.isEmpty() || imgUrl.equals("/no-avatar.png")) {
                    ((NovelViewHolder) holder).image.setImageResource(R.drawable.error_image);
                } else {
                    if (novel.getImg_file() != null) {
                        Bitmap img = DbBitmapUtility.getImage(novel.getImg_file());
                        ((NovelViewHolder) holder).image.setImageBitmap(img);
                    } else {
                        Glide.with(((NovelViewHolder) holder).image.getContext()).load(R.drawable.error_image).
                                into(((NovelViewHolder) holder).image);
                        if (!imgUrl.equals("") & novel.getImg_file() == null) {
                            new AsyncTask<Novel, Void, Bitmap>() {
                                @Override
                                protected Bitmap doInBackground(Novel... params) {
                                    Bitmap bitmap;
                                    try {
                                        bitmap = Glide.with(context).load(novel.getImage()).asBitmap().into(150, 150).get();
                                        return bitmap;
                                    } catch (InterruptedException | ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Bitmap bitmap) {
                                    if (bitmap != null) {
                                        novel.setImg_file(DbBitmapUtility.getBytes(bitmap));
                                        db.updateNovelImage(novel);
                                        ((NovelViewHolder) holder).image.setImageBitmap(bitmap);
                                    }
                                    super.onPostExecute(bitmap);
                                }
                            }.execute(novel);
                        }
                    }
                }

            } else if (novel.getUrl().contains(Constants.SNK_URL_ROOT)) {
                if (imgUrl == null || imgUrl.isEmpty()) {
                    ((NovelViewHolder) holder).image.setImageResource(R.drawable.snk_avatar);
//                    Glide.with(((NovelViewHolder) holder).image.getContext()).load(R.drawable.snk_avatar)
//                            .into(((NovelViewHolder) holder).image);
                } else {
                    if (position < 10 & resLayout == R.layout.item_search_novel_listview)
                        Glide.with(((NovelViewHolder) holder).image.getContext()).load(imgUrl).override(130, 130)
                                .error(R.drawable.error_image).into(((NovelViewHolder) holder).image);
                    else
                        Glide.with(((NovelViewHolder) holder).image.getContext()).load(imgUrl)
                                .error(R.drawable.error_image).into(((NovelViewHolder) holder).image);
                }
            }

            ((NovelViewHolder) holder).tv_novelName.setText(novel.getNovelName());
            ((NovelViewHolder) holder).tv_NovelType.setText(getTypeNovel(novel.getType()));
            holder.itemView.setTag(novel);
        }
    }

    private String getTypeNovel(String type) {
        if(type==null)
            return "";
        String result = "";
        if (type.contains(",")) {
            String[] parts = type.split(",");
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals("Manga")) {
                    if (i == 0) result = parts[i];
                    else result = result + ", " + parts[i];
                } else {
                    if (parts[i].equals("Idle") | parts[i].equals("Active") | parts[i].equals("Inactive"))
                        parts[i] = parts[i] + " Light";
                    if (i == 0) result = parts[i] + " Novel";
                    else result = result + ", " + parts[i] + " Novel";
                }
            }
        } else {
            if (type.equals("Manga"))
                result = type;
             else if (type.equals("Idle") | type.equals("Active") | type.equals("Inactive"))
                result = type + " Light Novel";
            else result = type + " Novel";
        }
        return result;
    }

//    private void setAnimation(View viewToAnimate, int position) {
//        if (position > lastPosition) {
//            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
//            viewToAnimate.startAnimation(animation);
//            lastPosition = position;
//        }
//    }
//
//    @Override
//    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
//        super.onViewDetachedFromWindow(holder);
//        holder.itemView.clearAnimation();
//
//    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    private static class NovelViewHolder extends RecyclerView.ViewHolder {
        SquareImageView image;
        TextView tv_novelName, tv_NovelType;

        NovelViewHolder(View v) {
            super(v);
            image = (SquareImageView) v.findViewById(R.id.image);
//            container = (FrameLayout) v.findViewById(R.id.container);
            tv_novelName = (TextView) v.findViewById(R.id.text);
            tv_NovelType = (TextView) v.findViewById(R.id.lastest);
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
            }, 100);
        }
    }

    public void filter(String text) {
        items.clear();

        if (text.isEmpty()) {
            items.addAll(itemsCopy);
        } else {
            text = text.toLowerCase();
            for (Novel i : itemsCopy) {
                if (i.getNovelName().toLowerCase().contains(text)) {
                    items.add(i);
                }
            }
        }
        notifyDataSetChanged();
    }

    private final SortedList.Callback<Novel> mCallback = new SortedList.Callback<Novel>() {
        @Override
        public void onInserted(int position, int count) {

        }

        @Override
        public void onRemoved(int position, int count) {

        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {

        }

        @Override
        public int compare(Novel o1, Novel o2) {
            return 0;
        }

        @Override
        public void onChanged(int position, int count) {

        }

        @Override
        public boolean areContentsTheSame(Novel oldItem, Novel newItem) {
            return false;
        }

        @Override
        public boolean areItemsTheSame(Novel item1, Novel item2) {
            return false;
        }
    };


    public interface OnItemClickListener {
        void onItemClick(View view, Novel novel);
    }

}

