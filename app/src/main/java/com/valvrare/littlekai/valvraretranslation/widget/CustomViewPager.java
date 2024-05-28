package com.valvrare.littlekai.valvraretranslation.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import com.valvrare.littlekai.valvraretranslation.NovelDescriptionActivity;
import com.valvrare.littlekai.valvraretranslation.helper.LNReaderApplication;

/**
 * Created by Kai on 9/22/2016.
 */
public class CustomViewPager extends ViewPager {

    private static final String TAG = "Kai";
    onViewPagerScrollListener scrollListener;
    private NovelDescriptionActivity activity;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setActivity(NovelDescriptionActivity activity) {
        this.activity = activity;
    }

    public void setOnViewPagerScrollListener(onViewPagerScrollListener listener) {
        this.scrollListener = listener;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        final int DISTANCE = 105;
        float startY = 0;
        float distY = 0;
        boolean isTitleHide = activity.isTitleHide();
        if (!isTitleHide) {
            int action = event.getAction();
             if (action == MotionEvent.ACTION_MOVE) {
                distY = event.getY() - startY;
                Log.d(TAG, "onInterceptTouchEvent: "+ pxToDp((int) distY));
                if (pxToDp((int) distY) > DISTANCE) {
                    if (scrollListener != null) {
                        scrollListener.viewPagerScrollListener();
                    }
                }
            }
        }

        return super.onInterceptTouchEvent(event);
    }

    public interface onViewPagerScrollListener {
        public void viewPagerScrollListener();
    }

    public int pxToDp(int px) {
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        return Math.round(px / (dm.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}