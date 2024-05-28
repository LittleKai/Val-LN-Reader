package com.valvrare.littlekai.valvraretranslation.animation;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.valvrare.littlekai.valvraretranslation.R;


public class AnimationHelper {
    private Context context;
    public AnimationHelper(Context c) {
        context = c;
    }

    public void fadeOut(View view) {
        try {
            Animation in = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
            view.startAnimation(in);
            view.setVisibility(View.INVISIBLE);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void slideInFromBottom(View view) {
        try {
            Animation in = AnimationUtils.loadAnimation(context, R.anim.abc_slide_in_bottom);
            view.startAnimation(in);
            view.setVisibility(View.VISIBLE);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void slideOutFromBottom(View view) {
        try {
            Animation in = AnimationUtils.loadAnimation(context, R.anim.abc_slide_out_bottom);
            view.startAnimation(in);
            view.setVisibility(View.GONE);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void slideInFromTop(View view) {
        try {
            Animation in = AnimationUtils.loadAnimation(context, R.anim.abc_slide_in_top);
            view.startAnimation(in);
            view.setVisibility(View.VISIBLE);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void slideOutFromTop(View view) {
        try {
            Animation in = AnimationUtils.loadAnimation(context, R.anim.abc_slide_out_top);
            view.startAnimation(in);
            view.setVisibility(View.GONE);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void fadeIn(View view) {
        try {
            Animation in = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            view.startAnimation(in);
            view.setVisibility(View.VISIBLE);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void slideIn(View view) {
        try {
            Animation in = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            view.startAnimation(in);
            view.setVisibility(View.VISIBLE);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
