package com.valvrare.littlekai.valvraretranslation.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.valvrare.littlekai.valvraretranslation.R;
import com.valvrare.littlekai.valvraretranslation.model.OptionMenu;

import java.util.List;

/**
 * Created by Kai on 8/4/2016.
 */
public class OptionMenuAdapter extends ArrayAdapter<OptionMenu> {

    private Context context;
    int resLayout;
    private List<OptionMenu> listNavItems;


    public OptionMenuAdapter(Context context, int resource, List<OptionMenu> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resLayout=resource;
        this.listNavItems=objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(context,resLayout,null);

        TextView tvTitle = (TextView) v.findViewById(R.id.nav_title);
        TextView tvsubTitle = (TextView) v.findViewById(R.id.nav_subtitle);
        ImageView tvIcon = (ImageView) v.findViewById(R.id.nav_icon);

        OptionMenu optionMenu = listNavItems.get(position);

        tvTitle.setText(optionMenu.getTitle());
        tvsubTitle.setText(optionMenu.getSubTitle());
        tvIcon.setImageResource(optionMenu.getImgId());
        return v;
    }
}
