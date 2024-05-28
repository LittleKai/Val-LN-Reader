package com.valvrare.littlekai.valvraretranslation.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.valvrare.littlekai.valvraretranslation.R;

/**
 * Created by Kai on 2/16/2017.
 */

public class CustomSearchTextView extends AutoCompleteTextView {
    boolean justCleared = false;

    private Context c;

    public interface OnClearListener {
        void onClear();
    }

    private OnClearListener defaultClearListener = new OnClearListener() {

        @Override
        public void onClear() {
            CustomSearchTextView et = CustomSearchTextView.this;
            et.setText("");
        }
    };

    private OnClearListener onClearListener = defaultClearListener;


    // The image we defined for the clear button
    public Drawable imgClearButton;
    public Drawable imgSearchButton;
    public Drawable bottomLine;
    private boolean modifyingText = false;

    public CustomSearchTextView(Context context) {
        super(context);
        c = context;
        init();
    }

    public CustomSearchTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        c = context;
        init();
    }

    public CustomSearchTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        c = context;
        init();
    }


    void init() {
//        underlineText();
        imgSearchButton = ContextCompat.getDrawable(c, R.drawable.ic_magnify_grey600_24dp);
        imgClearButton = ContextCompat.getDrawable(c, R.drawable.ic_close_grey600_24dp);

        // Set the bounds of the button
        this.setCompoundDrawablesWithIntrinsicBounds(imgSearchButton, null,
                imgClearButton, null);

        // if the clear button is pressed, fire up the handler. Otherwise do nothing
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                CustomSearchTextView et = CustomSearchTextView.this;

                if (et.getCompoundDrawables()[2] == null)
                    return false;

                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;

                if (event.getX() > et.getWidth() - et.getPaddingRight() - imgClearButton.getIntrinsicWidth()) {
                    onClearListener.onClear();
                    justCleared = true;
                }
                return false;
            }
        });

    }

    void underlineText() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                applySpannable();
            }
        });
        applySpannable();
    }

    private Spannable getSpannableString() {
        SpannableString finalText = new SpannableString(getText().toString());
        finalText.setSpan(new UnderlineSpan(), 0, finalText.length(), 0);
        return finalText;
    }

    private void applySpannable() {
        if (modifyingText) return;
        modifyingText = true;
        super.setText(getSpannableString(), BufferType.SPANNABLE);
        modifyingText = false;
    }

    public void setImgClearButton(Drawable imgClearButton) {
        this.imgClearButton = imgClearButton;
    }

    public void setOnClearListener(final OnClearListener clearListener) {
        this.onClearListener = clearListener;
    }

    public void hideClearButton() {
        this.setCompoundDrawables(imgSearchButton, null, null, null);
    }

    public void showClearButton() {
        this.setCompoundDrawablesWithIntrinsicBounds(imgSearchButton, null, imgClearButton, null);
    }
}
