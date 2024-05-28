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

import com.valvrare.littlekai.valvraretranslation.R;


public class CustomAutoCompleteTextView extends AutoCompleteTextView {

    boolean justCleared = false;

    private Context c;

    private OnClearListener defaultClearListener = new OnClearListener() {

        @Override
        public void onClear() {
            CustomAutoCompleteTextView et = CustomAutoCompleteTextView.this;
            et.setText("");
        }
    };

    private OnClearListener onClearListener = defaultClearListener;


    // The image we defined for the clear button
    public Drawable imgClearButton;
    public Drawable imgSearchButton;
    public Drawable bottomLine;
    private boolean modifyingText = false;

    public interface OnClearListener {
        void onClear();
    }


    /* Required methods, not used in this implementation */
    public CustomAutoCompleteTextView(Context context) {
        super(context);
        c = context;
        init();
    }

    /* Required methods, not used in this implementation */
    public CustomAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        c = context;
        init();
    }

    /* Required methods, not used in this implementation */
    public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        c = context;
        init();
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

    void init() {
        imgSearchButton = ContextCompat.getDrawable(c, R.drawable.ic_magnify_white_24dp);
        imgClearButton = ContextCompat.getDrawable(c, R.drawable.ic_action_clear);

        // Set the bounds of the button
        this.setCompoundDrawablesWithIntrinsicBounds(imgSearchButton, null,
                imgClearButton, null);

        // if the clear button is pressed, fire up the handler. Otherwise do nothing
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                CustomAutoCompleteTextView et = CustomAutoCompleteTextView.this;

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
        this.setCompoundDrawables(null, null, null, null);
    }

    public void showClearButton() {
        this.setCompoundDrawablesWithIntrinsicBounds(null, null, imgClearButton, null);
    }

}

