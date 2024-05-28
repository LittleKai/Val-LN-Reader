package com.valvrare.littlekai.valvraretranslation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.model.Novel;

import java.util.ArrayList;


/**
 * Created by Thanh on 7/12/2015.
 */
public class SearchAdapter extends ArrayAdapter<Novel> implements Filterable {
    private ArrayList<Novel> novels;
    private ArrayList<Novel> temp;
    private Context context;

    public SearchAdapter(Context c, int textViewResourceId, ArrayList<Novel> list) {
        super(c, textViewResourceId);
        context = c;
        novels = list;
        temp = new ArrayList<>(novels);
    }

    @Override
    public int getCount() {
        return novels.size();
    }

    @Override
    public Novel getItem(int position) {
        return novels.get(position);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = vi.inflate(R.layout.item_search_dropdown, parent, false);
        }

        TextView novel_name = (TextView) convertView.findViewById(R.id.search_novel_name);

        Novel novel = getItem(position);

        convertView.setTag(novel);

        novel_name.setText(novel.getNovelName());

        return convertView;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (results.count == 0) {
                    notifyDataSetInvalidated();
                } else {
                    novels = (ArrayList<Novel>) results.values;
                    notifyDataSetChanged();
                }

            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    results.values = temp;
                    results.count = temp.size();
                } else {
                    ArrayList<Novel> filteredRowItems = new ArrayList<>();
                    novels = temp;
                    for (Novel m : novels) {
                        if (m.getNovelName().trim().toLowerCase().contains(constraint.toString().trim().toLowerCase())) {
                            filteredRowItems.add(m);
                        }
                    }
                    results.values = filteredRowItems;
                    results.count = filteredRowItems.size();
                }
                return results;
            }
        };
    }


}
