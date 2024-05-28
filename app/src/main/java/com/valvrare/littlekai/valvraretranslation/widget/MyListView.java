package com.valvrare.littlekai.valvraretranslation.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

/**
 * Created by Kai on 2/9/2017.
 */

public class MyListView  extends ListView {

    public MyListView  (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView  (Context context) {
        super(context);
    }

    public MyListView  (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}