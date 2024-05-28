package com.valvrare.littlekai.valvraretranslation.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.database.ValvrareDatabaseHelper;
import com.valvrare.littlekai.valvraretranslation.model.Chapter;
import com.valvrare.littlekai.valvraretranslation.model.Novel;

import java.util.ArrayList;

/**
 * Created by Kai on 8/4/2016.
 */
public class ChapterListAdapter extends ArrayAdapter<Chapter> implements Filterable {

    private Context context;
    private int resId;
    private ArrayList<Chapter> listNavItems, items_copy;
    private ValvrareDatabaseHelper db;
    private Novel novel;

    //    private ChapterContentHelper db;
//LNReaderApplication         lnReaderApplication ;
    public ChapterListAdapter(Context context, int resource, ArrayList<Chapter> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resId = resource;
        this.listNavItems = objects;
        items_copy = objects;
//        items_copy = new ArrayList<>();
//        items_copy.addAll(objects);
        db = new ValvrareDatabaseHelper(context);
//        lnReaderApplication = (LNReaderApplication) context;
//        db = new ValvrareDatabaseHelper(context);
    }

//    public ArrayList<Chapter> getlistItems() {
//        return items_copy;
//    }

    @Override
    public int getCount() {
//        LNReaderApplication.setMax_chapter(listNavItems.size());
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
        TextView tv_chapterListName, tv_InsertedChapterDay;
        ImageView im_downloaded, im_avatar;
        CheckBox checkbox_fav;
    }

    @NonNull
    @Override
    public View getView(final int pos, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        final Chapter chapter = listNavItems.get(pos);
        if (resId == R.layout.item_snk_chapter_list) {
            if (convertView == null) {
                convertView = View.inflate(context, resId, null);
                holder = new ViewHolder();
                holder.tv_chapterListName = (TextView) convertView.findViewById(R.id.tv_chapterListName);
                holder.tv_InsertedChapterDay = (TextView) convertView.findViewById(R.id.tv_InsertedChapterDay);
                holder.im_avatar = (ImageView) convertView.findViewById(R.id.im_avatar);
                holder.im_downloaded = (ImageView) convertView.findViewById(R.id.im_downloaded);
                holder.checkbox_fav = (CheckBox) convertView.findViewById(R.id.checkbox_fav);
                convertView.setTag(holder);
            } else holder = (ViewHolder) convertView.getTag();
            int position = chapter.getOrderNo();

            if (chapter.isRead())
                holder.tv_chapterListName.setTextColor(0xffff6600);
            else
                holder.tv_chapterListName.setTextColor(0xff795548);

            String addNo = "";
            if (position > -1 & position < 10)
                addNo = "00";
            if (position > 9 & position < 100)
                addNo = "0";

            if (chapter.isDown()) {
                holder.im_downloaded.setVisibility(View.VISIBLE);
            } else {
                holder.im_downloaded.setVisibility(View.GONE);
            }

            if (chapter.getImg() != null & !chapter.getImg().isEmpty())
                Glide.with(holder.im_avatar.getContext()).load(chapter.getImg()).into(holder.im_avatar);
            else
                Glide.with(holder.im_avatar.getContext()).load(R.drawable.snk_avatar).into(holder.im_avatar);

            holder.checkbox_fav.setChecked(chapter.isFav());
            holder.checkbox_fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isFav = chapter.isFav();
                    if (isFav) {
                        if (db.deleteFavChapter(chapter)) {
                            chapter.setFav(false);
                        }
                    } else {
                        if (db.setChapterFav(chapter)) {
                            chapter.setFav(true);
                        }
                    }
                }
            });
            holder.tv_chapterListName.setText(Html.fromHtml("<b><font color = #24619d>" + addNo + position + "." + "</font></b>" + " " + chapter.getName()));
            holder.tv_InsertedChapterDay.setText(chapter.getSecond_name());
        } else if (resId == R.layout.item_val_chapter_list) {
            if (convertView == null) {
                convertView = View.inflate(context, resId, null);
                holder = new ViewHolder();
                holder.tv_chapterListName = (TextView) convertView.findViewById(R.id.tv_chapterListName);
                holder.tv_InsertedChapterDay = (TextView) convertView.findViewById(R.id.tv_InsertedChapterDay);
//            holder.im_fav = (ImageView) convertView.findViewById(R.id.im_fav);
                holder.im_downloaded = (ImageView) convertView.findViewById(R.id.im_downloaded);
                holder.checkbox_fav = (CheckBox) convertView.findViewById(R.id.checkbox_fav);
                convertView.setTag(holder);
            } else holder = (ViewHolder) convertView.getTag();

//        use <strong> or <b> tag
//        also, you can try with css <span style="font-weight:bold">text</span>
            int position = chapter.getOrderNo();
            if (chapter.isRead())
                holder.tv_chapterListName.setTextColor(0xffff6600);
            else
//            holder.tv_chapterListName.setTextColor(0xff24619d);
                holder.tv_chapterListName.setTextColor(0xff795548);
            String addNo = "";
            if (position > -1 & position < 10)
                addNo = "00";
            if (position > 9 & position < 100)
                addNo = "0";
//        if(position>99&position<1000)
//            addNo = "0";
            if (chapter.isDown()) {
                holder.im_downloaded.setVisibility(View.VISIBLE);
            } else {
//            if (holder.im_downloaded.getVisibility() == View.VISIBLE)
                holder.im_downloaded.setVisibility(View.GONE);
            }
            holder.checkbox_fav.setChecked(chapter.isFav());
            holder.checkbox_fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isFav = chapter.isFav();
                    if (isFav) {
                        if (db.deleteFavChapter(chapter)) {
                            chapter.setFav(false);
//                        Toast.makeText(context, "Đã Bỏ Dấu Chương", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (db.setChapterFav(chapter)) {
                            chapter.setFav(true);
//                        adapter.notifyDataSetChanged();
//                        Toast.makeText(context, "Đã Thêm Dấu Chương", Toast.LENGTH_SHORT).show();
                        }
                    }
//                notifyDataSetChanged();
                }
            });

//            holder.im_fav.setImageResource(R.drawable.star_fav);
//        } else holder.im_fav.setImageResource(R.drawable.star_unfav);
//        holder.tv_chapterListName.setText(Html.fromHtml("<![CDATA[<font color = '#24619d'>" + addNo + (position + 1)+"." +"</font>]]>"+ " " + listChapter.getName()) );
            holder.tv_chapterListName.setText(Html.fromHtml("<b><font color = #24619d>" + addNo + position + "." + "</font></b>" + " " + chapter.getName()));
//        holder.tv_chapterListName.setText("[#" + addNo + (position + 1) + "] " + listChapter.getName());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            holder.tv_InsertedChapterDay.setText(Html.fromHtml(listChapter.getInsertedChapterDay(),Html.FROM_HTML_MODE_COMPACT));
//        }else
//        if (listChapter.getInsertedChapterDay() != null)
            holder.tv_InsertedChapterDay.setText(Html.fromHtml(chapter.getInsertedChapterDay()));
//        else holder.tv_InsertedChapterDay.setVisibility(View.GONE);
        }
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Chapter> filteredList = new ArrayList<>();
                String key = constraint.toString().toLowerCase();
//                if (key.isEmpty())
//                    filteredList.addAll(items_copy);
//                else
                if (key.equals("bm")) {
                    for (Chapter i : items_copy) {
                        if (i.getName().toLowerCase().contains(key) | ("" + i.getOrderNo()).toLowerCase().contains(key) | i.isFav()) {
                            filteredList.add(i);
                        }
                    }
                } else {
                    for (Chapter i : items_copy) {
                        if (i.getName().toLowerCase().contains(key) | ("" + i.getOrderNo()).toLowerCase().contains(key)) {
                            filteredList.add(i);
                        }
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
        return filter;
    }

    public void filter(String key) {
        listNavItems.clear();

        if (key.isEmpty()) {
            Log.d("Kai", "filter: ");
            listNavItems.addAll(items_copy);
        } else {
            key = key.toLowerCase();
            if (key.equals("bm")) {
                for (Chapter i : items_copy) {
                    if (i.getName().toLowerCase().contains(key) | ("" + i.getOrderNo()).toLowerCase().contains(key) | i.isFav()) {
                        listNavItems.add(i);
                    }
                }
            } else {

                for (Chapter i : items_copy) {
                    if (i.getName().toLowerCase().contains(key) | ("" + i.getOrderNo()).toLowerCase().contains(key)) {
                        listNavItems.add(i);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }
}
